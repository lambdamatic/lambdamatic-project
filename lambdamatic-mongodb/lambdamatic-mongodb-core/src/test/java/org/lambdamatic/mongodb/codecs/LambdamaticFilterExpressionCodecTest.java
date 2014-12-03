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
import org.junit.Before;
import org.junit.Test;
import org.lambdamatic.FilterExpression;
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

	private ByteArrayOutputStream outputStream = null;
	private  BsonWriter bsonWriter = null; 
	private EncoderContext context = null;
	
	@Before
	public void setupEncoding() throws UnsupportedEncodingException {
		this.outputStream = new ByteArrayOutputStream();
		this.bsonWriter = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8")); 
		this.context = EncoderContext.builder().isEncodingCollectibleDocument(true).build();
	}
	
	@Test
	public void shouldConvertExpressionWithSingleOperand() throws IOException, JSONException {
		// given
		final FilterExpression<User_> expression = (User_ u) -> u.userName.equals("jdoe");
		// when
		new LambdamaticFilterExpressionCodec().encode(bsonWriter, expression, context); 
		// then
		final String actual = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
		LOGGER.debug("Output JSON: {}", actual);
		final String expected = "{userName: 'jdoe'}";
		JSONAssert.assertEquals(expected, actual, true);
	}

	@Test
	public void shouldConvertExpressionWithConditionalOR() throws IOException, JSONException {
		// given
		final FilterExpression<User_> expression = (User_ u) -> u.firstName.equals("john") || u.lastName.equals("doe") || u.userName.equals("jdoe");
		// when
		new LambdamaticFilterExpressionCodec().encode(bsonWriter, expression, context); 
		// then
		final String actual = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
		LOGGER.debug("Output JSON: {}", actual);
		final String expected = "{$or: [{firstName: 'john'}, {lastName:'doe'}, {userName: 'jdoe'}]}";
		JSONAssert.assertEquals(expected, actual, false);
	}

	@Test
	public void shouldConvertInfixORWithTwoArguments() throws IOException, JSONException {
		// given
		final FilterExpression<User_> expression = ((User_ u) -> u.firstName.equals("john")
				|| u.lastName.equals("doe"));
		// when
		new LambdamaticFilterExpressionCodec().encode(bsonWriter, expression, context);
		// then
		final String actual = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
		final String expected = "{$or: [{firstName: 'john'}, {lastName:'doe'}]}";
		JSONAssert.assertEquals(expected, actual, false);
	}

	@Test
	public void shouldConvertInfixANDWithTwoArguments() throws IOException, JSONException {
		// given
		final FilterExpression<User_> expression = ((User_ u) -> u.firstName.equals("john")
				&& u.lastName.equals("doe"));
		// when
		new LambdamaticFilterExpressionCodec().encode(bsonWriter, expression, context);
		// then
		final String actual = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
		final String expected = "{firstName: 'john', lastName:'doe'}";
		JSONAssert.assertEquals(expected, actual, false);
	}

	@Test
	public void shouldConvertInfixANDWithThreeArguments() throws IOException, JSONException {
		// given
		final FilterExpression<User_> expression = ((User_ u) -> u.firstName.equals("john")
				&& u.lastName.equals("doe") && u.userName.equals("jdoe"));
		// when
		new LambdamaticFilterExpressionCodec().encode(bsonWriter, expression, context);
		// then
		final String actual = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
		final String expected = "{firstName: 'john', lastName:'doe', userName: 'jdoe'}";
		JSONAssert.assertEquals(expected, actual, false);
	}

	@Test
	public void shouldConvertInfixORWithThreeArguments() throws IOException, JSONException {
		// given
		final FilterExpression<User_> expression = ((User_ u) -> u.firstName.equals("john")
				|| u.lastName.equals("doe") || u.userName.equals("jdoe"));
		// when
		new LambdamaticFilterExpressionCodec().encode(bsonWriter, expression, context);
		// then
		final String actual = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
		final String expected = "{$or: [{firstName: 'john'}, {lastName:'doe'}, {userName: 'jdoe'}]}";
		JSONAssert.assertEquals(expected, actual, false);
	}
	
	@Test
	public void shouldConvertMixOfInfixANDWithInfixOR() throws IOException, JSONException {
		// given
		final FilterExpression<User_> expression = ((User_ u) -> (u.firstName.equals("john") && u.lastName
				.equals("doe")) || u.userName.equals("jdoe"));
		// when
		new LambdamaticFilterExpressionCodec().encode(bsonWriter, expression, context);
		// then
		final String actual = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
		final String expected = "{$or: [{firstName: 'john', lastName:'doe'}, {userName: 'jdoe'}]}";
		JSONAssert.assertEquals(expected, actual, false);
	}
	
}
