/*******************************************************************************
 * Copyright (c) 2014, 2015 Red Hat. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for manipulating collections.
 * 
 * @author Xavier Coulon
 *
 */
public class CollectionUtils {

  /**
   * Private constructor for this utility class.
   */
  private CollectionUtils() {}

  /**
   * Joins all given elements in a single (flat) List.
   * 
   * @param element first element to include in the list
   * @param otherElements other elements to include in the list
   * @param <T> the elements type
   * @return a list combining all given elements
   */
  public static <T> List<T> join(T element, List<T> otherElements) {
    final List<T> result = new ArrayList<>();
    result.add(element);
    result.addAll(otherElements);
    return result;
  }

  /**
   * Compares the given two {@link List} and returns {@code true} if they both contains the same
   * elements, no matter what their respective order.
   * 
   * @param left the left-hand side {@link List} to compare
   * @param right the right-hand side {@link List} to compare
   * @param <T> the elements type
   * @return {@code true} if they have the same elements, {@code false} otherwise (including
   *         different size and {@code null} lists)
   */
  public static <T> boolean equivalent(final List<T> left, final List<T> right) {
    if (left == null || right == null) {
      return false;
    }
    if (left.size() != right.size()) {
      return false;
    }
    for (T element : left) {
      if (!right.contains(element)) {
        return false;
      }
    }
    return true;
  }

}

