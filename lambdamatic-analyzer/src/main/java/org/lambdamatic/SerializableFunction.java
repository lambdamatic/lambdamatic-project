/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic;

import java.io.Serializable;
import java.util.function.Function;

/**
 * {@link Serializable} version of the {@link Function} .
 * 
 * @param <T> the input type
 * @param <R> the result type
 */
@FunctionalInterface
public interface SerializableFunction<T, R> extends Function<T, R>, Serializable {
  // this function interface has no extra method but is serializable.

}

