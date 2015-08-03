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

import com.sample.EnumBar;
import com.sample.EnumFoo;
import com.sample.Foo;
import com.sample.Foo.FooBuilder;
import com.sample.QFoo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * Testing the {@link FilterExpressionCodec}
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
@RunWith(Parameterized.class)
public class FilterExpressionCodecTest {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FilterExpressionCodecTest.class);

	/**
	 * Utility method that makes the JUnit parameters declaration much more readable.
	 * 
	 * @param lambdaExpression
	 *            the {@link FunctionalInterface} to encode
	 * @param bson
	 *            the expected result
	 * @return
	 */
	private static Object[] match(final SerializablePredicate<QFoo> lambdaExpression, final String bson) {
		return new Object[] {
				lambdaExpression, bson };
	}

	@Parameters(name = "[{index}] {1}")
	public static Object[][] data() {
		final Foo f = new FooBuilder().withStringField("foo").build();
		final Polygon singleRing = new Polygon(new Location(0, 0), new Location(0, 1), new Location(1, 1),
				new Location(1, 0), new Location(0, 0));
		final List<String> namesList = Arrays.asList("John", "Jack");
		final List<EnumFoo> enumList = Arrays.asList(EnumFoo.FOO, EnumFoo.BAR);
		match(foo -> foo.primitiveByteField.equals(1), "{primitiveByteField: 1}");
		return new Object[][] {
				// $eq
				match(foo -> foo.primitiveBooleanField.equals(true), "{primitiveBooleanField: true}"),
				match(foo -> foo.primitiveBooleanField.equals(true), "{primitiveBooleanField: true}"),
				match(foo -> foo.primitiveBooleanField.equals(false), "{primitiveBooleanField: false}"),
				match(foo -> !foo.primitiveBooleanField.equals(false), "{$not:{primitiveBooleanField: false}}"),
				match(foo -> foo.primitiveByteField.equals(1), "{primitiveByteField: 1}"),
				match(foo -> foo.primitiveShortField.equals(1), "{primitiveShortField: 1}"),
				match(foo -> foo.primitiveIntField.equals(1), "{primitiveIntField: 1}"),
				match(foo -> foo.primitiveFloatField.equals(1), "{primitiveFloatField: 1}"),
				match(foo -> foo.primitiveLongField.equals(1l), "{primitiveLongField: {$numberLong:'1'}}"),
				match(foo -> foo.primitiveDoubleField.equals(1d), "{primitiveDoubleField: 1}"),
				match(foo -> foo.primitiveCharField.equals('A'), "{primitiveCharField: 'A'}"),
				match(foo -> foo.stringField.equals("John"), "{stringField: 'John'}"),
				match(foo -> foo.stringField.equals(f.getStringField()), "{stringField: 'foo'}"),
				// $ne
				match(foo -> foo.stringField.notEquals(f.getStringField()), "{stringField: { $ne: 'foo'}}"),
				match(foo -> !foo.stringField.equals(f.getStringField()), "{$not:{stringField: 'foo'}}"),
				match(foo -> foo.enumFoo.notEquals(EnumFoo.FOO), "{enumFoo: { $ne: 'FOO'}}"),
				match(foo -> !foo.enumFoo.equals(EnumFoo.FOO), "{$not:{enumFoo: 'FOO'}}"),
				match(foo -> !(foo.primitiveByteField.equals(1)), "{$not:{primitiveByteField: 1}}"),
				match(foo -> foo.primitiveIntField.notEquals(1), "{primitiveIntField: {$ne: 1}}"),
				match(foo -> foo.primitiveFloatField.notEquals(1.1f), "{primitiveFloatField: {$ne: 1.1}}}"),
				match(foo -> foo.primitiveCharField.notEquals('A'), "{primitiveCharField: {$ne: 'A'}}}"),
				// $gt
				match(foo -> foo.stringField.greaterThan("John"), "{stringField: { $gt: 'John'}}"),
				match(foo -> foo.primitiveIntField.greaterThan(1), "{primitiveIntField: {$gt: 1}}"),
				match(foo -> foo.primitiveFloatField.greaterThan(1.1f), "{primitiveFloatField: {$gt: 1.1}}}"),
				match(foo -> foo.primitiveCharField.greaterThan('A'), "{primitiveCharField: {$gt: 'A'}}}"),
				// $gte
				match(foo -> foo.stringField.greaterOrEquals("John"), "{stringField: { $gte: 'John'}}"),
				match(foo -> foo.primitiveIntField.greaterOrEquals(1), "{primitiveIntField: {$gte: 1}}"),
				match(foo -> foo.primitiveFloatField.greaterOrEquals(1.1f), "{primitiveFloatField: {$gte: 1.1}}}"),
				match(foo -> foo.primitiveCharField.greaterOrEquals('A'), "{primitiveCharField: {$gte: 'A'}}}"),
				// $lt
				match(foo -> foo.stringField.lessThan("John"), "{stringField: { $lt: 'John'}}"),
				match(foo -> foo.primitiveIntField.lessThan(1), "{primitiveIntField: {$lt: 1}}"),
				match(foo -> foo.primitiveFloatField.lessThan(1.1f), "{primitiveFloatField: {$lt: 1.1}}}"),
				match(foo -> foo.primitiveCharField.lessThan('A'), "{primitiveCharField: {$lt: 'A'}}}"),
				// $lte
				match(foo -> foo.stringField.lessOrEquals("John"), "{stringField: { $lte: 'John'}}"),
				match(foo -> foo.primitiveIntField.lessOrEquals(1), "{primitiveIntField: {$lte: 1}}"),
				match(foo -> foo.primitiveFloatField.lessOrEquals(1.1f), "{primitiveFloatField: {$lte: 1.1}}}"),
				match(foo -> foo.primitiveCharField.lessOrEquals('A'), "{primitiveCharField: {$lte: 'A'}}}"),
				// $in
				match(foo -> foo.stringField.in("John", "Jack"), "{stringField: { $in: ['John', 'Jack']}}"),
				match(foo -> foo.stringField.in(namesList), "{stringField: { $in: ['John', 'Jack']}}"),
				match(foo -> foo.enumFoo.in(EnumFoo.FOO, EnumFoo.BAR), "{enumFoo: { $in: ['FOO', 'BAR']}}"),
				match(foo -> foo.enumFoo.in(enumList), "{enumFoo: { $in: ['FOO', 'BAR']}}"),
				// FIXME: support this case for primitive types too
				
				// $nin
				match(foo -> foo.stringField.notIn("John", "Jack"), "{stringField: { $nin: ['John', 'Jack']}}"),
				match(foo -> !foo.stringField.in("John", "Jack"), "{$not:{stringField: {$in: ['John', 'Jack']}}}"),
				match(foo -> foo.stringField.notIn(namesList), "{stringField: { $nin: ['John', 'Jack']}}"),
				match(foo -> !foo.stringField.in(namesList), "{$not:{stringField: { $in: ['John', 'Jack']}}}"),
				match(foo -> foo.enumFoo.notIn(EnumFoo.FOO, EnumFoo.BAR), "{enumFoo: { $nin: ['FOO', 'BAR']}}"),
				match(foo -> !foo.enumFoo.in(EnumFoo.FOO, EnumFoo.BAR), "{$not:{enumFoo: { $in: ['FOO', 'BAR']}}}"),
				match(foo -> foo.enumFoo.notIn(enumList), "{enumFoo: { $nin: ['FOO', 'BAR']}}"),
				match(foo -> !foo.enumFoo.in(enumList), "{$not:{enumFoo: { $in: ['FOO', 'BAR']}}}"),
				// FIXME: support this case for primitive types too

				// $not
				// FIXME: support this special operator with an annotated helper method ? Eg : Query.not(...), similar to Projection.isInclude(...) 
				
				// polygon with single (closed) ring defined by an array of points
				match(foo -> foo.location.geoWithin(singleRing),
						"{location: { $geoWithin: { $geometry: {type: 'Polygon', coordinates: [[[0.0, 0.0], [0.0, 1.0], [1.0, 1.0], [1.0, 0.0], [0.0, 0.0]]] } } } }"),
				// element match on array
				match(foo -> foo.stringList.elementMatch(s -> s.greaterThan("bar") && s.lessThan("foo")),
						"{stringList: { $elemMatch: {$gt: 'bar', $lt: 'foo' }}}"),
				match(foo -> foo.barList.elementMatch(b -> b.stringField.equals("foo")),
						"{barList: { $elemMatch: {stringField: 'foo' }}}"),
				// nested documents
				match(foo -> foo.bar.enumBar.equals(EnumBar.BAR), "{bar.enumBar: 'BAR'}"),
				// testing combination of operators
				match(foo -> !foo.stringField.equals("john"), "{$not:{stringField:'john'}}"),
				match(foo -> foo.stringField.equals("john") || foo.primitiveIntField.equals(42)
						|| foo.enumFoo.equals(EnumFoo.FOO),
						"{$or: [{primitiveIntField: 42}, {enumFoo: 'FOO'}, {stringField: 'john'}]}"),
				match(foo -> foo.primitiveIntField.equals(42) || foo.enumFoo.equals(EnumFoo.FOO),
						"{$or: [{primitiveIntField: 42}, {enumFoo: 'FOO'}]}"),
				match(foo -> foo.primitiveIntField.equals(42) && foo.enumFoo.equals(EnumFoo.FOO),
						"{$and:[{primitiveIntField: 42}, {enumFoo: 'FOO'}]}"),
				match(foo -> foo.primitiveIntField.equals(42) && foo.enumFoo.equals(EnumFoo.FOO)
						&& foo.stringField.equals("john"),
						"{$and:[{primitiveIntField: 42}, {enumFoo: 'FOO'}, {stringField: 'john'}]}"),
				match(foo -> foo.primitiveIntField.equals(42) || foo.enumFoo.equals(EnumFoo.FOO)
						|| foo.stringField.equals("john"),
						"{$or: [{primitiveIntField: 42}, {enumFoo: 'FOO'}, {stringField: 'john'}]}"),

				match(foo -> (foo.primitiveIntField.equals(42) && foo.enumFoo.equals(EnumFoo.FOO))
						|| foo.stringField.equals("john"),
						"{$or: [{$and:[{primitiveIntField: 42}, {enumFoo: 'FOO'}]}, {stringField: 'john'}]}"),
				match(foo -> (foo.primitiveIntField.notEquals(42) && !foo.enumFoo.equals(EnumFoo.FOO))
						|| !foo.stringField.equals("john"),
						"{$or: [{$and:[{primitiveIntField: {$ne: 42}}, {$not:{enumFoo:'FOO'}}]},  {$not:{stringField:'john'}}]}"), };
	}

	private final SerializablePredicate<QFoo> expr;
	private final String expectedJSON;
	private static Level previousLoggerLevel;

	/**
	 * Test contructor
	 * 
	 * @param expr
	 *            the {@link SerializablePredicate} to convert
	 * @param expectedJSON
	 *            the expected JSON output
	 * @param debug
	 *            if the logger should set the level to DEBUG or not
	 */
	public FilterExpressionCodecTest(final SerializablePredicate<QFoo> expr, final String expectedJSON) {
		this.expr = expr;
		this.expectedJSON = expectedJSON;
	}

	private static Logger getCodecLogger() {
		final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		return lc.getLogger(BaseLambdaExpressionCodec.LOGGER_NAME);
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
	public void shouldEncodeFilterExpressionWithLoggerEnabled() throws IOException, JSONException {
		getCodecLogger().setLevel(Level.DEBUG);
		shouldEncodeFilterExpression();
	}

	@Test
	public void shouldEncodeFilterExpressionWithLoggerDisabled() throws IOException, JSONException {
		getCodecLogger().setLevel(Level.ERROR);
		shouldEncodeFilterExpression();
	}

	private void shouldEncodeFilterExpression() throws UnsupportedEncodingException, IOException, JSONException {
		// given
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final BsonWriter bsonWriter = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8"));
		final EncoderContext context = EncoderContext.builder().isEncodingCollectibleDocument(true).build();
		// when
		new FilterExpressionCodec().encode(bsonWriter, expr, context);
		// then
		final String actual = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
		LOGGER.debug("Comparing \nexpected: {}\nresult:   {}", expectedJSON.replaceAll(" ", ""), actual.replaceAll(" ", ""));
		JSONAssert.assertEquals(expectedJSON, actual, false);
	}

}
