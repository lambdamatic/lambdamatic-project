/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb;

import org.lambdamatic.FilterExpression;
import org.lambdamatic.mongodb.annotations.DocumentId;
import org.lambdamatic.mongodb.metadata.Metadata;

import com.mongodb.WriteConcernResult;
import com.mongodb.client.FindFluent;

/**
 * Database Collection for a given type of element (along with its associated metadata)
 * 
 * @author Xavier Coulon
 * @param <T>
 *
 */
public interface LambdamaticMongoCollection<T, M extends Metadata<T>> {

	/**
	 * Finds one or many documents matching the given lambda {@link FilterExpression}.
	 * @param expression the query in the form of a lambda expression 
	 * @return the {@link FindFluent} element to carry on with the query
	 */
	public FindFluent<T> find(final FilterExpression<M> expression);

	/**
	 * Insert the given {@code domainObject} in the underlying MongoDB
	 * Collection. If no {@code id} attribute (ie, annotated with
	 * {@link DocumentId}) was not set, a random value will be provided.
	 * 
	 * @param domainObject
	 *            the object to convert into a BSON document
	 * @return the {@link WriteConcernResult} for details on the insert operation that occurred.
	 */
	public WriteConcernResult insertOne(T domainObject);
	
}

