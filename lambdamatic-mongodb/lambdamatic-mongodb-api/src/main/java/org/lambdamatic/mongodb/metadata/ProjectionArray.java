/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.metadata;

import java.util.List;
import java.util.Set;

import org.lambdamatic.mongodb.FilterExpression;
import org.lambdamatic.mongodb.LambdamaticMongoCollection;
import org.lambdamatic.mongodb.Projection;

/**
 * MongoDB operation available on a given Document field of type Array in MongoDB (mapped as a
 * {@link List} or {@link Set} in Java) to specify a query projection.
 * 
 * @param <DomainType> the actual domain type in the array
 *
 */
public interface ProjectionArray<DomainType> extends ProjectionField {

  /**
   * The positional {@link MongoOperator#FIRST} operator limits the contents of an array from the
   * query results to contain only the first element matching the query document.
   * 
   * Use {@link ProjectionArray#first()} in the projection document of the
   * {@link LambdamaticMongoCollection#filter(FilterExpression)} method when you only need one
   * particular array element in selected documents.
   * 
   * @see <a href=
   *      "http://docs.mongodb.org/manual/reference/operator/projection/positional/#proj._S_">$
   *      (projection)</a>
   */
  @MongoOperation(MongoOperator.FIRST)
  public void first();

  /**
   * The {@link MongoOperator#ELEMEMT_MATCH} operator limits the contents of an array field from the
   * query results to contain only the <strong>first element</strong> matching the given
   * {@code expression} condition.
   * 
   * @param expression the condition to specify which elements should be returned
   * @return a {@link ProjectionField} for the {@link Projection#include(ProjectionField...)} and
   *         {@link Projection#exclude(ProjectionField...)} methods.
   * 
   * @see <a href=
   *      "http://docs.mongodb.org/manual/reference/operator/projection/elemMatch/#proj._S_elemMatch">
   *      $elemMatch (projection)</a>
   */
  @MongoOperation(MongoOperator.ELEMEMT_MATCH)
  public ProjectionField elementMatch(final FilterExpression<DomainType> expression);

}
