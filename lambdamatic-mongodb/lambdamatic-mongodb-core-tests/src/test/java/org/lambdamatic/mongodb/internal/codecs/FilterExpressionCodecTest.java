/**
 * 
 */
package org.lambdamatic.mongodb.internal.codecs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;
import org.bson.json.JsonWriter;
import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.lambdamatic.SerializablePredicate;
import org.lambdamatic.mongodb.types.geospatial.Location;
import org.lambdamatic.mongodb.types.geospatial.Polygon;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

import com.sample.EnumFoo;
import com.sample.Foo;
import com.sample.Foo.FooBuilder;
import com.sample.QFoo;

/**
 * Testing the {@link FilterExpressionCodec}
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
@RunWith(Parameterized.class)
public class FilterExpressionCodecTest {
	
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FilterExpressionCodecTest.class);
	
	@Parameters(name = "[{index}] {1}")
	public static Object[][] data() {
		final Foo f = new FooBuilder().withStringField("foo").build();
		final Polygon singleRing = new Polygon(new Location(0, 0), new Location(0, 1), new Location(1, 1), new Location(1, 0), new Location(0, 0));
		final Object[] namesArray = new Object[] { "John", "Jack" };
		final List<Object> namesList = Arrays.asList("John", "Jack");
		return new Object[][]{
				// $eq
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.primitiveByteField.equals(1)),
						"{primitiveByteField: 1}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.primitiveShortField.equals(1)),
						"{primitiveShortField: 1}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.primitiveIntField.equals(1)),
						"{primitiveIntField: 1}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.primitiveFloatField.equals(1)),
						"{primitiveFloatField: 1}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.primitiveLongField.equals(1)),
						"{primitiveLongField: 1}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.primitiveDoubleField.equals(1)),
						"{primitiveDoubleField: 1}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.primitiveCharField.equals('A')),
						"{primitiveCharField: 'A'}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.stringField.equals("John")),
						"{stringField: 'John'}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.stringField.equals(f.getStringField())),
						"{stringField: 'foo'}"
				},
				// $ne
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.stringField.notEquals(f.getStringField())),
						"{stringField: { $ne: 'foo'}}"
				},
				// $gt
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.stringField.greaterThan("John")),
						"{stringField: { $gt: 'John'}}"
				},
				// $gte
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.stringField.greaterOrEquals("John")),
						"{stringField: { $gte: 'John'}}"
				},
				// $lt
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.stringField.lessThan("John")),
						"{stringField: { $lt: 'John'}}"
				},
				// $lte
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.stringField.lessOrEquals("John")),
						"{stringField: { $lte: 'John'}}"
				},
				// $in
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.stringField.in("John", "Jack")),
						"{stringField: { $in: ['John', 'Jack']}}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.stringField.in(namesArray)),
						"{stringField: { $in: ['John', 'Jack']}}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.stringField.in(namesList)),
						"{stringField: { $in: ['John', 'Jack']}}"
				},
				// $nin
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.stringField.notIn("John", "Jack")),
						"{stringField: { $nin: ['John', 'Jack']}}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.stringField.notIn(namesArray)),
						"{stringField: { $nin: ['John', 'Jack']}}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.stringField.notIn(namesList)),
						"{stringField: { $nin: ['John', 'Jack']}}"
				},
				
				// testing other operators
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> !foo.stringField.equals("john")),
						"{stringField:{$not:{$eq:'john'}}}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.stringField.equals("john") || foo.primitiveIntField.equals(42) || foo.enumFoo.equals(EnumFoo.FOO)),
						"{$or: [{primitiveIntField: 42}, {enumFoo: 'FOO'}, {stringField: 'john'}]}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.primitiveIntField.equals(42) || foo.enumFoo.equals(EnumFoo.FOO)),
						"{$or: [{primitiveIntField: 42}, {enumFoo: 'FOO'}]}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.primitiveIntField.equals(42) && foo.enumFoo.equals(EnumFoo.FOO)),
						"{$and:[{primitiveIntField: 42}, {enumFoo: 'FOO'}]}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.primitiveIntField.equals(42) && foo.enumFoo.equals(EnumFoo.FOO) && foo.stringField.equals("john")),
						"{$and:[{primitiveIntField: 42}, {enumFoo: 'FOO'}, {stringField: 'john'}]}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.primitiveIntField.equals(42) || foo.enumFoo.equals(EnumFoo.FOO) || foo.stringField.equals("john")),
						"{$or: [{primitiveIntField: 42}, {enumFoo: 'FOO'}, {stringField: 'john'}]}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> (foo.primitiveIntField.equals(42) && foo.enumFoo.equals(EnumFoo.FOO)) || foo.stringField.equals("john")),
						"{$or: [{$and:[{primitiveIntField: 42}, {enumFoo: 'FOO'}]}, {stringField: 'john'}]}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> (!foo.primitiveIntField.equals(42) && !foo.enumFoo.equals(EnumFoo.FOO)) || !foo.stringField.equals("john")),
						"{$or: [{$and:[{primitiveIntField: {$not: {$eq: 42}}}, {enumFoo: {$not: {$eq:'FOO'}}}]},  {stringField:{$not:{$eq:'john'}}}]}"
				},
				// polygon with single (closed) ring defined by an array of Locations
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.location.geoWithin(singleRing)),
						"{location: { $geoWithin: { $geometry: {type: 'Polygon', coordinates: [[[0.0, 0.0], [0.0, 1.0], [1.0, 1.0], [1.0, 0.0], [0.0, 0.0]]] } } } }"
				},
		};
	}

	private final SerializablePredicate<QFoo> expr;
	private final String expectedJSON;
	private static Level previousLoggerLevel;
	
	/**
	 * Test contructor
	 * @param expr the {@link SerializablePredicate} to convert
	 * @param expectedJSON the expected JSON output
	 * @param debug if the logger should set the level to DEBUG or not
	 */
	public FilterExpressionCodecTest(final SerializablePredicate<QFoo> expr, final String expectedJSON) {
		this.expr = expr;
		this.expectedJSON = expectedJSON;
	}

	private static Logger getCodecLogger() {
		final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		return lc.getLogger(FilterExpressionCodec.LOGGER_NAME);
	}

	@BeforeClass
	public static void getLoggerLevel() {
		previousLoggerLevel = getCodecLogger().getLevel();
	}
	
	@AfterClass
	public static void resetLoggerLevel() {
		getCodecLogger().setLevel(previousLoggerLevel);
	}
	
	@Test
	public void shouldConvertWithLoggerEnabled() throws IOException, JSONException {
		getCodecLogger().setLevel(Level.DEBUG);
		performAndAssertConvertion();
	}
	
	@Test
	public void shouldConvertWithLoggerDisabled() throws IOException, JSONException {
		getCodecLogger().setLevel(Level.ERROR);
		performAndAssertConvertion();
	}

	private void performAndAssertConvertion() throws UnsupportedEncodingException, IOException, JSONException {
		// given
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final BsonWriter bsonWriter = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8")); 
		final EncoderContext context = EncoderContext.builder().isEncodingCollectibleDocument(true).build();
		// when
		new FilterExpressionCodec().encode(bsonWriter, expr, context);
		// then
		final String actual = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
		LOGGER.debug("Comparing \nexpected: {}\nresult:   {}", expectedJSON, actual);
		JSONAssert.assertEquals(expectedJSON, actual, false);
	}

}
