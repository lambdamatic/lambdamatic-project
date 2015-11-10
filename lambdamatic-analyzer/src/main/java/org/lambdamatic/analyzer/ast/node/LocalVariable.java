/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

/**
 * Variable passed during the call to the Lambda Expression serialized method.
 * 
 * @author Xavier Coulon
 *
 */
public class LocalVariable extends Expression {

  /** The variable index (in the bytecode). */
  private final int index;

  /** The variable name. */
  private final String name;

  /**
   * The variable type's associated {@link Class}.
   */
  private final Class<?> type;

  /**
   * Constructor when local variable is provided
   * <p>
   * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
   * </p>
   * 
   * @param index The variable index (as specified in the method bytecode).
   * @param name The variable name.
   * @param type The variable type.
   */
  public LocalVariable(final int index, final String name, final Class<?> type) {
    this(generateId(), index, name, type, false);
    if (type == null) {
      throw new IllegalArgumentException("Type of local variable '" + name + "' must not be null");
    }

  }

  /**
   * Full constructor.
   * 
   * @param id the synthetic id of this {@link Expression}.
   * @param index the variable index (as defined in the bytecode)
   * @param name The variable name
   * @param type the fully qualified type name of the variable.
   * @param inverted the inversion flag of this {@link Expression}.
   */
  public LocalVariable(final int id, final int index, final String name, final Class<?> type,
      final boolean inverted) {
    super(id, inverted);
    this.index = index;
    this.name = name;
    this.type = type;
  }

  @Override
  public LocalVariable duplicate(int id) {
    return new LocalVariable(id, this.index, this.name, this.type, isInverted());
  }

  /**
   * @return index the variable index (as defined in the bytecode).
   */
  public int getIndex() {
    return this.index;
  }

  /**
   * @return the variable name.
   */
  public String getName() {
    return this.name;
  }

  /**
   * @return the variable type.
   */
  public Class<?> getType() {
    return this.type;
  }

  @Override
  public ExpressionType getExpressionType() {
    return ExpressionType.LOCAL_VARIABLE;
  }

  @Override
  public ComplexExpression getParent() {
    return (ComplexExpression) super.getParent();
  }

  @Override
  public Class<?> getJavaType() {
    return this.type;
  }

  @Override
  public boolean canBeInverted() {
    return false;
  }

  @Override
  public String toString() {
    return this.name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getExpressionType() == null) ? 0 : getExpressionType().hashCode());
    result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
    result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
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
    LocalVariable other = (LocalVariable) obj;
    if (this.name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!this.name.equals(other.name)) {
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


