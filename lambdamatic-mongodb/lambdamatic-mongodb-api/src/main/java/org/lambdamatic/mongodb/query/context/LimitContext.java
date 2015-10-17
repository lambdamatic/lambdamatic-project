/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.query.context;

import java.util.List;

/**
 * API available after a call to the ".limit(int)" method
 * 
 * @param <DomainType> the actual domain type being queried.
 *
 */
public interface LimitContext<DomainType> {

  /**
   * @return a {@link List} of documents of type {@code T}.
   */
  public abstract List<DomainType> toList();


}
