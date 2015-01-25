/**
 * 
 */
package org.lambdamatic.mongodb.codecs;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.RootCodecRegistry;
import org.bson.json.JsonReader;
import org.bson.json.JsonWriter;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

import com.mongodb.DBObjectCodecProvider;
import com.mongodb.DBRefCodecProvider;
import com.sample.EnumFoo;
import com.sample.Foo;
import com.sample.Foo.FooBuilder;

/**
 * Testing the {@link LambdamaticFilterExpressionCodec}
 * 
 * @author xcoulon
 *
 */
@RunWith(Parameterized.class)
public class LambdamaticDocumentCodecTest {

	private static final RootCodecRegistry DEFAULT_CODEC_REGISTRY = new RootCodecRegistry(Arrays.asList(
			new ValueCodecProvider(), new DBRefCodecProvider(), new DBObjectCodecProvider(),
			new BsonValueCodecProvider()));
	/** The usual Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(LambdamaticDocumentCodecTest.class);

	private static Level previousLoggerLevel;

	private static ch.qos.logback.classic.Logger getCodecLogger() {
		final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		return lc.getLogger(LambdamaticDocumentCodec.LOGGER_NAME);
	}

	@BeforeClass
	public static void getLoggerLevel() {
		previousLoggerLevel = getCodecLogger().getLevel();
	}

	@AfterClass
	public static void resetLoggerLevel() {
		getCodecLogger().setLevel(previousLoggerLevel);
	}

	@Parameters(name = "[{index}] {1}")
	public static Collection<Object[]> data() {
		final List<Object[]> data = new ArrayList<>();
		data.add(new Object[] {
				new FooBuilder().withId(new ObjectId("5459fed60986a72813eb2d59")).withStringField("jdoe")
						.withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO).withLocation(40.1, -70.2).build(),
				"{_id : { $oid : '5459fed60986a72813eb2d59' }, _targetClass:'com.sample.Foo', stringField:'jdoe', primitiveIntField:42, enumFoo:'FOO', location:{type:'Point', coordinates:[40.1, -70.2]}}"

		});
		return data;
	}

	@Parameter(0)
	public Foo foo;

	@Parameter(1)
	public String jsonString;

	@Test
	public void shouldEncodeFooDocumentWithLogging() throws IOException, JSONException {
		shouldEncodeFooDocument(true);
	}

	@Test
	public void shouldEncodeFooDocumentWithoutLogging() throws IOException, JSONException {
		shouldEncodeFooDocument(false);
	}

	private void shouldEncodeFooDocument(final boolean loggerEnabled) throws UnsupportedEncodingException, IOException,
			JSONException {
		// given
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final BsonWriter bsonWriter = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8"));
		final EncoderContext context = EncoderContext.builder().isEncodingCollectibleDocument(true).build();
		if (loggerEnabled) {
			getCodecLogger().setLevel(Level.DEBUG);
		} else {
			getCodecLogger().setLevel(Level.ERROR);
		}
		// when
		new LambdamaticDocumentCodec<Foo>(Foo.class, DEFAULT_CODEC_REGISTRY).encode(bsonWriter, foo, context);
		// then
		final String actual = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
		LOGGER.debug("Output JSON: {}", actual);
		JSONAssert.assertEquals(jsonString, actual, true);
	}

	@Test
	public void shouldDecodeFooDocumentWithLogging() throws IOException, JSONException {
		shouldDecodeFooDocument(true);
	}

	@Test
	public void shouldDecodeFooDocumentWithoutLogging() throws IOException, JSONException {
		shouldDecodeFooDocument(false);
	}

	private void shouldDecodeFooDocument(boolean loggerEnabled) {
		// given
		final BsonReader bsonReader = new JsonReader(jsonString);
		final DecoderContext decoderContext = DecoderContext.builder().build();
		if (loggerEnabled) {
			getCodecLogger().setLevel(Level.DEBUG);
		} else {
			getCodecLogger().setLevel(Level.ERROR);
		}
		// when
		final Foo actualFoo = new LambdamaticDocumentCodec<Foo>(Foo.class, DEFAULT_CODEC_REGISTRY).decode(bsonReader, decoderContext);
		// then
		assertEquals(foo, actualFoo);
	}

	
}
