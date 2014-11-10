/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;


/**
 * {@link ReturnStatement} 
 * @author xcoulon
 *
 */
public class ReturnStatement extends Statement {

	private final Expression value; 
	
	public ReturnStatement(final Expression value) {
		this.value = value;
	}
	
	@Override
	public StatementType getStatementType() {
		return StatementType.RETURN_STMT;
	}

	public Expression getExpression() {
		return value;
	}
	
	@Override
	public void accept(final StatementVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("return");
		if(value != null) {
			builder.append(' ').append(value.toString());
		}
		return builder.toString();
	}
	
}

