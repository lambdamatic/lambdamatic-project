/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.lambdamatic.mongodb.testutils.CleanMongoCollectionsRule;

import com.mongodb.MongoClient;
import com.sample.BikeStation;
import com.sample.BikeStationCollection;
import com.sample.BikeStationStatus;


/**
 * Testing the MongoDB Lambda-based Fluent API
 * 
 * @author Xavier Coulon
 *
 */
public class MongoInsertionTest {
	
	private MongoClient mongoClient = new MongoClient();
	
	@Rule
	public CleanMongoCollectionsRule collectionCleaning = new CleanMongoCollectionsRule(mongoClient, "lambdamatic-tests", "users");
	
	@Test
	public void shouldInsertOneBikeStation() throws IOException {
		// given
		final BikeStationCollection bikeStationCollection = new BikeStationCollection(mongoClient, "lambdamatic-tests");
		final BikeStation bikeStation = new BikeStation();
		bikeStation.setAvailableBikes(10);
		bikeStation.setAvailableDocks(20);
		bikeStation.setTotalDocks(30);
		bikeStation.setStatus(BikeStationStatus.IN_SERVICE);
		// when
		bikeStationCollection.insertOne(bikeStation);
		// then
		assertThat(bikeStation.getId()).isNotNull();
	}
	
}

