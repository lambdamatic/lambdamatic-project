/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

import java.util.List;

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

	/** The Statements to execution when the expression is true.*/
	private final List<Statement> thenStatements;

	/** The Statements to execution when the expression is false.*/
	private final List<Statement> elseStatements;
	
	/**
	 * The full constructor
	 * @param ifExpression The Expression in the "if()"
	 * @param thenStatements The Statement to execution when the expression is true.
	 * @param elseStatements The Statement to execution when the expression is false.
	 */
	public IfStatement(final Expression ifExpression, final List<Statement> thenStatements, final List<Statement> elseStatements) {
		this.ifExpression = ifExpression;
		this.thenStatements = thenStatements;
		this.thenStatements.stream().forEach(s -> s.setParent(this));
		this.elseStatements = elseStatements;
		this.elseStatements.stream().forEach(s -> s.setParent(this));
	}

	@Override
	public StatementType getStatementType() {
		return StatementType.IF_STMT;
	}
	
	@Override
	public void accept(final StatementVisitor visitor) {
		if(visitor.visit(this)) {
			thenStatements.stream().forEach(s -> s.accept(visitor));
			elseStatements.stream().forEach(s -> s.accept(visitor));
		}
	}

	/**
	 * @return the ifExpression
	 */
	public Expression getIfExpression() {
		return ifExpression;
	}

	/**
	 * @return the thenStatements
	 */
	public List<Statement> getThenStatements() {
		return thenStatements;
	}

	/**
	 * @return the elseStatements
	 */
	public List<Statement> getElseStatements() {
		return elseStatements;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("if(");
		builder.append(this.ifExpression.toString()).append(") {");
		builder.append(this.thenStatements.toString());
		builder.append("} else {");
		builder.append(this.elseStatements.toString()).append('}');
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
		result = prime * result + ((elseStatements == null) ? 0 : elseStatements.hashCode());
		result = prime * result + ((ifExpression == null) ? 0 : ifExpression.hashCode());
		result = prime * result + ((thenStatements == null) ? 0 : thenStatements.hashCode());
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
		if (elseStatements == null) {
			if (other.elseStatements != null)
				return false;
		} else if (!elseStatements.equals(other.elseStatements))
			return false;
		if (ifExpression == null) {
			if (other.ifExpression != null)
				return false;
		} else if (!ifExpression.equals(other.ifExpression))
			return false;
		if (thenStatements == null) {
			if (other.thenStatements != null)
				return false;
		} else if (!thenStatements.equals(other.thenStatements))
			return false;
		return true;
	}
	
}

