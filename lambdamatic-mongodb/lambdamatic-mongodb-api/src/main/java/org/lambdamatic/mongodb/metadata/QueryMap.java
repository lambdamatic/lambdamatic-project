/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.metadata;

import java.util.Map;

import org.lambdamatic.mongodb.FilterExpression;

/**
 * MongoDB operation available on a given Document field of type Array in MongoDB mapped as a
 * {@link Map} in Java.
 * 
 * @author Xavier Coulon
 * @param <Key> the type of the key to look-up values in the map
 * @param <DomainMetadata> the value type (being a Query metadata of the actual domain type)
 */
public interface QueryMap<Key, DomainMetadata> {

  /**
   * Matches any array with the number of elements specified by the argument.
   * 
   * @param size the number of elements to match
   * @return a boolean so that this method can be used in a {@link FilterExpression}
   * @see <a href="http://docs.mongodb.org/manual/reference/operator/query/size/#op._S_size">MongoDB
   *      documentation</a>
   */
  @MongoOperation(MongoOperator.SIZE)
  public boolean size(long size);

  /**
   * Checks if any value indexed with <code>index</code> in the Map matches the given
   * <code>expression</code>.
   * 
   * @param expression the {@link FilterExpression}
   * @return a boolean so that this method can be used in a {@link FilterExpression}
   */
  @MongoOperation(MongoOperator.ELEMEMT_MATCH)
  public boolean elementMatch(final FilterExpression<DomainMetadata> expression);

  /**
   * Accessing a specific element in the domain {@link Map}.
   * 
   * @param key the key of the element in the {@link Map}
   * @return the desired element to carry on with the query in a {@link FilterExpression}
   */
  @ArrayElementAccessor
  public abstract DomainMetadata get(Key key);

}
