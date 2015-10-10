/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

/**
 * Wrapper {@link Statement} for an {@link Expression} node.
 * 
 * @author xcoulon
 *
 */
public class ExpressionStatement extends SimpleStatement {

  /**
   * Full constructor
   * 
   * @param expression The actual Expression.
   */
  public ExpressionStatement(final Expression expression) {
    super(expression);
  }

  @Override
  public ExpressionStatement duplicate() {
    return new ExpressionStatement(getExpression().duplicate());
  }

  @Override
  public Statement.StatementType getStatementType() {
    return StatementType.EXPRESSION_STMT;
  }

  @Override
  public String toString() {
    return this.expression.toString() + ';';
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.expression == null) ? 0 : this.expression.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   * 
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
    ExpressionStatement other = (ExpressionStatement) obj;
    if (this.expression == null) {
      if (other.expression != null) {
        return false;
      }
    } else if (!this.expression.equals(other.expression)) {
      return false;
    }
    return true;
  }

}

