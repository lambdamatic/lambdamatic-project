/**
 * 
 */
package org.lambdamatic.analyzer.ast;

import org.lambdamatic.analyzer.ast.node.ControlFlowStatement;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.ExpressionStatement;
import org.lambdamatic.analyzer.ast.node.ExpressionVisitor;
import org.lambdamatic.analyzer.ast.node.ExpressionVisitorUtil;
import org.lambdamatic.analyzer.ast.node.ReturnStatement;
import org.lambdamatic.analyzer.ast.node.Statement;
import org.lambdamatic.analyzer.ast.node.StatementVisitor;

/**
 * Visits a given {@link Statement} and applies the given {@link ExpressionVisitor} on the {@link Expression} that is
 * part of the visited {@link Statement}.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class StatementExpressionsDelegateVisitor extends StatementVisitor {

	private final ExpressionVisitor expressionVisitor;
	
	public StatementExpressionsDelegateVisitor(final ExpressionVisitor expressionVisitor) {
		this.expressionVisitor = expressionVisitor;
	}
	
	@Override
	public boolean visitControlFlowStatement(final ControlFlowStatement controlFlowStatement) {
		ExpressionVisitorUtil.visit(controlFlowStatement.getControlFlowExpression(), expressionVisitor);
		return super.visitControlFlowStatement(controlFlowStatement);
	}
	
	@Override
	public boolean visitExpressionStatement(final ExpressionStatement expressionStatement) {
		ExpressionVisitorUtil.visit(expressionStatement.getExpression(), expressionVisitor);
		return super.visitExpressionStatement(expressionStatement);
	}
	
	@Override
	public boolean visitReturnStatement(final ReturnStatement returnStatementNode) {
		final Expression returnExpression = returnStatementNode.getExpression();
		ExpressionVisitorUtil.visit(returnExpression, expressionVisitor);
		return super.visitReturnStatement(returnStatementNode);
	}
	
}
