/*******************************************************************************
 * Copyright (c) 2015 Red Hat.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat - Initial Contribution
 *******************************************************************************/
package org.lambdamatic.mongodb.apt.template;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;

import org.apache.commons.lang3.ClassUtils;
import org.lambdamatic.mongodb.annotations.EmbeddedDocument;
import org.lambdamatic.mongodb.annotations.TransientField;
import org.lambdamatic.mongodb.apt.BaseAnnotationProcessor;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.QueryMetadata;
import org.lambdamatic.mongodb.metadata.UpdateMetadata;

/**
 * Template Context builder for {@link QueryMetadata}, {@link ProjectionMetadata} and {@link UpdateMetadata} generated
 * classes
 */
public class MetadataTemplateContext extends BaseTemplateContext {

	/**
	 * {@link Function} to generate the simple class name for the {@link QueryMetadata} implementation of a given
	 * {@link TypeElement}.
	 */
	public static final Function<DeclaredType, String> elementToQuerySimpleClassNameFunction = t -> "Q"
			+ ClassUtils.getShortClassName(t.toString());

	/**
	 * {@link Function} to generate the simple class name for the {@link QueryMetadata} implementation of a given
	 * {@link TypeElement}.
	 */
	public static final Function<DeclaredType, String> elementToProjectionSimpleClassName = t -> "P"
			+ ClassUtils.getShortClassName(t.toString());

	/**
	 * {@link Function} to generate the simple class name for the {@link QueryMetadata} implementation of a given
	 * {@link TypeElement}.
	 */
	public static final Function<DeclaredType, String> elementToUpdateSimpleClassNameFunction = t -> "U"
			+ ClassUtils.getShortClassName(t.toString());

	/**
	 * {@link MetadataTemplateContext} constructor for a {@link QueryMetadata} implementation.
	 * 
	 * @param domainType
	 *            the type of the annotated document
	 */
	public static MetadataTemplateContext createQueryMetadataTemplateContext(final TypeElement domainType,
			final BaseAnnotationProcessor annotationProcessor) {
		return new MetadataTemplateContext(domainType, annotationProcessor, TemplateField.createQueryFieldFunction,
				elementToQuerySimpleClassNameFunction, "query_metadata_template.mustache");
	}

	/**
	 * {@link MetadataTemplateContext} constructor for a {@link ProjectionMetadata} implementation.
	 * 
	 * @param domainType
	 *            the type of the annotated document
	 */
	public static MetadataTemplateContext createProjectionMetadataTemplateContext(final TypeElement domainType,
			final BaseAnnotationProcessor annotationProcessor) {
		return new MetadataTemplateContext(domainType, annotationProcessor, TemplateField.createProjectionFieldFunction,
				elementToProjectionSimpleClassName, "projection_metadata_template.mustache");
	}

	/**
	 * {@link MetadataTemplateContext} constructor for a {@link UpdateMetadata} implementation.
	 * 
	 * @param domainType
	 *            the type of the annotated document
	 */
	public static MetadataTemplateContext createUpdateMetadataTemplateContext(final TypeElement domainType,
			final BaseAnnotationProcessor annotationProcessor) {
		return new MetadataTemplateContext(domainType, annotationProcessor, TemplateField.createUpdateFieldFunction,
				elementToUpdateSimpleClassNameFunction, "update_metadata_template.mustache");
	}

	/** The list of relevant fields to process. */
	private final List<VariableElement> domainTypeFields;

	/** the list of {@link TemplateField} that will be output in the generated source code. */
	private final List<TemplateField> templateFields;

	/** the simple name of the Java class to generate. */
	private final String simpleClassName;

	/** the fully qualified name of the Java class to generate. */
	private final String fullyQualifiedClassName;

	/** the name of the template to use to generate the source code. */
	private final String templateFileName;

	/** The annotations to include on the generated type. */
	private List<TemplateAnnotation> annotations;

	/**
	 * Full constructor
	 * 
	 * @param domainElement
	 *            the {@link TypeElement} to work on.
	 * @param templateFieldBuildFunction
	 *            the {@link Function} used to generate a single {@link TemplateField} from a given relevant
	 *            {@link VariableElement} in the given {@link TypeElement}.
	 * @param templateMethodsBuildFunction
	 *            the {@link Function} used to generate zero or more {@link TemplateMethods} from a given relevant
	 *            {@link VariableElement} in the given {@link TypeElement}.
	 * 
	 */
	private MetadataTemplateContext(final TypeElement domainElement, final BaseAnnotationProcessor annotationProcessor,
			final Function<VariableElement, TemplateField> templateFieldBuildFunction,
			final Function<DeclaredType, String> simpleClassNameBuilder, final String templateFileName) {
		super((DeclaredType)domainElement.asType(), annotationProcessor);
		this.domainTypeFields = domainElement.getEnclosedElements().stream().filter(e -> e.getKind() == ElementKind.FIELD)
				.filter(e -> e.getAnnotation(TransientField.class) == null).map(e -> (VariableElement) e)
				.collect(Collectors.toList());
		this.templateFields = this.domainTypeFields.stream().map(f -> {
			return templateFieldBuildFunction.apply(f);
		}).collect(Collectors.toList());
		this.simpleClassName = simpleClassNameBuilder.apply(this.domainType);
		this.fullyQualifiedClassName = getPackageName() + '.' + simpleClassName;
		this.templateFileName = templateFileName;
		this.annotations = Stream.of(domainElement.getAnnotationsByType(EmbeddedDocument.class))
				.map(a -> TemplateAnnotation.Builder.type(EmbeddedDocument.class).build()).collect(Collectors.toList());
	}

	@Override
	public String getFullyQualifiedClassName() {
		return this.fullyQualifiedClassName;
	}

	@Override
	public String getSimpleClassName() {
		return this.simpleClassName;
	}

	@Override
	public String getTemplateFileName() {
		return this.templateFileName;
	}

	/**
	 * @return the {@link List} of {@link TemplateField} for the associated domain type.
	 */
	public List<TemplateField> getFields() {
		return this.templateFields;
	}

	/**
	 * Finds all required import statements that need to be included in the target template
	 * 
	 * @param targetPackageName
	 *            the target package for the class to be generated
	 * @param fields
	 *            the query fields whose classes may need to ne imported
	 * @return the {@link List} of class imports (excluding those in the same package)
	 */
	public List<String> getRequiredImports() {
		// combining required types on annotations and fields
		final List<String> requiredImports = 
				Stream.concat(this.templateFields.stream().flatMap(f -> f.getRequiredJavaTypes().stream()),
						this.annotations.stream().flatMap(a -> a.getRequiredJavaTypes().stream()))
				.filter(i -> {
					final String packageCanonicalName = ClassUtils.getPackageCanonicalName(i);
					return !packageCanonicalName.equals(getPackageName()) && !packageCanonicalName.equals("java.lang");
				}).distinct().sorted().collect(Collectors.toList());
		return requiredImports;
	}

}