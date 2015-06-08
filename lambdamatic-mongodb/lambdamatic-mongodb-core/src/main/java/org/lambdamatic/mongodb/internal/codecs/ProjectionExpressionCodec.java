/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.internal.codecs;

import java.lang.reflect.Method;
import java.util.List;

import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.EncoderContext;
import org.lambdamatic.SerializableConsumer;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.Expression.ExpressionType;
import org.lambdamatic.analyzer.ast.node.LambdaExpression;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.lambdamatic.mongodb.Projection;
import org.lambdamatic.mongodb.exceptions.ConversionException;
import org.lambdamatic.mongodb.internal.codecs.ProjectionExpressionEncoder.ProjectionType;
import org.lambdamatic.mongodb.metadata.ExcludeFields;
import org.lambdamatic.mongodb.metadata.IncludeFields;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;



/**
 * Standalone {@link Codec} for Lambda with {@link SerializableConsumer}s.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class ProjectionExpressionCodec extends BaseLambdaExpressionCodec<SerializableConsumer<ProjectionMetadata<?>>> {

	void encodeExpression(final LambdaExpression lambdaExpression, final BsonWriter writer, final EncoderContext encoderContext) {
		final Expression expression = EncoderUtils.getSingleExpression(lambdaExpression);
		if(expression.getExpressionType() != ExpressionType.METHOD_INVOCATION) {
			throw new ConversionException("Invalid projection. See " + Projection.class.getName());
		}
		final MethodInvocation methodInvocation = (MethodInvocation) expression;
		final Method method = methodInvocation.getJavaMethod();
		final ProjectionType projectionType = getProjectionType(method);
		if(projectionType == null && methodInvocation.getParent().getExpressionType() == ExpressionType.LAMBDA_EXPRESSION && methodInvocation.getParent().getParent() == null) {
			throw new ConversionException("Invalid projection. See " + Projection.class.getName());
		}
		final List<Expression> arguments = methodInvocation.getArguments();
		if(arguments.size() != 1) {
			throw new ConversionException("Invalid projection: missing fields or fields not wrapped in an array");
		}
		final Expression argument = arguments.get(0);
		final ProjectionExpressionEncoder expressionEncoder = new ProjectionExpressionEncoder(
				lambdaExpression.getArgumentType(), writer, encoderContext, projectionType);
		argument.accept(expressionEncoder);
		writer.flush();
	}
	
	/**
	 * Finds the {@link ProjectionType} associated with the given Java {@link Method}, depending on whether it is annotated with {@link IncludeFields} ({@link ProjectionType#INCLUDE}) or with {@link ExcludeFields} ({@link ProjectionType#EXCLUDE}).
	 * @param method the method to analyze
	 * @return the corresponding {@link ProjectionType} or <code>null</code> if no relevant annotation was found on the given {@link Method}.
	 */
	private ProjectionType getProjectionType(final Method method) {
		if(method.getAnnotation(IncludeFields.class) != null) {
			return ProjectionType.INCLUDE;
		}
		if(method.getAnnotation(ExcludeFields.class) != null) {
			return ProjectionType.EXCLUDE;
		}
		return null;
	}

}
