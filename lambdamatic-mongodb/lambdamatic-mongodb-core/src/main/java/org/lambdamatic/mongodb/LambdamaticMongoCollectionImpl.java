/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb;

import java.util.Arrays;

import org.bson.codecs.configuration.RootCodecRegistry;
import org.lambdamatic.FilterExpression;
import org.lambdamatic.mongodb.converters.DBObjectConverter;
import org.lambdamatic.mongodb.converters.LambdamaticDocumentCodecProvider;
import org.lambdamatic.mongodb.converters.LambdamaticFilterExpressionCodecProvider;
import org.lambdamatic.mongodb.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DBObject;
import com.mongodb.DBObjectCodecProvider;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcernResult;
import com.mongodb.client.FindFluent;
import com.mongodb.client.MongoCollectionOptions;

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

	/** The metadata class associated with the user-defined domain class. */
	private final Class<M> metadataClass;

	/**
	 * Constructor.
	 * 
	 * @param mongoClient
	 *            the underlying MongoDB Client
	 */
	public LambdamaticMongoCollectionImpl(final MongoClient mongoClient, final String databaseName,
			final String collectionName, final Class<T> targetClass, final Class<M> metadataClass) {
		// final RootCodecRegistry codecRegistry =
		// MongoClient.getDefaultCodecRegistry().withCodec(new
		// LambdamaticCodec<T>(targetClass));
		final RootCodecRegistry codecRegistry = new RootCodecRegistry(Arrays.asList(
				new LambdamaticDocumentCodecProvider(), new LambdamaticFilterExpressionCodecProvider(), new DBObjectCodecProvider()));

		final MongoCollectionOptions options = MongoCollectionOptions.builder().codecRegistry(codecRegistry).build();
		this.mongoCollection = mongoClient.getDatabase(databaseName)
				.getCollection(collectionName, targetClass, options);
		this.targetClass = targetClass;
		this.metadataClass = metadataClass;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FindFluent<T> find(final FilterExpression<M> expression) {
		// FIXME: the conversion should be performed by the LambdamaticFilterExpressionCodec
		final DBObject filter = DBObjectConverter.convert(expression, metadataClass);
		LOGGER.debug("Running find with filter: {}", filter);
		return mongoCollection.find(filter);
	}

	@Override
	public WriteConcernResult insertOne(T domainObject) {
		return mongoCollection.insertOne(domainObject);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Collection of " + this.targetClass.getName();
	}

}
