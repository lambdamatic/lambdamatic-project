package org.lambdamatic.mongodb.internal.codecs;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonDocumentWriter;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;
import org.lambdamatic.SerializablePredicate;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.Expression.ExpressionType;
import org.lambdamatic.analyzer.ast.node.ExpressionVisitor;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.InfixExpression;
import org.lambdamatic.analyzer.ast.node.LocalVariable;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.exceptions.ConversionException;
import org.lambdamatic.mongodb.metadata.MongoOperation;
import org.lambdamatic.mongodb.metadata.MongoOperator;
import org.lambdamatic.mongodb.metadata.QueryMetadata;
import org.lambdamatic.mongodb.types.geospatial.Location;
import org.lambdamatic.mongodb.types.geospatial.Polygon;
import org.lambdamatic.mongodb.types.geospatial.Polygon.Ring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes a given {@link Expression} into a MongoDB {@link BsonWriter}.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 * @param <M>
 */
class FilterExpressionEncoder extends ExpressionVisitor {

	/** the usual logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(FilterExpressionEncoder.class);

	/**
	 * The {@link QueryMetadata} class associated with the domain class being queried.
	 */
	private final Class<?> queryMetadataClass;

	/** The {@link BsonWriter} to use. */
	private final BsonWriter writer;

	/** The {@link EncoderContext} to use. */
	private final EncoderContext encoderContext;

	/**
	 * Full constructor
	 * 
	 * @param queryMetadataClass
	 *            the {@link Class} linked to the {@link Expression} to visit.
	 * @param writer
	 *            the {@link BsonWriter} in which the {@link SerializablePredicate} representation will be written.
	 * @see: http://docs.mongodb.org/manual/reference/operator/query/
	 */
	FilterExpressionEncoder(final Class<?> queryMetadataClass, final BsonWriter writer,
			final EncoderContext encoderContext) {
		this.queryMetadataClass = queryMetadataClass;
		this.writer = writer;
		this.encoderContext = encoderContext;
	}

	@Override
	public boolean visitInfixExpression(final InfixExpression expr) {
		writer.writeStartDocument();
		switch (expr.getOperator()) {
		case CONDITIONAL_AND:
			// Syntax: { $and: [ { <expression1> }, { <expression2> } , ... , {
			// <expressionN> } ] }
			writeLogicalOperation(MongoOperator.AND, expr.getOperands());
			break;
		case CONDITIONAL_OR:
			// syntax: { $or: [ { <expression1> }, { <expression2> }, ... , {
			// <expressionN> } ] }
			writeLogicalOperation(MongoOperator.OR, expr.getOperands());
			break;
		case EQUALS:
			// eg: int == 3
			// Syntax: {field: value} }
			writeOperation(MongoOperator.EQUALS, expr.getOperands().get(0), expr.getOperands().get(1),
					expr.isInverted());
			break;
		case NOT_EQUALS:
			// eg: int != 3
			// Syntax: {field: {$ne: value} }
			writeOperation(MongoOperator.NOT_EQUALS, expr.getOperands().get(0), expr.getOperands().get(1),
					expr.isInverted());
			break;
		default:
			throw new UnsupportedOperationException("Generating a query with '" + expr.getOperator()
					+ "' is not supported yet (shame...)");
		}
		writer.writeEndDocument();
		writer.flush();
		return false;
	}

	/**
	 * Writes a logical operation of the following form:
	 * 
	 * <pre>
	 * { $operator: [ { <expression1> }, { <expression2> } , ... , {<expressionN> } ] }
	 * </pre>
	 * 
	 * @param operator
	 *            the operator to write 
	 * @param operands
	 *            the operands to write
	 */
	private void writeLogicalOperation(final MongoOperator operator, List<Expression> operands) {
		writer.writeStartArray(operator.getLiteral());
		for (Expression operand : operands) {
			final BsonDocument operandDocument = new BsonDocument();
			final BsonWriter operandBsonWriter = new BsonDocumentWriter(operandDocument);
			final FilterExpressionEncoder operandEncoder = new FilterExpressionEncoder(queryMetadataClass,
					operandBsonWriter, this.encoderContext);
			operand.accept(operandEncoder);
			final BsonReader operandBsonReader = new BsonDocumentReader(operandDocument);
			writer.pipe(operandBsonReader);
		}
		writer.writeEndArray();
	}

	@Override
	public boolean visitMethodInvocationExpression(final MethodInvocation methodInvocation) {
		if (methodInvocation.getArguments().size() > 1) {
			throw new ConversionException(
					"Generating a BSON document from a method invocation with multiple arguments is not supported yet");
		}
		writer.writeStartDocument();
		// FIXME: support other operands
		// FIXME: use $not: http://docs.mongodb.org/manual/reference/operator/query/not/#op._S_not
		final Method method = methodInvocation.getJavaMethod();
		final MongoOperation annotation = method.getAnnotation(MongoOperation.class);
		if (annotation != null) {
			switch (annotation.value()) {
			case GEO_WITHIN:
				writeGeoWithin(methodInvocation.getSourceExpression(), methodInvocation.getArguments(),
						methodInvocation.isInverted());
				break;
			default:
				writeOperation(annotation.value(), methodInvocation.getSourceExpression(), methodInvocation
						.getArguments().get(0), methodInvocation.isInverted());
			}
		}
		writer.writeEndDocument();
		return false;
	}

