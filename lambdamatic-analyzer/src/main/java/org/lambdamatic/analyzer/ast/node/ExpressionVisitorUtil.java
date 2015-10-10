/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A utility class to visit {@link Expression}.
 * 
 * @author Xavier Coulon
 *
 */
public class ExpressionVisitorUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExpressionVisitorUtil.class);

  /**
   * Uses the given {@link ExpressionVisitor} to visit the given {@link Expression}. The
   * {@link Expression} is wrapped in an {@link Expression} parent to allow for top-level
   * replacement (as a parent is always needed). The resulting visited/rewritten expression is
   * detached from the temporary wrapper before being returned.
   * 
   * @param expression the expression to visit.
   * @param visitor the visitor to use.
   * @return the result of the visit of the given visitor in the given expression
   */
  public static Expression visit(final Expression expression, final ExpressionVisitor visitor) {
    // wrap the expression to make sure it has a parent
    // because in some cases (eg: a boolean expression, the MethodInvocation#delete() would fail)
    final ExpressionWrapper wrapper = new ExpressionWrapper(expression);
    expression.accept(visitor);
    // now, detach and return the resulting wrapped expression
    final Expression resultExpression = wrapper.getExpression();
    resultExpression.setParent(null);
    LOGGER.debug("Result after : " + visitor.getClass().getName() + " visit: " + resultExpression);
    return resultExpression;
  }

  static class ExpressionWrapper extends ComplexExpression {

    private Expression expression = null;

    public ExpressionWrapper(final Expression expression) {
      super(generateId(), false);
      this.expression = expression;
      this.expression.setParent(this);
    }

    /**
     * @return the currently wrapped {@link Expression}. (it may not the one given in the
     *         constructor if the {@link ExpressionWrapper#replaceElement(Expression, Expression)}
     *         was called.
     */
    public Expression getExpression() {
      return this.expression;
    }

    @Override
    public void replaceElement(final Expression oldExpression, final Expression newExpression) {
      this.expression = newExpression;
    }

    @Override
    public ExpressionType getExpressionType() {
      return this.expression.getExpressionType();
    }

    @Override
    public Class<?> getJavaType() {
      return this.expression.getJavaType();
    }

    @Override
    public Expression inverse() {
      return this;
    }

    @Override
    public boolean canBeInverted() {
      return false;
    }

    @Override
    public Expression duplicate(int id) {
      return null;
    }

    @Override
    public Expression duplicate() {
      return null;
    }

    @Override
    public String toString() {
      return this.expression.toString() + " (wrapped)";
    }

  }

}
