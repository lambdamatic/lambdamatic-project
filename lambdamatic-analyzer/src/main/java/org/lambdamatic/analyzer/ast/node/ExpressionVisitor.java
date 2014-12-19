/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xcoulon
 *
 */
public abstract class ExpressionVisitor {

	/** The usual Logger.*/
	private static final Logger LOGGER = LoggerFactory.getLogger(ExpressionVisitor.class);
	
	/**
	 * Dispatch to the other visitXYZ methods
	 * <p>
	 * This method should not be called directly. Users should call {@link Expression#accept(ExpressionVisitor)} instead.
	 * </p>
	 * 
	 * @param expr
	 *            the {@link Expression} to visit
	 * @return {@code true} if the visit on the given {@link Expression}'s children should continue, {@code false} otherwise.
	 * 
	 * @see Expression#accept(ExpressionVisitor)
	 */
	public boolean visit(final Expression expr) {
		if (expr != null) {
			//LOGGER.trace("Visiting {}", expr);
			switch (expr.getExpressionType()) {
			case BOOLEAN_LITERAL:
				return visitBooleanLiteralExpression((BooleanLiteral) expr);
			case CHARACTER_LITERAL:
				return visitCharacterLiteralExpression((CharacterLiteral) expr);
			case FIELD_ACCESS:
				return visitFieldAccessExpression((FieldAccess) expr);
			case INFIX:
				return visitInfixExpression((InfixExpression) expr);
			case INSTANCE_OF:
				return visitInstanceOfExpression((InstanceOfExpression) expr);
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
			case CAPTURED_ARGUMENT:
				return visitCapturedArgument((CapturedArgument) expr);
			case LOCAL_VARIABLE:
				return visitLocalVariable((LocalVariable) expr);
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

	public boolean visitEnumLiteralExpression(final EnumLiteral expr) {
		return true;
	}
	
	public boolean visitClassLiteralExpression(final ClassLiteral expr) {
		return true;
	}
}

