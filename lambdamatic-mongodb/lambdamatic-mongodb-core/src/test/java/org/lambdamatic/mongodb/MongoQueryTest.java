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
import org.assertj.core.description.TextDescription;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.lambdamatic.mongodb.annotations.Document;
import static org.lambdamatic.mongodb.metadata.Projection.*;
import org.lambdamatic.mongodb.testutils.DropMongoCollectionsRule;

import com.mongodb.MongoClient;
import com.sample.EnumFoo;
import com.sample.Foo;
import com.sample.Foo.FooBuilder;
import com.sample.FooCollection;

/**
 * Testing the MongoDB Lambda-based Fluent API
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class MongoQueryTest {

	private static final String DATABASE_NAME = "lambdamatic-tests";

	private static final String COLLECTION_NAME = ((Document)Foo.class.getAnnotation(Document.class)).collection();
	
	private MongoClient mongoClient = new MongoClient();
	
	@Rule
	public DropMongoCollectionsRule collectionCleaning = new DropMongoCollectionsRule(mongoClient, DATABASE_NAME, COLLECTION_NAME);

	private FooCollection fooCollection;

	@Before
	public void setup() throws UnknownHostException {
		this.fooCollection = new FooCollection(mongoClient, DATABASE_NAME, COLLECTION_NAME);
		// insert test data
		final Foo foo = new FooBuilder().withStringField("jdoe").withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO)
				.withLocation(40, -70)
				.build();
		this.fooCollection.insert(foo);
	}

	@Test
	public void shouldFindOneFoo() throws IOException {
		// when
		final Foo foo = fooCollection.find(f -> f.stringField.equals("jdoe")).first();
		// then
		assertThat(foo).isNotNull().has(new Condition<Foo>() {
			@Override
			public boolean matches(final Foo value) {
				return value.getStringField().equals("jdoe") && value.getPrimitiveIntField() == 42
						&& value.getEnumFoo() == EnumFoo.FOO;
			}
		});
	}

	@Test
	public void shouldFindOneFooWithFieldInclusionProjection() throws IOException {
		// when
		final Foo foo = fooCollection
				.find(f -> f.stringField.equals("jdoe"))
				.projection(f -> include(f.stringField, f.location))
				.first();
		// then
		assertThat(foo).isNotNull().has(new Condition<Foo>() {
			@Override
			public boolean matches(final Foo value) {
				return value.getId() == null && value.getStringField().equals("jdoe") && value.getPrimitiveIntField() == 0
						&& value.getEnumFoo() == null && value.getLocation() != null;
			}
		}.as(new TextDescription("only a 'stringField' and 'location' fields initialized")));
	}

}
