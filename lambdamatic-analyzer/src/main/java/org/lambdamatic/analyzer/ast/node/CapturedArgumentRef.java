/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

import java.lang.invoke.SerializedLambda;

import org.lambdamatic.analyzer.exception.AnalyzeException;

/**
 * Not an actual {@link CapturedArgument}, but just a reference (for further processing) to a
 * {@link CapturedArgument}. The raw Expression that will be kept in cache will use these
 * {@link CapturedArgumentRef} until it is evaluated with the actual {@link CapturedArgument}.
 * 
 * @author Xavier Coulon
 *
 */
public class CapturedArgumentRef extends Expression {

  /**
   * index of the {@link CapturedArgument} in the {@link SerializedLambda}.
   */
  private final int index;
  /** the Java type of the referenced Java value. */
  private final Class<?> javaType;

  /**
   * Constructor
   * 
   * <p>
   * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
   * </p>
   * 
   * @param index the captured value index
   * @param javaType the actual type of the captured argument
   */
  public CapturedArgumentRef(final int index, final Class<?> javaType) {
    this(generateId(), index, javaType, false);
  }

  /**
   * Full constructor with given id.
   * 
   * @param id the synthetic id of this {@link Expression}
   * @param index the index in of the argument in the local variables table
   * @param javaType the actual type of the captured argument
   * @param inverted the inversion flag of this {@link Expression}
   */
  public CapturedArgumentRef(final int id, final int index, final Class<?> javaType,
      final boolean inverted) {
    super(id, inverted);
    this.index = index;
    this.javaType = javaType;
  }

  @Override
  public ComplexExpression getParent() {
    return (ComplexExpression) super.getParent();
  }

  @Override
  public CapturedArgumentRef duplicate(int id) {
    return new CapturedArgumentRef(id, this.index, this.javaType, isInverted());
  }

  @Override
  public Object getValue() {
    throw new AnalyzeException("Capture Argument reference does not hold any value by itself.");
  }

  /**
   * @return the index of the captured argument.
   * @see SerializedLambda#getCapturedArg(int)
   */
  public int getArgumentIndex() {
    return this.index;
  }

  @Override
  public ExpressionType getExpressionType() {
    return ExpressionType.CAPTURED_ARGUMENT_REF;
  }

  @Override
  public Class<?> getJavaType() {
    return this.javaType;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.lambdamatic.analyzer.ast.node.Expression#canBeInverted()
   */
  @Override
  public boolean canBeInverted() {
    return false;
  }

  @Override
  public String toString() {
    return "<CapturedArgument#" + this.index + ">";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + this.index;
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
    CapturedArgumentRef other = (CapturedArgumentRef) obj;
    if (this.index != other.index) {
      return false;
    }
    return true;
  }

}
