/**
 * 
 */
package org.bytesparadise.lambdamatic.internal.ast.node;

/**
 * @author xcoulon
 *
 */
public abstract class ExpressionVisitor {

	/**
	 * Dispatch to the other visitXYZ methods 
	 * @param expr the {@link Expression} to visit
	 * @return true if the visit on the given {@link Expression}'s children should continue, false otherwise.
	 */
	public boolean visit(final Expression expr) {
		if(expr != null) {
			switch(expr.getExpressionType()) {
			case BOOLEAN_LITERAL:
				return visitBooleanLiteralExpression((BooleanLiteral)expr);
			case CHARACTER_LITERAL:
				return visitCharacterLiteralExpression((CharacterLiteral)expr);
			case FIELD_ACCESS:
				return visitFieldAccessExpression((FieldAccess)expr);
			case INFIX:
				return visitInfixExpression((InfixExpression)expr);
			case INSTANCE_OF:
				return visitInstanceOfExpression((InstanceOfExpression)expr);
			case METHOD_INVOCATION:
				return visitMethodInvocationExpression((MethodInvocation)expr);
			case NULL_LITERAL:
				return visitNullLiteralExpression((NullLiteral)expr);
			case NUMBER_LITERAL:
				return visitNumberLiteralExpression((NumberLiteral)expr);
			case STRING_LITERAL:
				return visitStringLiteralExpression((StringLiteral)expr);
			case CAPTURED_ARGUMENT:
				return visitCapturedArgument((CapturedArgument)expr);
			case VARIABLE:
				return visitLocalVariable((LocalVariable)expr);
			}
		}
		return true;
	}

	public boolean visitLocalVariable(LocalVariable expr) {
		return true;
	}

	public boolean visitCapturedArgument(CapturedArgument expr) {
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
	
	public boolean visitInfixExpression(final InfixExpression expr) {
		return true;
	}
	
	public boolean visitInstanceOfExpression(final InstanceOfExpression expr) {
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
}


