/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.converters;

import java.lang.invoke.SerializedLambda;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.lambdamatic.FilterExpression;
import org.lambdamatic.analyzer.LambdaExpressionAnalyzer;
import org.lambdamatic.analyzer.ast.LambdaExpressionReader;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.objectweb.asm.Type;

/**
 * Standalone {@link Codec} for Lambda {@link FilterExpression}s.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class LambdamaticFilterExpressionCodec<E> implements Codec<E> {

	/** The class implementing the Lambda {@link FilterExpression} supported by this {@link Codec}.*/
	private final Class<E> filterExpressionImplementationClass;

	/**
	 * Constructor
	 * @param filterExpressionImplementationClass
	 * @param metadataClass
	 */
	public LambdamaticFilterExpressionCodec(final Class<E> filterExpressionImplementationClass) {
		this.filterExpressionImplementationClass = filterExpressionImplementationClass;
	}

	@Override
	public Class<E> getEncoderClass() {
		return filterExpressionImplementationClass;
	}
	
	@Override
	public void encode(final BsonWriter writer, final E expression, final EncoderContext encoderContext) {
		final SerializedLambda serializedLambda = LambdaExpressionReader.getSerializedLambda((FilterExpression<?>) expression);
		final Type[] argumentTypes = Type.getArgumentTypes(serializedLambda.getImplMethodSignature());
		final String metadataClassName = argumentTypes[0].getClassName();
		try {
			final Class<?> metadataClass = Class.forName(metadataClassName);
			final Expression filterExpressionRoot = new LambdaExpressionAnalyzer().analyzeLambdaExpression((FilterExpression<?>) expression);
			final ExpressionConverter expressionConverter = new ExpressionConverter(metadataClass, writer);
			writer.writeStartDocument();
			filterExpressionRoot.accept(expressionConverter);
			writer.writeEndDocument();
		} catch (ClassNotFoundException e) {
			throw new ConversionException("Failed to lookup class '" + metadataClassName + "' in project's classpath", e);
		}
	}

	@Override
	public E decode(final BsonReader reader, final DecoderContext decoderContext) {
		// the filter expression is used in the queries, so it can only be encoded
		return null;
	}

}
