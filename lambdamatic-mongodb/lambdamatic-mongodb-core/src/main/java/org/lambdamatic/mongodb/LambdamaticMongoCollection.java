/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb;

import org.lambdamatic.analyzer.FilterExpression;
import org.lambdamatic.mongodb.metadata.Metadata;

import com.mongodb.WriteConcern;

/**
 * Database Collection for a given type of element (along with its associated metadata)
 * 
 * @author Xavier Coulon
 * @param <T>
 *
 */
public interface DBCollection<T, T_ extends Metadata<T>> {

	/**
	 * Finds a single document in MongoDB, and returns the match element of type T
	 * @param expression the lambda expression that provides with search criteria
	 * @return the first matching document.
	 */
	public T findOne(final FilterExpression<T_> expression);
	
	/**
	 * Inserts the given {@code transientInstance} in this {@link DBCollection}, using the default {@link WriteConcern}
	 * configured for this {@link DBCollection}. If the given {@code transientInstance} has no {@code id} before the
	 * operation, a value will be set.
	 * 
	 * @param transientInstance
	 *            the instance to store
	 */
	public void insert(final T transientInstance);

}

