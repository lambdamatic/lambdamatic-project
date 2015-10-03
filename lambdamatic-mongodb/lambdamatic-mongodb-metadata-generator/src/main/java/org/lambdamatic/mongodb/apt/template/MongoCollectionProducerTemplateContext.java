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
import org.lambdamatic.mongodb.apt.BaseAnnotationProcessor;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.QueryMetadata;
import org.lambdamatic.mongodb.metadata.UpdateMetadata;

/**
 * Template Context builder for {@link QueryMetadata}, {@link ProjectionMetadata} and {@link UpdateMetadata} generated
 * classes
 */
public class MongoCollectionProducerTemplateContext extends BaseTemplateContext {

	/**
	 * {@link Function} to generate the simple class name for the {@link LambdamaticMongoCollectionImpl} implementation of a given
	 * {@link TypeElement}.
	 */
	public static final Function<DeclaredType, String> createMongoCollectionProducerSimpleClassNameFunction = t -> 
			ClassUtils.getShortClassName(t.toString()) + "CollectionProducer";

	/** Template name {@link Supplier} for the {@link LambdamaticMongoCollectionImpl} implementation. */
	public static final Supplier<String> collectionProducerTemplateNameSupplier = () -> "mongo_collection_producer_template.mustache";

	/**
	 * Full constructor
	 * 
	 * @param domainType
	 *            the {@link TypeElement} to work on.
	 */
	public MongoCollectionProducerTemplateContext(final TypeElement domainType, final BaseAnnotationProcessor annotationProcessor) {
		super((DeclaredType)domainType.asType(), annotationProcessor);
	}

	@Override
	public String getTemplateFileName() {
		return collectionProducerTemplateNameSupplier.get();
	}

	@Override
	public String getFullyQualifiedClassName() {
		return getPackageName() + "." + getSimpleClassName();
	}
	
	@Override
	public String getSimpleClassName() {
		return createMongoCollectionProducerSimpleClassNameFunction.apply(domainType);
	}
	
	public String getMongoCollectionClassName() {
		return MongoCollectionTemplateContext.createMongoCollectionSimpleClassNameFunction.apply(domainType);
	}

}