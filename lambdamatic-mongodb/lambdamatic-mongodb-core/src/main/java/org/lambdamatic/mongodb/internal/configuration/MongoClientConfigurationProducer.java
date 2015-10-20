/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.internal.configuration;

import java.io.IOException;
import java.io.InputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CDI Producer that loads and reads the JSON-based application configuration.
 * 
 * @author xcoulon
 * 
 */
@ApplicationScoped
public class MongoClientConfigurationProducer {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(MongoClientConfigurationProducer.class);

  /**
   * Reads the {@code config.json} configuration file in the classpath and builds a
   * {@link MongoClientConfiguration} from it.
   * 
   * @return the {@link MongoClientConfiguration}
   * @throws IOException if a problem occurred when reading the config file
   */
  @SuppressWarnings("static-method")
  @Produces
  @Default
  public MongoClientConfiguration getMongoClientConfiguration() throws IOException {
    try (
        final InputStream jsonConfigFile =
            Thread.currentThread().getContextClassLoader().getResourceAsStream("config.json");
        final JsonReader reader = Json.createReader(jsonConfigFile);) {
      final JsonObject root = (JsonObject) reader.read();
      final String databaseName = ((JsonString) root.get("databaseName")).getString();
      LOGGER.debug("Database name: {}", databaseName);
      final MongoClientConfiguration mongoClientConfiguration =
          new MongoClientConfiguration(databaseName);
      return mongoClientConfiguration;
    }
  }

}
