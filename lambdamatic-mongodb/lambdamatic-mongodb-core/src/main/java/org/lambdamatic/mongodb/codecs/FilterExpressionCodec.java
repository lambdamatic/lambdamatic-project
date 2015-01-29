/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.codecs;

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
import org.lambdamatic.FilterExpression;
import org.lambdamatic.LambdaExpression;
import org.lambdamatic.analyzer.LambdaExpressionAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Standalone {@link Codec} for Lambda {@link FilterExpression}s.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class FilterExpressionCodec implements Codec<FilterExpression<?>> {

	/** The Logger name to use when logging conversion results.*/
	static final String LOGGER_NAME = FilterExpressionCodec.class.getName();
	
	/** The usual Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(LOGGER_NAME);

	@Override
	public Class<FilterExpression<?>> getEncoderClass() {
		return null;
	}

	@Override
	public void encode(final BsonWriter writer, final FilterExpression<?> expression,
			final EncoderContext encoderContext) {
		final LambdaExpression filterExpression = LambdaExpressionAnalyzer.getInstance().analyzeLambdaExpression(expression);
		if (LOGGER.isInfoEnabled()) {
			try {
				// use an intermediate JsonWriter whose Outputstream can be
				// retrieved
				final ByteArrayOutputStream jsonOutputStream = new ByteArrayOutputStream();
				final BsonWriter debugWriter = new JsonWriter(new OutputStreamWriter(jsonOutputStream, "UTF-8"));
				encodeExpression(filterExpression, debugWriter, encoderContext);
				final String jsonContent = IOUtils.toString(jsonOutputStream.toByteArray(), "UTF-8");
				LOGGER.info("Encoded query: {}", jsonContent);
				// now, write the document in the target writer
				final JsonReader jsonContentReader = new JsonReader(jsonContent);
				writer.pipe(jsonContentReader);
				writer.flush();
			} catch (IOException e) {
				throw new ConversionException("Failed to convert '" + filterExpression.toString()
						+ "' to a BSON document", e);

			}
		} else {
			encodeExpression(filterExpression, writer, encoderContext);
		}
	}

	/**
	 * Encodes the given {@link LambdaExpression} into the given {@link BsonWriter}, using the {@link EncoderContext} if necessary.
	 * @param filterExpression the expression to encode
	 * @param writer the output writer
	 * @param encoderContext then encoder context
	 */
	private void encodeExpression(final LambdaExpression filterExpression, final BsonWriter writer, final EncoderContext encoderContext) {
		final FilterExpressionEncoder expressionEncoder = new FilterExpressionEncoder(
				filterExpression.getArgumentType(), writer, encoderContext);
		writer.writeStartDocument();
		filterExpression.getExpression().accept(expressionEncoder);
		writer.writeEndDocument();
		writer.flush();
	}

	@Override
	public FilterExpression<?> decode(final BsonReader reader, final DecoderContext decoderContext) {
		// the filter expression is used in the queries, so it can only be
		// encoded, never decoded
		return null;
	}

}
