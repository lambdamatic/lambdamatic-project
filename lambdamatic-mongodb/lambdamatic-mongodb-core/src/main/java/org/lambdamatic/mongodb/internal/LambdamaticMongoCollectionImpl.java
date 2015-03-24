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
import org.lambdamatic.SerializablePredicate;
import org.lambdamatic.mongodb.LambdamaticMongoCollection;
import org.lambdamatic.mongodb.internal.codecs.BindingService;
import org.lambdamatic.mongodb.internal.codecs.DocumentCodecProvider;
import org.lambdamatic.mongodb.internal.codecs.FilterExpressionCodecProvider;
import org.lambdamatic.mongodb.internal.codecs.IdFilterCodecProvider;
import org.lambdamatic.mongodb.internal.codecs.ProjectionExpressionCodecProvider;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.QueryMetadata;
import org.lambdamatic.mongodb.query.context.FindContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;

/**
 * Database Collection for a given type of element (along with its associated
 * metadata).
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class LambdamaticMongoCollectionImpl<T, QM extends QueryMetadata<T>, PM extends ProjectionMetadata<T>> implements LambdamaticMongoCollection<T, QM, PM> {

	private static final Logger LOGGER = LoggerFactory.getLogger(LambdamaticMongoCollectionImpl.class);

	/** The underlying MongoDB Collection. */
	private final com.mongodb.client.MongoCollection<T> mongoCollection;

	/** The user-defined domain class associated with this Collection. */
	private final Class<T> targetClass;

	/**
	 * Internal cache of bindings to convert domain class instances from/to
	 * {@link BsonDocument}.
	 */
	private final BindingService bindingService;

	/** The registry of the custom {@link Codec}.*/
	private final CodecRegistry codecRegistry;
	
	/**
	 * Constructor.
	 * 
	 * @param mongoClient
	 *            the underlying MongoDB Client.
	 * @param databaseName
	 *            the name of the underlying {@link MongoDatabase} to use with
	 *            the given client.
	 * @param collectionName
	 *            the name of the {@link MongoCollection} to use in the given
	 *            database.
	 * @param targetClass
	 *            the Java type associated with the documents in the
	 *            {@link MongoCollection}.
	 * @param metadataClass
	 *            the generated Java class carrying the metadata for the given
	 *            {@code targetClass}.
	 */
	//TODO: if we don't need the metadataClass argument here, can we manage to have something like:
	// new LambdamaticMongoCollectionImpl<T>(MongoClient, String) 
	// and retrieve the collection name from <T> ?
	// this would avoid the need to subclass LambdamaticMongoCollectionImpl :-)
	public LambdamaticMongoCollectionImpl(final MongoClient mongoClient, final String databaseName,
			final String collectionName, final Class<T> targetClass) {
		this.bindingService = new BindingService();
		this.codecRegistry = CodecRegistries.fromProviders(new DocumentCodecProvider(
				bindingService), new ProjectionExpressionCodecProvider(), new FilterExpressionCodecProvider(), new IdFilterCodecProvider(bindingService), new BsonValueCodecProvider());
		this.mongoCollection = mongoClient.getDatabase(databaseName).withCodecRegistry(codecRegistry)
				.getCollection(collectionName, targetClass);
		this.targetClass = targetClass;
		LOGGER.debug("Initialized MongoCollection for documents of class '{}'", targetClass);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FindContext<T, PM> find(final SerializablePredicate<QM> filterExpression) {
		final BsonDocument filterDocument = BsonDocumentWrapper.asBsonDocument(filterExpression, this.codecRegistry);
		return new FindContextImpl<T, PM>(mongoCollection.find(filterDocument), this.codecRegistry);
	}

	@Override
	public void insert(@SuppressWarnings("unchecked") final T... domainObjects) {
		if(domainObjects.length > 0) {
			mongoCollection.insertMany(Arrays.asList(domainObjects));
		}
	}

	@Override
	public void upsert(final T domainObject) {
		final BsonDocument idFilterDocument = BsonDocumentWrapper.asBsonDocument(new IdFilter<T>(domainObject), this.codecRegistry);
		mongoCollection.replaceOne(idFilterDocument, domainObject, new UpdateOptions().upsert(true));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Collection of " + this.targetClass.getName();
	}

}
