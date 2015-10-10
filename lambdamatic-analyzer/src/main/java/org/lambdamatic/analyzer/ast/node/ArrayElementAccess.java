/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

import java.util.function.Predicate;

/**
 * A somehow "fake" expression that corresponds to the access to a particular element in an array.
 * In the Java DSL, this is expressed by a method call such as {@code foo.bar(1)} where the
 * {@code bar} method is annotated with {@link ArrayElementAccess}.
 * 
 * <p>
 * Note: As opposed to a {@link FieldAccess}, there is no real underlying field in the Java code, so
 * special treatment is required during the encoding of any {@link Predicate} that might use this
 * kind of element.
 * </p>
 */
public class ArrayElementAccess extends Expression {

  /**
   * the FieldAccess designing the underlying Java field with is mapped on an indexed collection.
   */
  private final FieldAccess sourceField;

  /** the index of the particular element to access. */
  private String index;

  private final Class<?> returnType;

  /**
   * Constructor
   * 
   * @param methodInvocation the {@link MethodInvocation} to convert.
   */
  public ArrayElementAccess(final MethodInvocation methodInvocation) {
    this(generateId(), (FieldAccess) methodInvocation.getSource(),
        methodInvocation.getArguments().get(0).toString(), methodInvocation.getReturnType(), false);
  }

  /**
   * Constructor.
   * 
   * @param id the expression id
   * @param sourceField the field that maps to an array
   * @param index the index of the specific element to access
   * @param returnType the actual type returned when traversing this {@link ArrayElementAccess}
   * @param inverted flag to indicate if this expression is inverted
   */
  public ArrayElementAccess(final int id, final FieldAccess sourceField, final String index,
      final Class<?> returnType, final boolean inverted) {
    super(id, inverted);
    this.sourceField = sourceField;
    this.index = index;
    this.returnType = returnType;
  }

  /**
   * @return the source {@link FieldAccess}.
   */
  public FieldAccess getSourceField() {
    return this.sourceField;
  }

  /**
   * @return the index of the specific element to access.
   */
  public String getIndex() {
    return this.index;
  }

  @Override
  public ExpressionType getExpressionType() {
    return ExpressionType.ARRAY_ELEMENT_ACCESS;
  }

  /**
   * <strong>Note:</strong> The underlying Java type that is returned by this
   * {@link ArrayElementAccess} expression.
   */
  @Override
  public Class<?> getJavaType() {
    return this.returnType;
  }

  @Override
  public boolean canBeInverted() {
    return false;
  }

  @Override
  public Expression duplicate(int id) {
    return new ArrayElementAccess(id, this.sourceField, this.index, this.returnType, isInverted());
  }

  @Override
  public Expression duplicate() {
    return duplicate(generateId());
  }

  @Override
  public String toString() {
    return this.sourceField.toString() + ".get(" + this.index + ")";
  }
}
