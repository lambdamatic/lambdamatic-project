/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

/**
 * A node representing a {@link String} literal.
 * 
 * @author Xavier Coulon
 *
 */
public class StringLiteral extends ObjectInstance {

  /**
   * Full constructor
   * <p>
   * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
   * </p>
   * 
   * @param value the literal value
   */
  public StringLiteral(final String value) {
    this(generateId(), value, false);
  }

  /**
   * Full constructor with given id.
   * 
   * @param id the synthetic id of this node
   * @param value the literal value
   * @param inverted the inversion flag of this {@link Expression}
   */
  public StringLiteral(final int id, final String value, final boolean inverted) {
    super(id, value, inverted);
  }

  @Override
  public StringLiteral duplicate(int id) {
    return new StringLiteral(id, getValue(), isInverted());
  }

  @Override
  public String getValue() {
    return (String) super.getValue();
  }

  @Override
  public ExpressionType getExpressionType() {
    return ExpressionType.STRING_LITERAL;
  }

  @Override
  public String toString() {
    return getValue();
  }

}
