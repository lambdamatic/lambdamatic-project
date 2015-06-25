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

package org.lambdamatic.jpa.apt;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import org.lambdamatic.jpa.QueryMetadata;

import com.github.mustachejava.Mustache;

/**
 * @author xcoulon
 *
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({ "javax.persistence.Entity" })

public class EntityMetadataGenerator extends BaseAnnotationProcessor {

	@Override
	void doProcess(Map<String, Object> templateContextProperties) throws IOException {
		final Mustache template = getTemplate("query_metadata_template.mustache");
		final String targetClassName = templateContextProperties.get("packageName") + "."
				+ templateContextProperties.get(Constants.QUERY_METADATA_CLASS_NAME);
		generateSourceCode(targetClassName, template, templateContextProperties);
	}


	
	/**
	 * Generates the {@code QueryMetadata} implementation source code for the annotated class currently being processed.
	 * 
	 * @param allContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	void generateQueryMetadataSourceCode(final Mustache template, final Map<String, Object> templateContextProperties)
			throws IOException {
		final String targetPackageName = (String) templateContextProperties.get(Constants.PACKAGE_NAME);
		@SuppressWarnings("unchecked")
		final List<QueryFieldMetadata> queryFields = (List<QueryFieldMetadata>) templateContextProperties
				.get(Constants.QUERY_FIELDS);
		templateContextProperties.put(Constants.IMPORT_STATEMENTS, findRequiredImportStatements(targetPackageName, queryFields));
		final String targetClassName = targetPackageName + "."
				+ templateContextProperties.get(Constants.QUERY_METADATA_CLASS_NAME);
		generateSourceCode(targetClassName, template, templateContextProperties);
	}

	protected Map<String, Object> initializeTemplateContextProperties(final TypeElement domainElement) {
		final Map<String, Object> templateContextProperties = super.initializeTemplateContextProperties(domainElement);
		final Map<Class<?>, List<? extends BaseFieldMetadata>> allFields = getMetadataFields(domainElement);
		templateContextProperties.put(Constants.QUERY_FIELDS, allFields.get(QueryMetadata.class));
		templateContextProperties.put(Constants.QUERY_METADATA_CLASS_NAME, generateQueryMetadataSimpleClassName(domainElement));
		templateContextProperties.put(Constants.MONGO_COLLECTION_CLASS_NAME, generateJpaCollectionSimpleClassName(domainElement));
		return templateContextProperties;
	}

	/**
	 * Builds the simple name of the LambdamaticMongoCollection class associated with the given
	 * {@code typeElement}
	 * 
	 * @param typeElement
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, followed by
	 *         {@link Constants#MONGO_COLLECTION_CLASSNAME_SUFFIX}.
	 */
	public static String generateJpaCollectionSimpleClassName(final TypeElement typeElement) {
		return typeElement.getSimpleName().toString() + Constants.MONGO_COLLECTION_CLASSNAME_SUFFIX;
	}

	/**
	 * Generates the {@code LambdamaticMongoCollection} implementation source code for the underlying MongoDB
	 * collection.
	 * 
	 * @param templateContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	protected void generateJpaCollectionSourceCode(final Mustache template, final Map<String, Object> templateContextProperties) throws IOException {
		final String targetClassName = templateContextProperties.get("packageName") + "."
				+ templateContextProperties.get(Constants.MONGO_COLLECTION_CLASS_NAME);
		generateSourceCode(targetClassName, template, templateContextProperties);
	}
	

}
