/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.configuration;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class MongoDBClientConfiguration {

	private final String databaseName;
	
	public String getDatabaseName() {
		return databaseName;
	}
	
	public MongoDBClientConfiguration(final String databaseName) {
		this.databaseName = databaseName;
	}
}

