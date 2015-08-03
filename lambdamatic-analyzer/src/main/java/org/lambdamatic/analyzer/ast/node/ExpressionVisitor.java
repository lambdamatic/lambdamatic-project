/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public abstract class ExpressionVisitor {

	/**
	 * Dispatch to the other visitXYZ methods
	 * <p>
	 * This method should not be called directly. Users should call {@link Expression#accept(ExpressionVisitor)}
	 * instead.
	 * </p>
	 * 
	 * @param expr
	 *            the {@link Expression} to visit
	 * @return {@code true} if the visit on the given {@link Expression}'s children should continue, {@code false}
	 *         otherwise.
	 * 
	 * @see Expression#accept(ExpressionVisitor)
	 */
	public boolean visit(final Expression expr) {
		if (expr != null) {
			switch (expr.getExpressionType()) {
			case ASSIGNMENT:
				return visitAssignment((Assignment) expr);
			case BOOLEAN_LITERAL:
				return visitBooleanLiteralExpression((BooleanLiteral) expr);
			case CHARACTER_LITERAL:
				return visitCharacterLiteralExpression((CharacterLiteral) expr);
			case FIELD_ACCESS:
				return visitFieldAccessExpression((FieldAccess) expr);
			case COMPOUND:
				return visitInfixExpression((CompoundExpression) expr);
			case INSTANCE_OF:
				return visitInstanceOfExpression((InstanceOf) expr);
			case OBJECT_INSTANCIATION:
				return visitObjectVariableExpression((ObjectInstanciation) expr);
			case ARRAY_VARIABLE:
				return visitArrayVariableExpression((ArrayVariable) expr);
			case METHOD_INVOCATION:
				return visitMethodInvocationExpression((MethodInvocation) expr);
			case NULL_LITERAL:
				return visitNullLiteralExpression((NullLiteral) expr);
			case NUMBER_LITERAL:
				return visitNumberLiteralExpression((NumberLiteral) expr);
			case STRING_LITERAL:
				return visitStringLiteralExpression((StringLiteral) expr);
			case ENUM_LITERAL:
				return visitEnumLiteralExpression((EnumLiteral) expr);
			case CLASS_LITERAL:
				return visitClassLiteralExpression((ClassLiteral) expr);
			case OBJECT_INSTANCE:
				return visitObjectValue((ObjectInstance) expr);
			case CAPTURED_ARGUMENT:
				return visitCapturedArgument((CapturedArgument) expr);
			case CAPTURED_ARGUMENT_REF:
				return visitCapturedArgumentRef((CapturedArgumentRef) expr);
			case LOCAL_VARIABLE:
				return visitLocalVariable((LocalVariable) expr);
			case LAMBDA_EXPRESSION:
				return visitLambdaExpression((LambdaExpression) expr);
			default:
				break;
			}
		}
		return true;
	}

	public boolean visitAssignment(final Assignment expr) {
		return true;
	}

	public boolean visitLambdaExpression(final LambdaExpression expr) {
		return true;
	}

	public boolean visitLocalVariable(final LocalVariable expr) {
		return true;
	}

	public boolean visitObjectValue(final ObjectInstance expr) {
		return true;
	}

	public boolean visitCapturedArgument(final CapturedArgument expr) {
		return true;
	}

	public boolean visitCapturedArgumentRef(final CapturedArgumentRef expr) {
		return true;
	}

	public boolean visitBooleanLiteralExpression(final BooleanLiteral expr) {
		return true;
	}

	public boolean visitCharacterLiteralExpression(final CharacterLiteral expr) {
		return true;
	}

	public boolean visitFieldAccessExpression(final FieldAccess expr) {
		return true;
	}

	public boolean visitInfixExpression(final CompoundExpression expr) {
		return true;
	}

	public boolean visitInstanceOfExpression(final InstanceOf expr) {
		return true;
	}

	public boolean visitObjectVariableExpression(final ObjectInstanciation expr) {
		return true;
	}

	public boolean visitArrayVariableExpression(final ArrayVariable expr) {
		return true;
	}

	public boolean visitMethodInvocationExpression(final MethodInvocation expr) {
		return true;
	}

	public boolean visitNullLiteralExpression(final NullLiteral expr) {
		return true;
	}

	public boolean visitNumberLiteralExpression(final NumberLiteral expr) {
		return true;
	}

	public boolean visitStringLiteralExpression(final StringLiteral expr) {
		return true;
	}

	public boolean visitEnumLiteralExpression(final EnumLiteral expr) {
		return true;
	}

	public boolean visitClassLiteralExpression(final ClassLiteral expr) {
		return true;
	}
}
