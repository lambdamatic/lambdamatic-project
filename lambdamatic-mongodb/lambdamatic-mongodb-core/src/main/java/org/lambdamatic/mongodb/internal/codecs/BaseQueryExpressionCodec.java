package org.lambdamatic.mongodb.internal.codecs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.commons.io.IOUtils;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.json.JsonReader;
import org.bson.json.JsonWriter;
import org.lambdamatic.analyzer.LambdaExpressionAnalyzer;
import org.lambdamatic.analyzer.ast.node.LambdaExpression;
import org.lambdamatic.mongodb.exceptions.ConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseQueryExpressionCodec<T> implements Codec<T> {

	/** The Logger name to use when logging conversion results. */
	protected static final String LOGGER_NAME = BaseQueryExpressionCodec.class.getName();
	/** The usual Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(LOGGER_NAME);

	public BaseQueryExpressionCodec() {
		super();
	}

	@Override
	public Class<T> getEncoderClass() {
		// not used
		return null;
	}

	@Override
	public void encode(final BsonWriter writer, final T filterExpression, final EncoderContext encoderContext) {
		final LambdaExpression lambdaExpression = LambdaExpressionAnalyzer.getInstance()
				.analyzeExpression(filterExpression);
		if (LOGGER.isInfoEnabled()) {
			try {
				// use an intermediate JsonWriter whose Outputstream can be
				// retrieved
				final ByteArrayOutputStream jsonOutputStream = new ByteArrayOutputStream();
				final BsonWriter debugWriter = new JsonWriter(new OutputStreamWriter(jsonOutputStream, "UTF-8"));
				encodeExpression(lambdaExpression, debugWriter, encoderContext);
				final String jsonContent = IOUtils.toString(jsonOutputStream.toByteArray(), "UTF-8");
				LOGGER.info("Bson Expression: {}", jsonContent);
				// now, write the document in the target writer
				final JsonReader jsonContentReader = new JsonReader(jsonContent);
				writer.pipe(jsonContentReader);
				writer.flush();
			} catch (IOException e) {
				throw new ConversionException(
						"Failed to convert '" + lambdaExpression.toString() + "' to a BSON document", e);
			}
		} else {
			encodeExpression(lambdaExpression, writer, encoderContext);
		}
	}

	/**
	 * Encodes the given {@link LambdaExpression} into the given {@link BsonWriter}, using the {@link EncoderContext} if
	 * necessary.
	 * 
	 * @param lambdaExpression
	 *            the {@link LambdaExpression} to encode
	 * @param writer
	 *            the output writer
	 * @param encoderContext
	 *            the encoder context
	 */
	abstract void encodeExpression(final LambdaExpression lambdaExpression, final BsonWriter writer,
			final EncoderContext encoderContext);

	@Override
	public T decode(final BsonReader reader, final DecoderContext decoderContext) {
		// the filter expression is used in the queries, so it can only be
		// encoded, never decoded
		return null;
	}

}