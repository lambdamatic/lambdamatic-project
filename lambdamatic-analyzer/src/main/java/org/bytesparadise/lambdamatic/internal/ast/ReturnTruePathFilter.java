package org.bytesparadise.lambdamatic.internal.ast;

import java.util.ArrayList;
import java.util.List;

import org.bytesparadise.lambdamatic.internal.ast.node.BooleanLiteral;
import org.bytesparadise.lambdamatic.internal.ast.node.Expression;
import org.bytesparadise.lambdamatic.internal.ast.node.ExpressionStatement;
import org.bytesparadise.lambdamatic.internal.ast.node.InfixExpression;
import org.bytesparadise.lambdamatic.internal.ast.node.MethodInvocation;
import org.bytesparadise.lambdamatic.internal.ast.node.ReturnStatement;
import org.bytesparadise.lambdamatic.internal.ast.node.StatementVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom {@link StatementVisitor} that will retrieve all branches of the
 * bytecode AST that end with a {@link ReturnStatement} leaf node whose value is
 * {@link Boolean#TRUE}, an {@link InfixExpression} leaf node or a {@link MethodInvocation} leaf node.
 * 
 * @author xcoulon
 * 
 */
public class ReturnTruePathFilter extends StatementVisitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReturnTruePathFilter.class);
	
	private final List<ReturnStatement> returnStmts = new ArrayList<>();

	@Override
	public boolean visitReturnStatement(final ReturnStatement returnStatement) {
		final Expression returnExpression = returnStatement.getExpression();
		switch (returnExpression.getExpressionType()) {
		case BOOLEAN_LITERAL:
			if (((BooleanLiteral)returnExpression).getValue().equals(true)) {
				getReturnStmts().add(returnStatement);
			}
			break;
		case INFIX:
		case METHOD_INVOCATION:
			getReturnStmts().add(returnStatement);
			break;
		default:
			LOGGER.error("Unsupported expression type ({}) in return statement '{}'", returnExpression.getExpressionType().toString(), returnStatement.toString());
			break;
		}

		// no need to carry on.
		return false;
	}

	@Override
	public boolean visitExpressionStatement(ExpressionStatement expression) {
		// no need to visit child nodes of an Expression here.
		return false;
	}

	public List<ReturnStatement> getReturnStmts() {
		return returnStmts;
	}
}
