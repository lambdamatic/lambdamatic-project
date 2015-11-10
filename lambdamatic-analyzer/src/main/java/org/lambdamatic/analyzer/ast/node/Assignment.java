/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

/**
 * An assignment Expression.
 * 
 * @author Xavier Coulon
 *
 */
public class Assignment extends ComplexExpression {

  /**
   * the source {@link Expression}, to which the value will be assigned.
   */
  private Expression source;

  /**
   * the value {@link Expression} that is to be assigned to the source.
   */
  private Expression assignedValue;

  /**
   * Constructor.
   * 
   * @param source the source of the assignment
   * @param assignedValue the value to assign
   */
  public Assignment(final Expression source, final Expression assignedValue) {
    this(generateId(), source, assignedValue, false);
  }

  /**
   * Full constructor.
   * 
   * @param id the expression id
   * @param source the source of the assignment
   * @param assignedValue the value to assign
   * @param inverted if the expression is inverted
   */
  public Assignment(final int id, final Expression source, final Expression assignedValue,
      final boolean inverted) {
    super(id, inverted);
    setSource(source);
    setAssignedValue(assignedValue);
  }

  @Override
  public ExpressionType getExpressionType() {
    return ExpressionType.ASSIGNMENT;
  }

  /**
   * @return the source {@link Expression}.
   */
  public Expression getSource() {
    return this.source;
  }

  private void setSource(final Expression source) {
    this.source = source;
    this.source.setParent(this);
  }

  /**
   * @return the value to be assigned.
   */
  public Expression getAssignedValue() {
    return this.assignedValue;
  }

  private void setAssignedValue(Expression assignedValue) {
    this.assignedValue = assignedValue;
    this.assignedValue.setParent(this);
  }

  @Override
  public Class<?> getJavaType() {
    return this.source.getJavaType();
  }

  @Override
  public boolean canBeInverted() {
    return false;
  }

  @Override
  public Expression duplicate(int id) {
    return new Assignment(id, this.source.duplicate(), this.assignedValue.duplicate(),
        this.source.isInverted());
  }

  @Override
  public void accept(final ExpressionVisitor visitor) {
    if (visitor.visit(this)) {
      this.source.accept(visitor);
      this.assignedValue.accept(visitor);
    }
  }

  @Override
  public void replaceElement(final Expression oldExpression, final Expression newExpression) {
    if (oldExpression.equals(this.source)) {
      setSource(newExpression);
    } else if (oldExpression.equals(this.assignedValue)) {
      setAssignedValue(newExpression);
    }
  }

  @Override
  public String toString() {
    return this.source + " = " + this.assignedValue;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.assignedValue == null) ? 0 : this.assignedValue.hashCode());
    result = prime * result + ((this.source == null) ? 0 : this.source.hashCode());
    return result;
  }

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
    final Assignment other = (Assignment) obj;
    if (this.assignedValue == null) {
      if (other.assignedValue != null) {
        return false;
      }
    } else if (!this.assignedValue.equals(other.assignedValue)) {
      return false;
    }
    if (this.source == null) {
      if (other.source != null) {
        return false;
      }
    } else if (!this.source.equals(other.source)) {
      return false;
    }
    return true;
  }



}
