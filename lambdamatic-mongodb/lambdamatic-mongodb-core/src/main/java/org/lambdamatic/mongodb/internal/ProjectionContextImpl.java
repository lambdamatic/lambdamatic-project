/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.internal;

import org.bson.BsonDocument;
import org.lambdamatic.mongodb.FilterExpression;
import org.lambdamatic.mongodb.exceptions.ConversionException;
import org.lambdamatic.mongodb.query.context.ProjectionContext;
import org.lambdamatic.mongodb.query.context.SkipContext;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

/**
 * Context to specify the number of matching documents to skip.
 * 
 * @author Xavier Coulon
 *
 */
class ProjectionContextImpl<DomainType>
    extends SkipContextImpl<DomainType>
    implements ProjectionContext<DomainType> {

  /**
   * Constructor.
   * 
   * @param findIterable the document search context
   */
  ProjectionContextImpl(final FindIterable<DomainType> findIterable) {
    super(findIterable);
  }

  ProjectionContextImpl(MongoCollection<DomainType> mongoCollection) {
    super(mongoCollection);
  }

  ProjectionContextImpl(final MongoCollection<DomainType> mongoCollection,
      final FilterExpression<DomainType> filterExpression) {
    super(mongoCollection, filterExpression);
  }

  /**
   * Constructor
   * 
   * @param mongoCollection the {@link MongoCollection} to query or update
   * @param filterDocument the {@link BsonDocument} to determine which elements to find, update or
   *        remove.
   */
  ProjectionContextImpl(final MongoCollection<DomainType> mongoCollection,
      final BsonDocument filterDocument) {
    super(mongoCollection, filterDocument);
  }

  @Override
  public SkipContext<DomainType> skip(final int skip) {
    if (skip < 0) {
      throw new ConversionException("Skip value cannot be negative.");
    }
    return new SkipContextImpl<>(getFindIterable().skip(skip));
  }

}
