/**
 * 
 */
package org.lambdamatic.mongodb.converters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.commons.io.IOUtils;
import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;
import org.bson.json.JsonWriter;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.assertj.core.api.Assertions.*;

import com.sample.User;

/**
 * Testing the {@link LambdamaticFilterExpressionCodec}
 * @author xcoulon
 *
 */
public class LambdamaticFilterExpressionCodecTest {
	
	/** The usual Logger.*/
	private static final Logger LOGGER = LoggerFactory.getLogger(LambdamaticFilterExpressionCodecTest.class);

	@Test
	public void shouldEncodeUserDocumentWithId() throws IOException, JSONException {
		// given
		final User user = new User(new ObjectId("5459fed60986a72813eb2d59"), "jdoe", "John", "Doe");
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final BsonWriter bsonWriter = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8")); 
		final EncoderContext context = EncoderContext.builder().isEncodingCollectibleDocument(true).build();
		// when
		new LambdamaticDocumentCodec<User>(User.class).encode(bsonWriter, user, context); 
		// then
		final String actual = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
		LOGGER.debug("Output JSON: {}", actual);
		final String expected = "{_id: '5459fed60986a72813eb2d59', _targetClass:'com.sample.User', userName:'jdoe', firstName:'John', lastName:'Doe'}";
		JSONAssert.assertEquals(expected, actual, true);
	}

	@Test
	public void shouldGenerateDocumentIdWhileEncoding() throws IOException, JSONException {
		// given
		final User user = new User("jdoe", "John", "Doe");
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final BsonWriter bsonWriter = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8")); 
		final EncoderContext context = EncoderContext.builder().isEncodingCollectibleDocument(true).build();
		// when
		new LambdamaticDocumentCodec<User>(User.class).encode(bsonWriter, user, context); 
		// then
		final String actual = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
		LOGGER.debug("Output JSON: {}", actual);
		assertThat(user.getId()).isNotNull();
		final String expected = "{_id: '" + user.getId() + "', _targetClass:'com.sample.User', userName:'jdoe', firstName:'John', lastName:'Doe'}";
		JSONAssert.assertEquals(expected, actual, true);
	}
}
