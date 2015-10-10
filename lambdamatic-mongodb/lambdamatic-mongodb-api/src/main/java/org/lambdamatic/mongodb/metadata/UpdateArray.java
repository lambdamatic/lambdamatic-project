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

/**
 * MongoDB operation available on a given Document field of type Array in MongoDB (mapped as a
 * {@link List} or {@link Set} in Java to specify an update.
 * 
 * @param <T> can be a {@link UpdateMetadata} or a simple Java Type (String, Enum, etc.)
 *
 */
public interface UpdateArray<T> {

  /**
   * The $push operator appends a specified value to an array.
   * 
   * @param element the element to push in the array
   */
  @MongoOperation(MongoOperator.PUSH)
  public void push(final T element);

}
