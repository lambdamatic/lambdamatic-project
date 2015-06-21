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

package org.lambdamatic.mongodb.internal;

import java.util.ArrayList;
import java.util.List;

import org.bson.BsonDocument;
import org.lambdamatic.mongodb.FilterExpression;
import org.lambdamatic.mongodb.query.context.LimitContext;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

/**
 * Context to specify the method to retrieve the matching documents.
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
class LimitContextImpl<DomainType> implements LimitContext<DomainType> {

	/** The document search context. */
	private final FindIterable<DomainType> findIterable;

	/** the MongoDB collection to query */
	private final MongoCollection<DomainType> mongoCollection;
	
	/** the {@link BsonDocument} to determine which elements to find. */
	private final BsonDocument filterDocument;
	
	/**
	 * Constructor
	 * 
	 * @param mongoCollection
	 *            the MongoDB collection to query
	 */
	LimitContextImpl(final MongoCollection<DomainType> mongoCollection) {
		this.mongoCollection = mongoCollection;
		this.filterDocument = null;
		this.findIterable = null;
	}

	/**
	 * Constructor
	 * 
	 * @param mongoCollection
	 *            the MongoDB collection to query
	 * @param filterExpression the filterExpression to select the documents.
	 */
	LimitContextImpl(final MongoCollection<DomainType> mongoCollection, final FilterExpression<DomainType> filterExpression) {
		this.mongoCollection = mongoCollection;
		this.filterDocument = BsonUtils.asBsonDocument(filterExpression);
		this.findIterable = null;
	}

	/**
	 * Constructor
	 * 
	 * @param findIterable
	 *            the document search context
	 */
	LimitContextImpl(final FindIterable<DomainType> findIterable) {
		this.findIterable = findIterable;
		this.mongoCollection = null;
		this.filterDocument = null;
	}
	
	/**
	 * Constructor
	 * 
	 * @param mongoCollection
	 *            the {@link MongoCollection} to query or update
	 * @param filterDocument
	 *            the {@link BsonDocument} to determine which elements to find.
	 */
	LimitContextImpl(final MongoCollection<DomainType> mongoCollection, BsonDocument filterDocument) {
		this.mongoCollection = mongoCollection;
		this.filterDocument = filterDocument;
		this.findIterable = null;
	}

	@Override
	public List<DomainType> toList() {
		return getFindIterable().into(new ArrayList<>());
	}

	/**
	 * @return the {@link FindIterable} for this context, eventually (lately) initializing it if it was not provided in the constructor.
	 */
	FindIterable<DomainType> getFindIterable() {
		if(this.findIterable != null) {
			return this.findIterable;
		} else if(this.filterDocument == null) {
			return mongoCollection.find();
		}
		return this.mongoCollection.find(this.filterDocument);
	}
	
	/**
	 * @return the {@link MongoCollection} to query
	 */
	public MongoCollection<DomainType> getMongoCollection() {
		return mongoCollection;
	}
	
	/**
	 * @return the {@link BsonDocument} to determine which elements to find, update or remove.
	 */
	public BsonDocument getFilterDocument() {
		return this.filterDocument;
	}

}
