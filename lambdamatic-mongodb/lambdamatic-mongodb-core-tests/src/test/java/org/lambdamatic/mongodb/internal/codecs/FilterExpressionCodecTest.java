/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

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
import org.lambdamatic.mongodb.internal.codecs.utils.ParameterizedDataset;
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
 * Testing the {@link FilterExpressionCodec}.
 * 
 * @author Xavier Coulon
 *
 */
@RunWith(Parameterized.class)
public class FilterExpressionCodecTest {

  private static final org.slf4j.Logger LOGGER =
      LoggerFactory.getLogger(FilterExpressionCodecTest.class);

  /**
   * @return the data to use.
   */
  @Parameters(name = "[{index}] {1}")
  public static Object[][] data() {
    final Foo f = new FooBuilder().withStringField("javaObject").build();
    final Polygon singleRing = new Polygon(new Location(0, 0), new Location(0, 1),
        new Location(1, 1), new Location(1, 0), new Location(0, 0));
    final List<String> namesList = Arrays.asList("John", "Jack");
    final List<EnumFoo> enumList = Arrays.asList(EnumFoo.FOO, EnumFoo.BAR);
    final ParameterizedDataset<SerializablePredicate<QFoo>> data = new ParameterizedDataset<>();

    // $eq
    data.match(foo -> foo.primitiveByteField.equals(1), "{primitiveByteField: 1}");
    data.match(foo -> foo.primitiveByteField.equals(1), "{primitiveByteField: 1}");
    data.match(foo -> foo.primitiveBooleanField.equals(true), "{primitiveBooleanField: true}");
    data.match(foo -> foo.primitiveBooleanField.equals(true), "{primitiveBooleanField: true}");
    data.match(foo -> foo.primitiveBooleanField.equals(false), "{primitiveBooleanField: false}");
    data.match(foo -> !foo.primitiveBooleanField.equals(false),
        "{$not:{primitiveBooleanField: false}}");
    data.match(foo -> foo.primitiveByteField.equals(1), "{primitiveByteField: 1}");
    data.match(foo -> foo.primitiveShortField.equals(1), "{primitiveShortField: 1}");
    data.match(foo -> foo.primitiveIntField.equals(1), "{primitiveIntField: 1}");
    data.match(foo -> foo.primitiveFloatField.equals(1), "{primitiveFloatField: 1}");
    data.match(foo -> foo.primitiveLongField.equals(1L), "{primitiveLongField: {$numberLong:'1'}}");
    data.match(foo -> foo.primitiveDoubleField.equals(1d), "{primitiveDoubleField: 1}");
    data.match(foo -> foo.primitiveCharField.equals('A'), "{primitiveCharField: 'A'}");
    data.match(foo -> foo.stringField.equals("John"), "{stringField: 'John'}");
    data.match(foo -> foo.stringField.equals(f.getStringField()), "{stringField: 'javaObject'}");

    // $ne
    data.match(foo -> foo.stringField.notEquals(f.getStringField()),
        "{stringField: { $ne: 'javaObject'}}");
    data.match(foo -> !foo.stringField.equals(f.getStringField()),
        "{$not:{stringField: 'javaObject'}}");
    data.match(foo -> foo.enumFoo.notEquals(EnumFoo.FOO), "{enumFoo: { $ne: 'FOO'}}");
    data.match(foo -> !foo.enumFoo.equals(EnumFoo.FOO), "{$not:{enumFoo: 'FOO'}}");
    data.match(foo -> !(foo.primitiveByteField.equals(1)), "{$not:{primitiveByteField: 1}}");
    data.match(foo -> foo.primitiveIntField.notEquals(1), "{primitiveIntField: {$ne: 1}}");
    data.match(foo -> foo.primitiveFloatField.notEquals(1.1f),
        "{primitiveFloatField: {$ne: 1.1}}}");
    data.match(foo -> foo.primitiveCharField.notEquals('A'), "{primitiveCharField: {$ne: 'A'}}}");
    // $gt
    data.match(foo -> foo.stringField.greaterThan("John"), "{stringField: { $gt: 'John'}}");
    data.match(foo -> foo.primitiveIntField.greaterThan(1), "{primitiveIntField: {$gt: 1}}");
    data.match(foo -> foo.primitiveFloatField.greaterThan(1.1f),
        "{primitiveFloatField: {$gt: 1.1}}}");
    data.match(foo -> foo.primitiveCharField.greaterThan('A'), "{primitiveCharField: {$gt: 'A'}}}");
    // $gte
    data.match(foo -> foo.stringField.greaterOrEquals("John"), "{stringField: { $gte: 'John'}}");
    data.match(foo -> foo.primitiveIntField.greaterOrEquals(1), "{primitiveIntField: {$gte: 1}}");
    data.match(foo -> foo.primitiveFloatField.greaterOrEquals(1.1f),
        "{primitiveFloatField: {$gte: 1.1}}}");
    data.match(foo -> foo.primitiveCharField.greaterOrEquals('A'),
        "{primitiveCharField: {$gte: 'A'}}}");
    // $lt
    data.match(foo -> foo.stringField.lessThan("John"), "{stringField: { $lt: 'John'}}");
    data.match(foo -> foo.primitiveIntField.lessThan(1), "{primitiveIntField: {$lt: 1}}");
    data.match(foo -> foo.primitiveFloatField.lessThan(1.1f), "{primitiveFloatField: {$lt: 1.1}}}");
    data.match(foo -> foo.primitiveCharField.lessThan('A'), "{primitiveCharField: {$lt: 'A'}}}");
    // $lte
    data.match(foo -> foo.stringField.lessOrEquals("John"), "{stringField: { $lte: 'John'}}");
    data.match(foo -> foo.primitiveIntField.lessOrEquals(1), "{primitiveIntField: {$lte: 1}}");
    data.match(foo -> foo.primitiveFloatField.lessOrEquals(1.1f),
        "{primitiveFloatField: {$lte: 1.1}}}");
    data.match(foo -> foo.primitiveCharField.lessOrEquals('A'),
        "{primitiveCharField: {$lte: 'A'}}}");
    // $in
    data.match(foo -> foo.stringField.in("John", "Jack"),
        "{stringField: { $in: ['John', 'Jack']}}");
    data.match(foo -> foo.stringField.in(namesList), "{stringField: { $in: ['John', 'Jack']}}");
    data.match(foo -> foo.enumFoo.in(EnumFoo.FOO, EnumFoo.BAR),
        "{enumFoo: { $in: ['FOO', 'BAR']}}");
    data.match(foo -> foo.enumFoo.in(enumList), "{enumFoo: { $in: ['FOO', 'BAR']}}");
    // FIXME: support this case for primitive types too

    // $nin
    data.match(foo -> foo.stringField.notIn("John", "Jack"),
        "{stringField: { $nin: ['John', 'Jack']}}");
    data.match(foo -> !foo.stringField.in("John", "Jack"),
        "{$not:{stringField: {$in: ['John', 'Jack']}}}");
    data.match(foo -> foo.stringField.notIn(namesList), "{stringField: { $nin: ['John', 'Jack']}}");
    data.match(foo -> !foo.stringField.in(namesList),
        "{$not:{stringField: { $in: ['John', 'Jack']}}}");
    data.match(foo -> foo.enumFoo.notIn(EnumFoo.FOO, EnumFoo.BAR),
        "{enumFoo: { $nin: ['FOO', 'BAR']}}");
    data.match(foo -> !foo.enumFoo.in(EnumFoo.FOO, EnumFoo.BAR),
        "{$not:{enumFoo: { $in: ['FOO', 'BAR']}}}");
    data.match(foo -> foo.enumFoo.notIn(enumList), "{enumFoo: { $nin: ['FOO', 'BAR']}}");
    data.match(foo -> !foo.enumFoo.in(enumList), "{$not:{enumFoo: { $in: ['FOO', 'BAR']}}}");
    // FIXME: support this case for primitive types too

    // $not
    // FIXME: support this special operator with an annotated helper method ? Eg : Query.not(...),
    // similar to
    // Projection.isInclude(...)

    // polygon with single (closed) ring defined by an array of points
    data.match(foo -> foo.location.geoWithin(singleRing),
        "{location: { $geoWithin: { $geometry: {type: 'Polygon', "
        + "coordinates: [[[0.0, 0.0], [0.0, 1.0], [1.0, 1.0], [1.0, 0.0], [0.0, 0.0]]] } } } }");

    // element match on list (stored in a JSON Array)
    data.match(foo -> foo.stringList.elementMatch(e -> e.equals("bar")),
        "{stringList: {$elemMatch:{$eq:'bar'}}}");
    data.match(
        foo -> foo.stringList.elementMatch(e -> e.greaterThan("bar") && e.lessThan("javaObject")),
        "{stringList: {$elemMatch:{$gt: 'bar', $lt: 'javaObject' }}}");
    data.match(foo -> foo.barList.elementMatch(b -> b.stringField.equals("javaObject")),
        "{'barList': {$elemMatch:{stringField:'javaObject'}}}");
    // element match on a specific (indexed) element of list (stored in a JSON Array)
    data.match(foo -> foo.stringList.get(0).equals("bar"), "{'stringList.0':'bar'}");
    data.match(
        foo -> foo.stringList.get(0).greaterThan("bar")
            && foo.stringList.get(0).lessThan("javaObject"),
        "{$and:[{'stringList.0': {$gt: 'bar'}}, {'stringList.0': {$lt: 'javaObject' }}]}");
    data.match(foo -> foo.barList.get(1).stringField.equals("javaObject"),
        "{'barList.1.stringField': 'javaObject' }");

    // element match on map (stored as a JSON array of nested objects with a single field)
    data.match(
        foo -> foo.stringMap.elementMatch(s -> s.greaterThan("bar") && s.lessThan("javaObject")),
        "{stringMap: { $elemMatch: {$gt: 'bar', $lt: 'javaObject' }}}");
    data.match(foo -> foo.barMap.elementMatch(b -> b.stringField.equals("javaObject")),
        "{barMap: { $elemMatch: {stringField: 'javaObject' }}}");
    // element match on a specific (indexed) element of map (stored as a JSON array of nested
    // objects with a single field)
    data.match(
        foo -> foo.stringMap.get("entry").greaterThan("bar")
            && foo.stringMap.get("entry").lessThan("javaObject"),
        "{$and:[{'stringMap.entry':{$gt: 'bar'}}, {'stringMap.entry':{$lt: 'javaObject' }}]}");
    data.match(foo -> foo.barMap.get("entry").stringField.equals("javaObject"),
        "{'barMap.entry.stringField': 'javaObject' }}}");


    // nested documents
    data.match(foo -> foo.bar.enumBar.equals(EnumBar.BAR), "{bar.enumBar: 'BAR'}");
    // testing combination of operators
    data.match(foo -> !foo.stringField.equals("john"), "{$not:{stringField:'john'}}");
    data.match(
        foo -> foo.stringField.equals("john") || foo.primitiveIntField.equals(42)
            || foo.enumFoo.equals(EnumFoo.FOO),
        "{$or: [{primitiveIntField: 42}, {enumFoo: 'FOO'}, {stringField: 'john'}]}");
    data.match(foo -> foo.primitiveIntField.equals(42) || foo.enumFoo.equals(EnumFoo.FOO),
        "{$or: [{primitiveIntField: 42}, {enumFoo: 'FOO'}]}");
    data.match(foo -> foo.primitiveIntField.equals(42) && foo.enumFoo.equals(EnumFoo.FOO),
        "{$and:[{primitiveIntField: 42}, {enumFoo: 'FOO'}]}");
    data.match(
        foo -> foo.primitiveIntField.equals(42) && foo.enumFoo.equals(EnumFoo.FOO)
            && foo.stringField.equals("john"),
        "{$and:[{primitiveIntField: 42}, {enumFoo: 'FOO'}, {stringField: 'john'}]}");
    data.match(
        foo -> foo.primitiveIntField.equals(42) || foo.enumFoo.equals(EnumFoo.FOO)
            || foo.stringField.equals("john"),
        "{$or: [{primitiveIntField: 42}, {enumFoo: 'FOO'}, {stringField: 'john'}]}");

    data.match(
        foo -> (foo.primitiveIntField.equals(42) && foo.enumFoo.equals(EnumFoo.FOO))
            || foo.stringField.equals("john"),
        "{$or: [{$and:[{primitiveIntField: 42}, {enumFoo: 'FOO'}]}, {stringField: 'john'}]}");
    data.match(
        foo -> (foo.primitiveIntField.notEquals(42) && !foo.enumFoo.equals(EnumFoo.FOO))
            || !foo.stringField.equals("john"),
        "{$or: [{$and:[{primitiveIntField: {$ne: 42}}, {$not:{enumFoo:'FOO'}}]},  "
        + "{$not:{stringField:'john'}}]}");
    return data.toArray();
  }

  private final SerializablePredicate<QFoo> expr;
  
  private final String expectedJSON;
  
  private static Level previousLoggerLevel;

  /**
   * Test contructor.
   * 
   * @param expr the {@link SerializablePredicate} to convert
   * @param expectedJSON the expected JSON output
   * @param debug if the logger should set the level to DEBUG or not
   */
  public FilterExpressionCodecTest(final SerializablePredicate<QFoo> expr,
      final String expectedJSON) {
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

  private void shouldEncodeFilterExpression()
      throws UnsupportedEncodingException, IOException, JSONException {
    // given
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    final BsonWriter bsonWriter = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8"));
    final EncoderContext context =
        EncoderContext.builder().isEncodingCollectibleDocument(true).build();
    // when
    new FilterExpressionCodec().encode(bsonWriter, expr, context);
    // then
    final String actual = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
    LOGGER.debug("Comparing \nexpected: {}\nresult:   {}", expectedJSON.replaceAll(" ", ""),
        actual.replaceAll(" ", ""));
    JSONAssert.assertEquals(expectedJSON, actual, false);
  }

}
