/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;


/**
 * A node representing a return statement.
 * 
 * @author xcoulon
 *
 */
public class ReturnStatement extends SimpleStatement {

  /**
   * Constructor.
   * 
   * @param expression the {@link Expression} returned in this {@link Statement}.
   */
  public ReturnStatement(final Expression expression) {
    super(expression);
  }

  @Override
  public StatementType getStatementType() {
    return StatementType.RETURN_STMT;
  }

  @Override
  public ReturnStatement duplicate() {
    return new ReturnStatement(this.expression.duplicate());
  }

  @Override
  public void accept(final StatementVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("return");
    if (this.expression != null) {
      builder.append(' ').append(this.expression.toString());
    }
    builder.append(';');
    return builder.toString();
  }

}

