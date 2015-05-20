package org.lambdamatic.mongodb.internal.codecs;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
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
import org.lambdamatic.analyzer.ast.node.ExpressionFactory;
import org.lambdamatic.analyzer.ast.node.ExpressionVisitor;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.InfixExpression;
import org.lambdamatic.analyzer.ast.node.InfixExpression.InfixOperator;
import org.lambdamatic.analyzer.ast.node.LambdaExpression;
import org.lambdamatic.analyzer.ast.node.LocalVariable;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
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
 */
class FilterExpressionEncoder extends ExpressionVisitor {

	/** the usual logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(FilterExpressionEncoder.class);

	/**
	 * The {@link QueryMetadata} class associated with the domain class being queried.
	 */
	private final Class<?> queryMetadataClass;

	/**
	 * The {@link QueryMetadata} var name used in the Lambda Expression to analyze.
	 */
	private final String queryMetadataVarName;

	/** The {@link BsonWriter} to use. */
	private final BsonWriter writer;

	/** The {@link EncoderContext} to use. */
	private final EncoderContext encoderContext;

	/** boolean to indicate if the expression to encode is nested, ie, it must not be written with its own document. */
	private final boolean nestedExpression;

	/**
	 * Full constructor
	 * 
	 * @param queryMetadataClass
	 *            the {@link Class} linked to the {@link Expression} to visit.
	 * @param queryMetadataVarName
	 *            The {@link QueryMetadata} var name used in the Lambda Expression to analyze
	 * @param writer
	 *            the {@link BsonWriter} in which the {@link SerializablePredicate} representation will be written.
	 * @see: http://docs.mongodb.org/manual/reference/operator/query/
	 */
	FilterExpressionEncoder(final Class<?> queryMetadataClass, final String queryMetadataVarName,
			final BsonWriter writer, final EncoderContext encoderContext) {
		this(queryMetadataClass, queryMetadataVarName, writer, encoderContext, false);
	}

	/**
	 * Full constructor
	 * 
	 * @param queryMetadataClass
	 *            the {@link Class} linked to the {@link Expression} to visit.
	 * @param queryMetadataVarName
	 *            The {@link QueryMetadata} var name used in the Lambda Expression to analyze
	 * @param writer
	 *            the {@link BsonWriter} in which the {@link SerializablePredicate} representation will be written.
	 * @see: http://docs.mongodb.org/manual/reference/operator/query/
	 */
	FilterExpressionEncoder(final Class<?> queryMetadataClass, final String queryMetadataVarName,
			final BsonWriter writer, final EncoderContext encoderContext, final boolean nestedExpression) {
		this.queryMetadataClass = queryMetadataClass;
		this.queryMetadataVarName = queryMetadataVarName;
		this.writer = writer;
		this.encoderContext = encoderContext;
		this.nestedExpression = nestedExpression;
	}

