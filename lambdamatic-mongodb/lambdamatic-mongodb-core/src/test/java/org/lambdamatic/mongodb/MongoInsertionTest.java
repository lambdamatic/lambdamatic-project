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
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.lambdamatic.mongodb.testutils.CleanDBCollectionsRule;

import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.sample.BikeStation;
import com.sample.BikeStationStatus;
import com.sample.User;


/**
 * Testing the MongoDB Lambda-based Fluent API
 * 
 * @author Xavier Coulon
 *
 */
public class MongoInsertionTest {
	
	private static DataStore datastore;
	
	@Rule
	public CleanDBCollectionsRule cleanCollection = new CleanDBCollectionsRule();
	
	@BeforeClass
	public static void preChecks() throws UnknownHostException {
		datastore = new DataStore(new MongoClient());
		assertThat(datastore).isNotNull();
	}

	@Before
	public void setupData() throws UnknownHostException {
		final DBCollection usersCollection = new MongoClient().getDB("lambdamatic-tests").getCollection("users");
		// clean users collection
		usersCollection.drop();
	}		
	
	@Test
	public void shouldInsertOneUser() throws IOException {
		// given
		final User user = new User("John", "Doe", "jdoe");
		// when
		datastore.getUsers().insert(user);
		// then
		assertThat(user.getId()).isNotNull();
	}

	@Test
	public void shouldInsertOneBikeStation() throws IOException {
		// given
		final BikeStation bikeStation = new BikeStation();
		bikeStation.setAvailableBikes(10);
		bikeStation.setAvailableDocks(20);
		bikeStation.setTotalDocks(30);
		bikeStation.setStatus(BikeStationStatus.IN_SERVICE);
		// when
		datastore.getBikeStations().insert(bikeStation);
		// then
		assertThat(bikeStation.getId()).isNotNull();
	}
	
}

