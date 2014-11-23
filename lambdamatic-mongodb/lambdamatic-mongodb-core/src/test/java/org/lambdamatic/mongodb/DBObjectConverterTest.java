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
import org.junit.Test;
import org.lambdamatic.FilterExpression;
import org.lambdamatic.mongodb.converters.DBObjectConverter;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sample.BikeStation;
import com.sample.BikeStationStatus;
import com.sample.User_;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class DBObjectConverterTest {

	/**
	 * Test method for
	 * {@link org.lambdamatic.mongodb.DBObjectConverter.converters.DBObjectHelper#convert(org.lambdamatic.mongodb.FilterExpression)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void shouldConvertSingleExpression() throws IOException {
		// given
		final FilterExpression<User_> expression = ((User_ u) -> (u.firstName.equals("john") && u.lastName
				.equals("doe")) || u.userName.equals("jdoe"));
		// when
		final DBObject dbObject = DBObjectConverter.convert(expression, User_.class);
		// then
		final DBObject expectedResult = new BasicDBObject("$or", new DBObject[]{new BasicDBObject("firstName", "john").append("lastName", "doe"), new BasicDBObject("userName", "jdoe")});
		assertThat(dbObject).isNotNull().isEqualTo(expectedResult);
	}

	@Test
	public void shouldConvertInfixOrWithTwoArguments() throws IOException {
		// given
		final FilterExpression<User_> expression = ((User_ u) -> u.firstName.equals("john")
				|| u.lastName.equals("doe"));
		// when
		final DBObject dbObject = DBObjectConverter.convert(expression, User_.class);
		// then
		final DBObject expectedResult = new BasicDBObject("$or", new DBObject[] {
				new BasicDBObject("firstName", "john"), new BasicDBObject("lastName", "doe") });
		assertThat(dbObject).isNotNull().isEqualTo(expectedResult);
	}

	@Test
	public void shouldConvertInfixOrWithThreeArguments() throws IOException {
		// given
		final FilterExpression<User_> expression = ((User_ u) -> u.firstName.equals("john")
				|| u.lastName.equals("doe") || u.lastName.equals("smith"));
		// when
		final DBObject dbObject = DBObjectConverter.convert(expression, User_.class);
		// then
		final DBObject expectedResult = new BasicDBObject("$or", new DBObject[] {
				new BasicDBObject("firstName", "john"), new BasicDBObject("lastName", "doe"),
				new BasicDBObject("lastName", "smith") });
		assertThat(dbObject).isNotNull().isEqualTo(expectedResult);
	}

	@Test
	public void shouldConvertInfixAndWithTwoArguments() throws IOException {
		// given
		final FilterExpression<User_> expression = ((User_ u) -> u.firstName.equals("john")
				&& u.lastName.equals("doe"));
		// when
		final DBObject dbObject = DBObjectConverter.convert(expression, User_.class);
		// then
		final DBObject expectedResult = new BasicDBObject("firstName", "john").append("lastName", "doe");
		assertThat(dbObject).isNotNull().isEqualTo(expectedResult);
	}

	@Test
	public void shouldConvertInfixAndWithThreeArguments() throws IOException {
		// given
		final FilterExpression<User_> expression = ((User_ u) -> u.firstName.equals("john")
				&& u.lastName.equals("doe") && u.userName.equals("jdoe"));
		// when
		final DBObject dbObject = DBObjectConverter.convert(expression, User_.class);
		// then
		final DBObject expectedResult = new BasicDBObject("firstName", "john").append("lastName", "doe").append(
				"userName", "jdoe");
		assertThat(dbObject).isNotNull().isEqualTo(expectedResult);
	}

	@Test
	public void shouldConvertMixofInfixAndWithInfixOr() throws IOException {
		// given
		final FilterExpression<User_> expression = ((User_ u) -> (u.firstName.equals("john") && u.lastName
				.equals("doe")) || u.userName.equals("jdoe"));
		// when
		final DBObject dbObject = DBObjectConverter.convert(expression, User_.class);
		// then
		final DBObject expectedResult = new BasicDBObject("$or",
				new DBObject[] { new BasicDBObject().append("firstName", "john").append("lastName", "doe"), new BasicDBObject("userName", "jdoe")});
		assertThat(dbObject).isNotNull().isEqualTo(expectedResult);
	}
	
	@Test
	public void shouldConvertDBObjectToDomainClassWithEnum() {
		// given
		final DBObject dbObject = new BasicDBObject().append("id", new ObjectId("5459fed60986a72813eb2d59"))
				.append("_targetClass", "com.sample.BikeStation").append("availableDocks", 20).append("testStation", false)
				.append("totalDocks", 30).append("availableBikes", 10).append("status", "IN_SERVICE");
		// when
		final BikeStation bikeStation = DBObjectConverter.convert(dbObject, BikeStation.class);
		// then
		assertThat(bikeStation).isNotNull();
		assertThat(bikeStation.getId()).isNotNull();
		assertThat(bikeStation.getStatus()).isEqualTo(BikeStationStatus.IN_SERVICE);
		
	}

}

