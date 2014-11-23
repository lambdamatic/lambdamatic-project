/**
 * 
 */
package org.lambdamatic.analyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.lambdamatic.FilterExpression;
import org.lambdamatic.analyzer.ast.ExpressionRewriter;
import org.lambdamatic.analyzer.ast.LambdaExpressionReader;
import org.lambdamatic.analyzer.ast.ReturnTruePathFilter;
import org.lambdamatic.analyzer.ast.node.ASTNodeUtils;
import org.lambdamatic.analyzer.ast.node.CapturedArgument;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.Expression.ExpressionType;
import org.lambdamatic.analyzer.ast.node.ExpressionVisitor;
import org.lambdamatic.analyzer.ast.node.IfStatement;
import org.lambdamatic.analyzer.ast.node.InfixExpression;
import org.lambdamatic.analyzer.ast.node.InfixExpression.InfixOperator;
import org.lambdamatic.analyzer.ast.node.ReturnStatement;
import org.lambdamatic.analyzer.ast.node.Statement;
import org.lambdamatic.analyzer.exception.AnalyzeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xcoulon
 * 
 * @see http://cr.openjdk.java.net/~briangoetz/lambda/lambda-translation.html
 * 
 */
public class LambdaExpressionAnalyzer {

	static final Logger LOGGER = LoggerFactory.getLogger(LambdaExpressionAnalyzer.class);

	/**
	 * Analyzes the Java Bytecode for the given Lambda expression (whose body has already been desugared by the compiler
	 * into a method in the caller class)
	 * 
	 * @param filterExpression
	 *            the {@link FilterExpression} to parse
	 * @return an {@link Expression} based on the bytecode generated to execute the given {@link FilterExpression}.
	 * @throws AnalyzeException
	 */
	public <T> Expression analyzeLambdaExpression(FilterExpression<T> filterExpression) throws AnalyzeException {
		try {
			final Statement statement = new LambdaExpressionReader().readBytecodeStatement(filterExpression);
			final Expression thinedOutExpression = thinOut(statement);
			final Expression processedExpression = processMethodCalls(thinedOutExpression);
			if (processedExpression.getExpressionType() == ExpressionType.INFIX) {
				final InfixExpression infixExpression = (InfixExpression) processedExpression;
				final Expression simplifiedExpression = infixExpression.simplify();
				return simplifiedExpression;

			}
			return processedExpression;
		} catch (IOException e) {
			throw new AnalyzeException("Failed to analyze lambda expression", e);
		}
	}

	/**
	 * Performs the method calls on the {@link CapturedArgument}s wherever they would appear in the given
	 * {@link Expression}.
	 * 
	 * @param expression
	 *            the original {@link Expression}
	 * @return the equivalent expression, where method calls on {@link CapturedArgument}s have been replaced with their
	 *         actual values.
	 */
	private Expression processMethodCalls(final Expression expression) {
		final ExpressionVisitor visitor = new ExpressionRewriter();
		expression.accept(visitor);
		return expression;
	}

	/**
	 * Simplify the given {@link Statement} keeping all branches that end with a "return 1" node, and combining the
	 * remaining ones in an {@link InfixExpression}.
	 * 
	 * @param statement
	 *            the statement to thin out
	 * @return the resulting expression
	 */
	private Expression thinOut(final Statement statement) {
		LOGGER.debug("About to simplify \n{}", ASTNodeUtils.prettyPrint(statement));
		// find branches that end with 'return 1'
		final ReturnTruePathFilter filter = new ReturnTruePathFilter();
		statement.accept(filter);
		final List<ReturnStatement> returnStmts = filter.getReturnStmts();
		final List<Expression> expressions = new ArrayList<>();
		for (ReturnStatement returnStmt : returnStmts) {
			final LinkedList<Expression> relevantExpressions = new LinkedList<>();
			// current node being evaluated
			Statement currentStmt = returnStmt;
			// previous node evaluated, because it is important to remember
			// the path that was taken (in case of ConditionalStatements)
			Statement previousStmt = null;
			while (currentStmt != null) {
				switch (currentStmt.getStatementType()) {
				case IF_STMT:
					final IfStatement ifStatement = (IfStatement) currentStmt;
					final Expression ifExpression = ifStatement.getIfExpression();
					// if we come from the "eval true" path on this
					// condition
					if (ifStatement.getThenStatement().equals(previousStmt)) {
						relevantExpressions.add(0, ifExpression);
					} else {
						relevantExpressions.add(0, ifExpression.inverse());
					}
					break;
				case RETURN_STMT:
					final Expression returnExpression = ((ReturnStatement) currentStmt).getExpression();
					if (returnExpression.getExpressionType() == ExpressionType.METHOD_INVOCATION) {
						relevantExpressions.add(0, returnExpression);
					}
					break;
				default:
					LOGGER.debug("Ignoring node '{}'", currentStmt);
					break;
				}
				previousStmt = currentStmt;
				currentStmt = currentStmt.getParent();
			}
			if (relevantExpressions.size() > 1) {
				expressions.add(new InfixExpression(InfixOperator.CONDITIONAL_AND, relevantExpressions));
			} else {
				expressions.add(relevantExpressions.getFirst());
			}

		}
		final Expression result = (expressions.size() > 1) ? new InfixExpression(InfixOperator.CONDITIONAL_OR,
				expressions) : expressions.get(0);
		LOGGER.debug("Thinned out expression: #{}: {}", result.getId(), result.toString());
		return result;
	}

}

