/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;


/**
 * A {@link Statement} visitor.
 * 
 * @author xcoulon
 *
 */
public abstract class StatementVisitor {

  /**
   * Dispatch to the other visitXYZ methods
   * 
   * @param stmt the {@link Statement} to visit
   * @return true if the visit on the given statement's children should continue, false otherwise.
   */
  public boolean visit(final Statement stmt) {
    if (stmt != null) {
      switch (stmt.getStatementType()) {
        case EXPRESSION_STMT:
          return visitExpressionStatement((ExpressionStatement) stmt);
        case CONTROL_FLOW_STMT:
          return visitControlFlowStatement((ControlFlowStatement) stmt);
        case RETURN_STMT:
          return visitReturnStatement((ReturnStatement) stmt);
        default:
          break;
      }
    }
    return false;
  }

  /**
   * @param expressionStatement the {@link ExpressionStatement} to visit
   * @return <code>true</code> by default. Can be overridden.
   */
  @SuppressWarnings("static-method")
  public boolean visitExpressionStatement(final ExpressionStatement expressionStatement) {
    return true;
  }

  /**
   * @param controlFlowStatement the {@link ControlFlowStatement} to visit
   * @return <code>true</code> by default. Can be overridden.
   */
  @SuppressWarnings("static-method")
  public boolean visitControlFlowStatement(final ControlFlowStatement controlFlowStatement) {
    return true;
  }

  /**
   * @param returnStatementNode the {@link ReturnStatement} to visit
   * @return <code>true</code> by default. Can be overridden.
   */
  @SuppressWarnings("static-method")
  public boolean visitReturnStatement(final ReturnStatement returnStatementNode) {
    return true;
  }

}

