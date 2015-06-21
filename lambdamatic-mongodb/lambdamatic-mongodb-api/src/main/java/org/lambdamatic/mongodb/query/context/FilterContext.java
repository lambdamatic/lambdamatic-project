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

package org.lambdamatic.mongodb.query.context;

import org.lambdamatic.mongodb.ProjectionExpression;
import org.lambdamatic.mongodb.UpdateExpression;

/**
 * @author xcoulon
 *
 */
public interface FilterContext<DomainType, ProjectionType, UpdateType> extends ProjectionContext<DomainType> {

	/**
	 * Reduce the fields to be <strong>included<strong> or <strong>excluded</strong> in the returned to the given
	 * {@link ProjectionType} expression.
	 *
	 * @param projectionExpression
	 *            the set of fields to include in the returned document
	 * @return the {@link CollectionContext} to optionally specify more settings
	 */
	public ProjectionContext<DomainType> projection(final ProjectionExpression<ProjectionType> projectionExpression);

	/**
	 * Updates <strong>one</strong> {@code domainObject}, performing <strong>a partial replacement</strong> based on the
	 * field assignments in the given {@code updateExpression}.
	 * 
	 * @param updateExpression
	 *            the field assignments that explain how the document fields should be updated.
	 */
	public void forEach(final UpdateExpression<UpdateType> updateExpression);

	/**
	 * Removes all element matching the preceding request
	 */
	public abstract void remove();

	
}
