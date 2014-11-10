/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

/**
 * Wrapper {@link Statement} for an {@link Expression} node.
 * @author xcoulon
 *
 */
public class ExpressionStatement extends Statement {
	
	/** The actual Expression.*/
	private final Expression expression;

	/**
	 * Full constructor
	 * @param expression The actual Expression.
	 */
	public ExpressionStatement(final Expression expression) {
		this.expression = expression;
	}

	@Override
	public Statement.StatementType getStatementType() {
		return StatementType.EXPRESSION_STMT;
	}

	/**
	 * @return the actuall expression
	 */
	public Expression getExpression() {
		return expression;
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExpressionStatement other = (ExpressionStatement) obj;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		return true;
	}
	
}

