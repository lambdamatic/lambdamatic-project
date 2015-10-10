/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.internal;

import org.lambdamatic.mongodb.annotations.DocumentId;

/**
 * A query filter that will use the attribute of the given {@code domainObject} annotated with
 * {@link DocumentId}.
 * 
 * @param <DomainType> the domain type associated with this filter
 *
 */
public class IdFilter<DomainType> {

  private final DomainType domainObject;

  /**
   * Constructor.
   * 
   * @param domainObject the domain instance associated with this filter.
   */
  public IdFilter(final DomainType domainObject) {
    this.domainObject = domainObject;
  }

  /**
   * @return the domain instance associated with this filter.
   */
  public DomainType getDomainObject() {
    return this.domainObject;
  }
}
