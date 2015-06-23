/**
 * 
 */
package org.lambdamatic.mongodb.internal.codecs;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.ExpressionStatement;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.LambdaExpression;
import org.lambdamatic.analyzer.ast.node.ReturnStatement;
import org.lambdamatic.analyzer.ast.node.SimpleStatement;
import org.lambdamatic.analyzer.ast.node.Statement;
import org.lambdamatic.analyzer.exception.AnalyzeException;
import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.exceptions.ConversionException;
import org.lambdamatic.mongodb.metadata.QueryField;

/**
 * Utility class for {@link Codec} encoders.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class EncoderUtils {

	/**
	 * Private constructor of the utility class.
	 */
	private EncoderUtils() {
	}

	/**
	 * Extract the <strong>single</strong> {@link Expression} from the given {@link LambdaExpression#getBody()},
	 * assuming that there's a single {@link Statement} which is a {@link ReturnStatement}, otherwise, throws a
	 * {@link ConversionException}.
	 * 
	 * @param expression
	 *            the {@link LambdaExpression} to analyze
	 * @return the single {@link Statement}'s {@link Expression}
	 * @throws ConversionException
	 *             if the {@link LambdaExpression#getBody()} did not contain a single {@link ReturnStatement}
	 */
	public static Expression getSingleExpression(final LambdaExpression expression) {
		if (expression.getBody().isEmpty()) {
			throw new ConversionException(
					"The given LambdaExpression body does not contain any statement but one was expected.");
		}
		if (expression.getBody().size() != 1) {
			throw new ConversionException(
					"The given LambdaExpression body should contain a single statement:\n" + expression.getBody());
		}
		final Statement statement = expression.getBody().get(0);
		switch (statement.getStatementType()) {
		case RETURN_STMT:
		case EXPRESSION_STMT:
			return ((SimpleStatement) statement).getExpression();
		default:
			throw new AnalyzeException("Unsupported Statement type:" + statement.getStatementType());
		}
	}

	/**
	 * Extract <strong>all</strong> the {@link Expression} from the given {@link LambdaExpression#getBody()}, assuming
	 * they are {@link ExpressionStatement} or {@link ReturnStatement}.
	 * 
	 * @param expression
	 *            the {@link LambdaExpression} to analyze
	 * @return the {@link List} of {@link Statement}'s {@link Expression}
	 * @throws ConversionException
	 *             if the {@link LambdaExpression#getBody()} contains other types of {@link Statement}
	 */
	public static List<Expression> getAllExpressions(final LambdaExpression lambdaExpression) {
		return lambdaExpression.getBody().stream().map(s -> {
			switch (s.getStatementType()) {
			case EXPRESSION_STMT:
				return ((ExpressionStatement) s).getExpression();
			case RETURN_STMT:
				return ((ReturnStatement) s).getExpression();
			default:
				throw new ConversionException("Unexpected type of statement: " + s.getStatementType().name());
			}
		}).collect(Collectors.toList());
	}

	/**
	 * Finds and returns the document field associated with the given {@link Expression} expression
	 * 
	 * @param metadataClass
	 *            the Metadata class the FieldAccess belongs to
	 * @param expression
	 *            the {@link Expression} to analyze
	 * @return the document field name or <code>null</code> if the given {@link Expression} is not a valid one.
	 */
	public static String getDocumentFieldName(final Class<?> metadataClass, final Expression expression) {
		if (expression == null) {
			return null;
		}
		switch (expression.getExpressionType()) {
		case FIELD_ACCESS:
			final FieldAccess fieldAccess = (FieldAccess) expression;
			final String fieldName = fieldAccess.getFieldName();
			final String documentFieldName = EncoderUtils.getDocumentFieldName(metadataClass, fieldName);
			final String documentFieldParentName = getDocumentFieldName(metadataClass, fieldAccess.getSource());
			if (documentFieldParentName != null) {
				return documentFieldParentName + "." + documentFieldName;
			} else {
				return documentFieldName;
			}

		default:
			return null;
		}
	}

	/**
	 * Finds and returns the MongoDB Document Field associated with the given {@link QueryField}.
	 * 
	 * @param queryField
	 *            the {@link QueryField} to analyze
	 * @return the field name associated with the given {@link QueryField}
	 */
	public static String getDocumentFieldName(final Class<?> queryMetadataClass, final String fieldName) {
		try {
			final java.lang.reflect.Field field = queryMetadataClass.getField(fieldName);
			if (field != null) {
				final DocumentField fieldAnnotation = field.getAnnotation(DocumentField.class);
				if (fieldAnnotation != null) {
					final String annotatedFieldName = fieldAnnotation.name();
					if (annotatedFieldName != null && !annotatedFieldName.isEmpty()) {
						return annotatedFieldName;
					}
				}
			}
		} catch (NoSuchFieldException | SecurityException e) {
			throw new ConversionException("Unable to find the DocumentField annotation of field '" + fieldName
					+ "' in class " + queryMetadataClass.getName(), e);
		}
		throw new ConversionException("Unable to find the DocumentField annotation of field '" + fieldName
				+ "' in class " + queryMetadataClass.getName());
	}

	/**
	 * Writes the given unnamed value.
	 * 
	 * @param value
	 *            the value to write
	 */
	public static void writeValue(final BsonWriter writer, final Object value) {
		if (value == null) {
			writer.writeNull();
		} else if (value instanceof Integer) {
			writer.writeInt32((Integer) value);
		} else if (value instanceof Long) {
			writer.writeInt64((Long) value);
		} else if (value instanceof Character) {
			writer.writeString(((Character) value).toString());
		} else if (value instanceof String) {
			writer.writeString((String) value);
		} else if (value instanceof Date) {
			writer.writeDateTime(((Date) value).getTime());
		} else if (value instanceof Enum) {
			writer.writeString(((Enum<?>) value).name());
		} else if (value.getClass().isArray()) {
			writer.writeStartArray();
			final Object[] array = (Object[]) value;
			for (int i = 0; i < array.length; i++) {
				writeValue(writer, value);
			}
			writer.writeEndArray();
		} else {
			throw new UnsupportedOperationException(
					"Writing value of a '" + value.getClass() + "' is not supported yet");
		}
	}

	/**
	 * Writes the given named {@link Expression}
	 * 
	 * @param name
	 *            the Expression name
	 * @param valueExpr
	 *            the Expression itself
	 */
	public static void writeNamedExpression(final BsonWriter writer, final String name, final Expression valueExpr) {
		// LambdaExpressions have to be treated differently
		final Object value = (valueExpr != null) ? valueExpr.getValue() : null;
		if (value == null) {
			writer.writeNull(name);
		} else if (value instanceof Integer) {
			writer.writeInt32(name, (Integer) value);
		} else if (value instanceof Long) {
			writer.writeInt64(name, (Long) value);
		} else if (value instanceof Character) {
			writer.writeString(name, ((Character) value).toString());
		} else if (value instanceof String) {
			writer.writeString(name, (String) value);
		} else if (value instanceof Enum) {
			writer.writeString(name, ((Enum<?>) value).name());
		} else if (value instanceof Date) {
			writer.writeDateTime(name, ((Date) value).getTime());
		} else if (value.getClass().isArray()) {
			writer.writeStartArray(name);
			final Object[] array = (Object[]) value;
			for (int i = 0; i < array.length; i++) {
				EncoderUtils.writeValue(writer, array[i]);
			}
			writer.writeEndArray();
		} else if (Collection.class.isAssignableFrom(value.getClass())) {
			writer.writeStartArray(name);
			final Collection<?> collection = (Collection<?>) value;
			for (Iterator<?> iterator = collection.iterator(); iterator.hasNext();) {
				EncoderUtils.writeValue(writer, iterator.next());
			}
			writer.writeEndArray();
		} else {
			throw new ConversionException("Writing value of a type '" + value.getClass() + "' is not supported yet");
		}
	}

}
