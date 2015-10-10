/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

import org.lambdamatic.analyzer.exception.AnalyzeException;
import org.lambdamatic.analyzer.utils.MathUtils;

/**
 * An operation (can be an addition, subtraction, multiplication, division, etc.)
 * 
 * @author Xavier Coulon
 *
 */
public class Operation extends ComplexExpression {

  /**
   * The operator type.
   */
  public enum Operator {
    /** Addition operator. */
    ADD, /** Substraction operator. */
    SUBTRACT, /** Multiplication operator. */
    MULTIPLY, /** Division operator. */
    DIVIDE;
  }

  /** The operator. */
  private final Operator operator;

  /** The left operand. */
  private Expression leftOperand;

  /** The right operand. */
  private Expression rightOperand;

  /**
   * Constructor.
   * 
   * @param operator the operator
   * @param leftOperand the left operand in the operation
   * @param rightOperand the right operand in the operation
   */
  public Operation(final Operator operator, final Expression leftOperand,
      final Expression rightOperand) {
    this(generateId(), operator, leftOperand, rightOperand, false);
  }

  /**
   * Full constructor.
   * 
   * @param id the expression id
   * @param operator the operator
   * @param leftOperand the left operand in the operation
   * @param rightOperand the right operand in the operation
   * @param inverted if the operation is inverted
   */
  public Operation(final int id, final Operator operator, final Expression leftOperand,
      final Expression rightOperand, final boolean inverted) {
    super(id, inverted);
    this.operator = operator;
    setLeftOperand(leftOperand);
    setRightOperand(rightOperand);
  }

  @Override
  public ExpressionType getExpressionType() {
    return ExpressionType.OPERATION;
  }

  /**
   * @return the {@link Operator}.
   */
  public Operator getOperator() {
    return this.operator;
  }

  /**
   * @return the left-hand operand in this {@link Operation}.
   */
  public Expression getLeftOperand() {
    return this.leftOperand;
  }

  private void setLeftOperand(final Expression leftOperand) {
    this.leftOperand = leftOperand;
    this.leftOperand.setParent(this);
  }

  /**
   * @return the right-hand operand in this {@link Operation}.
   */
  public Expression getRightOperand() {
    return this.rightOperand;
  }

  private void setRightOperand(final Expression rightOperand) {
    this.rightOperand = rightOperand;
    this.rightOperand.setParent(this);
  }

  /**
   * (assumes that both left and right operand have the same Java type).
   */
  @Override
  public Class<?> getJavaType() {
    return this.leftOperand.getJavaType();
  }

  @Override
  public boolean canBeInverted() {
    return false;
  }

  @Override
  public Expression duplicate(int id) {
    return new Operation(id, this.operator, this.leftOperand.duplicate(),
        this.rightOperand.duplicate(), isInverted());
  }

  @Override
  public Expression duplicate() {
    return duplicate(generateId());
  }

  /**
   * @return the value of {@code this} Expression.
   */
  public Object getValue() {
    switch (this.operator) {
      case ADD:
        return new NumberLiteral(MathUtils.add((Number) this.leftOperand.getValue(),
            (Number) this.rightOperand.getValue()), true);
      case SUBTRACT:
        return new NumberLiteral(MathUtils.subtract((Number) this.leftOperand.getValue(),
            (Number) this.rightOperand.getValue()), true);
      case MULTIPLY:
        return new NumberLiteral(MathUtils.multiply((Number) this.leftOperand.getValue(),
            (Number) this.rightOperand.getValue()), true);
      case DIVIDE:
        return new NumberLiteral(MathUtils.divide((Number) this.leftOperand.getValue(),
            (Number) this.rightOperand.getValue()), true);
      default:
        throw new AnalyzeException("Operator '" + this.operator
            + "' is not supported while trying to evaluate the operation.");
    }
  }

  @Override
  public void accept(final ExpressionVisitor visitor) {
    if (visitor.visit(this)) {
      this.leftOperand.accept(visitor);
      this.rightOperand.accept(visitor);
    }
  }

  @Override
  public void replaceElement(final Expression oldExpression, final Expression newExpression) {
    if (oldExpression.equals(this.leftOperand)) {
      setLeftOperand(newExpression);
    } else if (oldExpression.equals(this.rightOperand)) {
      setRightOperand(newExpression);
    }
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append(this.leftOperand);
    switch (this.operator) {
      case ADD:
        builder.append(" + ");
        break;
      case DIVIDE:
        builder.append(" / ");
        break;
      case MULTIPLY:
        builder.append(" * ");
        break;
      case SUBTRACT:
        builder.append(" - ");
        break;
      default:
        break;

    }
    builder.append(this.rightOperand);
    return builder.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.leftOperand == null) ? 0 : this.leftOperand.hashCode());
    result = prime * result + ((this.operator == null) ? 0 : this.operator.hashCode());
    result = prime * result + ((this.rightOperand == null) ? 0 : this.rightOperand.hashCode());
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
    Operation other = (Operation) obj;
    if (this.leftOperand == null) {
      if (other.leftOperand != null) {
        return false;
      }
    } else if (!this.leftOperand.equals(other.leftOperand)) {
      return false;
    }
    if (this.operator != other.operator) {
      return false;
    }
    if (this.rightOperand == null) {
      if (other.rightOperand != null) {
        return false;
      }
    } else if (!this.rightOperand.equals(other.rightOperand)) {
      return false;
    }
    return true;
  }

}
