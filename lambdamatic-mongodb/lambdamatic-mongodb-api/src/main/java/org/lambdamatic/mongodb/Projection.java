/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb;

import org.lambdamatic.mongodb.metadata.ExcludeFields;
import org.lambdamatic.mongodb.metadata.IncludeFields;
import org.lambdamatic.mongodb.metadata.ProjectionField;

/**
 * A {@link Projection} which specifies which {@link ProjectionField} should be included or excluded
 * from the document(s) returned by the query.
 * 
 * @author Xavier Coulon
 *
 */
public interface Projection {

  /**
   * Specifies the document fields that should be included in the query result. All other fields
   * will be excluded, <strong>including the document id unless explicitly included</strong>.
   * 
   * @param fields the fields to include.
   */
  @IncludeFields
  public static void include(final ProjectionField... fields) {
    // no implementation
  }

  /**
   * Specifies all the document fields to exclude in the query result.
   * 
   * @param fields the fields to exclude
   */
  @ExcludeFields
  public static void exclude(final ProjectionField... fields) {
    // no implementation
  }

}
