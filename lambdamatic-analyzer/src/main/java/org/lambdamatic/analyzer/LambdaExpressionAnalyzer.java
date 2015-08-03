/**
 * 
 */
package org.lambdamatic.analyzer;

import java.io.IOException;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.Pair;
import org.lambdamatic.analyzer.ast.CapturedArgumentsEvaluator;
import org.lambdamatic.analyzer.ast.ExpressionSanitizer;
import org.lambdamatic.analyzer.ast.LambdaExpressionReader;
import org.lambdamatic.analyzer.ast.ReturnTruePathFilter;
import org.lambdamatic.analyzer.ast.SerializedLambdaInfo;
import org.lambdamatic.analyzer.ast.StatementExpressionsDelegateVisitor;
import org.lambdamatic.analyzer.ast.node.ASTNodeUtils;
import org.lambdamatic.analyzer.ast.node.CapturedArgument;
import org.lambdamatic.analyzer.ast.node.ControlFlowStatement;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.Expression.ExpressionType;
import org.lambdamatic.analyzer.ast.node.ExpressionStatement;
import org.lambdamatic.analyzer.ast.node.ExpressionVisitorUtil;
import org.lambdamatic.analyzer.ast.node.CompoundExpression;
import org.lambdamatic.analyzer.ast.node.CompoundExpression.CompoundExpressionOperator;
import org.lambdamatic.analyzer.ast.node.LambdaExpression;
import org.lambdamatic.analyzer.ast.node.LocalVariable;
import org.lambdamatic.analyzer.ast.node.ReturnStatement;
import org.lambdamatic.analyzer.ast.node.Statement;
import org.lambdamatic.analyzer.ast.node.Statement.StatementType;
import org.lambdamatic.analyzer.exception.AnalyzeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton service that analyzes the bytecode behind a Lambda Expression and returns its AST in the form of an
 * {@link Expression}, or executes the actual Lambda expression and returns the resulting Java object.
 * 
 * <p>
 * <strong>Note:</strong>Subsequent calls to analyze a given Lambda Expression return a cached version of the AST, only
 * {@link CapturedArgument} may be different.
 * </p>
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 * 
 * @see http://cr.openjdk.java.net/~briangoetz/lambda/lambda-translation.html
 * 
 */
public class LambdaExpressionAnalyzer {

	/** The usual logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(LambdaExpressionAnalyzer.class);

	/** singleton instance. */
	private static LambdaExpressionAnalyzer instance = new LambdaExpressionAnalyzer();

	/** {@link Expression} indexed by their functional implementation className.methodName. */
	private final Map<String, LambdaExpression> cache = new HashMap<>();

	private final Set<LambdaExpressionAnalyzerListener> listeners = new HashSet<LambdaExpressionAnalyzerListener>();

	/**
	 * Private constructor of the singleton
	 */
	private LambdaExpressionAnalyzer() {
	}

