/*******************************************************************************
 * Copyright (c) 2014, 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc. All rights
 * reserved. This program is made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.internal.connection;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.lambdamatic.mongodb.internal.configuration.MongoClientConfiguration;

import com.mongodb.MongoClient;

/**
 * CDI Producer for a single {@link MongoClient} instance.
 * 
 */
@ApplicationScoped
public class MongoClientProducer {

  /**
   * CDI Producer method to create a new {@link MongoClient}.
   * 
   * @param mongoClientConfiguration the configuration to use
   * @return a new instance of the {@link MongoClient}.
   */
  @SuppressWarnings("static-method")
  @Produces
  @Singleton
  public MongoClient createMongoClient(MongoClientConfiguration mongoClientConfiguration) {
    MongoClient mongoClient = new MongoClient();
    return mongoClient;
  }

  /**
   * Method called when the client is to be disposed.
   * 
   * @param mongoClient the {@link MongoClient} to dispose
   */
  @SuppressWarnings("static-method")
  public void disposeClient(@Disposes MongoClient mongoClient) {
    mongoClient.close();
  }

}
