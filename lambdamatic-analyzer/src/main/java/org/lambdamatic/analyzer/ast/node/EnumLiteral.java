/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

/**
 * An enumeration literal node.
 *
 */
public class EnumLiteral extends ObjectInstance {

  /**
   * Full constructor.
   * <p>
   * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
   * </p>
   * 
   * @param value the literal value
   */
  public EnumLiteral(final Enum<?> value) {
    this(generateId(), value, false);
  }

  /**
   * Full constructor with given id.
   * 
   * @param id the internal id of the node
   * @param value the literal value
   * @param inverted the inversion flag of this {@link Expression}
   */
  public EnumLiteral(final int id, final Enum<?> value, final boolean inverted) {
    super(id, value, inverted);
  }

  @Override
  public EnumLiteral duplicate(int id) {
    return new EnumLiteral(id, getValue(), isInverted());
  }

  @Override
  public Enum<?> getValue() {
    return (Enum<?>) super.getValue();
  }

  @Override
  public ExpressionType getExpressionType() {
    return ExpressionType.ENUM_LITERAL;
  }

  @Override
  public String toString() {
    return getValue().getClass().getName() + "." + getValue().name();
  }

}
