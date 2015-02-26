/**
 * 
 */
package org.lambdamatic.mongodb.codecs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

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
import com.sample.QFoo;

/**
 * Testing the {@link FilterExpressionCodec}
 * @author xcoulon
 *
 */
@RunWith(Parameterized.class)
public class FilterExpressionCodecTest {
	
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FilterExpressionCodecTest.class);
	
	@Parameters(name = "[{index}] {1}")
	public static Object[][] data() {
		final Polygon singleRing = new Polygon(new Location(0, 0), new Location(0, 1), new Location(1, 1), new Location(1, 0), new Location(0, 0));
		return new Object[][]{
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.stringField.equals("john")),
						"{stringField: 'john'}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> !foo.stringField.equals("john")),
						"{stringField: {$ne: 'john'}}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.stringField.equals("john") || foo.primitiveIntField == 42 || foo.enumFoo == EnumFoo.FOO),
						"{$or: [{primitiveIntField: 42}, {enumFoo: 'FOO'}, {stringField: 'john'}]}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.primitiveIntField == 42 || foo.enumFoo == EnumFoo.FOO),
						"{$or: [{primitiveIntField: 42}, {enumFoo: 'FOO'}]}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.primitiveIntField == 42 && foo.enumFoo == EnumFoo.FOO),
						"{$and:[{primitiveIntField: 42}, {enumFoo: 'FOO'}]}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.primitiveIntField == 42 && foo.enumFoo == EnumFoo.FOO && foo.stringField.equals("john")),
						"{$and:[{primitiveIntField: 42}, {enumFoo: 'FOO'}, {stringField: 'john'}]}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> foo.primitiveIntField == 42 || foo.enumFoo == EnumFoo.FOO || foo.stringField.equals("john")),
						"{$or: [{primitiveIntField: 42}, {enumFoo: 'FOO'}, {stringField: 'john'}]}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> (foo.primitiveIntField == 42 && foo.enumFoo == EnumFoo.FOO) || foo.stringField.equals("john")),
						"{$or: [{$and:[{primitiveIntField: 42}, {enumFoo: 'FOO'}]}, {stringField: 'john'}]}"
				},
				new Object[]{
						(SerializablePredicate<QFoo>)((QFoo foo) -> (foo.primitiveIntField != 42 && foo.enumFoo != EnumFoo.FOO) || !foo.stringField.equals("john")),
						"{$or: [{$and:[{primitiveIntField: {$ne: 42}}, {enumFoo: {$ne:'FOO'}}]}, {stringField: {$ne:'john'}}]}"
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
