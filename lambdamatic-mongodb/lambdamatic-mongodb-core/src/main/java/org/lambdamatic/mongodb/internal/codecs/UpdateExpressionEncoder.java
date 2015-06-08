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

import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;
import org.lambdamatic.analyzer.ast.node.Assignment;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.Expression.ExpressionType;
import org.lambdamatic.analyzer.ast.node.ExpressionVisitor;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.mongodb.exceptions.ConversionException;

/**
 * @author xcoulon
 *
 */
public class UpdateExpressionEncoder extends ExpressionVisitor {

	private final Class<?> argumentType;

	private final String argumentName;

	private final BsonWriter writer;

	private final EncoderContext encoderContext;

	public UpdateExpressionEncoder(final Class<?> argumentType, final String argumentName, final BsonWriter writer,
			final EncoderContext encoderContext) {
		this.argumentName = argumentName;
		this.argumentType = argumentType;
		this.encoderContext = encoderContext;
		this.writer = writer;
	}

	@Override
	public boolean visitAssignment(final Assignment assignmentExpression) {
		writer.writeStartDocument("$set");
		final String documentFieldName = getDocumentFieldName(assignmentExpression.getSource());
		if(assignmentExpression.getAssignedValue().getExpressionType() == ExpressionType.OPERATION) {
			throw new ConversionException("Unsupported operation in assignment. Needs to be implemented, though...");
		}
		EncoderUtils.writeNamedExpression(writer, documentFieldName, assignmentExpression.getAssignedValue());
		writer.writeEndDocument();
		return false;
	}

	/**
	 * @param source
	 *            the source {@link Expression} that indicates with document field is to update
	 * @return the document field
	 * @throws ConversionException if the given {@code source} is of an unexpected type.
	 */
	private String getDocumentFieldName(final Expression source) {
		switch (source.getExpressionType()) {
		case FIELD_ACCESS:
			return ((FieldAccess) source).getFieldName();
		default:
			throw new ConversionException("Unexpected expression type to indicate the document field to update:"
					+ source + " (" + source.getExpressionType().name() + ")");
		}
	}

}
