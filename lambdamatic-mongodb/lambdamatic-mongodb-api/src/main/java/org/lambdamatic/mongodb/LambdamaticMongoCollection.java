/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb;

import org.lambdamatic.SerializablePredicate;
import org.lambdamatic.mongodb.annotations.DocumentId;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.QueryMetadata;
import org.lambdamatic.mongodb.query.context.FindContext;
import org.lambdamatic.mongodb.query.context.FindTerminalContext;

/**
 * Database Collection for a given type of element (along with its associated metadata)
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 * @param <T>
 *
 */
public interface LambdamaticMongoCollection<T, QM extends QueryMetadata<T>, PM extends ProjectionMetadata<T>> {

	/**
	 * Finds one or many documents matching the given lambda {@link SerializablePredicate}.
	 * @param expression the query in the form of a lambda expression 
	 * @return the {@link FindTerminalContext} element to carry on with the query
	 */
	public FindContext<T, PM> find(final SerializablePredicate<QM> expression);

	/**
	 * Insert the given {@code domainObjects} in the underlying MongoDB
	 * Collection. If no {@code id} attribute (ie, annotated with
	 * {@link DocumentId}) was not set, a random value will be provided.
	 * 
	 * @param domainObjects
	 *            the domain objects to insert
	 */
	public void insert(@SuppressWarnings("unchecked") T... domainObjects);

	/**
	 * "Upserts" the given {@code domainObject} in the Database, ie, of the domain object exists, it is <strong>replaced</strong>, 
	 * otherwise it is <strong>inserted</strong>.
	 * 
	 * @param domainObject the domain object to insert/update.
	 */
	public void upsert(T domainObject);
	
}

