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

import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.sample.BikeStation;
import com.sample.User;


/**
 * Testing the MongoDB Lambda-based Fluent API
 * 
 * @author Xavier Coulon
 *
 */
public class MongoQueryTest {
	
	private static DataStore datastore;
	
	@BeforeClass
	public static void preChecks() throws UnknownHostException {
		datastore = new DataStore(new MongoClient());
		assertThat(datastore).isNotNull();
	}

	@Before
	public void setupData() throws UnknownHostException {
		final DBCollection usersCollection = new MongoClient().getDB("lambdamatic-tests").getCollection("users");
		// clean users collection
		usersCollection.remove(new BasicDBObject());
		// insert test data
		usersCollection.insert(new BasicDBObject("userName", "xcoulon").append("firstName", "Xavier"));
	}
	
	@Test
	public void shouldFindOneUser() throws IOException {
		// when
		final User user = datastore.getUsers().findOne(u -> u.userName.equals("xcoulon"));
		// then
		assertThat(user).isNotNull().has(new Condition<User>() {
			@Override
			public boolean matches(User value) {
				return "xcoulon".equals(value.getUserName()) && "Xavier".equals(value.getFirstName());
			}
		});
	}
	
	@Test
	public void shouldFindOneBikeStationWithAvailableDocks() throws IOException {
		// when
		final BikeStation bikeStation = datastore.getBikeStations().findOne(s -> s.availableDocks >= 1);
		// then
		assertThat(bikeStation).isNotNull();
	}
	
	@Test
	public void shouldFindOneBikeStationWithAvailableDocks2() throws IOException {
		// when
		final UsersCollection users = new UsersCollection();
		users.findOne(u -> u.firstName == null);
		final BikeStation bikeStation = datastore.getBikeStations().findOne(s -> s.availableDocks >= 1);
		// then
		assertThat(bikeStation).isNotNull();
	}
	

}

