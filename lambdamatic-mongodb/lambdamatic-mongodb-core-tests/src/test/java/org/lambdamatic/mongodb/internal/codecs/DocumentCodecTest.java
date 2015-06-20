/**
 * 
 */
package org.lambdamatic.mongodb.internal.codecs;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;

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
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DBObjectCodecProvider;
import com.mongodb.DBRefCodecProvider;
import com.sample.EnumFoo;
import com.sample.Foo;
import com.sample.Foo.FooBuilder;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

/**
 * Testing the {@link FilterExpressionCodec}
 * 
 * @author xcoulon
 *
 */
@RunWith(Parameterized.class)
public class DocumentCodecTest {

	public static final CodecRegistry DEFAULT_CODEC_REGISTRY = CodecRegistries.fromProviders(new ValueCodecProvider(),
			new DBRefCodecProvider(), new DBObjectCodecProvider(), new BsonValueCodecProvider());

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

	@Parameters(name = "[{index}] {0}")
	public static Object[][] data() {
		final Date date = new Date();
		final Object[][] data = new Object[][] {
				new Object[] { "Basic document",
						new FooBuilder().withId(new ObjectId("5459fed60986a72813eb2d59")).withStringField("jdoe")
								.withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO).withLocation(40.1, -70.2)
								.withDate(date).build(),
						"{_id : { $oid : '5459fed60986a72813eb2d59' }, _targetClass:'com.sample.Foo', stringField:'jdoe', "
								+ "primitiveIntField:42, enumFoo:'FOO', date: {$date:" + date.getTime()
								+ "}, location:{type:'Point', coordinates:[40.1, -70.2]}}" },
				new Object[] { "Document with list of String",
						new FooBuilder().withId(new ObjectId("5459fed60986a72813eb2d59")).withStringField("jdoe")
								.withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO).withLocation(40.1, -70.2)
								.withDate(date).withStringList("bar", "baz", "foo").build(),
						"{_id : { $oid : '5459fed60986a72813eb2d59' }, _targetClass:'com.sample.Foo', stringField:'jdoe', "
								+ "primitiveIntField:42, enumFoo:'FOO', date: {$date:" + date.getTime()
								+ "}, location:{type:'Point', coordinates:[40.1, -70.2]},"
								+ "stringList:['bar', 'baz', 'foo']}" },
				new Object[] { "Document with set of String",
						new FooBuilder().withId(new ObjectId("5459fed60986a72813eb2d59")).withStringField("jdoe")
								.withPrimitiveIntField(42)
								.withEnumFoo(
										EnumFoo.FOO)
								.withLocation(40.1, -70.2).withDate(date).withStringSet("bar", "baz", "foo").build(),
						"{_id : { $oid : '5459fed60986a72813eb2d59' }, _targetClass:'com.sample.Foo', stringField:'jdoe', "
								+ "primitiveIntField:42, enumFoo:'FOO', date: {$date:" + date.getTime()
								+ "}, location:{type:'Point', coordinates:[40.1, -70.2]},"
								+ "stringSet:['bar', 'baz', 'foo']}" },
				new Object[] { "Document with Array of String",
						new FooBuilder().withId(new ObjectId("5459fed60986a72813eb2d59")).withStringField("jdoe")
								.withPrimitiveIntField(42).withEnumFoo(EnumFoo.FOO).withLocation(40.1, -70.2)
								.withDate(date).withStringArray("bar", "baz", "foo").build(),
						"{_id : { $oid : '5459fed60986a72813eb2d59' }, _targetClass:'com.sample.Foo', stringField:'jdoe', "
								+ "primitiveIntField:42, enumFoo:'FOO', date: {$date:" + date.getTime()
								+ "}, location:{type:'Point', coordinates:[40.1, -70.2]},"
								+ "stringArray:['bar', 'baz', 'foo']}" } };
		return data;
	}

	@Parameter(0)
	public String title;

	@Parameter(1)
	public Object foo; // may be a list of documents

	@Parameter(2)
	public String expectedJson;

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

	private void shouldEncodeDocument() throws UnsupportedEncodingException, IOException, JSONException {
		Assume.assumeThat(foo, CoreMatchers.instanceOf(Foo.class));
		// given
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final BsonWriter bsonWriter = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8"));
		final EncoderContext context = EncoderContext.builder().isEncodingCollectibleDocument(true).build();
		// when
		new DocumentCodec<Foo>(Foo.class, DEFAULT_CODEC_REGISTRY, new BindingService()).encode(bsonWriter, (Foo) foo,
				context);
		// then
		final String actualJson = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
		LOGGER.debug("Comparing \nexpected: {} vs \nactual: {}", expectedJson, actualJson);
		JSONAssert.assertEquals(expectedJson, actualJson, true);
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
		final BsonReader bsonReader = new JsonReader(expectedJson);
		final DecoderContext decoderContext = DecoderContext.builder().build();
		// when
		final Foo actualFoo = new DocumentCodec<Foo>(Foo.class, DEFAULT_CODEC_REGISTRY, new BindingService())
				.decode(bsonReader, decoderContext);
		// then
		assertEquals(foo, actualFoo);
	}

}
