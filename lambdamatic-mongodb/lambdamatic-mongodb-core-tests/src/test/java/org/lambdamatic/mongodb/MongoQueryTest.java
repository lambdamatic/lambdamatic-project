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
import org.junit.Test;

import com.sample.Bar;
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
public class MongoQueryTest extends MongoBaseTest {

	private FooCollection fooCollection;

	@Before
	public void setup() throws UnknownHostException {
		this.fooCollection = new FooCollection(mongoClient, DATABASE_NAME, COLLECTION_NAME);
		// insert test data
		final Foo foo = new FooBuilder().withStringField("jdoe").withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO)
				.withLocation(40, -70)
				.withBar(new Bar.BarBuilder().withStringField("bar").build())
				.withBarList(new Bar.BarBuilder().withStringField("bar1").build(), new Bar.BarBuilder().withStringField("bar2").build())
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
			public boolean matches(final Foo foo) {
				return foo.getStringField().equals("jdoe") && foo.getPrimitiveIntField() == 42
						&& foo.getEnumFoo() == EnumFoo.FOO
						&& foo.getBar().getStringField().equals("bar");
			}
		});
	}

	@Test
	public void shouldFindOneFooBar() throws IOException {
		// when
		final Foo foo = fooCollection.find(f -> f.barList.stringField.equals("bar1")).first();
		// then
		assertThat(foo).isNotNull().has(new Condition<Foo>() {
			@Override
			public boolean matches(final Foo foo) {
				return foo.getBar().getStringField().equals("bar");
			}
		});
	}

	@Test
	public void shouldFindOneFooWithElementMatchBar() throws IOException {
		// when
		final Foo foo = fooCollection.find(f -> f.barList.elementMatch(b -> b.stringField.equals("bar1"))).first();
		// then
		assertThat(foo).isNotNull().has(new Condition<Foo>() {
			@Override
			public boolean matches(final Foo foo) {
				return foo.getBar().getStringField().equals("bar");
			}
		});
	}

	@Test
	public void shouldFindOneFooWithFieldInclusionProjection() throws IOException {
		// when
		final Foo foo = fooCollection
				.find(f -> f.stringField.equals("jdoe"))
				.projection(f -> Projection.include(f.stringField, f.barList.elementMatch(b -> b.stringField.equals("bar1"))))
				.first();
		// then
		assertThat(foo).isNotNull().has(new Condition<Foo>() {
			@Override
			public boolean matches(final Foo value) {
				return value.getId() == null && value.getStringField().equals("jdoe") && value.getPrimitiveIntField() == 0
						&& value.getEnumFoo() == null && value.getLocation() == null && value.getBarList().size() == 1;
			}
		}.as(new TextDescription("only a 'stringField' and 'location' fields initialized")));
	}

}
