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
import java.util.Date;
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
 * Testing the {@link FilterExpressionCodec}
 * 
 * @author xcoulon
 *
 */
@RunWith(Parameterized.class)
public class DocumentCodecTest {

	private static final RootCodecRegistry DEFAULT_CODEC_REGISTRY = new RootCodecRegistry(Arrays.asList(
			new ValueCodecProvider(), new DBRefCodecProvider(), new DBObjectCodecProvider(),
			new BsonValueCodecProvider()));
	/** The usual Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentCodecTest.class);

	private static Level previousLoggerLevel;

	private static ch.qos.logback.classic.Logger getCodecLogger() {
		final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		return lc.getLogger(DocumentCodec.LOGGER_NAME);
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
		final Date date = new Date();
		data.add(new Object[] {
				new FooBuilder().withId(new ObjectId("5459fed60986a72813eb2d59")).withStringField("jdoe")
						.withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO).withLocation(40.1, -70.2).withDate(date).build(),
				"{_id : { $oid : '5459fed60986a72813eb2d59' }, _targetClass:'com.sample.Foo', stringField:'jdoe', primitiveIntField:42, enumFoo:'FOO', date: {$date:"+ date.getTime() + "}, location:{type:'Point', coordinates:[40.1, -70.2]}}"

		});
		return data;
	}

	@Parameter(0)
	public Foo foo;

	@Parameter(1)
	public String jsonString;

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

	private void shouldEncodeDocument() throws UnsupportedEncodingException, IOException,
			JSONException {
		// given
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final BsonWriter bsonWriter = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8"));
		final EncoderContext context = EncoderContext.builder().isEncodingCollectibleDocument(true).build();
		// when
		new DocumentCodec<Foo>(Foo.class, DEFAULT_CODEC_REGISTRY, new BindingService()).encode(bsonWriter, foo, context);
		// then
		final String actual = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
		LOGGER.debug("Output JSON: {}", actual);
		JSONAssert.assertEquals(jsonString, actual, true);
	}

	@Test
	public void shouldDecodeFooDocumentWithLogging() throws IOException, JSONException {
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
		final BsonReader bsonReader = new JsonReader(jsonString);
		final DecoderContext decoderContext = DecoderContext.builder().build();
		// when
		final Foo actualFoo = new DocumentCodec<Foo>(Foo.class, DEFAULT_CODEC_REGISTRY, new BindingService()).decode(bsonReader, decoderContext);
		// then
		assertEquals(foo, actualFoo);
	}

	
}
