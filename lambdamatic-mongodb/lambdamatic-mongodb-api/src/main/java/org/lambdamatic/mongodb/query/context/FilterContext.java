/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.query.context;

import org.lambdamatic.mongodb.ProjectionExpression;
import org.lambdamatic.mongodb.UpdateExpression;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.UpdateMetadata;

/**
 * The filter context.
 * 
 * @param <DomainType> the actual domain type being queried.
 * @param <ProjectionType> the associated {@link ProjectionMetadata} class if a projection needs to
 *        be defined
 * @param <UpdateType> the associated {@link UpdateMetadata} class if an update needs is to be
 *        performed on the matching documents
 *
 */
public interface FilterContext<DomainType, ProjectionType, UpdateType>
    extends ProjectionContext<DomainType> {

  /**
   * Reduce the fields to be <strong>included</strong> or <strong>excluded</strong> in the returned
   * to the given {@link ProjectionExpression} expression.
   *
   * @param projectionExpression the set of fields to include in the returned document
   * @return the {@link ProjectionContext} to optionally specify more settings
   */
  public ProjectionContext<DomainType> projection(
      final ProjectionExpression<ProjectionType> projectionExpression);

  /**
   * Updates <strong>one</strong> {@code domainObject}, performing <strong>a partial
   * replacement</strong> based on the field assignments in the given {@code updateExpression}.
   * 
   * @param updateExpression the field assignments that explain how the document fields should be
   *        updated.
   */
  public void forEach(final UpdateExpression<UpdateType> updateExpression);

  /**
   * Removes all element matching the preceding request.
   */
  public abstract void remove();


}
