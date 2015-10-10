/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.utils;

/**
 * A generic-purpose tuple to retain a pair of objects.
 * 
 * @param <X> the type of the left-hand side of this Tuple
 * @param <Y> the type of the right-hand side of this Tuple
 */
public class Tuple<X, Y> {

  /**
   * the left-hand side of the tuple.
   */
  public final X left;

  /**
   * the right-hand side of the tuple.
   */
  public final Y right;

  /**
   * Constructor.
   * 
   * @param left the left-hand side of the pair
   * @param right the right-hand side of the pair.
   */
  public Tuple(final X left, final Y right) {
    this.left = left;
    this.right = right;
  }
}
