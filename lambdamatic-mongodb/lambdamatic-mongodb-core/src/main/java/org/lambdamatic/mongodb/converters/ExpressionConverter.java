package org.lambdamatic.mongodb.converters;

import java.util.ArrayList;
import java.util.List;

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

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Converts a given {@link Expression} into a MongoDB {@link DBObject}.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 * @param <M>
 */
class ExpressionConverter<M> extends ExpressionVisitor {

	/** the usual logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ExpressionConverter.class);
	
	/** The {@link Metadata} associated with the domain class being queried. */
	private final Class<M> metadataClass;

	/** the result {@link DBObject}. */
	private BasicDBObject resultDbObject;

	/**
	 * Full constructor
	 * 
	 * @param metadataClass
	 *            the {@link Class} linked to the {@link Expression} to visit.
	 */
	ExpressionConverter(final Class<M> metadataClass) {
		this.metadataClass = metadataClass;
	}

	/**
	 * @return the {@link DBObject} built after this visitor visited an {@link Expression}.
	 */
	public DBObject getResult() {
		return this.resultDbObject;
	}

	@Override
	public boolean visit(Expression expr) {
		this.resultDbObject = new BasicDBObject();
		return super.visit(expr);
	}

	@Override
	public boolean visitInfixExpression(final InfixExpression expr) {
		final List<DBObject> operandsDbObjects = new ArrayList<>();
		for (Expression operand : expr.getOperands()) {
			final ExpressionConverter<M> operandConverter = new ExpressionConverter<>(metadataClass);
			operand.accept(operandConverter);
			operandsDbObjects.add(operandConverter.getResult());
		}
		switch (expr.getOperator()) {
		case CONDITIONAL_AND:
			for (DBObject operandDbObject : operandsDbObjects) {
				for (String key : operandDbObject.keySet()) {
					this.resultDbObject.append(key, operandDbObject.get(key));
				}
			}
			break;
		case CONDITIONAL_OR:
			this.resultDbObject.append("$or", operandsDbObjects.toArray(new DBObject[operandsDbObjects.size()]));
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
		if (expr.getMethodName().equals("equals")) {
			resultDbObject.put(key, values.get(0));
		}
		return false;
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
			LOGGER.trace("Skipping variable '{} ({})", expr.getName(), expr.getJavaType().getName());
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
					builder.append(fieldAnnotation.name());
				}
			}
		} catch (NoSuchFieldException | SecurityException cause) {
			throw new ConversionException("Failed to get field '" + fieldName + "' on class '"
					+ metadataClass.getName() + "'", cause);
		}
		return builder.toString();
	}

}
