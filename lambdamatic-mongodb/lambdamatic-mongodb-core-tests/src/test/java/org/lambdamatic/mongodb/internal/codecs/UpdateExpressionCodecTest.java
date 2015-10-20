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

import org.apache.commons.io.IOUtils;
import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;
import org.bson.json.JsonWriter;
import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.lambdamatic.SerializableConsumer;
import org.lambdamatic.mongodb.internal.codecs.utils.ParameterizedDataset;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sample.Bar;
import com.sample.UFoo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

/**
 * Testing the {@link UpdateExpressionCodec}.
 *
 */
@RunWith(Parameterized.class)
public class UpdateExpressionCodecTest {

  /** The usual Logger. */
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateExpressionCodecTest.class);

  private static Level previousLoggerLevel;

  private static ch.qos.logback.classic.Logger getCodecLogger() {
    final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    return loggerContext.getLogger(ProjectionExpressionCodec.LOGGER_NAME);
  }

  private UpdateExpressionCodec codec;

  @BeforeClass
  public static void getLoggerLevel() {
    previousLoggerLevel = getCodecLogger().getLevel();
  }

  @AfterClass
  public static void resetLoggerLevel() {
    getCodecLogger().setLevel(previousLoggerLevel);
  }

  /**
   * @return the data to use in the tests.
   */
  @Parameters(name = "[{index}] {1}")
  public static Object[][] data() {
    final Bar bar = new Bar("javaObject", 1);
    final ParameterizedDataset<SerializableConsumer<UFoo>> data = new ParameterizedDataset<>();
    data.match(foo -> {
      foo.stringField = "javaObject";
    } , "{$set: {stringField: 'javaObject'}}");
    data.match(foo -> {
      foo.primitiveIntField++;
    } , "{$inc: {primitiveIntField: 1}}");
    data.match(foo -> {
      foo.barList.push(new Bar("javaObject", 1));
    } , "{$push: {barList: {_targetClass : 'com.sample.Bar', "
        + "stringField: 'javaObject', primitiveIntField: 1}}}");
    data.match(foo -> {
      foo.barList.push(bar);
    } , "{$push: {barList: {_targetClass : 'com.sample.Bar', "
        + "stringField: 'javaObject', primitiveIntField: 1}}}");
    return data.toArray();
  }

  @Parameter(0)
  public SerializableConsumer<UFoo> updateExpression;

  @Parameter(1)
  public String jsonString;

  @Before
  public void setupCodec() {
    codec = new UpdateExpressionCodec(DocumentCodecTest.DEFAULT_CODEC_REGISTRY);
  }

  @Test
  public void shouldEncodeUpdateExpressionWithLogging() throws IOException, JSONException {
    getCodecLogger().setLevel(Level.DEBUG);
    shouldEncodeUpdateExpression();
  }

  @Test
  public void shouldEncodeUpdateExpressionWithoutLogging() throws IOException, JSONException {
    getCodecLogger().setLevel(Level.ERROR);
    shouldEncodeUpdateExpression();
  }

  private void shouldEncodeUpdateExpression()
      throws UnsupportedEncodingException, IOException, JSONException {
    // given
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    final BsonWriter bsonWriter = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8"));
    final EncoderContext context =
        EncoderContext.builder().isEncodingCollectibleDocument(true).build();
    // when
    codec.encode(bsonWriter, updateExpression, context);
    // then
    final String actual = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
    LOGGER.debug("Output JSON: {}", actual);
    JSONAssert.assertEquals(jsonString, actual, true);
  }

}
