/**
 * 
 */
package org.lambdamatic.mongodb.codecs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.commons.io.IOUtils;
import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;
import org.bson.json.JsonWriter;
import org.json.JSONException;
import org.junit.Test;
import org.lambdamatic.FilterExpression;
import org.lambdamatic.mongodb.codecs.LambdamaticFilterExpressionCodec;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sample.User_;

/**
 * Testing the {@link LambdamaticFilterExpressionCodec}
 * @author xcoulon
 *
 */
public class LambdamaticFilterExpressionCodecTest {
	
	/** The usual Logger.*/
	private static final Logger LOGGER = LoggerFactory.getLogger(LambdamaticFilterExpressionCodecTest.class);

	@Test
	public void shouldConvertExpressionWithSingleOperand() throws IOException, JSONException {
		// given
		final FilterExpression<User_> expr = (User_ u) -> u.userName.equals("jdoe");
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final BsonWriter bsonWriter = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8")); 
		final EncoderContext context = EncoderContext.builder().isEncodingCollectibleDocument(true).build();
		// when
		new LambdamaticFilterExpressionCodec().encode(bsonWriter, expr, context); 
		// then
		final String actual = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
		LOGGER.debug("Output JSON: {}", actual);
		final String expected = "{userName: 'jdoe'}";
		JSONAssert.assertEquals(expected, actual, true);
	}

	@Test
	public void shouldConvertExpressionWithConditionalOR() throws IOException, JSONException {
		// given
		final FilterExpression<User_> expr = (User_ u) -> u.firstName.equals("john") || u.lastName.equals("doe") || u.userName.equals("jdoe");
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final BsonWriter bsonWriter = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8")); 
		final EncoderContext context = EncoderContext.builder().isEncodingCollectibleDocument(true).build();
		// when
		new LambdamaticFilterExpressionCodec().encode(bsonWriter, expr, context); 
		// then
		final String actual = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
		LOGGER.debug("Output JSON: {}", actual);
		final String expected = "{$or: [{firstName: 'john'}, {lastName:'doe'}, {userName: 'jdoe'}]}";
		JSONAssert.assertEquals(expected, actual, false);
	}

	@Test
	public void shouldConvertExpressionWithConditionalAND() throws IOException, JSONException {
		// given
		final FilterExpression<User_> expr = (User_ u) -> u.firstName.equals("john") && u.lastName.equals("doe") && u.userName.equals("jdoe");
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final BsonWriter bsonWriter = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8")); 
		final EncoderContext context = EncoderContext.builder().isEncodingCollectibleDocument(true).build();
		// when
		new LambdamaticFilterExpressionCodec().encode(bsonWriter, expr, context); 
		// then
		final String actual = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
		LOGGER.debug("Output JSON: {}", actual);
		final String expected = "{firstName: 'john', lastName:'doe', userName: 'jdoe'}";
		JSONAssert.assertEquals(expected, actual, false);
	}
	
	@Test
	public void shouldConvertExpressionWithMultipleOperands() throws IOException, JSONException {
		// given
		final FilterExpression<User_> expr = (User_ u) -> (u.firstName.equals("john") && u.lastName.equals("doe")) || u.userName.equals("jdoe");
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final BsonWriter bsonWriter = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8")); 
		final EncoderContext context = EncoderContext.builder().isEncodingCollectibleDocument(true).build();
		// when
		new LambdamaticFilterExpressionCodec().encode(bsonWriter, expr, context); 
		// then
		final String actual = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
		LOGGER.debug("Output JSON: {}", actual);
		final String expected = "{$or: [{firstName: 'john', lastName:'doe'}, {userName: 'jdoe'}]}";
		JSONAssert.assertEquals(expected, actual, false);
	}
}
