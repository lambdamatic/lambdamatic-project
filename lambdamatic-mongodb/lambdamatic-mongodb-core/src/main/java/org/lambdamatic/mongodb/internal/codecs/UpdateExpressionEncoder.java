/*******************************************************************************
 * Copyright (c) 2015 Red Hat.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.internal.codecs;

import java.lang.reflect.Method;
import java.util.List;

import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.lambdamatic.analyzer.ast.node.Assignment;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.Expression.ExpressionType;
import org.lambdamatic.analyzer.ast.node.ExpressionVisitor;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.lambdamatic.analyzer.ast.node.Operation;
import org.lambdamatic.mongodb.exceptions.ConversionException;
import org.lambdamatic.mongodb.metadata.MongoOperation;
import org.lambdamatic.mongodb.metadata.MongoOperator;

/**
 * @author xcoulon
 *
 */
public class UpdateExpressionEncoder extends ExpressionVisitor {

	private final Class<?> argumentType;

	private final BsonWriter writer;

	private final EncoderContext encoderContext;

	private final CodecRegistry codecRegistry;

	public UpdateExpressionEncoder(final Class<?> argumentType, final BsonWriter writer,
			final EncoderContext encoderContext, final CodecRegistry codecRegistry) {
		this.argumentType = argumentType;
		this.encoderContext = encoderContext;
		this.writer = writer;
		this.codecRegistry = codecRegistry;
	}

	@Override
	public boolean visitAssignment(final Assignment assignmentExpression) {
		final String documentFieldName = EncoderUtils.getDocumentFieldName(this.argumentType,
				assignmentExpression.getSource());
		if (assignmentExpression.getAssignedValue().getExpressionType() == ExpressionType.OPERATION) {
			final Operation operation = (Operation) assignmentExpression.getAssignedValue();
			switch (operation.getOperator()) {
			case ADD:
				writer.writeStartDocument("$inc");
				EncoderUtils.writeNamedExpression(writer, documentFieldName, operation.getRightOperand());
				writer.writeEndDocument();
				break;
			case SUBTRACT:
				writer.writeStartDocument("$inc");
				EncoderUtils.writeNamedExpression(writer, documentFieldName, operation.getRightOperand().inverse());
				writer.writeEndDocument();
				break;
			default:
				throw new ConversionException("Unsupported operator: " + operation.getOperator()
						+ ". Only addition and subtraction are supported.");
			}
		} else {
			writer.writeStartDocument("$set");
			EncoderUtils.writeNamedExpression(writer, documentFieldName, assignmentExpression.getAssignedValue());
			writer.writeEndDocument();
		}
		return false;
	}

	@Override
	public boolean visitMethodInvocationExpression(final MethodInvocation methodInvocation) {
		final Method method = methodInvocation.getJavaMethod();
		final MongoOperation annotation = method.getAnnotation(MongoOperation.class);
		if (annotation != null) {
			// FIXME: support other operands
			switch (annotation.value()) {
			case PUSH:
				writePush(methodInvocation.getSource(), methodInvocation.getArguments());
				break;
			default:
				throw new ConversionException("Unsupported MongoDB update operator: " + annotation.value());
			}
		} else {
			throw new ConversionException("Unsupported Java method: " + method.getName());
		}
		return false;
	}

	private void writePush(final Expression targetArray, final List<Expression> valueExpressions) {
		writer.writeStartDocument(MongoOperator.PUSH.getLiteral());
		for (Expression valueExpression : valueExpressions) {
			switch (valueExpression.getExpressionType()) {
			case OBJECT_INSTANCIATION:
			case OBJECT_INSTANCE:
				final String documentFieldName = EncoderUtils.getDocumentFieldName(argumentType, targetArray);
				writer.writeStartDocument(documentFieldName);
				try {
					EncoderUtils.encodeDomainObjectContent(this.writer, valueExpression.getValue(),
							this.encoderContext, this.codecRegistry);
				} catch (IllegalAccessException e) {
					throw new ConversionException(
							"Failed to encode argument during '" + MongoOperator.PUSH.getLiteral() + "' operation", e);
				}
				writer.writeEndDocument();
				break;
			default:
				throw new ConversionException(
						"Unsupported argument type: " + valueExpression.getExpressionType().name());
			}
		}
		writer.writeEndDocument();
	}

}
