/*******************************************************************************
 * Copyright (c) 2014, 2015 Red Hat. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link Expression} visitor that will count the {@link Expression}s or counters in an
 * {@link CompoundExpression}.
 * 
 * @author Xavier Coulon
 *
 */
public class ExpressionsCounter extends ExpressionVisitor {

  /**
   * Count of each operand found in the root {@link Expression} being visited.
   */
  private Map<Expression, AtomicInteger> counters = new HashMap<>();

  /**
   * @return the counters index by <strong>absolute</strong> form of expression.
   */
  public Map<Expression, AtomicInteger> getCounters() {
    return this.counters;
  }

  /**
   * Adds or increments the counter for the <strong>absolute</strong> version of the given
   * {@link Expression}.
   * 
   * @param expr the expression to count.
   * 
   * @see Expression#getAbsolute()
   */
  private void count(final Expression expr) {
    final Expression absolute = expr.getAbsolute();
    if (!this.counters.containsKey(absolute)) {
      this.counters.put(absolute, new AtomicInteger());
    }
    this.counters.get(absolute).incrementAndGet();
  }

  @Override
  public boolean visit(final Expression expr) {
    // for anything except Local variables, count the expression itself.
    switch (expr.getExpressionType()) {
      case LOCAL_VARIABLE:
      case CAPTURED_ARGUMENT_REF:
        // skip those elements, they can probably not be simplified here
        break;
      default:
        count(expr);
    }
    return super.visit(expr);
  }

  @Override
  public boolean visitMethodInvocationExpression(final MethodInvocation expr) {
    // don't count anything, the 'expr' itself was already counted during the call to visit(final
    // Expression expr) above.
    // don't visit source expression and arguments, it makes no sense in this context.
    return false;
  }

}

