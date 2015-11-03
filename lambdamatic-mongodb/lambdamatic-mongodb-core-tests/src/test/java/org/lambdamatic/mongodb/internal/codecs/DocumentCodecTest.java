/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.internal.codecs;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.json.JsonReader;
import org.bson.json.JsonWriter;
import org.bson.types.ObjectId;
import org.hamcrest.CoreMatchers;
import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.lambdamatic.mongodb.internal.codecs.utils.ParameterizedDataset;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DBObjectCodecProvider;
import com.mongodb.DBRefCodecProvider;
import com.sample.Bar;
import com.sample.EnumFoo;
import com.sample.Foo;
import com.sample.Foo.FooBuilder;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

/**
 * Testing the {@link FilterExpressionCodec}.
 * 
 * @author xcoulon
 *
 */
@RunWith(Parameterized.class)
public class DocumentCodecTest {

  public static final CodecRegistry DEFAULT_CODEC_REGISTRY =
      CodecRegistries.fromProviders(new ValueCodecProvider(), new DBRefCodecProvider(),
          new DBObjectCodecProvider(), new BsonValueCodecProvider());

  /** The usual Logger. */
  private static final Logger LOGGER = LoggerFactory.getLogger(DocumentCodecTest.class);

  private static Level previousLoggerLevel;

  private static ch.qos.logback.classic.Logger getCodecLogger() {
    final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    return loggerContext.getLogger(DocumentCodec.LOGGER_NAME);
  }

  @BeforeClass
  public static void getLoggerLevel() {
    previousLoggerLevel = getCodecLogger().getLevel();
  }

  @AfterClass
  public static void resetLoggerLevel() {
    getCodecLogger().setLevel(previousLoggerLevel);
  }

