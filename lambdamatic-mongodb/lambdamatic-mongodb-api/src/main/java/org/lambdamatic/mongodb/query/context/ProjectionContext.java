/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.query.context;

/**
 * Terminal context for the find operation.
 * 
 * @param <DomainType> the actual domain type being queried.
 *
 */
public interface ProjectionContext<DomainType> extends SkipContext<DomainType> {


  /**
   * Skips the first elements.
   * 
   * @param skip the number of elements to skip
   * @return the {@link SkipContext} to carry on with the query.
   */
  public abstract SkipContext<DomainType> skip(final int skip);
}
