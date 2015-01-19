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
import org.lambdamatic.FilterExpression;
import org.lambdamatic.mongodb.types.geospatial.Location;
import org.lambdamatic.mongodb.types.geospatial.Polygon;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

import com.sample.EnumFoo;
import com.sample.Foo_;

/**
 * Testing the {@link LambdamaticFilterExpressionCodec}
 * @author xcoulon
 *
 */
@RunWith(Parameterized.class)
public class LambdamaticFilterExpressionCodecTest {
	
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(LambdamaticFilterExpressionCodecTest.class);
	
	@Parameters(name = "[{index}] {1}")
	public static Object[][] data() {
		final Polygon singleRing = new Polygon(new Location(0, 0), new Location(0, 1), new Location(1, 1), new Location(1, 0), new Location(0, 0));
		return new Object[][]{
				new Object[]{
						(FilterExpression<Foo_>)((Foo_ foo) -> foo.stringField.equals("john")),
						"{stringField: 'john'}"
				},
				new Object[]{
						(FilterExpression<Foo_>)((Foo_ foo) -> foo.stringField.equals("john") || foo.primitiveIntField == 42 || foo.enumFoo == EnumFoo.FOO),
						"{$or: [{primitiveIntField: 42}, {enumFoo: 'FOO'}, {stringField: 'john'}]}"
				},
				new Object[]{
						(FilterExpression<Foo_>)((Foo_ foo) -> foo.primitiveIntField == 42 || foo.enumFoo == EnumFoo.FOO),
						"{$or: [{primitiveIntField: 42}, {enumFoo: 'FOO'}]}"
				},
				new Object[]{
						(FilterExpression<Foo_>)((Foo_ foo) -> foo.primitiveIntField == 42 && foo.enumFoo == EnumFoo.FOO),
						"{primitiveIntField: 42, enumFoo: 'FOO'}"
				},
				new Object[]{
						(FilterExpression<Foo_>)((Foo_ foo) -> foo.primitiveIntField == 42 && foo.enumFoo == EnumFoo.FOO && foo.stringField.equals("john")),
						"{primitiveIntField: 42, enumFoo: 'FOO', stringField: 'john'}"
				},
				new Object[]{
						(FilterExpression<Foo_>)((Foo_ foo) -> foo.primitiveIntField == 42 || foo.enumFoo == EnumFoo.FOO || foo.stringField.equals("john")),
						"{$or: [{primitiveIntField: 42}, {enumFoo: 'FOO'}, {stringField: 'john'}]}"
				},
				new Object[]{
						(FilterExpression<Foo_>)((Foo_ foo) -> (foo.primitiveIntField == 42 && foo.enumFoo == EnumFoo.FOO) || foo.stringField.equals("john")),
						"{$or: [{primitiveIntField: 42, enumFoo: 'FOO'}, {stringField: 'john'}]}"
				},
				// polygon with single (closed) ring defined by an array of Locations
				new Object[]{
						(FilterExpression<Foo_>)((Foo_ foo) -> foo.location.geoWithin(singleRing)),
						"{location: { $geoWithin: { $geometry: {type: 'Polygon', coordinates: [[[0.0, 0.0], [0.0, 1.0], [1.0, 1.0], [1.0, 0.0], [0.0, 0.0]]] } } } }"
				},
		};
	}

	private final FilterExpression<Foo_> expr;
	private final String expectedJSON;
	private static Level previousLoggerLevel;
	
	/**
	 * Test contructor
	 * @param expr the {@link FilterExpression} to convert
	 * @param expectedJSON the expected JSON output
	 * @param debug if the logger should set the level to DEBUG or not
	 */
	public LambdamaticFilterExpressionCodecTest(final FilterExpression<Foo_> expr, final String expectedJSON) {
		this.expr = expr;
		this.expectedJSON = expectedJSON;
	}

	private static Logger getCodecLogger() {
		final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		return lc.getLogger(LambdamaticFilterExpressionCodec.LOGGER_NAME);
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
		performAndAssertConvertion(true);
	}
	
	@Test
	public void shouldConvertWithLoggerDisabled() throws IOException, JSONException {
		performAndAssertConvertion(false);
	}

	private void performAndAssertConvertion(final boolean loggerEnabled) throws UnsupportedEncodingException, IOException, JSONException {
		// given
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final BsonWriter bsonWriter = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8")); 
		final EncoderContext context = EncoderContext.builder().isEncodingCollectibleDocument(true).build();
		if(loggerEnabled) {
			getCodecLogger().setLevel(Level.DEBUG);
		} else {
			getCodecLogger().setLevel(Level.ERROR);
		}
		// when
		new LambdamaticFilterExpressionCodec().encode(bsonWriter, expr, context);
		// then
		final String actual = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
		LOGGER.debug("Comparing \n{} vs \n{}", expectedJSON, actual);
		JSONAssert.assertEquals(expectedJSON, actual, false);
	}

}