  /**
   * @return the data to use.
   */
  @Parameters(name = "[{index}] {0}")
  public static Object[][] data() {
    final Date date = new Date();
    final ParameterizedDataset<Object, String> data = new ParameterizedDataset<>();
    data.match("Basic document",
        new FooBuilder().withId(new ObjectId("5459fed60986a72813eb2d59")).withStringField("jdoe")
            .withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO).withLocation(40.1, -70.2)
            .withDate(date).build(),
        "{_id : { $oid : '5459fed60986a72813eb2d59' }, _targetClass:'com.sample.Foo', "
            + "stringField:'jdoe', " + "primitiveIntField:42, enumFoo:'FOO', date: {$date:"
            + date.getTime() + "}, location:{type:'Point', coordinates:[40.1, -70.2]}}");
    data.match("Document with list of String",
        new FooBuilder().withId(new ObjectId("5459fed60986a72813eb2d59")).withStringField("jdoe")
            .withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO).withLocation(40.1, -70.2)
            .withDate(date).withStringList("bar", "baz", "javaObject").build(),
        "{_id : { $oid : '5459fed60986a72813eb2d59' }, _targetClass:'com.sample.Foo', "
            + "stringField:'jdoe', " + "primitiveIntField:42, enumFoo:'FOO', date: {$date:"
            + date.getTime() + "}, location:{type:'Point', coordinates:[40.1, -70.2]},"
            + "stringList:['bar', 'baz', 'javaObject']}");
    data.match("Document with set of String",
        new FooBuilder().withId(new ObjectId("5459fed60986a72813eb2d59")).withStringField("jdoe")
            .withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO).withLocation(40.1, -70.2)
            .withDate(date).withStringSet("bar", "baz", "javaObject").build(),
        "{_id : { $oid : '5459fed60986a72813eb2d59' }, _targetClass:'com.sample.Foo', "
            + "stringField:'jdoe', " + "primitiveIntField:42, enumFoo:'FOO', date: {$date:"
            + date.getTime() + "}, location:{type:'Point', coordinates:[40.1, -70.2]},"
            + "stringSet:['bar', 'baz', 'javaObject']}");
    data.match("Document with Array of String",
        new FooBuilder().withId(new ObjectId("5459fed60986a72813eb2d59")).withStringField("jdoe")
            .withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO).withLocation(40.1, -70.2)
            .withDate(date).withStringArray("bar", "baz", "javaObject").build(),
        "{_id : { $oid : '5459fed60986a72813eb2d59' }, _targetClass:'com.sample.Foo', "
            + "stringField:'jdoe', " + "primitiveIntField:42, enumFoo:'FOO', date: {$date:"
            + date.getTime() + "}, location:{type:'Point', coordinates:[40.1, -70.2]},"
            + "stringArray:['bar', 'baz', 'javaObject']}");
    data.match("Document with Array of embedded documents",
        new FooBuilder().withId(new ObjectId("5459fed60986a72813eb2d59"))
            .withBarList(new Bar("javaObject", 1)).build(),
        "{_id : { $oid : '5459fed60986a72813eb2d59' }, _targetClass:'com.sample.Foo', "
            + "barList:[{_targetClass: 'com.sample.Bar', stringField:'javaObject', "
            + "primitiveIntField:1}]}");

    final Map<String, String> stringMap = new TreeMap<>();
    stringMap.put("bar", "BAR");
    stringMap.put("baz", "BAZ");
    stringMap.put("foo", "FOO");
    data.match("Document with a Map of Strings",
        new FooBuilder().withId(new ObjectId("5459fed60986a72813eb2d59")).withStringMap(stringMap)
            .build(),
        "{_id : { $oid : '5459fed60986a72813eb2d59' }, _targetClass:'com.sample.Foo', "
            + "stringMap:[{bar:'BAR'}, {baz:'BAZ'},{foo:'FOO'}]}");

    final Map<String, Bar> barMap = new TreeMap<>();
    barMap.put("bar", new Bar("BAR", 1));
    barMap.put("baz", new Bar("BAZ", 2));
    barMap.put("foo", new Bar("FOO", 3));
    data.match("Document with a Map of Bars",
        new FooBuilder().withId(new ObjectId("5459fed60986a72813eb2d59")).withBarMap(barMap)
            .build(),
        "{_id : { $oid : '5459fed60986a72813eb2d59' }, _targetClass:'com.sample.Foo', "
            + "barMap:[{bar:{_targetClass: 'com.sample.Bar', stringField:'BAR', "
            + "primitiveIntField:1}}, "
            + "{baz:{_targetClass: 'com.sample.Bar', stringField:'BAZ' , primitiveIntField:2}},"
            + "{foo:{_targetClass: 'com.sample.Bar', stringField:'FOO' , primitiveIntField:3}}]}");
    return data.toArray();
  }

  @Parameter(0)
  public String title;

  @Parameter(1)
  public Object javaObject; // may be a list of documents

  @Parameter(2)
  public String jsonValue;

  @Test
  public void shouldEncodeDocumentWithLogging() throws IOException, JSONException {
    getCodecLogger().setLevel(Level.DEBUG);
    shouldEncodeDocument();
  }

  @Test
  public void shouldEncodeDocumentWithoutLogging() throws IOException, JSONException {
    getCodecLogger().setLevel(Level.ERROR);
    shouldEncodeDocument();
  }

  private void shouldEncodeDocument()
      throws UnsupportedEncodingException, IOException, JSONException {
    Assume.assumeThat(javaObject, CoreMatchers.instanceOf(Foo.class));
    // given
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    final BsonWriter bsonWriter = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8"));
    final EncoderContext context =
        EncoderContext.builder().isEncodingCollectibleDocument(true).build();
    // when
    new DocumentCodec<Foo>(Foo.class, DEFAULT_CODEC_REGISTRY).encode(bsonWriter, (Foo) javaObject,
        context);
    // then
    final String actualJson = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
    LOGGER.debug("Comparing \nexpected: {} vs \nactual: {}", jsonValue, actualJson);
    JSONAssert.assertEquals(jsonValue, actualJson, true);
  }

  @Test
  public void shouldDecodeDocumentWithLogging() throws IOException, JSONException {
    getCodecLogger().setLevel(Level.DEBUG);
    shouldDecodeDocument(true);
  }

  @Test
  public void shouldDecodeDocumentWithoutLogging() throws IOException, JSONException {
    getCodecLogger().setLevel(Level.ERROR);
    shouldDecodeDocument(false);
  }

  private void shouldDecodeDocument(boolean loggerEnabled) {
    // given
    final BsonReader bsonReader = new JsonReader(jsonValue);
    final DecoderContext decoderContext = DecoderContext.builder().build();
    // when
    final Foo actual = new DocumentCodec<Foo>(Foo.class, DEFAULT_CODEC_REGISTRY).decode(bsonReader,
        decoderContext);
    // then
    assertEquals(javaObject, actual);
  }

}
