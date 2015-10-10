/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

/**
 * A Class Literal Expression.
 * 
 * @author Xavier Coulon
 *
 */
public class ClassLiteral extends ObjectInstance {

  /**
   * Full constructor
   * <p>
   * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
   * </p>
   * 
   * @param value the literal value
   */
  public ClassLiteral(final Class<?> value) {
    this(generateId(), value, false);
  }

  /**
   * Full constructor with given id
   * 
   * @param id the synthetic id of this {@link Expression}.
   * @param value the literal value
   * @param inverted the inversion flag of this {@link Expression}.
   */
  public ClassLiteral(final int id, final Class<?> value, final boolean inverted) {
    super(id, value, inverted);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate(int)
   */
  @Override
  public ClassLiteral duplicate(int id) {
    return new ClassLiteral(id, getValue(), isInverted());
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.lambdamatic.analyzer.ast.node.Expression#getExpressionType()
   */
  @Override
  public ExpressionType getExpressionType() {
    return ExpressionType.CLASS_LITERAL;
  }

  @Override
  public Class<?> getJavaType() {
    return (Class<?>) super.getValue();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.lambdamatic.analyzer.ast.node.Expression#getValue()
   */
  @Override
  public Class<?> getValue() {
    return (Class<?>) super.getValue();
  }

  @Override
  public String toString() {
    return this.getValue().getName();
  }

}
