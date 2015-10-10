/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb;

import org.lambdamatic.SerializableConsumer;

/**
 * An {@link UpdateExpression} which specifies which document fields must be updated and how.
 * 
 * @param <DomainType> the actual type of the domain class used in the update expression
 *
 */
@FunctionalInterface
public interface UpdateExpression<DomainType> extends SerializableConsumer<DomainType> {
  // no extra method for this Consumer functional interface.
}
