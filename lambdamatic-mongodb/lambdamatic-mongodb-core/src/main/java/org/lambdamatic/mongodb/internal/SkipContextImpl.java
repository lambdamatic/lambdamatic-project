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
import org.lambdamatic.mongodb.query.context.LimitContext;
import org.lambdamatic.mongodb.query.context.SkipContext;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

/**
 * Context to specify the maximum number of Documents to retrieve or if only the first elment should
 * be retrieved.
 * 
 * @author Xavier Coulon
 *
 */
class SkipContextImpl<DomainType>
    extends LimitContextImpl<DomainType>
    implements SkipContext<DomainType> {

  /**
   * Constructor.
   * 
   * @param mongoCollection the {@link MongoCollection} to query or update
   */
  SkipContextImpl(MongoCollection<DomainType> mongoCollection) {
    super(mongoCollection);
  }

  /**
   * Constructor.
   * 
   * @param mongoCollection the MongoDB collection to query
   * @param filterExpression the filterExpression to select the documents
   */
  SkipContextImpl(final MongoCollection<DomainType> mongoCollection,
      final FilterExpression<DomainType> filterExpression) {
    super(mongoCollection, filterExpression);
  }

  /**
   * Constructor.
   * 
   * @param findIterable the document search context
   */
  SkipContextImpl(final FindIterable<DomainType> findIterable) {
    super(findIterable);
  }

  /**
   * Constructor
   * 
   * @param mongoCollection the {@link MongoCollection} to query or update
   * @param filterDocument the {@link BsonDocument} to determine which elements to find.
   */
  SkipContextImpl(final MongoCollection<DomainType> mongoCollection,
      final BsonDocument filterDocument) {
    super(mongoCollection, filterDocument);
  }

  @Override
  public LimitContext<DomainType> limit(int limit) {
    if (limit < 0) {
      throw new ConversionException("'limit' value cannot be negative.");
    }
    return new LimitContextImpl<>(getFindIterable().limit(limit));
  }

  @Override
  public DomainType first() {
    return getFindIterable().first();
  }

}
