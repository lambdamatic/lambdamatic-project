/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.internal.codecs;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonDocumentWriter;
import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;
import org.lambdamatic.SerializablePredicate;
import org.lambdamatic.analyzer.ast.node.ArrayElementAccess;
import org.lambdamatic.analyzer.ast.node.CompoundExpression;
import org.lambdamatic.analyzer.ast.node.CompoundExpression.CompoundExpressionOperator;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.Expression.ExpressionType;
import org.lambdamatic.analyzer.ast.node.ExpressionFactory;
import org.lambdamatic.analyzer.ast.node.ExpressionVisitor;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.LambdaExpression;
import org.lambdamatic.analyzer.ast.node.LocalVariable;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.lambdamatic.mongodb.exceptions.ConversionException;
import org.lambdamatic.mongodb.metadata.ArrayElementAccessor;
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
 * @author Xavier Coulon
 */
class FilterExpressionEncoder
    extends ExpressionVisitor {

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

  /**
   * The {@link BsonWriter} to use.
   */
  private final BsonWriter writer;

  /**
   * The {@link EncoderContext} to use.
   */
  private final EncoderContext encoderContext;

  /**
   * boolean to indicate if the expression to encode is nested, ie, it must not be written with its
   * own document.
   */
  private final boolean nestedExpression;

  /**
   * Full constructor
   * 
   * @param queryMetadataClass the {@link Class} linked to the {@link Expression} to visit.
   * @param queryMetadataVarName The {@link QueryMetadata} var name used in the Lambda Expression to
   *        analyze
   * @param writer the {@link BsonWriter} in which the {@link SerializablePredicate} representation
   *        will be written.
   * @see: http://docs.mongodb.org/manual/reference/operator/query/
   */
  FilterExpressionEncoder(final Class<?> queryMetadataClass, final String queryMetadataVarName,
      final BsonWriter writer, final EncoderContext encoderContext) {
    this(queryMetadataClass, queryMetadataVarName, writer, encoderContext, false);
  }

  /**
   * Full constructor
   * 
   * @param queryMetadataClass the {@link Class} linked to the {@link Expression} to visit.
   * @param queryMetadataVarName The {@link QueryMetadata} var name used in the Lambda Expression to
   *        analyze
   * @param writer the {@link BsonWriter} in which the {@link SerializablePredicate} representation
   *        will be written.
   * @see: http://docs.mongodb.org/manual/reference/operator/query/
   */
  FilterExpressionEncoder(final Class<?> queryMetadataClass, final String queryMetadataVarName,
      final BsonWriter writer, final EncoderContext encoderContext,
      final boolean nestedExpression) {
    this.queryMetadataClass = queryMetadataClass;
    this.queryMetadataVarName = queryMetadataVarName;
    this.writer = writer;
    this.encoderContext = encoderContext;
    this.nestedExpression = nestedExpression;
  }

  @Override
  public boolean visitInfixExpression(final CompoundExpression expr) {
    if (!this.nestedExpression) {
      this.writer.writeStartDocument();
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
      default:
        writeOperation(EncoderUtils.getMongoOperator(expr.getOperator()), expr.getOperands().get(0),
            expr.getOperands().get(1), expr.isInverted());
        break;
    }
    if (!this.nestedExpression) {
      this.writer.writeEndDocument();
    }
    // TODO: do we need to flush explicitly ?
    this.writer.flush();
    return false;
  }

  /**
   * Writes a logical operation of the following form:
   * 
   * <pre>
   * { $operator: [ { &lt;operand1&gt; }, { &lt;operand2&gt; } , ... , {&lt;operandN&gt; } ] }
   * </pre>
   * 
   * @param operator the operator to write
   * @param operands the operands to write
   */
  private void writeLogicalOperation(final MongoOperator operator,
      final List<Expression> operands) {
    this.writer.writeStartArray(operator.getLiteral());
    for (Expression operand : operands) {

      final BsonDocument operandDocument = new BsonDocument();
      try (
          final BsonDocumentWriter operandBsonWriter = new BsonDocumentWriter(operandDocument);
          final BsonDocumentReader operandBsonReader = new BsonDocumentReader(operandDocument);
        ) {
        final FilterExpressionEncoder operandEncoder =
            new FilterExpressionEncoder(this.queryMetadataClass, this.queryMetadataVarName,
                operandBsonWriter, this.encoderContext);
        operand.accept(operandEncoder);
        this.writer.pipe(operandBsonReader);
      }
    }
    this.writer.writeEndArray();
  }

  /**
   * Visits the given {@link MethodInvocation} expression. If underlying Java {@link Method} is
   * annotated with {@link MongoOperation}, then the expression is converted into a BSON document.
   * Otherwise, this method assumes that the given {@link MethodInvocation} is an argument of
   * another {@link Expression}, evaluates it and replaces it
   * 
   * @param methodInvocation the {@link MethodInvocation} to process
   */
  @Override
  public boolean visitMethodInvocationExpression(final MethodInvocation methodInvocation) {
    final Method method = methodInvocation.getJavaMethod();
    final MongoOperation operationAnnotation = method.getAnnotation(MongoOperation.class);
    final ArrayElementAccessor arrayAccessorAnnotation =
        method.getAnnotation(ArrayElementAccessor.class);
    if (operationAnnotation != null) {
      // FIXME: support other operands
      // FIXME: use $not: http://docs.mongodb.org/manual/reference/operator/query/not/#op._S_not
      if (!this.nestedExpression) {
        this.writer.writeStartDocument();
      }
      switch (operationAnnotation.value()) {
        case GEO_WITHIN:
          writeGeoWithin(methodInvocation.getSource(), methodInvocation.getArguments(),
              methodInvocation.isInverted());
          break;
        default:
          writeOperation(operationAnnotation.value(), methodInvocation.getSource(),
              methodInvocation.getArguments().get(0), methodInvocation.isInverted());
      }
      if (!this.nestedExpression) {
        this.writer.writeEndDocument();
      }
    } else if (arrayAccessorAnnotation != null) {
      methodInvocation.getParent().replaceElement(methodInvocation,
          new ArrayElementAccess(methodInvocation));
    } else {
      methodInvocation.getParent().replaceElement(methodInvocation,
          ExpressionFactory.getExpression(methodInvocation.evaluate()));
    }
    return false;
  }

  @Override
  public boolean visitFieldAccessExpression(final FieldAccess fieldAccess) {
    // skip if fieldAccess unless it is part of an CompoundExpression or root
    if (fieldAccess.getParent() != null
        && fieldAccess.getParent().getExpressionType() != ExpressionType.COMPOUND) {
      return false;
    }
    if (fieldAccess.getJavaType() == Boolean.class || fieldAccess.getJavaType() == boolean.class) {
      if (!this.nestedExpression) {
        this.writer.writeStartDocument();
      }
      this.writer.writeBoolean(fieldAccess.getFieldName(), !fieldAccess.isInverted());
      if (!this.nestedExpression) {
        this.writer.writeEndDocument();
      }
      return false;
    }
    throw new ConversionException("Unable to convert access to field '" + fieldAccess.getFieldName()
        + "' of type " + fieldAccess.getJavaType().getName() + " into a BSON query element.");
  }

  /**
   * Writes the operation for the given key/value pair.
   * <p>
   * Eg: <code>{key: value}</code>
   * </p>
   * 
   * @param operator the operator
   * @param keyExpr the key expression
   * @param valueExpr the value expression
   * @param inverted if the operation is inverted (ie, using the {@link MongoOperator#NOT} operand
   * @see MongoOperator
   */
  private void writeOperation(final MongoOperator operator, final Expression keyExpr,
      final Expression valueExpr, final boolean inverted) {
    final String key = extractKey(keyExpr);
    if (inverted) {
      this.writer.writeStartDocument(MongoOperator.NOT.getLiteral());
      writeOperation(operator, keyExpr, valueExpr, false);
      this.writer.writeEndDocument();
    } else {
      // simplified formula for EQUALS operator
      if (operator == MongoOperator.EQUALS && key != null) {
        EncoderUtils.writeNamedExpression(this.writer, key, valueExpr);
      } else {
        if (key != null && !this.nestedExpression) {
          this.writer.writeStartDocument(key);
        }
        if (valueExpr.getExpressionType() == ExpressionType.LAMBDA_EXPRESSION) {
          writeNamedLambdaExpression(operator.getLiteral(), (LambdaExpression) valueExpr);
        } else {
          EncoderUtils.writeNamedExpression(this.writer, operator.getLiteral(), valueExpr);
        }
        if (key != null && !this.nestedExpression) {
          this.writer.writeEndDocument();
        }
      }
    }
  }

  /**
   * Writes the given named {@link LambdaExpressionBlock} in a compact form.
   * <p>
   * E.g:
   * </p>
   * 
   * <pre>
   * $elemMatch: { &lt;operand1&gt;, &lt;operand2&gt; ... }
   * </pre>
   * 
   * @param name the LambdaExpressionBlock name
   * @param valueExpr the LambdaExpressionBlock itself
   */
  private void writeNamedLambdaExpression(final String name,
      final LambdaExpression lambdaExpression) {
    final Expression expression = EncoderUtils.getSingleExpression(lambdaExpression);
    this.writer.writeStartDocument(name);
    // writer each operand of the LambdaExpressionBlock in a compact form.
    switch (expression.getExpressionType()) {
      case COMPOUND:
        final CompoundExpression infixExpression = (CompoundExpression) expression;
        // assume that this is a logical AND operation
        if (infixExpression.getOperator() != CompoundExpressionOperator.CONDITIONAL_AND) {
          throw new ConversionException("Did not expect a logical operation of type '"
              + infixExpression.getOperator() + "' while writing a nested Lambda Expression");
        }
        for (Expression operand : infixExpression.getOperands()) {
          final FilterExpressionEncoder operandEncoder =
              new FilterExpressionEncoder(this.queryMetadataClass, this.queryMetadataVarName,
                  this.writer, this.encoderContext, true);
          operand.accept(operandEncoder);
        }
        break;
      default:
        final FilterExpressionEncoder expressionEncoder =
            new FilterExpressionEncoder(lambdaExpression.getArgumentType(),
                lambdaExpression.getArgumentName(), this.writer, this.encoderContext, true);
        expression.accept(expressionEncoder);
    }

    this.writer.writeEndDocument();
  }

  /**
   * Encodes the given <code>sourceExpression</code> and <code>arguments</code> into a geoWithin
   * query member.
   * 
   * Eg:
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
   * @param sourceExpression the source expression to encode
   * @param arguments a list of Array of {@link Location} or {@link Polygon}
   * @param inverted if the operation is inverted (ie, using the {@link MongoOperator#NOT} operand
   */
  private void writeGeoWithin(final Expression sourceExpression, final List<Expression> arguments,
      final boolean inverted) {
    if (arguments == null || arguments.isEmpty()) {
      throw new ConversionException("Cannot generate geoWithin query with empty arguments");
    }
    if (sourceExpression.getExpressionType() != ExpressionType.FIELD_ACCESS) {
      throw new ConversionException(
          "Did not expect to generate a 'geoWithin' query from a element of type "
              + sourceExpression.getExpressionType());
    }
    final List<Object> argumentValues =
        arguments.stream().map(e -> e.getValue()).collect(Collectors.toList());
    if (argumentValues.size() == 1) {
      final Object argument = argumentValues.get(0);
      if (argument instanceof Polygon) {
        // argument is an instance of Polygon
        final Polygon polygon = (Polygon) argument;
        this.writer.writeStartDocument(((FieldAccess) sourceExpression).getFieldName());
        encodePolygon(this.writer, polygon);
        this.writer.writeEndDocument();
      } else if (argument.getClass().isArray()
          && argument.getClass().getComponentType().equals(Location.class)) {
        // argument is an array of Location
        final Location[] locations = (Location[]) argument;
        final Polygon polygon = new Polygon(locations);
        this.writer.writeStartDocument(((FieldAccess) sourceExpression).getFieldName());
        encodePolygon(this.writer, polygon);
        this.writer.writeEndDocument();
      } else if (List.class.isInstance(argument)
          && listContains((List<?>) argument, Location.class)) {
        @SuppressWarnings("unchecked")
        final List<Location> locations = (List<Location>) argument;
        final Polygon polygon = new Polygon(locations);
        this.writer.writeStartDocument(((FieldAccess) sourceExpression).getFieldName());
        encodePolygon(this.writer, polygon);
        this.writer.writeEndDocument();
      }
    }
  }

  /**
   * Checks that the given list contains elements of the given element type
   * 
   * @param list the list to analyze
   * @param elementClass the type of element to check in the list
   * @return {@code true} if the list contains elements of the given type, {@code false} otherwise.
   */
  private static boolean listContains(final List<?> list, final Class<?> elementClass) {
    return !list.isEmpty() && list.get(0).getClass().equals(elementClass);
  }

  /**
   * Encodes the given {@link Polygon} into the given {@link BsonWriter}. This method assumes that
   * the {@link BsonDocument} already exists. The resulting document will have the following form: {
   * $geoWithin: { $geometry: { type: 'Polygon', coordinates: [ [0,0], [0,1], [1,1], [1,0], [0,0] ]
   * } } }
   * 
   * @see org.bson.codecs.Encoder#encode(org.bson.BsonWriter, java.lang.Object,
   *      org.bson.codecs.EncoderContext)
   */
  private static void encodePolygon(final BsonWriter writer, final Polygon polygon) {
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
      case ARRAY_ELEMENT_ACCESS:
        return extractKey((ArrayElementAccess) expr);
      default:
        return null;
    }
  }

  private String extractKey(final LocalVariable expr) {
    if (expr.getType().equals(this.queryMetadataClass)
        && expr.getName().equals(this.queryMetadataVarName)) {
      LOGGER.trace("Skipping variable '{}' ({})", expr.getName(), expr.getJavaType().getName());
      return null;
    }
    return expr.getName();
  }

  private String extractKey(final FieldAccess expr) {
    final StringBuilder builder = new StringBuilder();
    final String target = extractKey(expr.getSource());
    if (target != null) {
      builder.append(target).append('.');
    }
    final String fieldName = expr.getFieldName();
    final String documentFieldName =
        EncoderUtils.getDocumentFieldName(expr.getSource().getJavaType(), fieldName);
    builder.append(documentFieldName);
    return builder.toString();
  }

  private String extractKey(final ArrayElementAccess expr) {
    final StringBuilder builder = new StringBuilder();
    final String target = extractKey(expr.getSourceField());
    if (target != null) {
      builder.append(target).append('.');
    }
    final String fieldName = expr.getIndex();
    builder.append(fieldName);
    return builder.toString();
  }

}
