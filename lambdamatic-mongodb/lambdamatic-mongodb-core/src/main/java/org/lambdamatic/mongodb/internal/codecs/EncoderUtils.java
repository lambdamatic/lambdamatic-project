/**
 * 
 */
package org.lambdamatic.mongodb.internal.codecs;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.ExpressionStatement;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.LambdaExpression;
import org.lambdamatic.analyzer.ast.node.ReturnStatement;
import org.lambdamatic.analyzer.ast.node.SimpleStatement;
import org.lambdamatic.analyzer.ast.node.Statement;
import org.lambdamatic.analyzer.exception.AnalyzeException;
import org.lambdamatic.mongodb.annotations.Document;
import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.exceptions.ConversionException;
import org.lambdamatic.mongodb.metadata.QueryField;
import org.lambdamatic.mongodb.types.geospatial.Location;

/**
 * Utility class for {@link Codec} encoders.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class EncoderUtils {

	/** Name of the field used to store the document id in MongoDB. */
	public static final String MONGOBD_DOCUMENT_ID = "_id";

	/** Name of the field handling the Java type associated with a document in MongoDB. */
	public static final String TARGET_CLASS_FIELD = "_targetClass";

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
			final String documentFieldName = getDocumentFieldName(metadataClass, fieldName);
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

	public static void encodeDomainObject(final BsonWriter writer, final Object domainObject,
			final EncoderContext encoderContext, final CodecRegistry codecRegistry) {
		try {
			writer.writeStartDocument();
			encodeDomainObjectContent(writer, domainObject, encoderContext, codecRegistry);
			writer.writeEndDocument();
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new ConversionException("Failed to convert following domain object to BSON document: " + domainObject,
					e);
		} finally {
			writer.flush();
		}
	}

	private static void encodeDomainObject(final BsonWriter writer, final String documentName, final Object domainObject,
			final EncoderContext encoderContext, final CodecRegistry codecRegistry) {
		try {
			writer.writeStartDocument(documentName);
			encodeDomainObjectContent(writer, domainObject, encoderContext, codecRegistry);
			writer.writeEndDocument();
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new ConversionException("Failed to convert following domain object to BSON document: " + domainObject,
					e);
		} finally {
			writer.flush();
		}
	}

	public static void encodeDomainObjectContent(final BsonWriter writer, final Object domainObject,
			final EncoderContext encoderContext, final CodecRegistry codecRegistry) throws IllegalAccessException {
		final Map<String, Field> bindings = BindingService.getInstance().getBindings(domainObject.getClass());
		// write the "_id" attribute first if the domainObject class is annotated with @Document (embedded documents
		// don't have such an '_id' field)
		if (domainObject.getClass().getAnnotation(Document.class) != null) {
			final Optional<Entry<String, Field>> idBinding = bindings.entrySet().stream()
					.filter(e -> BindingService.getInstance().isIdBinding(e)).findFirst();
			if (idBinding.isPresent()) {
				final Object idValue = BindingService.getInstance().getFieldValue(domainObject, idBinding.get().getValue());
				if (idValue == null) {
					final ObjectId generatedIdValue = new ObjectId();
					idBinding.get().getValue().set(domainObject, generatedIdValue);
					writeNamedValue(writer, MONGOBD_DOCUMENT_ID, generatedIdValue, encoderContext, codecRegistry);
				} else {
					writeNamedValue(writer, MONGOBD_DOCUMENT_ID, idValue, encoderContext, codecRegistry);
				}
			}
		}
		// write the technical/inner "_targetClassName" attribute
		writer.writeString(TARGET_CLASS_FIELD, domainObject.getClass().getName());
		// write other attributes
		bindings.entrySet().stream().filter(e -> !BindingService.getInstance().isIdBinding(e)).forEach(binding -> {
			writeNamedValue(writer, binding.getKey(), BindingService.getInstance().getFieldValue(domainObject, binding.getValue()),
					encoderContext, codecRegistry);
		});
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
	 * Writes the given named value
	 * 
	 * @param writer
	 *            the {@link BsonWriter} to use
	 * @param name
	 *            the name of the value
	 * @param value
	 *            the actual value
	 * @param encoderContext
	 *            the {@link EncoderContext}
	 * 
	 */
	// FIXME: move into EncoderUtils
	static void writeNamedValue(final BsonWriter writer, final String name, final Object value,
			final EncoderContext encoderContext, final CodecRegistry codecRegistry) {
		if (value == null) {
			// skip null named values
			return;
		}
		// Enum
		else if (value.getClass().isEnum()) {
			writer.writeString(name, ((Enum<?>) value).name());
		}
		// List and Sets
		else if (value instanceof Collection) {
			writer.writeStartArray(name);
			final Collection<?> values = (Collection<?>) value;
			values.stream().forEach(v -> writeValue(writer, v, encoderContext, codecRegistry));
			writer.writeEndArray();
		}
		// List and Sets
		else if (value.getClass().isArray()) {
			writer.writeStartArray(name);
			final Object[] values = (Object[]) value;
			Stream.of(values).forEach(v -> writeValue(writer, v, encoderContext, codecRegistry));
			writer.writeEndArray();
		}
		// other cases
		else {
			// FIXME: complete the switches to cover all writer.writeXXX methods
			final String attributeValueClassName = value.getClass().getName();
			switch (attributeValueClassName) {
			case "org.bson.types.ObjectId":
				writer.writeObjectId(MONGOBD_DOCUMENT_ID, (ObjectId) value);
				break;
			case "java.lang.Boolean": // also covers "boolean"
				final boolean booleanValue = (boolean) value;
				if (booleanValue != false) {
					writer.writeBoolean(name, booleanValue);
				}
				break;
			case "java.lang.Byte": // covers also "int" which is autoboxed
				final byte byteValue = (byte) value;
				if (byteValue != 0) {
					writer.writeInt32(name, byteValue);
				}
				break;
			case "java.lang.Short": // covers also "int" which is autoboxed
				final short shortValue = (short) value;
				if (shortValue != 0) {
					writer.writeInt32(name, shortValue);
				}
				break;
			case "java.lang.Character": // covers also "int" which is autoboxed
				final char charValue = (char) value;
				if (charValue != 0) {
					writer.writeInt32(name, charValue);
				}
				break;
			case "java.lang.Integer": // covers also "int" which is autoboxed
				final int intValue = (int) value;
				if (intValue != 0) {
					writer.writeInt32(name, intValue);
				}
				break;
			case "java.lang.Long": // covers also "long" which is autoboxed
				final long longValue = (long) value;
				if (longValue != 0) {
					writer.writeInt64(name, longValue);
				}
				break;
			case "java.lang.Float": // covers also "float" which is autoboxed
				final float floatValue = (float) value;
				if (floatValue != 0) {
					writer.writeDouble(name, floatValue);
				}
				break;
			case "java.lang.Double": // covers also "double" which is autoboxed
				final double doubleValue = (double) value;
				if (doubleValue != 0) {
					writer.writeDouble(name, doubleValue);
				}
				break;
			case "java.lang.String":
				writer.writeString(name, (String) value);
				break;
			case "java.util.Date":
				writer.writeDateTime(name, ((Date) value).getTime());
				break;
			case "org.lambdamatic.mongodb.types.geospatial.Location":
				writer.writeStartDocument(name);
				new LocationCodec(codecRegistry).encode(writer, (Location) value, encoderContext);
				writer.writeEndDocument();
				break;
			// check if this is an embedded document
			default:
				// FIXME: verify that class is annotated with @EmbeddedDocument
				encodeDomainObject(writer, name, value, encoderContext, codecRegistry);
			}
		}
	}
	
	/**
	 * Writes the given named value
	 * 
	 * @param writer
	 *            the {@link BsonWriter} to use
	 * @param value
	 *            the actual value
	 * @param encoderContext
	 *            the {@link EncoderContext}
	 * 
	 */
	static void writeValue(final BsonWriter writer, final Object value, final EncoderContext encoderContext,
			final CodecRegistry codecRegistry) {
		if (value == null) {
			writer.writeNull();
		} else if (value.getClass().isEnum()) {
			writer.writeString(((Enum<?>) value).name());
		} else if (value instanceof Collection) {
			writer.writeStartArray();
			final Collection<?> values = (Collection<?>) value;
			values.stream().forEach(v -> writeValue(writer, v, encoderContext, codecRegistry));
			writer.writeEndArray();
		} else {
			// FIXME: complete the switches to cover all writer.writeXXX methods
			final String attributeValueClassName = value.getClass().getName();
			switch (attributeValueClassName) {
			case "org.bson.types.ObjectId":
				writer.writeObjectId(MONGOBD_DOCUMENT_ID, (ObjectId) value);
				break;
			case "java.lang.Boolean": // also covers "boolean"
				final boolean booleanValue = (boolean) value;
				if (booleanValue != false) {
					writer.writeBoolean(booleanValue);
				}
				break;
			case "java.lang.Byte": // covers also "int" which is autoboxed
				final byte byteValue = (byte) value;
				if (byteValue != 0) {
					writer.writeInt32(byteValue);
				}
				break;
			case "java.lang.Short": // covers also "int" which is autoboxed
				final short shortValue = (short) value;
				if (shortValue != 0) {
					writer.writeInt32(shortValue);
				}
				break;
			case "java.lang.Character": // covers also "int" which is autoboxed
				final char charValue = (char) value;
				if (charValue != 0) {
					writer.writeInt32(charValue);
				}
				break;
			case "java.lang.Integer": // covers also "int" which is autoboxed
				final int intValue = (int) value;
				if (intValue != 0) {
					writer.writeInt32(intValue);
				}
				break;
			case "java.lang.Long": // covers also "long" which is autoboxed
				final long longValue = (long) value;
				if (longValue != 0) {
					writer.writeInt64(longValue);
				}
				break;
			case "java.lang.Float": // covers also "float" which is autoboxed
				final float floatValue = (float) value;
				if (floatValue != 0) {
					writer.writeDouble(floatValue);
				}
				break;
			case "java.lang.Double": // covers also "double" which is autoboxed
				final double doubleValue = (double) value;
				if (doubleValue != 0) {
					writer.writeDouble(doubleValue);
				}
				break;
			case "java.lang.String":
				writer.writeString((String) value);
				break;
			case "java.util.Date":
				writer.writeDateTime(((Date) value).getTime());
				break;
			case "org.lambdamatic.mongodb.types.geospatial.Location":
				writer.writeStartDocument();
				new LocationCodec(codecRegistry).encode(writer, (Location) value, encoderContext);
				writer.writeEndDocument();
				break;
			// assume this is an embedded document
			default:
				encodeDomainObject(writer, value, encoderContext, codecRegistry);
			}
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
				writeValue(writer, array[i]);
			}
			writer.writeEndArray();
		} else if (Collection.class.isAssignableFrom(value.getClass())) {
			writer.writeStartArray(name);
			final Collection<?> collection = (Collection<?>) value;
			for (Iterator<?> iterator = collection.iterator(); iterator.hasNext();) {
				writeValue(writer, iterator.next());
			}
			writer.writeEndArray();
		} else {
			throw new ConversionException("Writing value of a type '" + value.getClass() + "' is not supported yet");
		}
	}

}
