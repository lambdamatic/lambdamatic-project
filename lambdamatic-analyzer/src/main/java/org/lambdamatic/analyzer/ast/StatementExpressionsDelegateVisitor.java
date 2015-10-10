/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast;

import org.lambdamatic.analyzer.ast.node.ControlFlowStatement;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.ExpressionStatement;
import org.lambdamatic.analyzer.ast.node.ExpressionVisitor;
import org.lambdamatic.analyzer.ast.node.ExpressionVisitorUtil;
import org.lambdamatic.analyzer.ast.node.ReturnStatement;
import org.lambdamatic.analyzer.ast.node.Statement;
import org.lambdamatic.analyzer.ast.node.StatementVisitor;

/**
 * Visits a given {@link Statement} and applies the given {@link ExpressionVisitor} on the
 * {@link Expression} that is part of the visited {@link Statement}.
 * 
 */
public class StatementExpressionsDelegateVisitor extends StatementVisitor {

  private final ExpressionVisitor expressionVisitor;

  /**
   * Constructor.
   * 
   * @param expressionVisitor the visitor to use on the expressions
   */
  public StatementExpressionsDelegateVisitor(final ExpressionVisitor expressionVisitor) {
    this.expressionVisitor = expressionVisitor;
  }

  @Override
  public boolean visitControlFlowStatement(final ControlFlowStatement controlFlowStatement) {
    ExpressionVisitorUtil.visit(controlFlowStatement.getControlFlowExpression(),
        this.expressionVisitor);
    return super.visitControlFlowStatement(controlFlowStatement);
  }

  @Override
  public boolean visitExpressionStatement(final ExpressionStatement expressionStatement) {
    ExpressionVisitorUtil.visit(expressionStatement.getExpression(), this.expressionVisitor);
    return super.visitExpressionStatement(expressionStatement);
  }

  @Override
  public boolean visitReturnStatement(final ReturnStatement returnStatementNode) {
    final Expression returnExpression = returnStatementNode.getExpression();
    ExpressionVisitorUtil.visit(returnExpression, this.expressionVisitor);
    return super.visitReturnStatement(returnStatementNode);
  }

}
