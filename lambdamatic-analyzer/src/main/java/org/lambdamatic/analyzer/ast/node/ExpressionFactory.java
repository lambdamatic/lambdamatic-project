/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

import java.lang.reflect.Field;

/**
 * {@link Expression} factory.
 * 
 * @author Xavier Coulon
 *
 */
public class ExpressionFactory {

  /**
   * Private constructor of the utility class.
   */
  private ExpressionFactory() {}

  /**
   * Converts the given {@code value} to an {@link Expression}.
   * 
   * @param value the value to convert into an expression.
   * @return the {@link Expression} wrapping the given value.
   */
  public static Expression getExpression(final Object value) {
    if (value instanceof Expression) {
      return (Expression) value;
    } else if (value == null) {
      return new NullLiteral();
    } else if (value instanceof Boolean) {
      return new BooleanLiteral((Boolean) value);
    } else if (value instanceof Character) {
      return new CharacterLiteral((Character) value);
    } else if (value instanceof Number) {
      return new NumberLiteral((Number) value, true);
    } else if (value instanceof Enum<?>) {
      return new EnumLiteral((Enum<?>) value);
    } else if (value instanceof String) {
      return new StringLiteral(value.toString());
    } else if (value instanceof Field) {
      final Field field = (Field) value;
      return new FieldAccess(new ClassLiteral(field.getDeclaringClass()), field.getName());
    } else if (value.getClass().isArray()) {
      final Class<?> componentType = value.getClass().getComponentType();
      // value is already an array of Expression, just need to wrap it in an ArrayVariable
      if (Expression.class.isAssignableFrom(componentType)) {
        return new ArrayVariable(componentType, (Expression[]) value);
      } else {
        // wrap each element in an expression and add it into an ArrayVariable
        final Object[] values = (Object[]) value;
        final ArrayVariable arrayVariable = new ArrayVariable(componentType, values.length);
        for (int i = 0; i < values.length; i++) {
          arrayVariable.setElement(i, getExpression(values[i]));
        }
        return arrayVariable;
      }
    }
    // throw new AnalyzeException("Unsupported element class when converting to expression: " +
    // value.getClass().getName());
    return new ObjectInstance(value);
  }

  /**
   * Converts the given {@code numberLiteral} to a literal {@link Expression} matching the given
   * targetTypeName
   * 
   * @param numberLiteral the {@link Expression} to convert.
   * @param targetTypeName the name of the target type
   * @return the literal {@link Expression} converted in the given type.
   */
  public static Expression getLiteral(final NumberLiteral numberLiteral,
      final String targetTypeName) {
    final Number value = numberLiteral.getValue();
    if (char.class.getName().equals(targetTypeName)
        || Character.class.getName().equals(targetTypeName)) {
      return new CharacterLiteral((char) value.intValue());
    } else if (boolean.class.getName().equals(targetTypeName)
        || Boolean.class.getName().equals(targetTypeName)) {
      switch (value.intValue()) {
        case 0:
          return new BooleanLiteral(false);
        default:
          return new BooleanLiteral(true);
      }
    }
    return numberLiteral;
  }

}
