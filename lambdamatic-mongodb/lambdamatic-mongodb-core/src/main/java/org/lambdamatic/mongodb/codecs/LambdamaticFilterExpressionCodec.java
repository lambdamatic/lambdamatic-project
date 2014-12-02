/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.lambdamatic.FilterExpression;
import org.lambdamatic.LambdaExpression;
import org.lambdamatic.analyzer.LambdaExpressionAnalyzer;

/**
 * Standalone {@link Codec} for Lambda {@link FilterExpression}s.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class LambdamaticFilterExpressionCodec implements Codec<FilterExpression<?>> {

	/**
	private final Class<FilterExpression<?>> filterExpressionImplementationClass;

	 * Constructor
	 * @param filterExpressionImplementationClass
	 * @param metadataClass
	public LambdamaticFilterExpressionCodec(final Class<FilterExpression<?>> filterExpressionImplementationClass) {
		this.filterExpressionImplementationClass = filterExpressionImplementationClass;
	}
	 */

	@Override
	public Class<FilterExpression<?>> getEncoderClass() {
		//return filterExpressionImplementationClass;
		return null;
	}
	
	@Override
	public void encode(final BsonWriter writer, final FilterExpression<?> expression, final EncoderContext encoderContext) {
		final LambdaExpression filterExpression = new LambdaExpressionAnalyzer().analyzeLambdaExpression(expression);
		// TODO: use an intermediate JsonWriter, then pipe a BsonReader into the given writer, and log somewhere in the middle ?
		final FilterExpressionEncoder expressionEncoder = new FilterExpressionEncoder(filterExpression.getArgumentType(), writer);
		writer.writeStartDocument();
		filterExpression.getExpression().accept(expressionEncoder);
		writer.writeEndDocument();
		writer.flush();
	}

	@Override
	public FilterExpression<?> decode(final BsonReader reader, final DecoderContext decoderContext) {
		// the filter expression is used in the queries, so it can only be encoded, never decoded
		return null;
	}

}
