/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

/**
 * Wrapper {@link Statement} for an {@link Expression} node.
 * @author xcoulon
 *
 */
public class ExpressionStatement extends SimpleStatement {
	
	/**
	 * Full constructor
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
		return expression.toString() + ';';
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

