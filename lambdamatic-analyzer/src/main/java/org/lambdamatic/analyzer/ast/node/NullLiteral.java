/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

/**
 * A node representing the <code>null</code> literal.
 *
 */
public class NullLiteral extends ObjectInstance {

  /**
   * Full constructor
   * <p>
   * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
   * </p>
   * 
   */
  public NullLiteral() {
    this(generateId(), false);
  }

  /**
   * Full constructor with given id.
   * 
   * @param id the synthetic id of this {@link Expression}.
   * @param inverted the inversion flag of this {@link Expression}.
   */
  public NullLiteral(final int id, final boolean inverted) {
    super(id, null, inverted);
  }

  @Override
  public NullLiteral duplicate(int id) {
    return new NullLiteral(id, isInverted());
  }

  @Override
  public ExpressionType getExpressionType() {
    return ExpressionType.NULL_LITERAL;
  }

  @Override
  public String toString() {
    return "null";
  }

}
