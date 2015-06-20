/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.internal.codecs;

import java.util.List;

import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.lambdamatic.SerializableConsumer;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.LambdaExpression;
import org.lambdamatic.mongodb.metadata.UpdateMetadata;

/**
 * Standalone {@link Codec} for Lambda with {@link SerializableConsumer}s.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class UpdateExpressionCodec extends BaseLambdaExpressionCodec<SerializableConsumer<UpdateMetadata<?>>> {

	/** the codec registry, to decode elements of an incoming BSON document. */
	private final CodecRegistry codecRegistry;

	/** The binding service. */
	private final BindingService bindingService;

	/**
	 * Constructor
	 * 
	 * @param codecRegistry
	 * @param bindingService
	 */
	public UpdateExpressionCodec(final CodecRegistry codecRegistry, final BindingService bindingService) {
		this.codecRegistry = codecRegistry;
		this.bindingService = bindingService;
	}

	void encodeExpression(final LambdaExpression lambdaExpression, final BsonWriter writer,
			final EncoderContext encoderContext) {
		final List<Expression> expressions = EncoderUtils.getAllExpressions(lambdaExpression);
		writer.writeStartDocument();
		for (Expression expression : expressions) {
			final UpdateExpressionEncoder expressionEncoder = new UpdateExpressionEncoder(
					lambdaExpression.getArgumentType(), lambdaExpression.getArgumentName(), writer, encoderContext,
					codecRegistry, bindingService);
			expression.accept(expressionEncoder);
		}
		writer.writeEndDocument();
		writer.flush();
	}

}
