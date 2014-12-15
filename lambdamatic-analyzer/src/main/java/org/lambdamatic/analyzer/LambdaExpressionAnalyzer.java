/**
 * 
 */
package org.lambdamatic.analyzer;

import java.io.IOException;
import java.lang.invoke.SerializedLambda;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.lambdamatic.FilterExpression;
import org.lambdamatic.LambdaExpression;
import org.lambdamatic.analyzer.ast.ExpressionRewriter;
import org.lambdamatic.analyzer.ast.LambdaExpressionReader;
import org.lambdamatic.analyzer.ast.ReturnTruePathFilter;
import org.lambdamatic.analyzer.ast.node.ASTNodeUtils;
import org.lambdamatic.analyzer.ast.node.BooleanLiteral;
import org.lambdamatic.analyzer.ast.node.CapturedArgument;
import org.lambdamatic.analyzer.ast.node.ComplexExpression;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.Expression.ExpressionType;
import org.lambdamatic.analyzer.ast.node.ExpressionVisitor;
import org.lambdamatic.analyzer.ast.node.IfStatement;
import org.lambdamatic.analyzer.ast.node.InfixExpression;
import org.lambdamatic.analyzer.ast.node.InfixExpression.InfixOperator;
import org.lambdamatic.analyzer.ast.node.ReturnStatement;
import org.lambdamatic.analyzer.ast.node.Statement;
import org.lambdamatic.analyzer.exception.AnalyzeException;
import org.objectweb.asm.Type;
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
	public <T> LambdaExpression analyzeLambdaExpression(final FilterExpression<T> filterExpression) throws AnalyzeException {
		try {
			final SerializedLambda serializedLambda = LambdaExpressionReader.getSerializedLambda(filterExpression);
			final Type[] argumentTypes = Type.getArgumentTypes(serializedLambda.getImplMethodSignature());
			final String argumentClassName = argumentTypes[0].getClassName();
			final Class<?> argumentClass = Class.forName(argumentClassName);
			final Statement statement = new LambdaExpressionReader().readBytecodeStatement(serializedLambda);
			final Expression thinedOutExpression = thinOut(statement);
			final Expression processedExpression = processMethodCalls(thinedOutExpression);
			final Expression resultExpression = simplifyExpression(processedExpression);
			return new LambdaExpression(resultExpression, argumentClass);
		} catch (IOException | ClassNotFoundException e) {
			throw new AnalyzeException("Failed to analyze lambda expression", e);
		}
	}

	/**
	 * @param processedExpression
	 * @return
	 */
	private Expression simplifyExpression(final Expression processedExpression) {
		if (processedExpression.getExpressionType() == ExpressionType.INFIX) {
			final InfixExpression infixExpression = (InfixExpression) processedExpression;
			final Expression simplifiedExpression = infixExpression.simplify();
			return simplifiedExpression;
		}
		return processedExpression;
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
		// wrap the expression to make sure it has a parent
		// because in some cases (eg: a boolean expression, the MethodInvocation#delete() would fail)
		final ExpressionWrapper wrapper = new ExpressionWrapper(expression);
		expression.accept(visitor);
		// now, detach and return the resulting wrapped expression
		final Expression resultExpression = wrapper.getExpression();
		resultExpression.setParent(null);
		return resultExpression;
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

	static class ExpressionWrapper extends ComplexExpression {

		private Expression expression = null;
		
		public ExpressionWrapper(final Expression expression) {
			super(generateId(), false);
			this.expression = expression;
			this.expression.setParent(this);
		}

		/**
		 * @return the currently wrapped {@link Expression}.
		 * (it may not the one given in the constructor if the {@link ExpressionWrapper#replaceElement(Expression, Expression)} was called.
		 */
		public Expression getExpression() {
			return expression;
		}
		
		@Override
		public void replaceElement(final Expression oldExpression, final Expression newExpression) {
			this.expression = newExpression;
		}

		@Override
		public ExpressionType getExpressionType() {
			return expression.getExpressionType();
		}

		@Override
		public Class<?> getJavaType() {
			return expression.getJavaType();
		}

		@Override
		public Expression inverse() {
			return this;
		}

		@Override
		public boolean canBeInverted() {
			return false;
		}

		@Override
		public Expression duplicate(int id) {
			return null;
		}
		
	}
}

