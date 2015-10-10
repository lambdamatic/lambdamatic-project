/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.query.context;

/**
 * The "skip" part of a query.
 * 
 * @param <DomainType> the actual domain type being queried.
 * 
 */
public interface SkipContext<DomainType> extends LimitContext<DomainType> {

  /**
   * Limits the number of results returned by the Query
   * 
   * @param size the number of documents to skip
   * @return the {@link LimitContext} to carry on with the query.
   */
  public abstract LimitContext<DomainType> limit(final int size);

  /**
   * @return the <strong>first</strong> element matching the query.
   */
  public abstract DomainType first();

}
