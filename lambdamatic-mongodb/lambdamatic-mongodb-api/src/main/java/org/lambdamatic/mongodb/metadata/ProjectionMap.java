/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.metadata;

import java.util.Map;

/**
 * MongoDB projection operators available on a given Document field of type Array in MongoDB mapped
 * as a {@link Map} in Java.
 * 
 * @author Xavier Coulon
 * @param <K> the type of the key used to look-up values in the map
 * @param <V> the type of the values in the map
 */
public interface ProjectionMap<K, V> {
  // empty marker interface

}
