/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

/**
 * A Character Literal Expression.
 * 
 * @author Xavier Coulon
 *
 */
public class CharacterLiteral extends ObjectInstance {

  /**
   * Full constructor.
   * <p>
   * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
   * </p>
   * 
   * @param value the literal value
   */
  public CharacterLiteral(final Character value) {
    this(generateId(), value, false);
  }

  /**
   * Full constructor with given id.
   * 
   * @param id the synthetic id of this {@link Expression}.
   * @param value the literal value
   * @param inverted the inversion flag of this {@link Expression}.
   */
  public CharacterLiteral(final int id, final Character value, final boolean inverted) {
    super(id, value, inverted);
  }

  @Override
  public CharacterLiteral duplicate(int id) {
    return new CharacterLiteral(id, getValue(), isInverted());
  }

  @Override
  public ExpressionType getExpressionType() {
    return ExpressionType.CHARACTER_LITERAL;
  }

  @Override
  public Character getValue() {
    return (Character) super.getValue();
  }

  @Override
  public String toString() {
    return "'" + super.getValue().toString() + "'";
  }

}

