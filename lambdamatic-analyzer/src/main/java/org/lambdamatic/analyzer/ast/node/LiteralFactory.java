/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

import org.lambdamatic.analyzer.ast.node.Expression.ExpressionType;

/**
 * @author xcoulon
 *
 */
public class LiteralFactory {

	/**
	 * Private constructor of the utility class
	 */
	private LiteralFactory() {
	}
	
	/**
	 * Converts the given {@code value} to a literal {@link Expression}. 
	 * @param value
	 * @return the literal {@link Expression} wrapping the given value.
	 */
	public static Expression getLiteral(final Object value) {
		if (value == null) {
			return new NullLiteral();
		} else if (value instanceof Boolean) {
			return new BooleanLiteral((Boolean) value);
		} else if (value instanceof Character) {
			return new CharacterLiteral((Character) value);
		} else if (value instanceof Number) {
			return new NumberLiteral((Number) value);
		} else if (value instanceof Enum<?>) {
			return new EnumLiteral((Enum<?>)value);
		}
		return new StringLiteral(value.toString());
	}

	/**
	 * Converts the given {@code value} to a literal {@link Expression}, in the context of the given {@code expression}
	 * @param value the value to wrap in a Literal {@link Expression}.
	 * @param expression the expression that helps in selecting the specific type of literal to help
	 * @return the literal {@link Expression} wrapping the given value.
	 */
	public static Expression getLiteral(final int value, final Expression expression) {
		if(expression.getExpressionType() == ExpressionType.METHOD_INVOCATION) {
			final MethodInvocation methodInvocation = (MethodInvocation) expression;
			final Class<?> returnType = methodInvocation.getReturnType();
			if(char.class.isAssignableFrom(returnType) || Character.class.isAssignableFrom(returnType)) {
				return new CharacterLiteral((char)value);
			} else if (boolean.class.isAssignableFrom(returnType) || Boolean.class.isAssignableFrom(returnType)) {
				switch(value) {
				case 0: 
					return new BooleanLiteral(false);
				default:
					return new BooleanLiteral(true);
				}
			} 
			return new NumberLiteral(value);
		}
		// default case
		return getLiteral(value);
	}

}
