/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

import org.lambdamatic.analyzer.ast.node.Expression.ExpressionType;

/**
 * Checks if the visited {@link Expression} matches the expected {@link ExpressionType}.
 * 
 * @author Xavier Coulon
 *
 */
public class ExpressionTypeMatcher extends ExpressionVisitor {

  private final ExpressionType expectedExpressionType;

  private boolean matchFound = false;

  /**
   * Constructor.
   * 
   * @param expectedExpressionType the type of {@link Expression} to match.
   */
  public ExpressionTypeMatcher(final ExpressionType expectedExpressionType) {
    this.expectedExpressionType = expectedExpressionType;
  }

  /**
   * @return <code>true</code> if the expected {@link ExpressionType} was found in the visited
   *         {@link Expression}, <code>false</code> otherwise.
   */
  public boolean isMatchFound() {
    return this.matchFound;
  }

  @Override
  public boolean visit(final Expression expr) {
    if (expr.getExpressionType() == this.expectedExpressionType) {
      this.matchFound = true;
      return false;
    }
    return super.visit(expr);
  }

}
