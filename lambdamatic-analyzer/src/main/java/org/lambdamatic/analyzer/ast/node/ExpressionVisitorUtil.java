/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class ExpressionVisitorUtil {

	/**
	 * Uses the given {@link ExpressionVisitor} to visit the given {@link Expression}. The {@link Expression} is wrapped
	 * in an {@link ExpressionWrapper} parent to allow for top-level replacement (as a parent is always needed). The
	 * resulting visited/rewritten expression is detached from the temporary wrapper before being returned.
	 * 
	 * @param expression
	 *            the expression to visit.
	 * @param visitor
	 *            the visitor to use.
	 */
	public static Expression visit(final Expression expression, final ExpressionVisitor visitor) {
		// wrap the expression to make sure it has a parent
		// because in some cases (eg: a boolean expression, the MethodInvocation#delete() would fail)
		final ExpressionWrapper wrapper = new ExpressionWrapper(expression);
		expression.accept(visitor);
		// now, detach and return the resulting wrapped expression
		final Expression resultExpression = wrapper.getExpression();
		resultExpression.setParent(null);
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
		 * @return the currently wrapped {@link Expression}. (it may not the one given in the constructor if the
		 *         {@link ExpressionWrapper#replaceElement(Expression, Expression)} was called.
		 */
		public Expression getExpression() {
			return expression;
		}

		@Override
		public void replaceElement(final Expression oldExpression, final Expression newExpression) {
			this.expression = newExpression;
		}

		@Override
		public ExpressionType getExpressionType() {
			return expression.getExpressionType();
		}

		@Override
		public Class<?> getJavaType() {
			return expression.getJavaType();
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
