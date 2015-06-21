/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb;

import org.lambdamatic.SerializablePredicate;
import org.lambdamatic.mongodb.annotations.Document;
import org.lambdamatic.mongodb.annotations.DocumentId;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.QueryMetadata;
import org.lambdamatic.mongodb.metadata.UpdateMetadata;
import org.lambdamatic.mongodb.query.context.FilterContext;

/**
 * Database Collection for a given type of element (along with its associated metadata)
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 * @param <DomainType>
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
public interface LambdamaticMongoCollection<DomainType, QueryType extends QueryMetadata<DomainType>, ProjectionType extends ProjectionMetadata<DomainType>, UpdateType extends UpdateMetadata<DomainType>> extends FilterContext<DomainType, ProjectionType, UpdateType>{

	/**
	 * Finds one or many documents matching the given lambda {@link SerializablePredicate}.
	 * 
	 * @param filterExpression
	 *            the filter query in the form of a lambda expression
	 * @return the {@link CollectionContext} element to carry on with the query using the given <code>filterExpression</code>
	 */
	public FilterContext<DomainType, ProjectionType, UpdateType> filter(
			final FilterExpression<QueryType> filterExpression);

	/**
	 * @return the {@link CollectionContext} element to carry on with the query <strong>matching all elements</strong>.
	 */
	public FilterContext<DomainType, ProjectionType, UpdateType> all();
	
	/**
	 * Adds (Inserts) the given {@code domainObjects} in the underlying MongoDB Collection. If no {@code id} attribute
	 * (ie, annotated with {@link DocumentId}) was not set, a random value will be provided.
	 * 
	 * @param domainObjects
	 *            the domain objects to insert
	 */
	public void add(final @SuppressWarnings("unchecked") DomainType... domainObjects);

	/**
	 * "Upserts" the given {@code domainObject} in the Database, ie, of the domain object exists, it is
	 * <strong>replaced</strong>, otherwise it is <strong>inserted</strong>.
	 * 
	 * @param domainObject
	 *            the domain object to insert/update.
	 */
	public void upsert(final DomainType domainObject);

	/**
	 * Replaces (Updates) with the given {@code domainObject}, performing <strong>a full replacement</strong> of the
	 * actual content identified by the {@link DocumentId} in the associated MongoDB collection.
	 * 
	 * @param domainObject
	 *            the domain object to update.
	 */
	public void replace(final DomainType domainObject);

}
