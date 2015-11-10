/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.lambdamatic.analyzer.exception.AnalyzeException;

/**
 * A field access: {@code source.fieldName}
 * 
 * @author xcoulon
 *
 */
public class FieldAccess extends ComplexExpression {

  /** the Expression on containing the field to access (may change if it is evaluated). */
  private Expression source;

  /** the name of the accessed field. */
  private final String fieldName;

  /**
   * Full constructor
   * <p>
   * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
   * </p>
   * 
   * @param sourceExpression the source containing the field to access
   * @param fieldName the name of the accessed field
   */
  public FieldAccess(final Expression sourceExpression, final String fieldName) {
    this(sourceExpression, fieldName, false);
  }

  /**
   * Full constructor
   * <p>
   * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
   * </p>
   * 
   * @param sourceExpression the source containing the field to access
   * @param fieldName the name of the accessed field
   * @param inverted if this {@link FieldAccess} expression is inverted
   */
  public FieldAccess(final Expression sourceExpression, final String fieldName,
      final boolean inverted) {
    this(generateId(), sourceExpression, fieldName, inverted);
  }

  /**
   * Full constructor with given id.
   * 
   * @param id the synthetic id of this {@link Expression}.
   * @param sourceExpression the source {@link Expression} from which the field is accessed.
   * @param fieldName the name of the field to access
   * @param inverted the inversion flag of this {@link Expression}.
   */
  public FieldAccess(final int id, final Expression sourceExpression, final String fieldName,
      final boolean inverted) {
    super(id, inverted);
    setSourceExpression(sourceExpression);
    this.fieldName = fieldName;
  }

  private void setSourceExpression(final Expression expression) {
    this.source = expression;
    this.source.setParent(this);
  }

  @Override
  public ComplexExpression getParent() {
    return (ComplexExpression) super.getParent();
  }

  @Override
  public void accept(final ExpressionVisitor visitor) {
    this.source.accept(visitor);
    visitor.visit(this);
  }

  @Override
  public void replaceElement(final Expression oldSourceExpression,
      final Expression newSourceExpression) {
    if (oldSourceExpression == this.source) {
      setSourceExpression(newSourceExpression);
    }
  }

  @Override
  public boolean anyElementMatches(ExpressionType type) {
    return this.source.anyElementMatches(type);
  }

  /**
   * @return the source on containing the field to access.
   */
  public Expression getSource() {
    return this.source;
  }

  @Override
  public FieldAccess duplicate(int id) {
    return new FieldAccess(id, getSource().duplicate(), getFieldName(), isInverted());
  }

  /**
   * @return an inverted instance of the current element, only if the underlying Java type is
   *         {@link Boolean}.
   */
  @Override
  public FieldAccess inverse() {
    if (getJavaType() == Boolean.class || getJavaType() == boolean.class) {
      return new FieldAccess(this.source, this.fieldName, !isInverted());
    }
    throw new UnsupportedOperationException("Field access on '" + getFieldName()
        + "' with Java type '" + getJavaType() + "' does not support inversion.");
  }

  @Override
  public ExpressionType getExpressionType() {
    return ExpressionType.FIELD_ACCESS;
  }

  @Override
  public Class<?> getJavaType() {
    try {
      return this.source.getJavaType().getField(this.fieldName).getType();
    } catch (NoSuchFieldException | SecurityException e) {
      throw new AnalyzeException(
          "Failed to retrieve field '" + this.fieldName + "' in " + this.source, e);
    }
  }

  /**
   * @return the fieldName.
   */
  public String getFieldName() {
    return this.fieldName;
  }

  /**
   * Returns the underlying Java field.
   */
  @Override
  public Object getValue() {
    try {
      return FieldUtils.getField(this.source.getJavaType(), getFieldName());
    } catch (IllegalArgumentException e) {
      throw new AnalyzeException("Cannot retrieve field named '" + this.fieldName + "' on class "
          + this.source.getJavaType(), e);
    }
  }

  @Override
  public boolean canBeInverted() {
    return false;
  }

  @Override
  public String toString() {
    return this.source.toString() + "." + this.fieldName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getExpressionType() == null) ? 0 : getExpressionType().hashCode());
    result = prime * result + ((this.source == null) ? 0 : this.source.hashCode());
    result = prime * result + ((this.fieldName == null) ? 0 : this.fieldName.hashCode());
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
    FieldAccess other = (FieldAccess) obj;
    if (this.source == null) {
      if (other.source != null) {
        return false;
      }
    } else if (!this.source.equals(other.source)) {
      return false;
    }
    if (this.fieldName == null) {
      if (other.fieldName != null) {
        return false;
      }
    } else if (!this.fieldName.equals(other.fieldName)) {
      return false;
    }
    return true;
  }

}

