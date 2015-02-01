package org.lambdamatic.mongodb.codecs;

import static org.lambdamatic.mongodb.codecs.MongoOperators.NOT_EQUALS;

import java.util.List;
import java.util.stream.Collectors;

import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonDocumentWriter;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;
import org.lambdamatic.FilterExpression;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.Expression.ExpressionType;
import org.lambdamatic.analyzer.ast.node.ExpressionVisitor;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.InfixExpression;
import org.lambdamatic.analyzer.ast.node.LocalVariable;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.metadata.Metadata;
import org.lambdamatic.mongodb.types.geospatial.Location;
import org.lambdamatic.mongodb.types.geospatial.Polygon;
import org.lambdamatic.mongodb.types.geospatial.Polygon.Ring;
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

	/**
	 * The {@link Metadata} class associated with the domain class being
	 * queried.
	 */
	private final Class<?> metadataClass;

	/** The {@link BsonWriter} to use. */
	private final BsonWriter writer;

	/** The {@link EncoderContext} to use.*/
	private final EncoderContext encoderContext;
	
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
	FilterExpressionEncoder(final Class<?> metadataClass, final BsonWriter writer, final EncoderContext encoderContext) {
		this.metadataClass = metadataClass;
		this.writer = writer;
		this.encoderContext = encoderContext;
	}

	@Override
	public boolean visitInfixExpression(final InfixExpression expr) {
		switch (expr.getOperator()) {
		case CONDITIONAL_AND:
			// Syntax: { $and: [ { <expression1> }, { <expression2> } , ... , {
			// <expressionN> } ] }
			for (Expression operand : expr.getOperands()) {
				final FilterExpressionEncoder operandEncoder = new FilterExpressionEncoder(
						metadataClass, writer, this.encoderContext);
				operand.accept(operandEncoder);
			}
			break;
		case CONDITIONAL_OR:
			// syntax: { $or: [ { <expression1> }, { <expression2> }, ... , {
			// <expressionN> } ] }
			writer.writeStartArray("$or");
			for (Expression operand : expr.getOperands()) {
				final BsonDocument operandDocument = new BsonDocument();
				final BsonWriter operandBsonWriter = new BsonDocumentWriter(operandDocument);
				final FilterExpressionEncoder operandEncoder = new FilterExpressionEncoder(
						metadataClass, operandBsonWriter, this.encoderContext);
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
			writeEquals(expr.getOperands().get(0), expr.getOperands().get(1));
			break;
		case NOT_EQUALS:
			// Syntax: {field: {$ne: value} }
			// FIXME: this needs testing
			writeNotEquals(expr.getOperands().get(0), expr.getOperands().get(1));
			break;
		default:
			throw new UnsupportedOperationException("Generating a query with '" + expr.getOperator()
					+ "' is not supported yet (shame...)");
		}
		return false;
	}

	@Override
	public boolean visitMethodInvocationExpression(final MethodInvocation expr) {
		if (expr.getArguments().size() > 1) {
			throw new ConversionException(
					"Generating a BSON document from a method invocation with multiple arguments is not supported yet");
		}
		// FIXME: support other methods here
		if (expr.getMethodName().equals("equals")) {
			writeEquals(expr.getSourceExpression(), expr.getArguments().get(0));
		} else if (expr.getMethodName().equals("geoWithin")) {
			writeGeoWithin(expr.getSourceExpression(), expr.getArguments());
		}
		return false;
	}

	/**
	 * Encodes the given <code>sourceExpression</code> and <code>arguments</code> into a geoWithin query member, such as:
	 * <pre>
	 * loc: {
     *   $geoWithin: {
     *     $geometry: {
     *       type : "Polygon" ,
     *       coordinates: [ 
     *         [ [ 0, 0 ], [ 3, 6 ], [ 6, 1 ], [ 0, 0 ] ] 
     *       ]
     *     }
     *   }
     * }
	 * 
	 * </pre>
	 * @param sourceExpression
	 * @param arguments a list of Array of {@link Location} or {@link Polygon}
	 */
	private void writeGeoWithin(final Expression sourceExpression, final List<Expression> arguments) {
		if(arguments == null || arguments.isEmpty()) {
			throw new ConversionException("Cannot generate geoWithin query with empty arguments");
		}
		if(sourceExpression.getExpressionType() != ExpressionType.FIELD_ACCESS) {
			throw new ConversionException("Did not expect to generate a 'geoWithin' query from a element of type " + sourceExpression.getExpressionType());
		}
		final List<Object> argumentValues = arguments.stream().map(e -> e.getValue()).collect(Collectors.toList());
		if(argumentValues.size() == 1) {
			final Object argument = argumentValues.get(0);
			// argument is an instance of Polygon
			if(argument instanceof Polygon) {
				final Polygon polygon = (Polygon) argument;
				writer.writeStartDocument(((FieldAccess)sourceExpression).getFieldName());
				encodePolygon(writer, polygon);
				writer.writeEndDocument();
			} 
			// argument is an array of Location
			else if(argument.getClass().isArray() && argument.getClass().getComponentType().equals(Location.class)) {
				final Location[] locations = (Location[]) argument;
				final Polygon polygon = new Polygon(locations);
				writer.writeStartDocument(((FieldAccess)sourceExpression).getFieldName());
				encodePolygon(writer, polygon);
				writer.writeEndDocument();
			} else if(List.class.isInstance(argument) && listContains((List<?>)argument, Location.class)) {
				@SuppressWarnings("unchecked")
				final List<Location> locations = (List<Location>) argument;
				final Polygon polygon = new Polygon(locations);
				writer.writeStartDocument(((FieldAccess)sourceExpression).getFieldName());
				encodePolygon(writer, polygon);
				writer.writeEndDocument();
			}
		}
	}

	/**
	 * Checks that the given list contains elements of the given element type
	 * @param list
	 * @param elementClass
	 * @return {@code true} if the list contains elements of the given type, {@code false} otherwise.
	 */
	private boolean listContains(final List<?> list, final Class<?> elementClass) {
		return !list.isEmpty() && list.get(0).getClass().equals(elementClass);
	}

	/**
	 * Encodes the given {@link Polygon} into the given {@link BsonWriter}. This method assumes that the {@link BsonDocument} already exists.
	 * The resulting document will have the following form:
	 * { $geoWithin: 
	 *  { $geometry: 
	 *   { type: 'Polygon', 
	 *     coordinates: [ [0,0], [0,1], [1,1], [1,0], [0,0] ]
	 *   }  
	 *  }  
	 * }
	 *  
	 * @see org.bson.codecs.Encoder#encode(org.bson.BsonWriter, java.lang.Object, org.bson.codecs.EncoderContext)
	 */
	private void encodePolygon(final BsonWriter writer, final Polygon polygon) {
		writer.writeStartDocument("$geoWithin");
		writer.writeStartDocument("$geometry");
		writer.writeString("type", "Polygon");
		writer.writeStartArray("coordinates");
		for(Ring ring : polygon.getRings()) {
			writer.writeStartArray();
			for(Location point : ring.getPoints()) {
				writer.writeStartArray();
				writer.writeDouble(point.getLatitude());
				writer.writeDouble(point.getLongitude());
				writer.writeEndArray();
			}
			writer.writeEndArray(); // ring
		}
		writer.writeEndArray(); // coordinates
		writer.writeEndDocument(); // $geometry
		writer.writeEndDocument(); // $geoWithin
	}

	/**
	 * Writes the equals query member for the given key/value pair
	 * <p>
	 * Eg: <code>{key: value}</code>
	 * </p>
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	private void writeEquals(final Expression keyExpr, final Expression valueExpr) {
		final String key = extractKey(keyExpr);
		final Object value = (valueExpr != null) ? valueExpr.getValue() : null;
		if (value == null) {
			writer.writeNull(key);
		} else if (value instanceof Integer) {
			writer.writeInt32(key, (Integer) value);
		} else if (value instanceof Long) {
			writer.writeInt64(key, (Long) value);
		} else if (value instanceof String) {
			writer.writeString(key, (String) value);
		} else if (value instanceof Enum) {
			writer.writeString(key, ((Enum<?>) value).name());
		} else {
			throw new UnsupportedOperationException("Writing value of a '" + valueExpr.getExpressionType()
					+ "' is not supported yet");
		}
	}

	/**
	 * Writes the equals query member for the given key/value pair
	 * <p>
	 * Eg: <code>{key: {$ne: value}}</code>
	 * </p>
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	private void writeNotEquals(final Expression keyExpr, final Expression valueExpr) {
		final String key = extractKey(keyExpr);
		writer.writeStartDocument(key);
		final Object value = (valueExpr != null) ? valueExpr.getValue() : null;
		if (value == null) {
			writer.writeNull(NOT_EQUALS);
		} else if (value instanceof Integer) {
			writer.writeInt32(NOT_EQUALS, (Integer) value);
		} else if (value instanceof Long) {
			writer.writeInt64(NOT_EQUALS, (Long) value);
		} else if (value instanceof String) {
			writer.writeString(NOT_EQUALS, (String) value);
		} else if (value instanceof Enum) {
			writer.writeString(NOT_EQUALS, ((Enum<?>) value).name());
		} else {
			throw new UnsupportedOperationException("Writing value of a '" + valueExpr.getExpressionType()
					+ "' is not supported yet");
		}
		writer.writeEndDocument();
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
					if (annotatedFieldName != null && !annotatedFieldName.isEmpty()) {
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
