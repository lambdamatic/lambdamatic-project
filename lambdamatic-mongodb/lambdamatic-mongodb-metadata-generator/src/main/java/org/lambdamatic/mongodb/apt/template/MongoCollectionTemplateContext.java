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

import java.util.function.Function;
import java.util.function.Supplier;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

import org.apache.commons.lang3.ClassUtils;
import org.lambdamatic.mongodb.annotations.Document;
import org.lambdamatic.mongodb.apt.BaseAnnotationProcessor;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.QueryMetadata;
import org.lambdamatic.mongodb.metadata.UpdateMetadata;

/**
 * Template Context builder for {@link QueryMetadata}, {@link ProjectionMetadata} and {@link UpdateMetadata} generated
 * classes
 */
public class MongoCollectionTemplateContext extends BaseTemplateContext {

	/**
	 * {@link Function} to generate the simple class name for the {@link LambdamaticMongoCollectionImpl} implementation
	 * of a given {@link TypeElement}.
	 */
	public static final Function<DeclaredType, String> createMongoCollectionSimpleClassNameFunction = t -> ClassUtils
			.getShortClassName(t.toString()) + "Collection";

	/** Template name {@link Supplier} for the {@link LambdamaticMongoCollectionImpl} implementation. */
	public static final Supplier<String> collectionTemplateNameSupplier = () -> "mongo_collection_template.mustache";

	/** The collection name, retrieved from the {@link Document} annotation on the domain class. */
	private final String collectionName;
	
	/**
	 * Full constructor
	 * 
	 * @param domainType
	 *            the {@link TypeElement} to work on.
	 */
	public MongoCollectionTemplateContext(final TypeElement domainType,
			final BaseAnnotationProcessor annotationProcessor) {
		super((DeclaredType)domainType.asType(), annotationProcessor);
		this.collectionName = ElementUtils.getAnnotationValue(domainType.getAnnotation(Document.class),
				docAnnotation -> docAnnotation.collection(), "");
	}

	@Override
	public String getFullyQualifiedClassName() {
		return getPackageName() + "." + createMongoCollectionSimpleClassNameFunction.apply(domainType);
	}

	@Override
	public String getSimpleClassName() {
		return createMongoCollectionSimpleClassNameFunction.apply(domainType);
	}

	@Override
	public String getTemplateFileName() {
		return collectionTemplateNameSupplier.get();
	};

	/**
	 * @return the The collection name, retrieved from the {@link Document} annotation on the domain class.
	 */
	public String getCollectionName() {
		return this.collectionName;
	}
	
	/**
	 * @return the simple name of the {@link QueryMetadata} implementation associated with the {@code domainType} class
	 *         (in the same package)
	 */
	public String getQueryMetadataClassName() {
		return MetadataTemplateContext.elementToQuerySimpleClassNameFunction.apply(domainType);
	}

	/**
	 * @return the simple name of the {@link ProjectionMetadata} implementation associated with the {@code domainType} class
	 *         (in the same package)
	 */
	public String getProjectionMetadataClassName() {
		return MetadataTemplateContext.elementToProjectionSimpleClassName.apply(domainType);
	}
	
	/**
	 * @return the simple name of the {@link UpdateMetadata} implementation associated with the {@code domainType} class
	 *         (in the same package)
	 */
	public String getUpdateMetadataClassName() {
		return MetadataTemplateContext.elementToUpdateSimpleClassNameFunction.apply(domainType);
	}
	
}