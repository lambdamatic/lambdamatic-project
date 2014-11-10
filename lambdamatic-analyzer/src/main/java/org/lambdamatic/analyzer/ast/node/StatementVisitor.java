/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;


/**
 * @author xcoulon
 *
 */
public abstract class StatementVisitor {

	/**
	 * Dispatch to the other visitXYZ methods 
	 * @param stmt the {@link Statement} to visit
	 * @return true if the visit on the given statement's children should continue, false otherwise.
	 */
	public boolean visit(final Statement stmt) {
		if(stmt != null) {
			switch(stmt.getStatementType()) {
			case EXPRESSION_STMT:
				return visitExpressionStatement((ExpressionStatement) stmt);
			case IF_STMT:
				return visitIfStatement((IfStatement) stmt);
			case RETURN_STMT:
				return visitReturnStatement((ReturnStatement) stmt);
			default:
			}
		}
		return false;
	}

	public boolean visitExpressionStatement(final ExpressionStatement expressionNode) {
		return true;
	}

	public boolean visitIfStatement(final IfStatement logicalOperatorNode) {
		return true;
	}
	
	public boolean visitReturnStatement(final ReturnStatement returnStatementNode) {
		return true;
	}

}

