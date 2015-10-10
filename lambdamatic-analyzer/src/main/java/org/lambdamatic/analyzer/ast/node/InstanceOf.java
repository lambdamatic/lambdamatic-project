/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

/**
 * InstanceOf expression AST node type: {@code Expression instanceof Type}.
 *
 * @author xcoulon
 *
 */
public class InstanceOf extends Expression {

  /** The expression being evaluated. */
  private final Expression expression;

  /** The expected expression type. */
  private final Type type;

  /**
   * Full constructor.
   * <p>
   * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
   * </p>
   * 
   * @param expression the expression being evaluated.
   * @param type the expected expression type.
   */
  public InstanceOf(final Expression expression, final Type type) {
    this(generateId(), expression, type, false);
  }

  /**
   * Full constructor with given id.
   * 
   * @param id the synthetic id of this {@link Expression}.
   * @param expression the expression being evaluated
   * @param type the type being evaluated
   * @param inverted the inversion flag of this {@link Expression}.
   */
  public InstanceOf(final int id, final Expression expression, final Type type,
      final boolean inverted) {
    super(id, inverted);
    this.expression = expression;
    this.type = type;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate(int)
   */
  @Override
  public InstanceOf duplicate(int id) {
    return new InstanceOf(id, getExpression(), getType(), isInverted());
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate()
   */
  @Override
  public InstanceOf duplicate() {
    return duplicate(generateId());
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.lambdamatic.analyzer.ast.node.Expression#getExpressionType()
   */
  @Override
  public ExpressionType getExpressionType() {
    return ExpressionType.INSTANCE_OF;
  }

  /**
   * {@link InstanceOf} return a {@link Boolean} type. {@inheritDoc}
   * 
   * @see org.lambdamatic.analyzer.ast.node.Expression#getJavaType()
   */
  @Override
  public Class<?> getJavaType() {
    return Boolean.class;
  }

  /**
   * @return the expression.
   */
  public Expression getExpression() {
    return this.expression;
  }

  /**
   * @return the type.
   */
  public Type getType() {
    return this.type;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.lambdamatic.analyzer.ast.node.Expression#inverse()
   */
  @Override
  public Expression inverse() {
    return new InstanceOf(generateId(), this.expression, this.type, !isInverted());
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.lambdamatic.analyzer.ast.node.Expression#canBeInverted()
   */
  @Override
  public boolean canBeInverted() {
    return true;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getExpressionType() == null) ? 0 : getExpressionType().hashCode());
    result = prime * result + ((this.expression == null) ? 0 : this.expression.hashCode());
    result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
    return result;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    InstanceOf other = (InstanceOf) obj;
    if (this.expression == null) {
      if (other.expression != null) {
        return false;
      }
    } else if (!this.expression.equals(other.expression)) {
      return false;
    }
    if (this.type == null) {
      if (other.type != null) {
        return false;
      }
    } else if (!this.type.equals(other.type)) {
      return false;
    }
    return true;
  }

}

