/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.metadata;

import org.lambdamatic.mongodb.exceptions.ConversionException;

/**
 * The MongoDB operators that can be used in queries.
 * 
 * @author Xavier Coulon
 *
 */
public enum MongoOperator {
  /** the "$or" operator. */
  OR("$or"), /** the "$and" operator. */
  AND("$and"), /** the "$not" operator. */
  NOT("$not"), /** the "$eq" operator. */
  EQUALS("$eq"), /** the "$ne" operator. */
  NOT_EQUALS("$ne"), /** the "$gt" operator. */
  GREATER("$gt"), /** the "$gte" operator. */
  GREATER_EQUALS("$gte"), /** the "$lt" operator. */
  LESS("$lt"), /** the "$lte" operator. */
  LESS_EQUALS("$lte"), /** the "$in" operator. */
  IN("$in"), /** the "$nin" operator. */
  NOT_IN("$nin"), /** the "$geoWithin" operator. */
  GEO_WITHIN("$geoWithin"), /** the "$all" operator. */
  ALL("$all"), /** the "$elemMatch" operator. */
  ELEMEMT_MATCH("$elemMatch"), /** the "$size" operator. */
  SIZE("$size"), /** the "$" operator. */
  FIRST("$"), /** the "$push" operator. */
  PUSH("$push");

  private final String literal;

  private MongoOperator(final String literal) {
    this.literal = literal;
  }

  /**
   * @return the literal value of the operator, to use in BSON documents.
   */
  public String getLiteral() {
    return this.literal;
  }

  /**
   * @return the "inverted" operator.
   */
  public MongoOperator inverse() {
    switch (this) {
      case EQUALS:
        return NOT_EQUALS;
      case GREATER:
        return LESS_EQUALS;
      case GREATER_EQUALS:
        return LESS;
      case IN:
        return NOT_IN;
      case LESS:
        return GREATER_EQUALS;
      case LESS_EQUALS:
        return GREATER;
      case NOT_EQUALS:
        return EQUALS;
      case NOT_IN:
        return IN;
      default:
        throw new ConversionException("No opposite operator is defined for " + this.literal);

    }
  }

}
