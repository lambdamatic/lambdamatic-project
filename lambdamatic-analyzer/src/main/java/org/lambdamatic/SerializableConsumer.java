/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * {@link Serializable} version of the {@link Consumer} .
 * 
 * @param <T> the type of argument
 */
@FunctionalInterface
public interface SerializableConsumer<T> extends Consumer<T>, Serializable {
  // this function interface has no extra method but is serializable.

}

