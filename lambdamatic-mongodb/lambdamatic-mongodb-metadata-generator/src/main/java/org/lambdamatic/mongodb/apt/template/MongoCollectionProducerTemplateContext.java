/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.apt.template;

import java.util.function.Function;
import java.util.function.Supplier;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

import org.apache.commons.lang3.ClassUtils;
import org.lambdamatic.mongodb.LambdamaticMongoCollection;
import org.lambdamatic.mongodb.apt.BaseAnnotationProcessor;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.QueryMetadata;
import org.lambdamatic.mongodb.metadata.UpdateMetadata;

/**
 * Template Context builder for {@link QueryMetadata}, {@link ProjectionMetadata} and
 * {@link UpdateMetadata} generated classes.
 */
public class MongoCollectionProducerTemplateContext extends BaseTemplateContext {

  /**
   * {@link Function} to generate the simple class name for the {@link LambdamaticMongoCollection}
   * producer of a given {@link TypeElement}.
   */
  public static final Function<DeclaredType, String> mongoCollectionProducerTypeToSimpleClassName =
      t -> ClassUtils.getShortClassName(t.toString()) + "CollectionProducer";

  /**
   * {@link Supplier} for template name of the {@link LambdamaticMongoCollection} JavaEE CDI
   * producer.
   */
  public static final Supplier<String> collectionProducerTemplateNameSupplier =
      () -> "mongo_collection_producer_template.mustache";

  /**
   * Full constructor
   * 
   * @param domainType the {@link TypeElement} to work on.
   * @param annotationProcessor the annotation processor used to generate the
   *        {@link LambdamaticMongoCollection} JavaEE CDI producer
   */
  public MongoCollectionProducerTemplateContext(final TypeElement domainType,
      final BaseAnnotationProcessor annotationProcessor) {
    super((DeclaredType) domainType.asType(), annotationProcessor);
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
    return mongoCollectionProducerTypeToSimpleClassName.apply(this.domainType);
  }

  /**
   * @return the fully qualified name of the {@link LambdamaticMongoCollection} JavaEE CDI producer.
   */
  public String getMongoCollectionClassName() {
    return MongoCollectionTemplateContext.mongoCollectionTypeToSimpleClassName
        .apply(this.domainType);
  }

}
