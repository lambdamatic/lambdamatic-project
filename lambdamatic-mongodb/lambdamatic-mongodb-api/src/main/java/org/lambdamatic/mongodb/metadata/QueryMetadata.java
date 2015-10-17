/*******************************************************************************
 * Copyright (c) 2014, 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc. All rights
 * reserved. This program is made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.metadata;

/**
 * Marker interface for generated metadata classes used to express filters in queries.
 * 
 * @param <DomainType> the actual domain type being queried.
 *
 */
public interface QueryMetadata<DomainType> {
  // empty marker interface
}
