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
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.lambdamatic.SerializableFunction;
import org.lambdamatic.mongodb.metadata.Projection;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

import com.sample.PFoo;

/**
 * Testing the {@link ProjectionExpressionCodec}
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
@RunWith(Parameterized.class)
public class ProjectionExpressionCodecTest {

	/** The usual Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectionExpressionCodecTest.class);

	private static Level previousLoggerLevel;

	private static ch.qos.logback.classic.Logger getCodecLogger() {
		final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		return lc.getLogger(DocumentCodec.LOGGER_NAME);
	}

	private ProjectionExpressionCodec codec = new ProjectionExpressionCodec();

	@BeforeClass
	public static void getLoggerLevel() {
		previousLoggerLevel = getCodecLogger().getLevel();
	}

	@AfterClass
	public static void resetLoggerLevel() {
		getCodecLogger().setLevel(previousLoggerLevel);
	}
	
	@Parameters(name = "[{index}] {1}")
	public static Object[][] data() {
		return new Object[][] {
				new Object[] {
						(SerializableFunction<PFoo, Projection>) ((PFoo foo) -> Projection.include(foo.stringField,
								foo.location)), "{stringField: 1, location: 1, _id: 0}" },
				new Object[] {
						(SerializableFunction<PFoo, Projection>) ((PFoo foo) -> Projection.include(foo.id,
								foo.stringField, foo.location)), "{stringField: 1, location: 1, _id: 1}" }

		};
	}

	@Parameter(0)
	public SerializableFunction<ProjectionMetadata<?>,Projection> projectionExpression;

	@Parameter(1)
	public String jsonString;

	@Before
	public void setupCodec() {
		codec = new ProjectionExpressionCodec();
	}
	
	@Test
	public void shouldEncodeProjectionWithLogging() throws IOException, JSONException {
		getCodecLogger().setLevel(Level.DEBUG);
		shouldEncodeProjection();
	}

	@Test
	public void shouldEncodeProjectionWithoutLogging() throws IOException, JSONException {
		getCodecLogger().setLevel(Level.ERROR);
		shouldEncodeProjection();
	}

	private void shouldEncodeProjection() throws UnsupportedEncodingException, IOException, JSONException {
		// given
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final BsonWriter bsonWriter = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8"));
		final EncoderContext context = EncoderContext.builder().isEncodingCollectibleDocument(true).build();
		// when
		codec.encode(bsonWriter, projectionExpression, context);
		// then
		final String actual = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
		LOGGER.debug("Output JSON: {}", actual);
		JSONAssert.assertEquals(jsonString, actual, true);
	}

}
