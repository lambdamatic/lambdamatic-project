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

import org.bson.types.ObjectId;
import org.junit.Rule;
import org.junit.Test;
import org.lambdamatic.mongodb.testutils.DropMongoCollectionsRule;

import com.mongodb.MongoBulkWriteException;
import com.mongodb.MongoClient;
import com.sample.EnumFoo;
import com.sample.Foo;
import com.sample.Foo.FooBuilder;
import com.sample.FooCollection;


/**
 * Testing the MongoDB Lambda-based Fluent API
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 */
public class MongoInsertionTest {
	
	private static final String COLLECTION_NAME = "users";

	private static final String DATABASE_NAME = "lambdamatic-tests";

	private MongoClient mongoClient = new MongoClient();
	
	@Rule
	public DropMongoCollectionsRule collectionCleaning = new DropMongoCollectionsRule(mongoClient, DATABASE_NAME, COLLECTION_NAME);
	
	@Test
	public void shouldInsertOneFoo() throws IOException {
		// given
		final FooCollection fooCollection = new FooCollection(mongoClient, DATABASE_NAME, COLLECTION_NAME);
		final Foo foo = new FooBuilder().withStringField("jdoe").withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO).build();
		// when
		fooCollection.insert(foo);
		// then
		assertThat(foo.getId()).isNotNull();
	}
	
	@Test(expected=MongoBulkWriteException.class)
	public void shouldNotInsertOneFooWithIdTwice() throws IOException {
		// given
		final FooCollection fooCollection = new FooCollection(mongoClient, DATABASE_NAME, COLLECTION_NAME);
		final Foo foo = new FooBuilder().withId(new ObjectId("54c28b0b0f2dacc85ede5286")).withStringField("jdoe").withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO).build();
		// when 2 inserts should fail
		fooCollection.insert(foo);
		fooCollection.insert(foo);
	}
	
	@Test
	public void shouldUpsertOneFooTwice() throws IOException {
		// given
		final FooCollection fooCollection = new FooCollection(mongoClient, DATABASE_NAME, COLLECTION_NAME);
		// when
		Foo foo = new FooBuilder().withId(new ObjectId("54c28b0b0f2dacc85ede5286")).withStringField("jdoe").withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO).build();
		fooCollection.upsert(foo);
		foo = new FooBuilder().withId(new ObjectId("54c28b0b0f2dacc85ede5286")).withStringField("j.doe").withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO).build();
		fooCollection.upsert(foo);
		// then
		assertThat(foo.getStringField()).isNotNull().isEqualTo("j.doe");
	}
	
}

