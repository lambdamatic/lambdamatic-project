/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.analyzer.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class CollectionUtils {

	/**
	 * Private constructor for this utility class
	 */
	private CollectionUtils() { }
	
	/**
	 * Joins all given elements in a single (flat) List
	 * @param element first element to include in the list
	 * @param otherElements other elements to include in the list
	 * @return a list combining all given elements
	 */
	public static <T> List<T> join(T element, List<T> otherElements) {
		final List<T> result = new ArrayList<T>();
		result.add(element);
		result.addAll(otherElements);
		return result;
	}
	
	/**
	 * Compares the given two {@link List} and returns {@code true} if they both contains the same elements, no matter what their respective order.
	 * @param left
	 * @param right
	 * @return {@code true} if they have the same elements, {@code false} otherwise (including different size and {@code null} lists)
	 */
	public static <T> boolean equivalent(final List<T> left, final List<T> right) {
		if(left == null || right == null) {
			return false;
		}
		if(left.size() != right.size()) {
			return false;
		}
		for(T element : left) {
			if(!right.contains(element)) {
				return false;
			}
		}
		return true;
	}

}

