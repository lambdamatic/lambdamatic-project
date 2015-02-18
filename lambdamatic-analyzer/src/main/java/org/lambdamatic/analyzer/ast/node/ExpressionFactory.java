/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

import java.lang.reflect.Field;




/**
 * {@link Expression} factory
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class ExpressionFactory {

	/**
	 * Private constructor of the utility class
	 */
	private ExpressionFactory() {
	}
	
	/**
	 * Converts the given {@code value} to an {@link Expression}. 
	 * @param value
	 * @return the {@link Expression} wrapping the given value.
	 */
	public static Expression getExpression(final Object value) {
		if(value instanceof Expression) {
			return (Expression)value;
		} else if (value == null) {
			return new NullLiteral();
		} else if (value instanceof Boolean) {
			return new BooleanLiteral((Boolean) value);
		} else if (value instanceof Character) {
			return new CharacterLiteral((Character) value);
		} else if (value instanceof Number) {
			return new NumberLiteral((Number) value);
		} else if (value instanceof Enum<?>) {
			return new EnumLiteral((Enum<?>)value);
		} else if (value instanceof String) {
			return new StringLiteral(value.toString());
		} else if (value instanceof Field) {
			final Field field = (Field)value;
			return new FieldAccess(new ClassLiteral(field.getDeclaringClass()), field.getName());
		} else if(value.getClass().isArray()) {
			final Class<?> componentType = value.getClass().getComponentType();
			// value is already an array of Expression, just need to wrap it in an ArrayVariable
			if(Expression.class.isAssignableFrom(componentType)) {
				return new ArrayVariable(componentType, (Expression[])value);
			} 
			// wrap each element in an expression and add it into an ArrayVariable
			else {
				final Object[] values = (Object[]) value;
				final ArrayVariable arrayVariable = new ArrayVariable(componentType, values.length);
				for(int i = 0; i < values.length; i++) {
					arrayVariable.setElement(i, getExpression(values[i]));
				}
				return arrayVariable;
			}
		}
		return new CapturedArgument(value);
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
