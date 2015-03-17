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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.lambdamatic.LambdaExpression;
import org.lambdamatic.analyzer.ast.CapturedArgumentsEvaluator;
import org.lambdamatic.analyzer.ast.LambdaExpressionReader;
import org.lambdamatic.analyzer.ast.LambdaExpressionRewriter;
import org.lambdamatic.analyzer.ast.ReturnTruePathFilter;
import org.lambdamatic.analyzer.ast.node.ASTNodeUtils;
import org.lambdamatic.analyzer.ast.node.CapturedArgument;
import org.lambdamatic.analyzer.ast.node.CapturedArgumentRef;
import org.lambdamatic.analyzer.ast.node.ComplexExpression;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.ExpressionVisitorUtil;
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
 * Singleton service that analyzes the bytecode behind a Lambda Expression and returns its AST in the form of an
 * {@link Expression}, or executes the actual Lambda expression and returns the resulting Java object.
 * 
 * <p><strong>Note:</strong>Subsequent calls to analyze a given Lambda Expression return a cached version of the AST, only
 * {@link CapturedArgument} may be different.</p>
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
	private final Map<String, Expression> cache = new HashMap<>();

	/** Number of times when the cache was hit. */
	private AtomicInteger cacheHits = new AtomicInteger();

	/** Number of times when the cache was missed. */
	private AtomicInteger cacheMisses = new AtomicInteger();

	/**
	 * Private constructor of the singleton
	 */
	private LambdaExpressionAnalyzer() {
	}

	/**
	 * @return the singleton instance
	 */
	public static LambdaExpressionAnalyzer getInstance() {
		return instance;
	}

	public void resetHitCounters() {
		this.cacheHits.set(0);
		this.cacheMisses.set(0);
	}

	public int getCacheHits() {
		return cacheHits.get();
	}

	public int getCacheMisses() {
		return cacheMisses.get();
	}

	/**
	 * Returns the {@link SerializedLambda} for the given {@code expression}
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
	private <T> SerializedLambda getSerializedLambda(final Object expression) {
		final Class<?> cl = expression.getClass();
		try {
			final Method m = cl.getDeclaredMethod("writeReplace");
			m.setAccessible(true);
			final Object result = m.invoke(expression);
			if (result instanceof SerializedLambda) {
				final SerializedLambda serializedLambda = (SerializedLambda) result;
				LOGGER.debug(" Lambda FunctionalInterface: {}.{} ({})", serializedLambda.getFunctionalInterfaceClass(),
						serializedLambda.getFunctionalInterfaceMethodName(), serializedLambda.getFunctionalInterfaceMethodSignature());
				LOGGER.debug(" Lambda Implementation: {}.{} ({})", serializedLambda.getImplClass(), serializedLambda.getImplMethodName(),
						serializedLambda.getImplMethodSignature());
				for (int i = 0; i < serializedLambda.getCapturedArgCount(); i++) {
					LOGGER.debug("  with Captured Arg(" + i + "): '" + serializedLambda.getCapturedArg(i) + 
							((serializedLambda.getCapturedArg(i) != null) ? "' (" + serializedLambda.getCapturedArg(i).getClass().getName() + ")" : ""));
				}
				return serializedLambda;
			}
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new AnalyzeException("Failed to find the Serialized form for the given Lambda Expression", e);
		}
		return null;
	}

	/**
	 * Gets the type of the argument used in the Functional Interface
	 * 
	 * @param serializedLambda
	 *            the info about the Lambda Expression implementation
	 * @return the argument type
	 * @throws ClassNotFoundException
	 */
	public Class<?> getArgumentType(final SerializedLambda serializedLambda) {
		// parameter type is the last argument
		final Type[] argumentTypes = Type.getArgumentTypes(serializedLambda.getImplMethodSignature());
		final String argumentTypeClassName = argumentTypes[argumentTypes.length - 1].getClassName();
		try {
			final Class<?> argumentTypeClass = Class.forName(argumentTypeClassName);
			return argumentTypeClass;
		} catch (ClassNotFoundException e) {
			throw new AnalyzeException("Failed to retrieve the argument type for the Lambda expression", e);
		}
	}
	
	/**
	 * Gets the type of the argument used in the Functional Interface
	 * 
	 * @param expression
	 *            the Lambda Expression 
	 * @return the argument type
	 * @throws ClassNotFoundException
	 */
	public Class<?> getArgumentType(final Object expression) {
		final SerializedLambda serializedLambda = getSerializedLambda(expression);
		return getArgumentType(serializedLambda);
	}
	
	/**
	 * Analyzes the Java Bytecode for the given user-defined Lambda Expression object (whose body has already been
	 * desugared by the compiler into a method in the caller class)
	 * 
	 * @param lambdaExpression
	 *            the user-defined Lambda Expression to parse
	 * @param localVariables
	 *            the local variables passed to the Lambda Expression a runtime (not the captured arguments)
	 * @return an {@link Expression} based on the bytecode generated to execute the given {@code lambdaExpression}.
	 * @throws AnalyzeException
	 */
	public LambdaExpression analyzeExpression(final Object lambdaExpression)
			throws AnalyzeException {
		try {
			final SerializedLambda serializedLambda = getSerializedLambda(lambdaExpression);
			final Class<?> argumentTypeClass = getArgumentType(serializedLambda);
			final Expression rawExpression = getRawExpression(serializedLambda);
			final Expression resultExpression = evaluateCapturedArguments(rawExpression, serializedLambda);
			// final Expression resultExpression = evaluateLocalVariables(intermediateExpression, );
			return new LambdaExpression(resultExpression, argumentTypeClass);
		} catch (IOException | ClassNotFoundException e) {
			throw new AnalyzeException("Failed to analyze lambda expression", e);
		}
	}
	
	/**
	 * Returns the "raw" {@link Expression} matching the lambda expression implementation associated with the given
	 * {@link SerializedLambda}, ie, an {@link Expression} whose {@link CapturedArgument} and
	 * {@link CapturedArgumentRef} have not been evaluated yet.
	 * 
	 * @param serializedLambda
	 *            the info about the Lambda Expression implementation
	 * @return the raw {@link Expression}
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private Expression getRawExpression(final SerializedLambda serializedLambda) throws ClassNotFoundException,
			IOException {
		final String methodImplementationId = serializedLambda.getImplClass() + "."
				+ serializedLambda.getImplMethodName();
		synchronized (methodImplementationId) {
			if (cache.containsKey(methodImplementationId)) {
				this.cacheHits.incrementAndGet();
			} else {
				this.cacheMisses.incrementAndGet();
				final Expression rawExpression = analyzeByteCode(serializedLambda);
				cache.put(methodImplementationId, rawExpression);
			}
			// we need to return a duplicate of the expression to be sure the original is kept *unchanged*
			return cache.get(methodImplementationId).duplicate();
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
	private Expression analyzeByteCode(final SerializedLambda serializedLambda) throws ClassNotFoundException,
			IOException {
		LOGGER.debug("Analyzing lambda expression bytecode...");
		final Statement statement = new LambdaExpressionReader().readBytecodeStatement(serializedLambda);
		final Expression thinedOutExpression = thinOut(statement);
		final Expression simplifiedExpression = simplifyExpression(thinedOutExpression);
		final Expression processedExpression = processMethodCalls(simplifiedExpression);
		return processedExpression;
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
		return ExpressionVisitorUtil.visit(expression, new LambdaExpressionRewriter());
	}

	/**
	 * Performs the method calls on the {@link CapturedArgument}s wherever they would appear
	 * in the given {@link Expression}.
	 * 
	 * @param sourceExpression
	 *            the original {@link Expression}
	 * @param serializedLambda
	 *            the {@link SerializedLambda} form
	 * @return the equivalent expression, where method calls on {@link CapturedArgument}s have been replaced with their
	 *         actual values.
	 */
	private Expression evaluateCapturedArguments(final Expression sourceExpression,
			final SerializedLambda serializedLambda) {
		// nothing to process
		if (serializedLambda.getCapturedArgCount() == 0) {
			return sourceExpression;
		}
		// retrieve the captured arguments from the given serializedLambda
		final List<Object> capturedArgs = new ArrayList<>();
		for (int i = 0; i < serializedLambda.getCapturedArgCount(); i++) {
			capturedArgs.add(serializedLambda.getCapturedArg(i));
		}
		final ExpressionVisitor visitor = new CapturedArgumentsEvaluator(capturedArgs);
		return ExpressionVisitorUtil.visit(sourceExpression, visitor);
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
		 * @return the currently wrapped {@link Expression}. (it may not the one given in the constructor if the
		 *         {@link ExpressionWrapper#replaceElement(Expression, Expression)} was called.
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

		@Override
		public Expression duplicate() {
			return null;
		}

		@Override
		public String toString() {
			return this.expression.toString() + " (wrapped)";
		}

	}

}
