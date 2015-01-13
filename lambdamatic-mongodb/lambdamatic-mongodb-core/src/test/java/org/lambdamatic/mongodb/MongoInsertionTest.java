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
import com.sample.EnumFoo;
import com.sample.Foo;
import com.sample.FooCollection;
import com.sample.Foo.FooBuilder;


/**
 * Testing the MongoDB Lambda-based Fluent API
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 */
public class MongoInsertionTest {
	
	private MongoClient mongoClient = new MongoClient();
	
	@Rule
	public CleanMongoCollectionsRule collectionCleaning = new CleanMongoCollectionsRule(mongoClient, "lambdamatic-tests", "users");
	
	@Test
	public void shouldInsertOneFoo() throws IOException {
		// given
		final FooCollection FooCollection = new FooCollection(mongoClient, "lambdamatic-tests");
		final Foo foo = new FooBuilder().withStringField("jdoe").withPrimitiveIntField(42).withEnumFool(EnumFoo.FOO).build();
		// when
		FooCollection.insertOne(foo);
		// then
		assertThat(foo.getId()).isNotNull();
	}
	
}

