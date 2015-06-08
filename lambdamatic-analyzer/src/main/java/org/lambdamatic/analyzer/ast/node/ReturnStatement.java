/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;


/**
 * {@link ReturnStatement} 
 * @author xcoulon
 *
 */
public class ReturnStatement extends SimpleStatement {

	public ReturnStatement(final Expression expression) {
		super(expression);
	}
	
	@Override
	public StatementType getStatementType() {
		return StatementType.RETURN_STMT;
	}

	@Override
	public ReturnStatement duplicate() {
		return new ReturnStatement(expression.duplicate());
	} 
	@Override
	public void accept(final StatementVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("return");
		if(expression != null) {
			builder.append(' ').append(expression.toString());
		}
		builder.append(';');
		return builder.toString();
	}

}

