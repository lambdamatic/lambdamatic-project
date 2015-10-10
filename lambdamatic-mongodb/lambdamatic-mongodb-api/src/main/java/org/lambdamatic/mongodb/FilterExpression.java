/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb;

import org.lambdamatic.SerializablePredicate;

/**
 * A {@link FilterExpression} which specifies which {@code DonainType} should be part of the
 * find/update/delete operation.
 * 
 * @author Xavier Coulon
 * @param <DomainType> the actual type of the domain class to filter
 *
 */
@FunctionalInterface
public interface FilterExpression<DomainType> extends SerializablePredicate<DomainType> {
  // this predicate interface has no extra method.
}

