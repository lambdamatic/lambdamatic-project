/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.internal.codecs;

import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.EncoderContext;
import org.lambdamatic.SerializablePredicate;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.LambdaExpression;

/**
 * Standalone {@link Codec} for Lambda {@link SerializablePredicate}s.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 */
public class FilterExpressionCodec extends BaseLambdaExpressionCodec<SerializablePredicate<?>> {

	@Override
	void encodeExpression(final LambdaExpression lambdaExpression, final BsonWriter writer,
			final EncoderContext encoderContext) {
		final FilterExpressionEncoder expressionEncoder = new FilterExpressionEncoder(
				lambdaExpression.getArgumentType(), lambdaExpression.getArgumentName(), writer, encoderContext);
		final Expression expression = EncoderUtils.getSingleExpression(lambdaExpression);
		expression.accept(expressionEncoder);
		writer.flush();
	}

}
