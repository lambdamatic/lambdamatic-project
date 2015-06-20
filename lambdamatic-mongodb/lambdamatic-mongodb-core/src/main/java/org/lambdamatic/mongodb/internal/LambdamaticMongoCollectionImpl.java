/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.internal;

import java.util.Arrays;

import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.lambdamatic.mongodb.FilterExpression;
import org.lambdamatic.mongodb.LambdamaticMongoCollection;
import org.lambdamatic.mongodb.annotations.Document;
import org.lambdamatic.mongodb.exceptions.OperationException;
import org.lambdamatic.mongodb.internal.codecs.BindingService;
import org.lambdamatic.mongodb.internal.codecs.DocumentCodecProvider;
import org.lambdamatic.mongodb.internal.codecs.FilterExpressionCodecProvider;
import org.lambdamatic.mongodb.internal.codecs.IdFilterCodecProvider;
import org.lambdamatic.mongodb.internal.codecs.ProjectionExpressionCodecProvider;
import org.lambdamatic.mongodb.internal.codecs.UpdateExpressionCodecProvider;
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
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 * @param <DomainType>
 *            the Domain Type annotated with {@link Document}
 * @param <QueryType>
 *            the {@link QueryMetadata} associated with Domain Type
 * @param <ProjectionType>
 *            the {@link ProjectionMetadata} associated with Domain Type
 * @param <UpdateType>
 *            the {@link UpdateMetadata} associated with Domain Type
 * 
 */
public class LambdamaticMongoCollectionImpl<DomainType, QueryType extends QueryMetadata<DomainType>, ProjectionType extends ProjectionMetadata<DomainType>, UpdateType extends UpdateMetadata<DomainType>>
		implements LambdamaticMongoCollection<DomainType, QueryType, ProjectionType, UpdateType> {

	private static final Logger LOGGER = LoggerFactory.getLogger(LambdamaticMongoCollectionImpl.class);

	/** The underlying MongoDB Collection. */
	private final com.mongodb.client.MongoCollection<DomainType> mongoCollection;

	/** The user-defined domain class associated with this Collection. */
	private final Class<DomainType> targetClass;

	/**
	 * Internal cache of bindings to convert domain class instances from/to {@link BsonDocument}.
	 */
	private final BindingService bindingService;

	/** The registry of the custom {@link Codec}. */
	private final CodecRegistry codecRegistry;

	/**
	 * Constructor.
	 * 
	 * @param mongoClient
	 *            the underlying MongoDB Client.
	 * @param databaseName
	 *            the name of the underlying {@link MongoDatabase} to use with the given client.
	 * @param collectionName
	 *            the name of the {@link MongoCollection} to use in the given database.
	 * @param targetClass
	 *            the Java type associated with the documents in the {@link MongoCollection}.
	 * @param metadataClass
	 *            the generated Java class carrying the metadata for the given {@code targetClass}.
	 */
	// TODO: if we don't need the metadataClass argument here, can we manage to have something like:
	// new LambdamaticMongoCollectionImpl<T>(MongoClient, String)
	// and retrieve the collection name from <T> ?
	// this would avoid the need to subclass LambdamaticMongoCollectionImpl :-)
	public LambdamaticMongoCollectionImpl(final MongoClient mongoClient, final String databaseName,
			final String collectionName, final Class<DomainType> targetClass) {
		this.bindingService = new BindingService();
		this.codecRegistry = CodecRegistries.fromProviders(new DocumentCodecProvider(bindingService),
				new FilterExpressionCodecProvider(), new ProjectionExpressionCodecProvider(),
				new UpdateExpressionCodecProvider(bindingService), new IdFilterCodecProvider(bindingService),
				new BsonValueCodecProvider());
		this.mongoCollection = mongoClient.getDatabase(databaseName).withCodecRegistry(codecRegistry)
				.getCollection(collectionName, targetClass);
		this.targetClass = targetClass;
		LOGGER.debug("Initialized MongoCollection for documents of class '{}'", targetClass);
	}

	@Override
	public FilterContext<DomainType, ProjectionType, UpdateType> filter(
			final FilterExpression<QueryType> filterExpression) {
		return new FilterContextImpl<DomainType, QueryType, ProjectionType, UpdateType>(mongoCollection,
				filterExpression, this.codecRegistry);
	}

	@Override
	public void add(@SuppressWarnings("unchecked") final DomainType... domainObjects) {
		if (domainObjects.length > 0) {
			mongoCollection.insertMany(Arrays.asList(domainObjects));
		}
	}

	@Override
	public void upsert(final DomainType domainObject) {
		final BsonDocument idFilterDocument = BsonDocumentWrapper.asBsonDocument(new IdFilter<DomainType>(domainObject),
				this.codecRegistry);
		mongoCollection.replaceOne(idFilterDocument, domainObject, new UpdateOptions().upsert(true));
	}

	@Override
	public void replace(final DomainType domainObject) {
		final BsonDocument idFilterDocument = BsonDocumentWrapper.asBsonDocument(new IdFilter<DomainType>(domainObject),
				this.codecRegistry);
		final UpdateResult result = mongoCollection.replaceOne(idFilterDocument, domainObject);
		if (result.isModifiedCountAvailable() && result.getMatchedCount() != 1) {
			throw new OperationException(
					"Invalid number of document match during the update operation: " + result.getMatchedCount());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "MongoDB Collection of " + this.targetClass.getName();
	}

}
