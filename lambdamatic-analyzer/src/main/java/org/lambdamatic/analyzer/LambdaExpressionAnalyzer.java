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

import org.apache.commons.lang3.tuple.Pair;
import org.lambdamatic.analyzer.ast.CapturedArgumentsEvaluator;
import org.lambdamatic.analyzer.ast.ExpressionSanitizer;
import org.lambdamatic.analyzer.ast.LambdaExpressionReader;
import org.lambdamatic.analyzer.ast.ReturnTruePathFilter;
import org.lambdamatic.analyzer.ast.SerializedLambdaInfo;
import org.lambdamatic.analyzer.ast.node.ASTNodeUtils;
import org.lambdamatic.analyzer.ast.node.CapturedArgument;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.Expression.ExpressionType;
import org.lambdamatic.analyzer.ast.node.ExpressionStatement;
import org.lambdamatic.analyzer.ast.node.ExpressionVisitor;
import org.lambdamatic.analyzer.ast.node.ExpressionVisitorUtil;
import org.lambdamatic.analyzer.ast.node.IfStatement;
import org.lambdamatic.analyzer.ast.node.InfixExpression;
import org.lambdamatic.analyzer.ast.node.InfixExpression.InfixOperator;
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
	 * Removes the given {@link LambdaExpressionAnalyzerListener} from the list of listeners to be notified when a Lambda
	 * Expression is analyzed. Has no effect if the same instance ss not registered.
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
				for (int i = 0; i < serializedLambda.getCapturedArgCount(); i++) {
					LOGGER.debug(
							"  with Captured Arg(" + i + "): '" + serializedLambda.getCapturedArg(i)
									+ ((serializedLambda.getCapturedArg(i) != null)
											? "' (" + serializedLambda.getCapturedArg(i).getClass().getName() + ")"
											: ""));
				}
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
		final Expression resultExpression = evaluateCapturedArguments(rawExpression.getExpression(),
				lambdaInfo.getCapturedArguments());
		return new LambdaExpression(resultExpression, rawExpression.getArgumentType(), rawExpression.getArgumentName());
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
		final Pair<List<Statement>, List<LocalVariable>> bytecode = lambdaExpressionReader.readBytecodeStatement(lambdaInfo);
		final List<LocalVariable> lambdaExpressionArguments = bytecode.getRight();
		final List<Statement> lambdaStatements = bytecode.getLeft();
		// FIXME: support multiple statements, iterate on each one to thinOut/simplify
		final Expression thinedOutExpression = thinOut(lambdaStatements.get(0));
		final Expression simplifiedExpression = simplifyExpression(thinedOutExpression);
		final Expression processedExpression = processMethodCalls(simplifiedExpression);
		final LocalVariable lambdaExpressionArgument = lambdaExpressionArguments.get(0);
		return new LambdaExpression(processedExpression, lambdaExpressionArgument.getJavaType(), lambdaExpressionArgument.getName());
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
	public static Expression evaluateCapturedArguments(final Expression sourceExpression,
			final List<CapturedArgument> capturedArguments) {
		// nothing to process
		if (capturedArguments.isEmpty()) {
			return sourceExpression;
		} else {
			// retrieve the captured arguments from the given serializedLambda
			final List<Object> capturedArgValues = capturedArguments.stream().map(a -> a.getValue())
					.collect(Collectors.toList());
			final ExpressionVisitor visitor = new CapturedArgumentsEvaluator(capturedArgValues);
			return ExpressionVisitorUtil.visit(sourceExpression, visitor);
		}
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
		LOGGER.debug("About to simplify \n\t{}", ASTNodeUtils.prettyPrint(statement));
		if (statement.getStatementType() == StatementType.EXPRESSION_STMT) {
			return ((ExpressionStatement) statement).getExpression();
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
					case IF_STMT:
						final IfStatement ifStatement = (IfStatement) currentStmt;
						final Expression ifExpression = ifStatement.getIfExpression();
						// if we come from the "eval true" path on this
						// condition
						if (ifStatement.getThenStatements().contains(previousStmt)) {
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
			final Expression result = (expressions.size() > 1)
					? new InfixExpression(InfixOperator.CONDITIONAL_OR, expressions) : expressions.get(0);
			LOGGER.debug("Thinned out expression: #{}: {}", result.getId(), result.toString());
			return result;
		}
	}

}
