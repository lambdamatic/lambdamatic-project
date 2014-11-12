/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb;


import org.bson.types.ObjectId;
import org.lambdamatic.analyzer.FilterExpression;
import org.lambdamatic.mongodb.converters.DBObjectConverter;
import org.lambdamatic.mongodb.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * Database Collection for a given type of element (along with its associated metadata)
 * 
 * @author Xavier Coulon
 *
 */
public abstract class DBCollectionImpl<T, M extends Metadata<T>> implements DBCollection<T, M>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DBCollectionImpl.class);

	/** The underlying MongoDB Collection. */
	private final com.mongodb.DBCollection dbCollection;

	/** The user-defined domain class associated with this Collection. */
	private final Class<T> targetClass;

	/** The generated metadata class associated user-defined domain class. */
	private final Class<M> metadataClass;

	/**
	 * Constructor.
	 * 
	 * @param mongoClient
	 *            the underlying MongoDB Client
	 */
	public DBCollectionImpl(final MongoClient mongoClient, final String databaseName, final String collectionName,
			final Class<T> targetClass, final Class<M> metadataClass) {
		this.dbCollection = mongoClient.getDB(databaseName).getCollection(collectionName);
		this.targetClass = targetClass;
		this.metadataClass = metadataClass;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T findOne(final FilterExpression<M> expression) {
		// convert the given expression in a MongoDB DBObject (a JSON document)
		final DBObject query = DBObjectConverter.convert(expression, metadataClass);
		LOGGER.debug("Requested: {}", query.toString());
		// submit the request and retrieve a result as an instance of DBObject (provided by MongoDB Driver)
		final DBObject dbObjectResult = dbCollection.findOne(query);
		LOGGER.debug("Responded: {}", dbObjectResult.toString());
		return DBObjectConverter.convert(dbObjectResult, this.targetClass);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insert(final T transientInstance) {
		final DBObject dbObject = DBObjectConverter.convert(transientInstance);
		LOGGER.debug("About to insert: {}", dbObject);
		dbCollection.insert(dbObject);
		final ObjectId objectId = (ObjectId) dbObject.get(DBObjectConverter.MONGOBD_DOCUMENT_ID);
		DBObjectConverter.setDocumentId(transientInstance, objectId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Collection of " + this.targetClass.getName();
	}

}

