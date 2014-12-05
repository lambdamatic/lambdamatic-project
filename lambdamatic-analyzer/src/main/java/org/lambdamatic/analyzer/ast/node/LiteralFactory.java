/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

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
		}
		return new StringLiteral(value.toString());
	}

}