	/**
	 * Adds the given {@link LambdaExpressionAnalyzerListener} to the list of listeners to be notified when a Lambda
	 * Expression is analyzed. Has no effect if the same instance is already registered.
	 * 
	 * @param listener
	 *            the listener to add.
	 */
	public void addListener(final LambdaExpressionAnalyzerListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * Removes the given {@link LambdaExpressionAnalyzerListener} from the list of listeners to be notified when a
	 * Lambda Expression is analyzed. Has no effect if the same instance ss not registered.
	 * 
	 * @param listener
	 *            the listener to remove.
	 */
	public void removeListener(final LambdaExpressionAnalyzerListener listener) {
		this.listeners.remove(listener);
	}

	/**
	 * @return the singleton instance
	 */
	public static LambdaExpressionAnalyzer getInstance() {
		return instance;
	}

	/**
	 * Returns the {@link SerializedLambdaInfo} for the given {@code expression}
	 * 
	 * @param expression
	 *            the expression to analyze.
	 * @return the corresponding {@link SerializedLambda}
	 * @throws AnalyzeException
	 *             if something wrong happened (a {@link NoSuchMethodException}, {@link IllegalArgumentException} or
	 *             {@link InvocationTargetException} exception occurred).
	 * 
	 * @see http://cr.openjdk.java.net/~briangoetz/lambda/lambda-translation.html
	 * @see http ://docs.oracle.com/javase/8/docs/api/java/lang/invoke/SerializedLambda.html
	 * @see http ://stackoverflow.com/questions/21860875/printing-debug-info-on-errors
	 *      -with-java-8-lambda-expressions/21879031 #21879031
	 */
	private static <T> SerializedLambdaInfo getSerializedLambdaInfo(final Object expression) {
		final Class<?> cl = expression.getClass();
		try {
			final Method m = cl.getDeclaredMethod("writeReplace");
			m.setAccessible(true);
			final Object result = m.invoke(expression);
			if (result instanceof SerializedLambda) {
				final SerializedLambda serializedLambda = (SerializedLambda) result;
				LOGGER.debug(" Lambda FunctionalInterface: {}.{} ({})", serializedLambda.getFunctionalInterfaceClass(),
						serializedLambda.getFunctionalInterfaceMethodName(),
						serializedLambda.getFunctionalInterfaceMethodSignature());
				LOGGER.debug(" Lambda Implementation: {}.{} ({})", serializedLambda.getImplClass(),
						serializedLambda.getImplMethodName(), serializedLambda.getImplMethodSignature());
				IntStream.range(0, serializedLambda.getCapturedArgCount())
						.forEach(
								i -> LOGGER
										.debug("  with Captured Arg(" + i + "): '" + serializedLambda.getCapturedArg(i)
												+ ((serializedLambda.getCapturedArg(i) != null) ? "' ("
														+ serializedLambda.getCapturedArg(i).getClass().getName() + ")"
														: "")));
				serializedLambda.getCapturedArgCount();

				return new SerializedLambdaInfo(serializedLambda);
			}
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new AnalyzeException("Failed to find the Serialized form for the given Lambda Expression", e);
		}
		return null;
	}

	/**
	 * Gets the type of the argument used in the Functional Interface
	 * 
	 * @param expression
	 *            the Lambda Expression
	 * @return the argument type
	 * @throws ClassNotFoundException
	 */
	public static Class<?> getArgumentType(final Object expression) {
		final SerializedLambdaInfo lambdaInfo = getSerializedLambdaInfo(expression);
		return getArgumentType(lambdaInfo);
	}

	/**
	 * Analyzes the Java Bytecode for the given user-defined Lambda Expression object (whose body has already been
	 * desugared by the compiler into a method in the caller class)
	 * 
	 * @param lambdaExpression
	 *            the user-defined Lambda Expression to parse
	 * @return an {@link Expression} based on the bytecode generated to execute the given {@code lambdaExpression}.
	 * @throws AnalyzeException
	 */
	public LambdaExpression analyzeExpression(final Object lambdaExpression) throws AnalyzeException {
		final SerializedLambdaInfo lambdaInfo = getSerializedLambdaInfo(lambdaExpression);
		final LambdaExpression rawExpression = analyzeExpression(lambdaInfo);
		final List<Statement> result = evaluateCapturedArguments(rawExpression.getBody(),
				lambdaInfo.getCapturedArguments());
		return new LambdaExpression(result, rawExpression.getArgumentType(), rawExpression.getArgumentName());
	}

	/**
	 * Analyzes the Java Bytecode for the given user-defined Lambda Expression object (whose body has already been
	 * desugared by the compiler into a method in the caller class)
	 * 
	 * @param lambdaInfo
	 *            the {@link SerializedLambdaInfo} about the user-defined Lambda Expression to parse
	 * @return an {@link Expression} based on the bytecode generated to execute the given {@code lambdaExpression}.
	 * @throws AnalyzeException
	 */
	public LambdaExpression analyzeExpression(final SerializedLambdaInfo serializedLambdaInfo) {
		try {
			final String methodImplementationId = serializedLambdaInfo.getImplMethodId();
			synchronized (methodImplementationId) {
				if (cache.containsKey(methodImplementationId)) {
					this.listeners.stream().forEach(l -> l.cacheHit(methodImplementationId));
				} else {
					this.listeners.stream().forEach(l -> l.cacheMissed(methodImplementationId));
					final LambdaExpression rawExpression = analyzeByteCode(serializedLambdaInfo);
					cache.put(methodImplementationId, rawExpression);
				}
				// we need to return a duplicate of the expression to be sure the original is kept *unchanged*
				return cache.get(methodImplementationId).duplicate();
			}
		} catch (IOException e) {
			throw new AnalyzeException("Failed to analyze lambda expression", e);
		}
	}

	/**
	 * Performs the actual bytecode analysis from the given {@link SerializedLambda}.
	 * 
	 * @param serializedLambda
	 *            the info about the bytecode method to analyze
	 * @return the AST {@link Expression}
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws AnalyzeException
	 */
	private LambdaExpression analyzeByteCode(final SerializedLambdaInfo lambdaInfo) throws IOException {
		LOGGER.debug("Analyzing lambda expression bytecode at {}.{}", lambdaInfo.getImplClassName(),
				lambdaInfo.getImplMethodName());
		final LambdaExpressionReader lambdaExpressionReader = new LambdaExpressionReader();
		final Pair<List<Statement>, List<LocalVariable>> bytecode = lambdaExpressionReader
				.readBytecodeStatement(lambdaInfo);
		final List<LocalVariable> lambdaExpressionArguments = bytecode.getRight();
		final List<Statement> lambdaExpressionStatements = bytecode.getLeft();
		final List<Statement> processedBlock = lambdaExpressionStatements.stream().map(s -> thinOut(s))
				.map(s -> simplify(s)).collect(Collectors.toList());
		final LocalVariable lambdaExpressionArgument = lambdaExpressionArguments.get(0);
		return new LambdaExpression(processedBlock, lambdaExpressionArgument.getJavaType(),
				lambdaExpressionArgument.getName());
	}

	private Statement simplify(final Statement statement) {
		switch (statement.getStatementType()) {
		case CONTROL_FLOW_STMT:
			final ControlFlowStatement controlFlowStmt = (ControlFlowStatement) statement;
			final Expression simplifiedControlFlowExpression = simplify(controlFlowStmt.getControlFlowExpression());
			final List<Statement> simplifiedThenStmts = controlFlowStmt.getThenStatements().stream()
					.map(s -> simplify(s)).collect(Collectors.toList());
			final List<Statement> simplifiedElseStmts = controlFlowStmt.getElseStatements().stream()
					.map(s -> simplify(s)).collect(Collectors.toList());
			return new ControlFlowStatement(simplifiedControlFlowExpression, simplifiedThenStmts, simplifiedElseStmts);
		case EXPRESSION_STMT:
			final ExpressionStatement expressionStmt = (ExpressionStatement) statement;
			final Expression simplifiedExpression = simplify(expressionStmt.getExpression());
			return new ExpressionStatement(simplifiedExpression);
		case RETURN_STMT:
			final ReturnStatement returnStmt = (ReturnStatement) statement;
			final Expression simplifiedReturnExpression = simplify(returnStmt.getExpression());
			return new ReturnStatement(simplifiedReturnExpression);
		default:
			throw new AnalyzeException("Unexpected statement type to simplify: " + statement.getStatementType());
		}
	}

	/**
	 * @param expression
	 *            the {@link Expression} to simplify
	 * @return a simplified {@link Expression} if the given one is an {@link ExpressionType#COMPOUND}, otherwise returns
	 *         the given {@link Expression}.
	 */
	private Expression simplify(final Expression expression) {
		if (expression.getExpressionType() == ExpressionType.COMPOUND) {
			final CompoundExpression infixExpression = (CompoundExpression) expression;
			final Expression simplifiedExpression = infixExpression.simplify();
			return ExpressionVisitorUtil.visit(simplifiedExpression, new ExpressionSanitizer());
		}
		return ExpressionVisitorUtil.visit(expression, new ExpressionSanitizer());
	}

	/**
	 * Performs the method calls on the {@link CapturedArgument}s wherever they would appear in the given
	 * {@link Expression}.
	 * 
	 * @param sourceExpression
	 *            the original {@link Expression}
	 * @param capturedArguments
	 *            the actual captured arguments during the call
	 * @return the equivalent expression, where method calls on {@link CapturedArgument}s have been replaced with their
	 *         actual values.
	 */
	public static List<Statement> evaluateCapturedArguments(final List<Statement> statements,
			final List<CapturedArgument> capturedArguments) {
		// nothing to process
		if (capturedArguments.isEmpty()) {
			return statements;
		} else {
			// retrieve the captured arguments from the given serializedLambda
			final List<Object> capturedArgValues = capturedArguments.stream().map(a -> a.getValue())
					.collect(Collectors.toList());
			statements.stream().forEach(s -> s.accept(
					new StatementExpressionsDelegateVisitor(new CapturedArgumentsEvaluator(capturedArgValues))));
			// final StatementVisitor visitor = new CapturedArgumentsEvaluator(capturedArgValues);
			// return ExpressionVisitorUtil.visit(sourceExpression, visitor);
			return statements;
		}
	}

	/**
	 * Simplify the given {@link Statement} keeping all branches that end with a "return 1" node, and combining the
	 * remaining ones in an {@link CompoundExpression}.
	 * 
	 * @param statement
	 *            the statement to thin out
	 * @return the resulting "thined out" {@link Statement}
	 */
	private Statement thinOut(final Statement statement) {
		LOGGER.debug("About to simplify \n\t{}", ASTNodeUtils.prettyPrint(statement));
		if (statement.getStatementType() == StatementType.EXPRESSION_STMT) {
			return statement;
		} else {
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
					case CONTROL_FLOW_STMT:
						final ControlFlowStatement controlFlowStatement = (ControlFlowStatement) currentStmt;
						final Expression controlFlowExpression = controlFlowStatement.getControlFlowExpression();
						// if we come from the "eval true" path on this
						// condition
						if (controlFlowStatement.getThenStatements().contains(previousStmt)) {
							relevantExpressions.add(0, controlFlowExpression);
						} else {
							relevantExpressions.add(0, controlFlowExpression.inverse());
						}
						break;
					case RETURN_STMT:
						final Expression returnExpression = ((ReturnStatement) currentStmt).getExpression();
						if (returnExpression.getExpressionType() == ExpressionType.METHOD_INVOCATION) {
							relevantExpressions.add(0, returnExpression);
						}
						break;
					default:
						LOGGER.trace("Ignoring node '{}'", currentStmt);
						break;
					}
					previousStmt = currentStmt;
					currentStmt = currentStmt.getParent();
				}
				if (relevantExpressions.size() > 1) {
					expressions.add(new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, relevantExpressions));
				} else if (!relevantExpressions.isEmpty()) {
					expressions.add(relevantExpressions.getFirst());
				}

			}
			if (expressions.isEmpty()) {
				return statement;
			}
			final Statement result = (expressions.size() > 1)
					? new ReturnStatement(new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, expressions))
					: new ReturnStatement(expressions.get(0));
			LOGGER.debug("Thinned out expression: {}", result.toString());
			return result;
		}
	}

}