	@Override
	public boolean visitInfixExpression(final InfixExpression expr) {
		if (!this.nestedExpression) {
			writer.writeStartDocument();
		}
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
			// Syntax: {field: value}
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
			throw new UnsupportedOperationException(
					"Generating a query with '" + expr.getOperator() + "' is not supported yet (shame...)");
		}
		if (!this.nestedExpression) {
			writer.writeEndDocument();
		}
		// TODO: do we need to flush explicitly ?
		writer.flush();
		return false;
	}

	/**
	 * Writes a logical operation of the following form:
	 * 
	 * <pre>
	 * { $operator: [ { <operand1> }, { <operand2> } , ... , {<operandN> } ] }
	 * </pre>
	 * 
	 * @param operator
	 *            the operator to write
	 * @param operands
	 *            the operands to write
	 */
	private void writeLogicalOperation(final MongoOperator operator, final List<Expression> operands) {
		writer.writeStartArray(operator.getLiteral());
		for (Expression operand : operands) {
			final BsonDocument operandDocument = new BsonDocument();
			final BsonWriter operandBsonWriter = new BsonDocumentWriter(operandDocument);
			final FilterExpressionEncoder operandEncoder = new FilterExpressionEncoder(this.queryMetadataClass,
					this.queryMetadataVarName, operandBsonWriter, this.encoderContext);
			operand.accept(operandEncoder);
			final BsonReader operandBsonReader = new BsonDocumentReader(operandDocument);
			writer.pipe(operandBsonReader);
		}
		writer.writeEndArray();
	}

	/**
	 * Visits the given {@link MethodInvocation} expression. If underlying Java {@link Method} is annotated with
	 * {@link MongoOperation}, then the expression is converted into a BSON document. Otherwise, this method assumes
	 * that the given {@link MethodInvocation} is an argument of another {@link Expression}, evaluates it and replaces
	 * it
	 * 
	 * @param methodInvocation
	 *            the {@link MethodInvocation} to process
	 */
	@Override
	public boolean visitMethodInvocationExpression(final MethodInvocation methodInvocation) {
		final Method method = methodInvocation.getJavaMethod();
		final MongoOperation annotation = method.getAnnotation(MongoOperation.class);
		if (annotation != null) {
			// FIXME: support other operands
			// FIXME: use $not: http://docs.mongodb.org/manual/reference/operator/query/not/#op._S_not
			if (!this.nestedExpression) {
				writer.writeStartDocument();
			}
			switch (annotation.value()) {
			case GEO_WITHIN:
				writeGeoWithin(methodInvocation.getSourceExpression(), methodInvocation.getArguments(),
						methodInvocation.isInverted());
				break;
			default:
				writeOperation(annotation.value(), methodInvocation.getSourceExpression(),
						methodInvocation.getArguments().get(0), methodInvocation.isInverted());
			}
			if (!this.nestedExpression) {
				writer.writeEndDocument();
			}
		} else {
			methodInvocation.getParent().replaceElement(methodInvocation,
					ExpressionFactory.getExpression(methodInvocation.evaluate()));
		}
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
			writeNamedExpression(key, valueExpr);
		} else {
			if (key != null && !this.nestedExpression) {
				writer.writeStartDocument(key);
			}
			if (inverted) {
				writer.writeStartDocument(MongoOperator.NOT.getLiteral());
				writeNamedExpression(operator.getLiteral(), valueExpr);
				writer.writeEndDocument();
			} else if (valueExpr.getExpressionType() == ExpressionType.LAMBDA_EXPRESSION) {
				writeNamedLambdaExpression(operator.getLiteral(), (LambdaExpression) valueExpr);
			} else {
				writeNamedExpression(operator.getLiteral(), valueExpr);
			}
			if (key != null && !this.nestedExpression) {
				writer.writeEndDocument();
			}
		}
	}

	/**
	 * Writes the given named {@link LambdaExpression} in a compact form.
	 * <p>
	 * E.g:
	 * </p>
	 * 
	 * <pre>
	 * $elemMatch: { &lt;operand1&gt;, &lt;operand2&gt; ... }
	 * </pre>
	 * 
	 * @param name
	 *            the LambdaExpression name
	 * @param valueExpr
	 *            the LambdaExpression itself
	 */
	private void writeNamedLambdaExpression(final String name, final LambdaExpression lambdaExpression) {
		writer.writeStartDocument(name);
		// writer each operand of the LambdaExpression in a compact form.
		switch (lambdaExpression.getExpression().getExpressionType()) {
		case INFIX:
			final InfixExpression infixExpression = (InfixExpression) lambdaExpression.getExpression();
			// assume that this is a logical AND operation
			if (infixExpression.getOperator() != InfixOperator.CONDITIONAL_AND) {
				throw new ConversionException("Did not expect a logical operation of type '"
						+ infixExpression.getOperator() + "' while writing a nested Lambda Expression");
			}
			for (Expression operand : infixExpression.getOperands()) {
				final FilterExpressionEncoder operandEncoder = new FilterExpressionEncoder(this.queryMetadataClass,
						this.queryMetadataVarName, writer, encoderContext, true);
				operand.accept(operandEncoder);
			}
			break;
		default:
			final Expression expression = lambdaExpression.getExpression();
			final FilterExpressionEncoder expressionEncoder = new FilterExpressionEncoder(
					lambdaExpression.getArgumentType(), lambdaExpression.getArgumentName(), writer, encoderContext, true);
			expression.accept(expressionEncoder);
		}

		writer.writeEndDocument();

	}

	/**
	 * Writes the given named {@link Expression}
	 * 
	 * @param name
	 *            the Expression name
	 * @param valueExpr
	 *            the Expression itself
	 */
	private void writeNamedExpression(final String name, final Expression valueExpr) {
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
		} else if (value.getClass().isArray()) {
			writer.writeStartArray(name);
			final Object[] array = (Object[]) value;
			for (int i = 0; i < array.length; i++) {
				writeValue(array[i]);
			}
			writer.writeEndArray();
		} else if (Collection.class.isAssignableFrom(value.getClass())) {
			writer.writeStartArray(name);
			final Collection<?> collection = (Collection<?>) value;
			for (Iterator<?> iterator = collection.iterator(); iterator.hasNext();) {
				writeValue(iterator.next());
			}
			writer.writeEndArray();
		} else {
			throw new ConversionException("Writing value of a type '" + value.getClass() + "' is not supported yet");
		}
	}

	/**
	 * Writes the given unnamed value.
	 * 
	 * @param value
	 *            the value to write
	 */
	private void writeValue(final Object value) {
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
		} else if (value instanceof Enum) {
			writer.writeString(((Enum<?>) value).name());
		} else if (value.getClass().isArray()) {
			writer.writeStartArray();
			final Object[] array = (Object[]) value;
			for (int i = 0; i < array.length; i++) {
				writeValue(value);
			}
			writer.writeEndArray();
		} else {
			throw new UnsupportedOperationException(
					"Writing value of a '" + value.getClass() + "' is not supported yet");
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
		if (expr.getType().equals(queryMetadataClass) && expr.getName().equals(this.queryMetadataVarName)) {
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
		final String documentFieldName = EncoderUtils.getDocumentFieldName(queryMetadataClass, fieldName);
		builder.append(documentFieldName);
		return builder.toString();
	}

}
