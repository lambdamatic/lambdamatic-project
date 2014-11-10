/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.connexion;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.lambdamatic.mongodb.configuration.MongoDBClientConfiguration;

import com.mongodb.DB;
import com.mongodb.MongoClient;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
@ApplicationScoped
public class MongoDBDatabaseProducer {

	@Produces
	@Singleton
	public DB getDatabase(MongoClient mongoClient, MongoDBClientConfiguration mongodDBClientConfiguration) {
		return mongoClient.getDB(mongodDBClientConfiguration.getDatabaseName());
	}
}

