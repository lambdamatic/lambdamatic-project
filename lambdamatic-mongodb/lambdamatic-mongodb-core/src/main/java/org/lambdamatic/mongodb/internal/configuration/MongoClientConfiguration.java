/*******************************************************************************
 * Copyright (c) 2014, 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc. All rights
 * reserved. This program is made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.internal.configuration;

/**
 * The MongoDB client configuration.
 *
 */
public class MongoClientConfiguration {

  /** the name of the database to connect to. */
  private final String databaseName;

  /**
   * Constructor.
   * 
   * @param databaseName the name of the database to connect to
   */
  public MongoClientConfiguration(final String databaseName) {
    this.databaseName = databaseName;
  }

  /**
   * @return the name of the database to connect to.
   */
  public String getDatabaseName() {
    return this.databaseName;
  }

}
