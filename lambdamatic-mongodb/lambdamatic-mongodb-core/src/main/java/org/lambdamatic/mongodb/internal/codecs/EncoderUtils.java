/**
 * 
 */
package org.lambdamatic.mongodb.internal.codecs;

import org.bson.codecs.Codec;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.exceptions.ConversionException;
import org.lambdamatic.mongodb.metadata.ProjectionField;
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
	 * Finds and returns the document field associated with the given {@link Expression} expression
	 * 
	 * @param metadataClass
	 *            the Metadata class the FieldAccess belongs to
	 * @param expression
	 *            the {@link Expression} to analyze
	 * @return the document field
	 */
	private static String getDocumentFieldName(final Class<?> metadataClass, final Expression expression) {
		if (expression == null) {
			return "";
		}
		switch (expression.getExpressionType()) {
		case FIELD_ACCESS:
			final String fieldName = ((FieldAccess) expression).getFieldName();
			final String documentFieldName = EncoderUtils.getDocumentFieldName(metadataClass, fieldName);
			return getDocumentFieldName(metadataClass, expression.getParent()) + documentFieldName + ".";
		default:
			return "";
		}
	}

	/**
	 * Finds and returns the document field associated with the given {@link FieldAccess} expression
	 * 
	 * @param metadataClass
	 *            the Metadata class the FieldAccess belongs to
	 * @param fieldAccess
	 *            the {@link FieldAccess} to analyze
	 * @return the document field
	 */
	public static String getDocumentFieldName(final Class<?> metadataClass, final FieldAccess fieldAccess) {
		final String fieldName = fieldAccess.getFieldName();
		final String parentExpressionName = EncoderUtils.getDocumentFieldName(metadataClass, fieldAccess.getParent());
		final String documentFieldName = parentExpressionName + EncoderUtils.getDocumentFieldName(metadataClass, fieldName);
		return documentFieldName;
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
	 * 
	 * @param projectionField
	 * @return
	 */
	public String getDocumentFieldName(final ProjectionField projectionField) {
		return null;
	}

}
