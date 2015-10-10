/*******************************************************************************
 * Copyright (c) 2014, 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc. All rights
 * reserved. This program is made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.internal;

import java.util.Arrays;

import org.bson.BsonDocument;
import org.lambdamatic.mongodb.FilterExpression;
import org.lambdamatic.mongodb.LambdamaticMongoCollection;
import org.lambdamatic.mongodb.annotations.Document;
import org.lambdamatic.mongodb.exceptions.OperationException;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.QueryMetadata;
import org.lambdamatic.mongodb.metadata.UpdateMetadata;
import org.lambdamatic.mongodb.query.context.FilterContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

/**
 * Database Collection for a given type of element (along with its associated metadata).
 * 
 * @param <DomainType> the Domain Type annotated with {@link Document}
 * @param <QueryType> the {@link QueryMetadata} associated with Domain Type
 * @param <ProjectionType> the {@link ProjectionMetadata} associated with Domain Type
 * @param <UpdateType> the {@link UpdateMetadata} associated with Domain Type
 * 
 */
public class LambdamaticMongoCollectionImpl<
      DomainType, 
      QueryType extends QueryMetadata<DomainType>, 
      ProjectionType extends ProjectionMetadata<DomainType>, 
      UpdateType extends UpdateMetadata<DomainType>>
    extends FilterContextImpl<DomainType, QueryType, ProjectionType, UpdateType>
    implements LambdamaticMongoCollection<DomainType, QueryType, ProjectionType, UpdateType> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(LambdamaticMongoCollectionImpl.class);

  /** The user-defined domain class associated with this Collection. */
  private final Class<DomainType> targetClass;

  /**
   * Constructor.
   * 
   * @param mongoClient the underlying MongoDB Client.
   * @param databaseName the name of the underlying {@link MongoDatabase} to use with the given
   *        client.
   * @param collectionName the name of the {@link MongoCollection} to use in the given database.
   * @param targetClass the Java type associated with the documents in the {@link MongoCollection}.
   */
  public LambdamaticMongoCollectionImpl(final MongoClient mongoClient, final String databaseName,
      final String collectionName, final Class<DomainType> targetClass) {
    super(mongoClient.getDatabase(databaseName).withCodecRegistry(BsonUtils.codecRegistry)
        .getCollection(collectionName, targetClass));
    this.targetClass = targetClass;
    LOGGER.debug("Initialized MongoCollection for documents of class '{}'", targetClass);
  }

  @Override
  public FilterContext<DomainType, ProjectionType, UpdateType> all() {
    return new FilterContextImpl<DomainType, QueryType, ProjectionType, UpdateType>(
        getMongoCollection());
  }

  @Override
  public FilterContext<DomainType, ProjectionType, UpdateType> filter(
      final FilterExpression<QueryType> filterExpression) {
    return new FilterContextImpl<DomainType, QueryType, ProjectionType, UpdateType>(
        getMongoCollection(), BsonUtils.asBsonDocument(filterExpression));
  }

  @Override
  public void add(@SuppressWarnings("unchecked") final DomainType... domainObjects) {
    if (domainObjects.length > 0) {
      getMongoCollection().insertMany(Arrays.asList(domainObjects));
    }
  }

  @Override
  public void upsert(final DomainType domainObject) {
    final BsonDocument idFilterDocument = BsonUtils.asBsonDocument(new IdFilter<>(domainObject));
    getMongoCollection().replaceOne(idFilterDocument, domainObject,
        new UpdateOptions().upsert(true));
  }

  @Override
  public void replace(final DomainType domainObject) {
    final BsonDocument idFilterDocument = BsonUtils.asBsonDocument(new IdFilter<>(domainObject));
    final UpdateResult result = getMongoCollection().replaceOne(idFilterDocument, domainObject);
    if (result.isModifiedCountAvailable() && result.getMatchedCount() != 1) {
      throw new OperationException("Invalid number of document match during the update operation: "
          + result.getMatchedCount());
    }
  }

  @Override
  public String toString() {
    return "MongoDB Collection of " + this.targetClass.getName();
  }

}
