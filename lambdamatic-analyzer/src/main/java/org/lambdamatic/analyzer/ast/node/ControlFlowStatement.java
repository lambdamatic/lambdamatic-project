/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An If Statement:
 * {@code if( Expression) ThenStatement else ElseStatement} 
 * 
 * @author xcoulon
 *
 */
public class ControlFlowStatement extends Statement {

	/** The Expression in the "if()".*/
	private final Expression controlFlowExpression;

	/** The {@link List} of {@link Statement} of {@link Statement}s to execution when the expression is true.*/
	private final List<Statement> thenStatements;

	/** The The {@link List} of {@link Statement} of {@link Statement}s to execution when the expression is false.*/
	private final List<Statement> elseStatements;
	
	/**
	 * The full constructor
	 * @param controlFlowExpression The Expression in the "if()"
	 * @param thenStatements The Statement to execution when the expression is true.
	 * @param elseStatements The Statement to execution when the expression is false.
	 */
	public ControlFlowStatement(final Expression controlFlowExpression, final List<Statement> thenStatements, final List<Statement> elseStatements) {
		this.controlFlowExpression = controlFlowExpression;
		this.thenStatements = thenStatements;
		this.thenStatements.stream().forEach(s -> s.setParent(this));
		this.elseStatements = elseStatements;
		this.elseStatements.stream().forEach(s -> s.setParent(this));
	}

	@Override
	public ControlFlowStatement duplicate() {
		final List<Statement> duplicateThenStatements = this.thenStatements.stream().map(s -> s.duplicate()).collect(Collectors.toList());
		final List<Statement> duplicateElseStatements = this.elseStatements.stream().map(s -> s.duplicate()).collect(Collectors.toList());
		return new ControlFlowStatement(this.controlFlowExpression.duplicate(), duplicateThenStatements, duplicateElseStatements);
	}
	
	@Override
	public StatementType getStatementType() {
		return StatementType.CONTROL_FLOW_STMT;
	}
	
	@Override
	public void accept(final StatementVisitor visitor) {
		if(visitor.visit(this)) {
			thenStatements.stream().forEach(s -> s.accept(visitor));
			elseStatements.stream().forEach(s -> s.accept(visitor));
		}
	}

	/**
	 * @return the controlFlowExpression
	 */
	public Expression getControlFlowExpression() {
		return controlFlowExpression;
	}

	/**
	 * @return the then-branch Statements
	 */
	public List<Statement> getThenStatements() {
		return thenStatements;
	}

	/**
	 * @return the else-branch Statements
	 */
	public List<Statement> getElseStatements() {
		return elseStatements;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("if(");
		builder.append(this.controlFlowExpression.toString()).append(") {");
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
		result = prime * result + ((controlFlowExpression == null) ? 0 : controlFlowExpression.hashCode());
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
		ControlFlowStatement other = (ControlFlowStatement) obj;
		if (elseStatements == null) {
			if (other.elseStatements != null)
				return false;
		} else if (!elseStatements.equals(other.elseStatements))
			return false;
		if (controlFlowExpression == null) {
			if (other.controlFlowExpression != null)
				return false;
		} else if (!controlFlowExpression.equals(other.controlFlowExpression))
			return false;
		if (thenStatements == null) {
			if (other.thenStatements != null)
				return false;
		} else if (!thenStatements.equals(other.thenStatements))
			return false;
		return true;
	}
	
}

