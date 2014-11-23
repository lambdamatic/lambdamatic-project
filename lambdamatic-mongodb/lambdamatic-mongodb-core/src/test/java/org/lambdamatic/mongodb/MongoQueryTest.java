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
import org.junit.Rule;
import org.junit.Test;
import org.lambdamatic.mongodb.testutils.CleanMongoCollectionsRule;

import com.mongodb.MongoClient;
import com.sample.User;
import com.sample.UserCollection;


/**
 * Testing the MongoDB Lambda-based Fluent API
 * 
 * @author Xavier Coulon
 *
 */
public class MongoQueryTest {
	
	private MongoClient mongoClient = new MongoClient();
	
	@Rule
	public CleanMongoCollectionsRule collectionCleaning = new CleanMongoCollectionsRule(mongoClient, "lambdamatic-tests", "users");
	
	private UserCollection userCollection;
	
	@Before
	public void setup() throws UnknownHostException {
		this.userCollection = new UserCollection(mongoClient, "lambdamatic-tests");
		// insert test data
		this.userCollection.insertOne(new User("Xavier", "Coulon", "xcoulon"));
	}
	
	@Test
	public void shouldFindOneUser() throws IOException {
		// when
		final User user = userCollection.find(u -> u.userName.equals("xcoulon")).first();
		// then
		assertThat(user).isNotNull().has(new Condition<User>() {
			@Override
			public boolean matches(final User value) {
				return "xcoulon".equals(value.getUserName()) && "Xavier".equals(value.getFirstName());
			}
		});
	}
	
	

}

