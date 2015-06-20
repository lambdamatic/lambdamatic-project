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

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import com.lordofthejars.nosqlunit.annotation.ShouldMatchDataSet;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.mongodb.MongoBulkWriteException;
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
public class MongoInsertionTest extends MongoBaseTest {

	private FooCollection fooCollection;

	public MongoInsertionTest() {
		super(Foo.class);
	}

	@Before
	public void setup() {
		this.fooCollection = new FooCollection(getMongoClient(), DATABASE_NAME);
	}

	@Test
	@UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
	@ShouldMatchDataSet(location = "/expected-insertions.json")
	public void shouldInsertOneBasicDocument() throws IOException {
		final Foo foo = new FooBuilder().withStringField("jdoe").withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO)
				.withStringList("foo", "bar", "baz").build();
		// when
		fooCollection.add(foo);
		// then
		assertThat(getMongoClient().getDatabase(DATABASE_NAME).getCollection(getCollectionName()).count()).isEqualTo(1);
		assertThat(foo.getId()).isNotNull();
	}

	@Test
	@UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
	public void shouldInsertOneDocument() throws IOException {
		// given
		final Bar bar = new BarBuilder().withStringField("jbar").withPrimitiveIntField(21).withEnumBar(EnumBar.BAR)
				.build();
		final Foo foo = new FooBuilder().withStringField("jdoe").withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO)
				.withBar(bar).build();
		// when
		fooCollection.add(foo);
		// then
		assertThat(foo.getId()).isNotNull();
		assertThat(getMongoClient().getDatabase(DATABASE_NAME).getCollection(getCollectionName()).count()).isEqualTo(1);
		final Document createdDoc = getMongoClient().getDatabase(DATABASE_NAME).getCollection(getCollectionName())
				.find().first();
		final Document barSubdoc = (Document) createdDoc.get("bar");
		assertThat(barSubdoc).isNotNull();
		assertThat(barSubdoc.get("_id")).as("Check embedded doc has no '_id' field").isNull();
	}

	@Test
	@UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
	public void shouldInsertTwoDocuments() throws IOException {
		// given
		final Bar bar1 = new BarBuilder().withStringField("jbar1").withPrimitiveIntField(21).withEnumBar(EnumBar.BAR)
				.build();
		final Foo foo1 = new FooBuilder().withStringField("jdoe1").withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO)
				.withBar(bar1).build();
		final Bar bar2 = new BarBuilder().withStringField("jbar2").withPrimitiveIntField(21).withEnumBar(EnumBar.BAR)
				.build();
		final Foo foo2 = new FooBuilder().withStringField("jdoe2").withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO)
				.withBar(bar2).build();
		// when
		fooCollection.add(foo1, foo2);
		// then
		assertThat(foo1.getId()).isNotNull();
		assertThat(getMongoClient().getDatabase(DATABASE_NAME).getCollection(getCollectionName()).count()).isEqualTo(2);
		final Document createdDoc = getMongoClient().getDatabase(DATABASE_NAME).getCollection(getCollectionName())
				.find().first();
		final Document barSubdoc = (Document) createdDoc.get("bar");
		assertThat(barSubdoc).isNotNull();
		assertThat(barSubdoc.get("_id")).as("Check embedded doc has no '_id' field").isNull();
	}

	@Test(expected = MongoBulkWriteException.class)
	@UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
	public void shouldNotInsertOneFooWithIdTwice() throws IOException {
		// given
		final Foo foo = new FooBuilder().withId(new ObjectId("54c28b0b0f2dacc85ede5286")).withStringField("jdoe")
				.withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO).build();
		// when 2 inserts should fail
		fooCollection.add(foo);
		fooCollection.add(foo);
	}

	@Test
	@UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
	public void shouldUpsertOneFooTwice() throws IOException {
		// given
		// when
		Foo foo = new FooBuilder().withId(new ObjectId("54c28b0b0f2dacc85ede5286")).withStringField("jdoe")
				.withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO).build();
		fooCollection.upsert(foo);
		foo = new FooBuilder().withId(new ObjectId("54c28b0b0f2dacc85ede5286")).withStringField("j.doe")
				.withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO).build();
		fooCollection.upsert(foo);
		// then
		assertThat(getMongoClient().getDatabase(DATABASE_NAME).getCollection(getCollectionName()).count()).isEqualTo(1);
		assertThat(foo.getStringField()).isNotNull().isEqualTo("j.doe");
	}

}
