/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.internal.connexion;

import java.net.UnknownHostException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.lambdamatic.mongodb.internal.configuration.MongoClientConfiguration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

/**
 * CDI Producer for a single {@link MongoClient} instance.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 *
 */
@ApplicationScoped
public class MongoClientProducer {
	
	@Produces
	@Singleton
	public MongoClient createMongoClient(MongoClientConfiguration mongoDBClientConfiguration) throws UnknownHostException {
	    MongoClient mongoClient = new MongoClient();
	    return mongoClient;
	}

	public void disposeClient(@Disposes MongoClient mongoClient) {
		mongoClient.close();
	}
	

}

