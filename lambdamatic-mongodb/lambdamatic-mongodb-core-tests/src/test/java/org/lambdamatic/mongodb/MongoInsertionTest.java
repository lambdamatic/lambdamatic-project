/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb;

import static com.lordofthejars.nosqlunit.mongodb.MongoDbConfigurationBuilder.mongoDb;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Rule;
import org.junit.Test;
import org.lambdamatic.mongodb.testutils.DropMongoCollectionsRule;

import com.lordofthejars.nosqlunit.annotation.ShouldMatchDataSet;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.MongoClient;
import com.sample.Bar;
import com.sample.Bar.BarBuilder;
import com.sample.EnumBar;
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

	private static final String DATABASE_NAME = "lambdamatic-tests";

	private static final String COLLECTION_NAME = ((org.lambdamatic.mongodb.annotations.Document) Foo.class
			.getAnnotation(org.lambdamatic.mongodb.annotations.Document.class)).collection();

	private MongoClient mongoClient = new MongoClient();

	@Rule
	public DropMongoCollectionsRule collectionCleaning = new DropMongoCollectionsRule(mongoClient, DATABASE_NAME,
			COLLECTION_NAME);

	@Rule
	public MongoDbRule remoteMongoDbRule = new MongoDbRule(mongoDb().databaseName(DATABASE_NAME).build());
	
	@Test
	@UsingDataSet(loadStrategy=LoadStrategyEnum.DELETE_ALL)
	@ShouldMatchDataSet(location="/expected-insertions.json")
	public void shouldInsertOneBasicDocument() throws IOException {
		// given
		final FooCollection fooCollection = new FooCollection(mongoClient, DATABASE_NAME, COLLECTION_NAME);
		final Foo foo = new FooBuilder().withStringField("jdoe").withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO).
				withStringList("foo", "bar", "baz").build();
		// when
		fooCollection.insert(foo);
		// then
		assertThat(mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME).count()).isEqualTo(1);
		assertThat(foo.getId()).isNotNull();
	}

	@Test
	public void shouldInsertOneDocument() throws IOException {
		// given
		final FooCollection fooCollection = new FooCollection(mongoClient, DATABASE_NAME, COLLECTION_NAME);
		final Bar bar = new BarBuilder().withStringField("jbar").withPrimitiveIntField(21).withEnumBar(EnumBar.BAR)
				.build();
		final Foo foo = new FooBuilder().withStringField("jdoe").withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO)
				.withBar(bar).build();
		// when
		fooCollection.insert(foo);
		// then
		assertThat(foo.getId()).isNotNull();
		assertThat(mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME).count()).isEqualTo(1);
		final Document createdDoc = mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME).find().first();
		final Document barSubdoc = (Document) createdDoc.get("bar");
		assertThat(barSubdoc).isNotNull();
		assertThat(barSubdoc.get("_id")).as("Check embedded doc has no '_id' field").isNull();
	}

	@Test
	public void shouldInsertTwoDocuments() throws IOException {
		// given
		final FooCollection fooCollection = new FooCollection(mongoClient, DATABASE_NAME, COLLECTION_NAME);
		final Bar bar1 = new BarBuilder().withStringField("jbar1").withPrimitiveIntField(21).withEnumBar(EnumBar.BAR)
				.build();
		final Foo foo1 = new FooBuilder().withStringField("jdoe1").withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO)
				.withBar(bar1).build();
		final Bar bar2 = new BarBuilder().withStringField("jbar2").withPrimitiveIntField(21).withEnumBar(EnumBar.BAR)
				.build();
		final Foo foo2 = new FooBuilder().withStringField("jdoe2").withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO)
				.withBar(bar2).build();
		// when
		fooCollection.insert(foo1, foo2);
		// then
		assertThat(foo1.getId()).isNotNull();
		assertThat(mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME).count()).isEqualTo(2);
		final Document createdDoc = mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME).find().first();
		final Document barSubdoc = (Document) createdDoc.get("bar");
		assertThat(barSubdoc).isNotNull();
		assertThat(barSubdoc.get("_id")).as("Check embedded doc has no '_id' field").isNull();
	}
	
	@Test(expected = MongoBulkWriteException.class)
	public void shouldNotInsertOneFooWithIdTwice() throws IOException {
		// given
		final FooCollection fooCollection = new FooCollection(mongoClient, DATABASE_NAME, COLLECTION_NAME);
		final Foo foo = new FooBuilder().withId(new ObjectId("54c28b0b0f2dacc85ede5286")).withStringField("jdoe")
				.withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO).build();
		// when 2 inserts should fail
		fooCollection.insert(foo);
		fooCollection.insert(foo);
	}

	@Test
	public void shouldUpsertOneFooTwice() throws IOException {
		// given
		final FooCollection fooCollection = new FooCollection(mongoClient, DATABASE_NAME, COLLECTION_NAME);
		// when
		Foo foo = new FooBuilder().withId(new ObjectId("54c28b0b0f2dacc85ede5286")).withStringField("jdoe")
				.withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO).build();
		fooCollection.upsert(foo);
		foo = new FooBuilder().withId(new ObjectId("54c28b0b0f2dacc85ede5286")).withStringField("j.doe")
				.withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO).build();
		fooCollection.upsert(foo);
		// then
		assertThat(mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME).count()).isEqualTo(1);
		assertThat(foo.getStringField()).isNotNull().isEqualTo("j.doe");
	}

}
