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
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.bson.BsonArray;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonString;
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
import com.sample.Foo;
import com.sample.Foo.FooBuilder;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

/**
 * Test specific to encoding and decoding binary types.
 */
@RunWith(Parameterized.class)
public class DocumentBinaryTypeCodecTest {

  public static final CodecRegistry DEFAULT_CODEC_REGISTRY =
      CodecRegistries.fromProviders(new ValueCodecProvider(), new DBRefCodecProvider(),
          new DBObjectCodecProvider(), new BsonValueCodecProvider());

  /** The usual Logger. */
  private static final Logger LOGGER = LoggerFactory.getLogger(DocumentBinaryTypeCodecTest.class);

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
    final Foo foo = new FooBuilder().withId(new ObjectId("5459fed60986a72813eb2d59"))
        .withBytes(new byte[] {1, 2, 3, 4})
        .withBytesList(Arrays.asList(new byte[] {1, 2, 3, 4}, new byte[] {5, 6})).build();
    final BsonDocument expectation = new BsonDocument();
    expectation.append("_id", new BsonObjectId(new ObjectId("5459fed60986a72813eb2d59")));
    expectation.append("_targetClass", new BsonString("com.sample.Foo"));
    expectation.append("raw_content", new BsonBinary(new byte[] {1, 2, 3, 4}));
    expectation.append("raw_contents", new BsonArray(
        Arrays.asList(new BsonBinary(new byte[] {1, 2, 3, 4}), new BsonBinary(new byte[] {5, 6}))));
    final ParameterizedDataset<Object, BsonDocument> data = new ParameterizedDataset<>();
    data.match("Document with binaries", foo, expectation);
    return data.toArray();
  }
  
  @Parameter(0)
  public String title;

  @Parameter(1)
  public Object javaObject; // may be a list of documents

  @Parameter(2)
  public BsonDocument expectation;
  
  
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
    final JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8"));
    final EncoderContext context =
        EncoderContext.builder().isEncodingCollectibleDocument(true).build();
    // when
    new DocumentCodec<Foo>(Foo.class, DEFAULT_CODEC_REGISTRY).encode(jsonWriter, (Foo) javaObject, context);
    // then
    final String actualJson = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
    LOGGER.debug("Comparing \nexpected: {} vs \nactual: {}", expectation.toJson(), actualJson);
    JSONAssert.assertEquals(expectation.toJson(), actualJson, true);
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
    final BsonReader bsonReader = new BsonDocumentReader(expectation);
    final DecoderContext decoderContext = DecoderContext.builder().build();
    // when
    final Foo actual = new DocumentCodec<Foo>(Foo.class, DEFAULT_CODEC_REGISTRY).decode(bsonReader,
        decoderContext);
    // then
    
    assertEquals(javaObject, actual);
  }
}
