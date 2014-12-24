package org.lambdamatic.mongodb.codecs;

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
	 * @see: http://docs.mongodb.org/manual/reference/operator/query/
	 */
	FilterExpressionEncoder(final Class<?> metadataClass, final BsonWriter writer) {
		this.metadataClass = metadataClass;
		this.writer = writer;
	}

	@Override
	public boolean visitInfixExpression(final InfixExpression expr) {
		switch (expr.getOperator()) {
		case CONDITIONAL_AND:
			// Syntax: { $and: [ { <expression1> }, { <expression2> } , ... , { <expressionN> } ] }
			for (Expression operand : expr.getOperands()) {
				final FilterExpressionEncoder operandEncoder = new FilterExpressionEncoder(metadataClass, writer);
				operand.accept(operandEncoder);
			}
			break;
		case CONDITIONAL_OR:
			// syntax: { $or: [ { <expression1> }, { <expression2> }, ... , { <expressionN> } ] }
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
		case EQUALS:
			// Syntax: {field: value} }
			write(writer, expr.getOperands().get(0), expr.getOperands().get(1));
			break;
		case NOT_EQUALS:
			// Syntax: {field: {$ne: value} }
			write(writer, expr.getOperands().get(0), expr.getOperands().get(1));
			break;
		default:
			throw new UnsupportedOperationException("Generating a query with '" + expr.getOperator() + "' is not supported yet (shame...)");
		}
		return false;
	}
	
	@Override
	public boolean visitMethodInvocationExpression(final MethodInvocation expr) {
		if(expr.getArguments().size() > 1) {
			throw new UnsupportedOperationException("Generating a BSON document from a method invocation with multiple arguments is not supported yet");
		}
		// FIXME: support other methods here
		if (expr.getMethodName().equals("equals")) {
			write(writer, expr.getSourceExpression(), expr.getArguments().get(0));
		}
		return false;
	}
	
	/**
	 * Writes the given key/value pair
	 * @param writer the {@link BsonWriter} to write into.
	 * @param key the key 
	 * @param value the value
	 */
	//FIXME: need to complete with more 'instanceof', and support for Enumerations, too.
	private void write(final BsonWriter writer, final Expression keyExpr, final Expression valueExpr) {
		final String key = extractKey(keyExpr);
		final Object value = (valueExpr != null) ? valueExpr.getValue() : null;
		if(value == null) {
			writer.writeNull(key);
		}
		else if(value instanceof Integer) {
			writer.writeInt32(key, (Integer)value);
		}
		else if(value instanceof Long) {
			writer.writeInt64(key, (Long)value);
		}
		else if(value instanceof String) {
			writer.writeString(key, (String)value);
		} else if (value instanceof Enum) {
			writer.writeString(key, ((Enum<?>)value).name());
		} else {
			throw new UnsupportedOperationException("Writing value of a '" + valueExpr.getExpressionType() + "' is not supported yet");
		}
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
	
}
