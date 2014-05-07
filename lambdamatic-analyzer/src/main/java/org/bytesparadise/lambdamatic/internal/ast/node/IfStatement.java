/**
 * 
 */
package org.bytesparadise.lambdamatic.internal.ast.node;

/**
 * An If Statement:
 * {@code if( Expression) ThenStatement else ElseStatement} 
 * 
 * @author xcoulon
 *
 */
public class IfStatement extends Statement {

	/** The Expression in the "if()".*/
	private final Expression ifExpression;

	/** The Statement to execution when the expression is true.*/
	private final Statement thenStatement;

	/** The Statement to execution when the expression is false.*/
	private final Statement elseStatement;
	
	/**
	 * The full constructor
	 * @param ifExpression The Expression in the "if()"
	 * @param thenStatement The Statement to execution when the expression is true.
	 * @param elseStatement The Statement to execution when the expression is false.
	 */
	public IfStatement(final Expression ifExpression, final Statement thenStatement, final Statement elseStatement) {
		this.ifExpression = ifExpression;
		this.thenStatement = thenStatement;
		this.thenStatement.setParent(this);
		this.elseStatement = elseStatement;
		this.elseStatement.setParent(this);
	}

	@Override
	public StatementType getStatementType() {
		return StatementType.IF_STMT;
	}
	
	@Override
	public void accept(final StatementVisitor visitor) {
		if(visitor.visit(this)) {
			thenStatement.accept(visitor);
			elseStatement.accept(visitor);
		}
	}

	/**
	 * @return the ifExpression
	 */
	public Expression getIfExpression() {
		return ifExpression;
	}

	/**
	 * @return the thenStatement
	 */
	public Statement getThenStatement() {
		return thenStatement;
	}

	/**
	 * @return the elseStatement
	 */
	public Statement getElseStatement() {
		return elseStatement;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("if(");
		builder.append(this.ifExpression.toString()).append(") {");
		builder.append(this.thenStatement.toString());
		builder.append("} else {");
		builder.append(this.elseStatement.toString()).append('}');
		return builder.toString();
	}
	
	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((elseStatement == null) ? 0 : elseStatement.hashCode());
		result = prime * result + ((ifExpression == null) ? 0 : ifExpression.hashCode());
		result = prime * result + ((thenStatement == null) ? 0 : thenStatement.hashCode());
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
		IfStatement other = (IfStatement) obj;
		if (elseStatement == null) {
			if (other.elseStatement != null)
				return false;
		} else if (!elseStatement.equals(other.elseStatement))
			return false;
		if (ifExpression == null) {
			if (other.ifExpression != null)
				return false;
		} else if (!ifExpression.equals(other.ifExpression))
			return false;
		if (thenStatement == null) {
			if (other.thenStatement != null)
				return false;
		} else if (!thenStatement.equals(other.thenStatement))
			return false;
		return true;
	}
	
}
