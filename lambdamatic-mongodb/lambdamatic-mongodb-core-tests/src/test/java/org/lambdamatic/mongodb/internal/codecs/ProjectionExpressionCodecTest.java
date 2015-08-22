/**
 * 
 */
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
import org.lambdamatic.mongodb.Projection;
import org.lambdamatic.mongodb.internal.codecs.utils.ParameterizedDataset;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sample.PFoo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

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
		final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		return loggerContext.getLogger(ProjectionExpressionCodec.LOGGER_NAME);
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
		final ParameterizedDataset<SerializableConsumer<PFoo>> data = new ParameterizedDataset<>();

		data.match(foo -> Projection.include(foo.stringField), "{stringField: 1, _id: 0}");
		data.match(foo -> Projection.include(foo.stringField, foo.id), "{stringField: 1, _id: 1}");
		data.match(foo -> Projection.include(foo.stringField, foo.location),
						"{stringField: 1, location: 1, _id: 0}");
		data.match(foo -> Projection.include(foo.id, foo.stringField, foo.location),
						"{stringField: 1, location: 1, _id: 1}");
		// @see http://docs.mongodb.org/manual/reference/operator/projection/elemMatch/
		data.match(foo -> Projection.include(foo.id, foo.barList.elementMatch(b -> b.stringField.equals("bar")),
						foo.location),
						"{barList: { $elemMatch: { stringField: 'bar' } }, location: 1, _id: 1}");
		data.match(foo -> Projection.include(foo.barList.elementMatch(b -> b.stringField.equals("bar")),
						foo.location), "{barList: { $elemMatch: { stringField: 'bar' } }, location: 1, _id: 0}");
		data.match(foo -> Projection.include(foo.barList.elementMatch(b -> b.stringField.equals("bar"))),
						"{barList: { $elemMatch: { stringField: 'bar' } }, _id: 0}");
		return data.toArray();
	}

	@Parameter(0)
	public SerializableConsumer<ProjectionMetadata<?>> projectionExpression;

	@Parameter(1)
	public String jsonString;

	@Before
	public void setupCodec() {
		codec = new ProjectionExpressionCodec();
	}

	@Test
	public void shouldEncodeProjectionExpressionWithLogging() throws IOException, JSONException {
		getCodecLogger().setLevel(Level.DEBUG);
		shouldEncodeProjectionExpression();
	}

	@Test
	public void shouldEncodeProjectionExpressionWithoutLogging() throws IOException, JSONException {
		getCodecLogger().setLevel(Level.ERROR);
		shouldEncodeProjectionExpression();
	}

	private void shouldEncodeProjectionExpression() throws UnsupportedEncodingException, IOException, JSONException {
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
