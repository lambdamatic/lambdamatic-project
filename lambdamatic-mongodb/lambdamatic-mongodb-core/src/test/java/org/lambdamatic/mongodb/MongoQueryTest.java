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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.lambdamatic.mongodb.testutils.CleanMongoCollectionsRule;

import com.mongodb.MongoClient;
import com.sample.EnumFoo;
import com.sample.Foo;
import com.sample.FooCollection;


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
	
	private FooCollection fooCollection;
	
	@Before
	public void setup() throws UnknownHostException {
		this.fooCollection = new FooCollection(mongoClient, "lambdamatic-tests");
		// insert test data
		this.fooCollection.insertOne(new Foo("john", 42, EnumFoo.FOO));
	}
	
	@Test
	public void shouldFindOneUser() throws IOException {
		Assert.fail("Should add logs to driver requests/responses");
		// when
		final Foo foo = fooCollection.find(f -> f.stringField.equals("john")).first();
		// then
		assertThat(foo).isNotNull().has(new Condition<Foo>() {
			@Override
			public boolean matches(final Foo value) {
				return value.getStringField().equals("john") && value.getPrimitiveIntField() == 42;
			}
		});
	}
	
	

}

