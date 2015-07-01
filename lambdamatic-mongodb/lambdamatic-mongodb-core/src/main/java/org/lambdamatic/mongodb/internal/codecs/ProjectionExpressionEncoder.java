/**
 * 
 */
package org.lambdamatic.mongodb.internal.codecs;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.EncoderContext;
import org.lambdamatic.SerializablePredicate;
import org.lambdamatic.analyzer.ast.node.ArrayVariable;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.Expression.ExpressionType;
import org.lambdamatic.analyzer.ast.node.ExpressionVisitor;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.LambdaExpression;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.lambdamatic.mongodb.Projection;
import org.lambdamatic.mongodb.exceptions.ConversionException;
import org.lambdamatic.mongodb.metadata.MongoOperator;
import org.lambdamatic.mongodb.metadata.QueryMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes a given Projection {@link Expression} into a MongoDB {@link BsonWriter}.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class ProjectionExpressionEncoder extends ExpressionVisitor {

	enum ProjectionType {
		INCLUDE(1), EXCLUDE(0);

		private final int value;

		private ProjectionType(final int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	/** the usual logger. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectionExpressionEncoder.class);

	/**
	 * The {@link QueryMetadata} class associated with the domain class being queried.
	 */
	private final Class<?> projectionMetadataClass;

	/** The {@link BsonWriter} to use. */
	private final BsonWriter writer;

	/** The {@link EncoderContext} to use. */
	private final EncoderContext encoderContext;

	/** boolean to indicate if the expression to encode is nested, ie, it must not be written with its own document. */
	private final boolean nestedExpression;

	/** boolean to indicate if the expression to encode is nested, ie, it must not be written with its own document. */
	private final ProjectionType projectionType;

	/** the names of the fields included in the projection. */
	private final Set<String> projectedFieldNames;

	/**
	 * Full constructor
	 * 
	 * @param projectionMetadataClass
	 *            the {@link Class} linked to the {@link Expression} to visit.
	 * @param writer
	 *            the {@link BsonWriter} in which the {@link SerializablePredicate} representation will be written.
	 * @param projectionType
	 * @see: http://docs.mongodb.org/manual/reference/operator/query/
	 */
	ProjectionExpressionEncoder(final Class<?> projectionMetadataClass, final BsonWriter writer,
			final EncoderContext encoderContext, final ProjectionType projectionType) {
		this(projectionMetadataClass, writer, encoderContext, projectionType, false);
	}

	/**
	 * Full constructor
	 * 
	 * @param projectionMetadataClass
	 *            the {@link Class} linked to the {@link Expression} to visit.
	 * @param writer
	 *            the {@link BsonWriter} in which the {@link SerializablePredicate} representation will be written.
	 * @see: http://docs.mongodb.org/manual/reference/operator/query/
	 */
	ProjectionExpressionEncoder(final Class<?> projectionMetadataClass, final BsonWriter writer,
			final EncoderContext encoderContext, final ProjectionType projectionType, final boolean nestedExpression) {
		this.projectionMetadataClass = projectionMetadataClass;
		this.writer = writer;
		this.encoderContext = encoderContext;
		this.nestedExpression = nestedExpression;
		this.projectionType = projectionType;
		this.projectedFieldNames = new HashSet<String>();
	}

	/**
	 * Encodes each argument of the given {@link ArrayVariable} and then skips the visit of each nested
	 * {@link Expression}.
	 */
	@Override
	public boolean visitArrayVariableExpression(final ArrayVariable arrayVariable) {
		if (!nestedExpression) {
			writer.writeStartDocument();
		}
		for (Expression element : arrayVariable.getElements()) {
			element.accept(this);
		}
		if (this.projectionType == ProjectionType.INCLUDE
				&& !projectedFieldNames.contains(EncoderUtils.MONGOBD_DOCUMENT_ID)) {
			encode(EncoderUtils.MONGOBD_DOCUMENT_ID, ProjectionType.EXCLUDE);
		}
		if (!nestedExpression) {
			writer.writeEndDocument();
		}
		return false;

	}

	/**
	 * Encodes the given {@link FieldAccess} as it is the only argument given in the {@link Projection} definition.
	 * 
	 */
	@Override
	public boolean visitFieldAccessExpression(final FieldAccess fieldAccess) {
		if (fieldAccess.getParent().getExpressionType() == ExpressionType.ARRAY_VARIABLE) {
			final String documentFieldName = EncoderUtils.getDocumentFieldName(projectionMetadataClass, fieldAccess);
			this.projectedFieldNames.add(documentFieldName);
			encode(documentFieldName, this.projectionType);
		}
		return false;
	}

	/**
	 * Encodes the given {@link MethodInvocation} as it is the only argument given in the {@link Projection} definition.
	 * 
	 */
	@Override
	public boolean visitMethodInvocationExpression(final MethodInvocation methodInvocation) {
		// embedded Lambda Expression on Array field
		if (methodInvocation.getParent().getExpressionType() == ExpressionType.ARRAY_VARIABLE) {
			final List<Expression> arguments = methodInvocation.getArguments();
			final Expression sourceExpression = methodInvocation.getSource();
			if (sourceExpression.getExpressionType() != ExpressionType.FIELD_ACCESS) {
				throw new ConversionException(
						"Did not expect something else than a field access as the source expression in the following method invocation: "
								+ methodInvocation.toString());
			}
			final String documentFieldName = EncoderUtils.getDocumentFieldName(projectionMetadataClass,
					sourceExpression);
			this.projectedFieldNames.add(documentFieldName);
			if (arguments.size() != 1 && arguments.get(0).getExpressionType() != ExpressionType.LAMBDA_EXPRESSION) {
				throw new ConversionException(
						"Invalid projection: expected a Lambda Expression in " + methodInvocation.toString());
			}
			final LambdaExpression lambdaExpression = (LambdaExpression) arguments.get(0);
			writer.writeStartDocument(documentFieldName);
			writer.writeStartDocument(MongoOperator.ELEMEMT_MATCH.getLiteral());
			// use a dedicated Encoder
			final FilterExpressionEncoder lambdaExpressionEncoder = new FilterExpressionEncoder(
					lambdaExpression.getArgumentType(), lambdaExpression.getArgumentName(), this.writer,
					this.encoderContext, true);
			final Expression expression = EncoderUtils.getSingleExpression(lambdaExpression);
			expression.accept(lambdaExpressionEncoder);
			writer.writeEndDocument();
			writer.writeEndDocument();
		}
		return false;
	}

	/**
	 * Encodes the given <code>documentFieldName</code> using the given <code>fieldProjection</code>.
	 * 
	 * @param documentFieldName
	 *            the name of the field to include in the projection Bson {@link Document}
	 * @param fieldProjection
	 *            the type of projection to write
	 */
	private void encode(final String documentFieldName, final ProjectionType fieldProjection) {
		writer.writeInt32(documentFieldName, fieldProjection.getValue());
	}

}
