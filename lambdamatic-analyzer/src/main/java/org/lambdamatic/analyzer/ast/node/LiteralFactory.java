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
	public static Expression getLiteral(final NumberLiteral numberLiteral, final Class<?> targetType) {
		final Number value = numberLiteral.getValue();
		if(char.class.isAssignableFrom(targetType) || Character.class.isAssignableFrom(targetType)) {
			return new CharacterLiteral((char)value.intValue());
		} else if (boolean.class.isAssignableFrom(targetType) || Boolean.class.isAssignableFrom(targetType)) {
			switch(value.intValue()) {
			case 0: 
				return new BooleanLiteral(false);
			default:
				return new BooleanLiteral(true);
			}
		} 
		return new NumberLiteral(value);
	}

}
