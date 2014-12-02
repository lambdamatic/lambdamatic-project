package org.lambdamatic.mongodb.codecs;

import java.util.ArrayList;
import java.util.List;

import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonDocumentWriter;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.lambdamatic.FilterExpression;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.ExpressionVisitor;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.InfixExpression;
import org.lambdamatic.analyzer.ast.node.LocalVariable;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.lambdamatic.analyzer.ast.node.StringLiteral;
import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes a given {@link Expression} into a MongoDB {@link BsonWriter}.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 * @param <M>
 */
class FilterExpressionEncoder extends ExpressionVisitor {

	/** the usual logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(FilterExpressionEncoder.class);
	
	/** The {@link Metadata} class associated with the domain class being queried. */
	private final Class<?> metadataClass;

	/** The {@link BsonWriter} to use. */
	private final BsonWriter writer;
	
	/**
	 * Full constructor
	 * 
	 * @param metadataClass
	 *            the {@link Class} linked to the {@link Expression} to visit.
	 * @param writer
	 *            the {@link BsonWriter} in which the {@link FilterExpression}
	 *            representation will be written.
	 */
	FilterExpressionEncoder(final Class<?> metadataClass, final BsonWriter writer) {
		this.metadataClass = metadataClass;
		this.writer = writer;
	}

	@Override
	public boolean visit(Expression expr) {
		return super.visit(expr);
	}

	@Override
	public boolean visitInfixExpression(final InfixExpression expr) {
		switch (expr.getOperator()) {
		case CONDITIONAL_AND:
			for (Expression operand : expr.getOperands()) {
				final FilterExpressionEncoder operandEncoder = new FilterExpressionEncoder(metadataClass, writer);
				operand.accept(operandEncoder);
			}
			break;
		case CONDITIONAL_OR:
			writer.writeStartArray("$or");
			for (Expression operand : expr.getOperands()) {
				final BsonDocument operandDocument = new BsonDocument(); 
				final BsonWriter operandBsonWriter = new BsonDocumentWriter(operandDocument);
				final FilterExpressionEncoder operandEncoder = new FilterExpressionEncoder(metadataClass, operandBsonWriter);
				operandBsonWriter.writeStartDocument();
				operand.accept(operandEncoder);
				operandBsonWriter.writeEndDocument();
				final BsonReader operandBsonReader = new BsonDocumentReader(operandDocument);
				writer.pipe(operandBsonReader);
			}
			writer.writeEndArray();
			break;
		default:
			break;
		}
		return false;
	}
	
	@Override
	public boolean visitMethodInvocationExpression(final MethodInvocation expr) {
		final String key = extractKey(expr.getSourceExpression());
		final List<Object> values = new ArrayList<>();
		for (Expression argumentExpr : expr.getArguments()) {
			values.add(extractValue(argumentExpr));
		}
		// FIXME: support other methods here
		if (expr.getMethodName().equals("equals")) {
			BsonWriterUtil.write(writer, key, values.get(0));
		}
		return false;
	}

	private String extractKey(final Expression expr) {
		switch (expr.getExpressionType()) {
		case LOCAL_VARIABLE:
			return extractKey((LocalVariable) expr);
		case FIELD_ACCESS:
			return extractKey((FieldAccess) expr);
		default:
			return null;
		}
	}

	private String extractKey(final LocalVariable expr) {
		if (expr.getType().equals(metadataClass)) {
			LOGGER.trace("Skipping variable '{}' ({})", expr.getName(), expr.getJavaType().getName());
			return null;
		}
		return expr.getName();
	}

	private String extractKey(final FieldAccess expr) {
 		final StringBuilder builder = new StringBuilder();
		final String target = extractKey(expr.getSourceExpression());
		if (target != null) {
			builder.append(target).append('.');
		}
		final String fieldName = expr.getFieldName();
		try {
			final java.lang.reflect.Field field = metadataClass.getField(fieldName);
			if (field != null) {
				final DocumentField fieldAnnotation = field.getAnnotation(DocumentField.class);
				if (fieldAnnotation != null) {
					final String annotatedFieldName = fieldAnnotation.name();
					if(annotatedFieldName != null&& !annotatedFieldName.isEmpty()) {
						builder.append(annotatedFieldName);
					} else {
						builder.append(field.getName());
					}
				}
			}
		} catch (NoSuchFieldException | SecurityException cause) {
			throw new ConversionException("Failed to get field '" + fieldName + "' on class '"
					+ metadataClass.getName() + "'", cause);
		}
		return builder.toString();
	}
	
	private String extractValue(final Expression expr) {
		switch (expr.getExpressionType()) {
		case STRING_LITERAL:
			return extractValue((StringLiteral) expr);
		default:
			return null;
		}
	}

	private String extractValue(final StringLiteral expr) {
		return expr.getValue();
	}

}