	/**
	 * Writes the operation for the given key/value pair
	 * <p>
	 * Eg: <code>{key: value}</code>
	 * </p>
	 * 
	 * @param operator
	 *            the operator
	 * @param keyExpr
	 *            the key expression
	 * @param valueExpr
	 *            the value expression
	 * @param inverted
	 *            if the operation is inverted (ie, using the {@link MongoOperator#NOT} operand
	 * @see MongoOperator
	 */
	private void writeOperation(final MongoOperator operator, final Expression keyExpr, final Expression valueExpr,
			final boolean inverted) {
		final String key = extractKey(keyExpr);
		// simplified formula for EQUALS operator (when not inverted)
		if (operator == MongoOperator.EQUALS && !inverted) {
			writeNamedValue(key, valueExpr);
		} else {
			writer.writeStartDocument(key);
			if (inverted) {
				writer.writeStartDocument(MongoOperator.NOT.getLiteral());
				writeNamedValue(operator.getLiteral(), valueExpr);
				writer.writeEndDocument();
			} else {
				writeNamedValue(operator.getLiteral(), valueExpr);
			}
			writer.writeEndDocument();
		}
	}

	private void writeNamedValue(final String name, final Expression valueExpr) {
		final Object value = (valueExpr != null) ? valueExpr.getValue() : null;
		if (value == null) {
			writer.writeNull(name);
		} else if (value instanceof Integer) {
			writer.writeInt32(name, (Integer) value);
		} else if (value instanceof Long) {
			writer.writeInt64(name, (Long) value);
		} else if (value instanceof String) {
			writer.writeString(name, (String) value);
		} else if (value instanceof Enum) {
			writer.writeString(name, ((Enum<?>) value).name());
		} else {
			throw new UnsupportedOperationException("Writing value of a '" + valueExpr.getExpressionType()
					+ "' is not supported yet");
		}
	}

	/**
	 * Encodes the given <code>sourceExpression</code> and <code>arguments</code> into a geoWithin query member, such
	 * as:
	 * 
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
	 * 
	 * @param sourceExpression
	 * @param arguments
	 *            a list of Array of {@link Location} or {@link Polygon}
	 * @param inverted
	 *            if the operation is inverted (ie, using the {@link MongoOperator#NOT} operand
	 */
	private void writeGeoWithin(final Expression sourceExpression, final List<Expression> arguments,
			final boolean inverted) {
		if (arguments == null || arguments.isEmpty()) {
			throw new ConversionException("Cannot generate geoWithin query with empty arguments");
		}
		if (sourceExpression.getExpressionType() != ExpressionType.FIELD_ACCESS) {
			throw new ConversionException("Did not expect to generate a 'geoWithin' query from a element of type "
					+ sourceExpression.getExpressionType());
		}
		final List<Object> argumentValues = arguments.stream().map(e -> e.getValue()).collect(Collectors.toList());
		if (argumentValues.size() == 1) {
			final Object argument = argumentValues.get(0);
			// argument is an instance of Polygon
			if (argument instanceof Polygon) {
				final Polygon polygon = (Polygon) argument;
				writer.writeStartDocument(((FieldAccess) sourceExpression).getFieldName());
				encodePolygon(writer, polygon);
				writer.writeEndDocument();
			}
			// argument is an array of Location
			else if (argument.getClass().isArray() && argument.getClass().getComponentType().equals(Location.class)) {
				final Location[] locations = (Location[]) argument;
				final Polygon polygon = new Polygon(locations);
				writer.writeStartDocument(((FieldAccess) sourceExpression).getFieldName());
				encodePolygon(writer, polygon);
				writer.writeEndDocument();
			} else if (List.class.isInstance(argument) && listContains((List<?>) argument, Location.class)) {
				@SuppressWarnings("unchecked")
				final List<Location> locations = (List<Location>) argument;
				final Polygon polygon = new Polygon(locations);
				writer.writeStartDocument(((FieldAccess) sourceExpression).getFieldName());
				encodePolygon(writer, polygon);
				writer.writeEndDocument();
			}
		}
	}

	/**
	 * Checks that the given list contains elements of the given element type
	 * 
	 * @param list
	 * @param elementClass
	 * @return {@code true} if the list contains elements of the given type, {@code false} otherwise.
	 */
	private boolean listContains(final List<?> list, final Class<?> elementClass) {
		return !list.isEmpty() && list.get(0).getClass().equals(elementClass);
	}

	/**
	 * Encodes the given {@link Polygon} into the given {@link BsonWriter}. This method assumes that the
	 * {@link BsonDocument} already exists. The resulting document will have the following form: { $geoWithin: {
	 * $geometry: { type: 'Polygon', coordinates: [ [0,0], [0,1], [1,1], [1,0], [0,0] ] } } }
	 * 
	 * @see org.bson.codecs.Encoder#encode(org.bson.BsonWriter, java.lang.Object, org.bson.codecs.EncoderContext)
	 */
	private void encodePolygon(final BsonWriter writer, final Polygon polygon) {
		writer.writeStartDocument("$geoWithin");
		writer.writeStartDocument("$geometry");
		writer.writeString("type", "Polygon");
		writer.writeStartArray("coordinates");
		for (Ring ring : polygon.getRings()) {
			writer.writeStartArray();
			for (Location point : ring.getPoints()) {
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

	// TODO: move 'extract' methods to ExpressionUtils ?
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
		if (expr.getType().equals(queryMetadataClass)) {
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
			final java.lang.reflect.Field field = queryMetadataClass.getField(fieldName);
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
					+ queryMetadataClass.getName() + "'", cause);
		}
		return builder.toString();
	}

}
