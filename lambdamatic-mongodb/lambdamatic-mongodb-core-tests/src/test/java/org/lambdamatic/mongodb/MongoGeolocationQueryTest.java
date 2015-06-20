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
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;
import org.lambdamatic.mongodb.types.geospatial.Location;
import org.lambdamatic.mongodb.types.geospatial.Polygon;

import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.sample.Foo;
import com.sample.Foo.FooBuilder;
import com.sample.FooCollection;

/**
 * Testing the MongoDB Lambda-based Fluent API.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class MongoGeolocationQueryTest extends MongoBaseTest {

	private FooCollection fooCollection;

	public MongoGeolocationQueryTest() {
		super(Foo.class);
	}

	@Before
	public void setup() throws UnknownHostException {
		this.fooCollection = new FooCollection(getMongoClient(), DATABASE_NAME);
		// insert test data
		final Foo foo1 = new FooBuilder().withStringField("Item1").withLocation(40.72, -73.92).build();
		final Foo foo2 = new FooBuilder().withStringField("Item2").withLocation(40.73, -73.92).build();
		final Foo foo3 = new FooBuilder().withStringField("Item3").withLocation(40.73, -73.92).build();
		final Foo foo4 = new FooBuilder().withStringField("Item4").withLocation(40.72, -73.92).build();
		final Foo foo5 = new FooBuilder().withStringField("Item5").withLocation(40.0, -73.0).build();
		this.fooCollection.add(foo1, foo2, foo3, foo4, foo5);
		// mongoClient.getDatabase(DATABASE_NAME).getCollection(FOO_COLLECTION_NAME).createIndex("location",
		// new CreateIndexOptions().twoDSphereIndexVersion(2));
	}

	@Test
	@UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
	public void shouldFindGeoWithinPolygon() throws IOException {
		// when
		final Polygon corners = new Polygon(new Location(40.70, -73.90), new Location(40.75, -73.90),
				new Location(40.75, -73.95), new Location(40.70, -73.95));
		final List<Foo> matches = fooCollection.filter(f -> f.location.geoWithin(corners)).toList();
		// then
		assertThat(matches).isNotNull().hasSize(4).are(new Condition<Foo>("Checking location is set") {
			@Override
			public boolean matches(final Foo item) {
				return item.getLocation() != null && item.getLocation().getLatitude() != 0
						&& item.getLocation().getLongitude() != 0;
			}
		});
	}

	@Test
	@UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
	public void shouldFindGeoWithinArrayOfLocations() throws IOException {
		// when
		final Location[] corners = new Location[] { new Location(40.70, -73.90), new Location(40.75, -73.90),
				new Location(40.75, -73.95), new Location(40.70, -73.95) };
		final List<Foo> matches = fooCollection.filter(f -> f.location.geoWithin(corners)).toList();
		// then
		assertThat(matches).isNotNull().hasSize(4);
	}

	@Test
	@UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
	public void shouldFindGeoWithinListOfLocations() throws IOException {
		// when
		final List<Location> corners = new ArrayList<>();
		corners.add(new Location(40.70, -73.90));
		corners.add(new Location(40.75, -73.90));
		corners.add(new Location(40.75, -73.95));
		corners.add(new Location(40.70, -73.95));
		final List<Foo> matches = fooCollection.filter(f -> f.location.geoWithin(corners)).toList();
		// then
		assertThat(matches).isNotNull().hasSize(4);
	}

}
