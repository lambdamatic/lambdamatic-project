/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb;

import java.util.Arrays;

import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.configuration.RootCodecRegistry;
import org.lambdamatic.FilterExpression;
import org.lambdamatic.mongodb.codecs.LambdamaticDocumentCodecProvider;
import org.lambdamatic.mongodb.codecs.LambdamaticFilterExpressionCodecProvider;
import org.lambdamatic.mongodb.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.WriteConcernResult;
import com.mongodb.client.FindFluent;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCollectionOptions;
import com.mongodb.client.MongoDatabase;

/**
 * Database Collection for a given type of element (along with its associated
 * metadata)
 * 
 * @author Xavier Coulon
 *
 */
public class LambdamaticMongoCollectionImpl<T, M extends Metadata<T>> implements LambdamaticMongoCollection<T, M> {

	private static final Logger LOGGER = LoggerFactory.getLogger(LambdamaticMongoCollectionImpl.class);

	/** The underlying MongoDB Collection. */
	private final com.mongodb.client.MongoCollection<T> mongoCollection;

	/** The user-defined domain class associated with this Collection. */
	private final Class<T> targetClass;

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
		// final RootCodecRegistry codecRegistry =
		// MongoClient.getDefaultCodecRegistry().withCodec(new
		// LambdamaticCodec<T>(targetClass));
		final RootCodecRegistry codecRegistry = new RootCodecRegistry(Arrays.asList(
				new LambdamaticDocumentCodecProvider(), new LambdamaticFilterExpressionCodecProvider<M>(), new BsonValueCodecProvider()));
		final MongoCollectionOptions options = MongoCollectionOptions.builder().codecRegistry(codecRegistry).build();
		this.mongoCollection = mongoClient.getDatabase(databaseName)
				.getCollection(collectionName, targetClass, options);
		this.targetClass = targetClass;
		LOGGER.debug("Initialized MongoCollection for documents of class '{}'", targetClass);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FindFluent<T> find(final FilterExpression<M> filterExpression) {
		return mongoCollection.find(filterExpression);
	}

	@Override
	public void insertOne(T domainObject) {
		mongoCollection.insertOne(domainObject);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Collection of " + this.targetClass.getName();
	}

}
