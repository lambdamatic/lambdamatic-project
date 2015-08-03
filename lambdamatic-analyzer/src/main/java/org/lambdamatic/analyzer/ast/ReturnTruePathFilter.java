package org.lambdamatic.analyzer.ast;

import java.util.ArrayList;
import java.util.List;

import org.lambdamatic.analyzer.ast.node.BooleanLiteral;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.ExpressionStatement;
import org.lambdamatic.analyzer.ast.node.CompoundExpression;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.lambdamatic.analyzer.ast.node.NumberLiteral;
import org.lambdamatic.analyzer.ast.node.ReturnStatement;
import org.lambdamatic.analyzer.ast.node.StatementVisitor;
import org.lambdamatic.analyzer.exception.AnalyzeException;

/**
 * Custom {@link StatementVisitor} that will retrieve all branches of the
 * bytecode AST that end with a {@link ReturnStatement} leaf node whose value is
 * {@link Boolean#TRUE}, an {@link CompoundExpression} leaf node or a {@link MethodInvocation} leaf node.
 * 
 * @author xcoulon
 * 
 */
public class ReturnTruePathFilter extends StatementVisitor {

	private final List<ReturnStatement> returnStmts = new ArrayList<>();

	@Override
	public boolean visitReturnStatement(final ReturnStatement returnStatement) {
		final Expression returnExpression = returnStatement.getExpression();
		switch (returnExpression.getExpressionType()) {
		case BOOLEAN_LITERAL:
			final BooleanLiteral booleanLiteral = (BooleanLiteral)returnExpression;
			if (booleanLiteral.getValue().equals(true)) {
				getReturnStmts().add(returnStatement);
			}
			break;
		case NUMBER_LITERAL:
			final NumberLiteral numberLiteral = (NumberLiteral)returnExpression;
			if (numberLiteral.getValue().equals(1)) {
				getReturnStmts().add(returnStatement);
			}
			break;
		case COMPOUND:
		case METHOD_INVOCATION:
		case FIELD_ACCESS:
			getReturnStmts().add(returnStatement);
			break;
		default:
			throw new AnalyzeException("Unsupported expression type ("
					+ returnExpression.getExpressionType().toString() + ") in return statement '"
					+ returnStatement.toString() + "'");
		}

		// no need to carry on.
		return false;
	}

	@Override
	public boolean visitExpressionStatement(final ExpressionStatement expression) {
		// no need to visit child nodes of an Expression here.
		return false;
	}

	public List<ReturnStatement> getReturnStmts() {
		return returnStmts;
	}
}

