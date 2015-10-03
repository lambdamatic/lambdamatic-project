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

import static org.lambdamatic.mongodb.apt.template.ElementUtils.getAnnotationValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.lang.model.element.VariableElement;

import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.annotations.DocumentId;
import org.lambdamatic.mongodb.apt.MetadataGenerationException;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.QueryMetadata;
import org.lambdamatic.mongodb.metadata.UpdateMetadata;

/**
 * A field used in a template when generating source code.
 */
public class TemplateField extends TemplateElement {

	public static final String MONGOBD_DOCUMENT_ID = "_id";

	/**
	 * Creates a {@link TemplateField} for the {@link QueryMetadata} implementation from the given field annotated with
	 * {@link DocumentId} or optionally {@link DocumentField}.
	 * 
	 * @param field
	 *            the field element
	 * @throws MetadataGenerationException
	 */
	public static Function<VariableElement, TemplateField> createQueryFieldFunction = field ->
		createTemplateField(field, f -> TemplateType.getQueryMetadataFieldType(f.asType()));

	/**
	 * Creates a {@link TemplateField} for the {@link ProjectionMetadata} implementation from the given field annotated
	 * with {@link DocumentId} or optionally {@link DocumentField}.
	 * 
	 * @param field
	 *            the field element
	 * @throws MetadataGenerationException
	 */
	public static Function<VariableElement, TemplateField> createProjectionFieldFunction = field -> createTemplateField(
			field, f -> TemplateType.getProjectionMetadataFieldType(f.asType()));

	/**
	 * Creates a {@link TemplateField} for the {@link UpdateMetadata} implementation from the given field annotated with
	 * {@link DocumentId} or optionally {@link DocumentField}.
	 * 
	 * @param variableElement
	 *            the field element
	 * @throws MetadataGenerationException
	 */
	public static Function<VariableElement, TemplateField> createUpdateFieldFunction = field -> createTemplateField(
			field, f -> TemplateType.getUpdateMetadataFieldType(f.asType()));

	/**
	 * Creates a {@link TemplateField} from the given field annotated with
	 * {@link DocumentId} or optionally {@link DocumentField}.
	 * 
	 * @param field
	 *            the field element
	 * @throws MetadataGenerationException
	 */
	public static TemplateField createTemplateField(final VariableElement field, final Function<VariableElement, TemplateType> fieldToTemplateTypeFunction) {
		final String javaFieldName = field.getSimpleName().toString();
		if (field.getAnnotation(DocumentId.class) != null) {
			return new TemplateField(javaFieldName, fieldToTemplateTypeFunction.apply(field), MONGOBD_DOCUMENT_ID);
		}
		return new TemplateField(javaFieldName, fieldToTemplateTypeFunction.apply(field),
				getAnnotationValue(field.getAnnotation(DocumentField.class), a -> a.name(), javaFieldName));
	}

	/** The java field name. */
	private final String javaFieldName;
	/** The document field name. */
	private final String documentFieldName;
	/** The java field type. */
	private final TemplateType javaFieldType;
	/** the field annotations. */
	private final List<TemplateAnnotation> annotations;
	/** the required imports to compile the generated source code.*/
	private final List<String> requiredImports;

	/**
	 * Creates a {@link TemplateField}
	 * 
	 * @param javaFieldName
	 *            The java field name
	 * @param javaFieldType
	 *            The java field type
	 * @param documentFieldName
	 *            The document field name
	 */
	private TemplateField(final String javaFieldName, final TemplateType javaFieldType, final String documentFieldName)
			throws MetadataGenerationException {
		this.javaFieldName = javaFieldName;
		this.javaFieldType = javaFieldType;
		this.documentFieldName = documentFieldName;
		this.annotations = getAnnotations(this.javaFieldType, this.documentFieldName);
		final Set<String> requiredJavaTypes = new HashSet<>();
		requiredJavaTypes.addAll(this.javaFieldType.getRequiredJavaTypes());
		requiredJavaTypes.addAll(annotations.stream().map(annotation -> annotation.getRequiredJavaTypes())
				.flatMap(requiredTypes -> requiredTypes.stream()).collect(Collectors.toList()));
		this.requiredImports = requiredJavaTypes.stream().sorted().collect(Collectors.toList());
		
	}

	private static List<TemplateAnnotation> getAnnotations(final TemplateType javaFieldType, final String documentFieldName) {
		final List<TemplateAnnotation> annotations = new ArrayList<>();
		annotations.add(TemplateAnnotation.Builder.type(DocumentField.class).attribute("name", documentFieldName).build());
		return annotations;
	}

	/**
	 * @return the document field name.
	 */
	public String getDocumentFieldName() {
		return documentFieldName;
	}

	/**
	 * @return the Java field name.
	 */
	public String getJavaFieldName() {
		return javaFieldName;
	}

	/**
	 * @return the Java field type.
	 */
	public TemplateType getJavaFieldType() {
		return javaFieldType;
	}

	/**
	 * @return the method annotations.
	 */
	public List<TemplateAnnotation> getAnnotations() {
		return this.annotations;
	}

	/**
	 * @return the required Java types (to include in the import statements)
	 */
	public Collection<String> getRequiredJavaTypes() {
		return this.requiredImports;
	}

	@Override
	public String toString() {
		return "@documentField(\"" + this.documentFieldName + "\") " + this.javaFieldType + " " + this.javaFieldName;
	}
}