package org.lambdamatic.analyzer.ast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import org.lambdamatic.analyzer.ast.node.BooleanLiteral;
import org.lambdamatic.analyzer.ast.node.ClassLiteral;
import org.lambdamatic.analyzer.ast.node.ComplexExpression;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.Expression.ExpressionType;
import org.lambdamatic.analyzer.ast.node.ExpressionFactory;
import org.lambdamatic.analyzer.ast.node.ExpressionVisitor;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.InfixExpression;
import org.lambdamatic.analyzer.ast.node.InfixExpression.InfixOperator;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.lambdamatic.analyzer.exception.AnalyzeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a new {@link Expression} where method calls such as
 * {@link Long#longValue()}, etc. are removed because they are not relevant in
 * our case (they are just underlying conversion methods) Finally,
 * {@link InfixExpression} with a boolean conditions (eg:
 * {@link MethodInvocation} == {@link BooleanLiteral}) are simplified as well:
 * the {@link InfixExpression} is replaced with the meaningful operand.
 *
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class ExpressionSanitizer extends ExpressionVisitor {

	/** The logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ExpressionSanitizer.class);

	/**
	 * If the {@link InfixExpression} with a boolean conditions (eg:
	 * {@link MethodInvocation} == {@link BooleanLiteral}), replace it with the meaningful operand (which may need to be inverted).
	 * 
	 * @param expr the {@link InfixExpression} to analyze
	 */
	@Override
	public boolean visitInfixExpression(final InfixExpression expr) {
		// manually visit all operands, first
		expr.getOperands().stream().forEach(operand -> operand.accept(this));
		if ((expr.getOperator() == InfixOperator.EQUALS || expr.getOperator() == InfixOperator.NOT_EQUALS)
				&& expr.getOperands().size() == 2
				&& expr.getOperands().stream().anyMatch(e -> e.getExpressionType() == ExpressionType.BOOLEAN_LITERAL)) {
			final ComplexExpression parentExpression = expr.getParent();
			final Optional<Expression> replacementExpr = expr.getOperands().stream().filter(e -> e.getExpressionType() != ExpressionType.BOOLEAN_LITERAL).findFirst();
			if(replacementExpr.isPresent()) {
				parentExpression.replaceElement(expr,
						(expr.getOperator() == InfixOperator.EQUALS) ? replacementExpr.get() : replacementExpr.get()
								.inverse());
			}
		}
		// the branch was already visited
		return true;
	}
	
	/**
	 * Removes all auto-boxing methods
	 */
	@Override
	public boolean visitMethodInvocationExpression(final MethodInvocation methodInvocation) {
		if(Stream.of(new MethodMatcher(Boolean.class, "booleanValue"), 
				new MethodMatcher(Byte.class, "byteValue"),
				new MethodMatcher(Short.class, "shortValue"),
				new MethodMatcher(Integer.class, "intValue"),
				new MethodMatcher(Long.class, "longValue"),
				new MethodMatcher(Float.class, "floatValue"),
				new MethodMatcher(Double.class, "doubleValue"),
				new MethodMatcher(Character.class, "charValue")
				).anyMatch(m -> m.matches(methodInvocation))) {
			methodInvocation.delete();
		} else if(new MethodMatcher(Boolean.class, "valueOf", boolean.class).matches(methodInvocation)) {
			methodInvocation.getParent().replaceElement(methodInvocation, methodInvocation.getArguments().get(0));
		} else if(new MethodMatcher(Integer.class, "valueOf", int.class).matches(methodInvocation)) {
			methodInvocation.getParent().replaceElement(methodInvocation, methodInvocation.getArguments().get(0));
		} else if(new MethodMatcher(Short.class, "valueOf", short.class).matches(methodInvocation)) {
			methodInvocation.getParent().replaceElement(methodInvocation, methodInvocation.getArguments().get(0));
		} else if(new MethodMatcher(Long.class, "valueOf", long.class).matches(methodInvocation)) {
			methodInvocation.getParent().replaceElement(methodInvocation, methodInvocation.getArguments().get(0));
		} else if(new MethodMatcher(Float.class, "valueOf", float.class).matches(methodInvocation)) {
			methodInvocation.getParent().replaceElement(methodInvocation, methodInvocation.getArguments().get(0));
		} else if(new MethodMatcher(Double.class, "valueOf", double.class).matches(methodInvocation)) {
			methodInvocation.getParent().replaceElement(methodInvocation, methodInvocation.getArguments().get(0));
		}
		return true;
	}

	/**
	 * Attempts to replace a {@link FieldAccess} with its actual value.
	 */ 
	@Override
	public boolean visitFieldAccessExpression(final FieldAccess fieldAccess) {
		if (fieldAccess.getSource().getExpressionType() == ExpressionType.CLASS_LITERAL) {
			final ClassLiteral sourceClass = (ClassLiteral) fieldAccess.getSource();
			final String fieldName = fieldAccess.getFieldName();
			try {
				final Class<?> source = sourceClass.getValue();
				final Field f = ReflectionUtils.getFieldToInvoke(source, fieldName);
				f.setAccessible(true);
				final Object replacement = f.get(source);
				final ComplexExpression parentExpression = fieldAccess.getParent();
				if (parentExpression != null) {
					final Expression fieldAccessReplacement = ExpressionFactory.getExpression(replacement);
					LOGGER.trace(" replacing {} ({}) with {} ({})", fieldAccess, fieldAccess.getExpressionType(), fieldAccessReplacement, fieldAccessReplacement.getExpressionType());  
					parentExpression.replaceElement(fieldAccess, fieldAccessReplacement);
				}
				// no further visiting on this (obsolete) branch of the expression tree.
				return false;
			} catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException e) {
				throw new AnalyzeException("Failed to execute method '" + fieldName + "' on class '" + sourceClass.toString() + "'", e);
			}
			
		}
		return super.visitFieldAccessExpression(fieldAccess);
	}
	
	/**
	 * A {@link MethodInvocation} matcher
	 * @author Xavier Coulon <xcoulon@redhat.com>
	 *
	 */
	static class MethodMatcher {
		/** the type of the element on which the method is called.*/
		private final Class<?> sourceType;
		/** the name of the method. */
		private final String methodName;
		/** the type of the arguments of this method.*/
		private final Class<?>[] argumentTypes;
		
		/**
		 * Constructor
		 * @param sourceType the type of the element on which the method is called.
		 * @param methodName the name of the method.
		 * @param argumentTypes the type of the arguments of this method
		 */
		MethodMatcher(final Class<?> sourceType, final String methodName) {
			super();
			this.sourceType = sourceType;
			this.methodName = methodName;
			this.argumentTypes = new Class<?>[0];
		}

		/**
		 * Constructor
		 * @param sourceType the type of the element on which the method is called.
		 * @param methodName the name of the method.
		 * @param argumentTypes the type of the arguments of this method
		 */
		MethodMatcher(final Class<?> sourceType, final String methodName, final Class<?>... argumentTypes) {
			super();
			this.sourceType = sourceType;
			this.methodName = methodName;
			this.argumentTypes = argumentTypes;
		}
		
		/** 
		 * 
		 * @param methodInvocation the {@link MethodInvocation} to inspect
		 * @return <code>true</code> if the given {@link MethodInvocation} matches on the source, method name and argument types, <code>false</code> otherwise.
		 */
		boolean matches(final MethodInvocation methodInvocation) {
			if(methodInvocation == null) {
				return false;
			}
			// fail fast if source type or method name or number of arguments don't match
			final Method javaMethod = methodInvocation.getJavaMethod();
			if(this.sourceType.equals(javaMethod.getDeclaringClass())
					&& this.methodName.equals(methodInvocation.getMethodName())
					&& Arrays.deepEquals(javaMethod.getParameterTypes(), argumentTypes)) { 
				return true;
			}
			return false;
		}
		
		
	}

}
