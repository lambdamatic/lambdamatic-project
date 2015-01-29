/**
 * 
 */
package org.lambdamatic.analyzer.ast;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.lambdamatic.analyzer.ast.node.CapturedArgument;
import org.lambdamatic.analyzer.ast.node.ComplexExpression;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.Expression.ExpressionType;
import org.lambdamatic.analyzer.ast.node.CapturedArgumentRef;
import org.lambdamatic.analyzer.ast.node.ExpressionVisitor;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.ExpressionFactory;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Visitor that will replace the {@link CapturedArgument} with their actual
 * value.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class CapturedArgumentsEvaluator extends ExpressionVisitor {
	
	/** The logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CapturedArgumentsEvaluator.class);

	/** the captured arguments provided by the {@link SerializedLambda} */
	private final List<Object> capturedArgs;
	
	/**
	 * Constructor
	 * 
	 * @param capturedArgs the captured arguments provided by the {@link SerializedLambda}
	 */
	public CapturedArgumentsEvaluator(final List<Object> capturedArgs) {
		this.capturedArgs = capturedArgs;
	}
	
	@Override
	public boolean visitMethodInvocationExpression(final MethodInvocation methodInvocation) {
		if (methodInvocation.getSourceExpression().getExpressionType() == ExpressionType.CAPTURED_ARGUMENT) {
			final String methodName = methodInvocation.getMethodName();
			final CapturedArgument capturedSourceArgument = (CapturedArgument) methodInvocation.getSourceExpression();
			final Object source = capturedSourceArgument.getValue();
			replaceCaptureArgValue(methodInvocation, methodName, source);
			// no further visiting on this (obsolete) branch of the expression tree.
			return false;
		} else if (methodInvocation.getSourceExpression().getExpressionType() == ExpressionType.CAPTURED_ARGUMENT_REF) {
			final String methodName = methodInvocation.getMethodName();
			final CapturedArgumentRef capturedSourceArgumentRef = (CapturedArgumentRef) methodInvocation.getSourceExpression();
			final Object source = this.capturedArgs.get(capturedSourceArgumentRef.getArgumentIndex());
			replaceCaptureArgValue(methodInvocation, methodName, source);
			// no further visiting on this (obsolete) branch of the expression tree.
			return false;
		}
		return true;
	}

	private void replaceCaptureArgValue(final MethodInvocation methodInvocation, final String methodName,
			final Object source) {
		try {
			final List<Object> args = new ArrayList<>();
			final List<Class<?>> argTypes = new ArrayList<>();
			for (Expression methodArgument : methodInvocation.getArguments()) {
				final Object methodArgValue = methodArgument.getValue();
				args.add(methodArgValue);
				argTypes.add(methodArgValue.getClass());
			}
			final Method m = ReflectionUtils.getMethodToInvoke(source, methodName, argTypes);
			m.setAccessible(true);
			final Object replacement = m.invoke(source, args.toArray());
			final ComplexExpression parentExpression = methodInvocation.getParent();
			if (parentExpression != null) {
				parentExpression.replaceElement(methodInvocation, ExpressionFactory.getExpression(replacement));
			}
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			LOGGER.error("Failed to execute method '{}' on captured argument '{}'", methodName, source);
		}
	}

	@Override
	public boolean visitFieldAccessExpression(final FieldAccess fieldAccess) {
		if (fieldAccess.getSourceExpression().getExpressionType() == ExpressionType.CAPTURED_ARGUMENT) {
			final CapturedArgument capturedSourceArgument = (CapturedArgument) fieldAccess.getSourceExpression();
			final String fieldName = fieldAccess.getFieldName();
			try {
				final Object source = capturedSourceArgument.getValue();
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
				LOGGER.error("Failed to execute method '{}' on captured argument '{}'", fieldName, capturedSourceArgument.getValue());
			}
		} 
		return super.visitFieldAccessExpression(fieldAccess);
	}

	@Override
	public boolean visitCapturedArgumentRef(final CapturedArgumentRef capturedArgumentRef) {
		final int index = capturedArgumentRef.getArgumentIndex();
		final Object capturedArgument = this.capturedArgs.get(index);
		final Expression replacement = ExpressionFactory.getExpression(capturedArgument);
		capturedArgumentRef.getParent().replaceElement(capturedArgumentRef, replacement);
		// no need to process further
		return false;
	}
	
}
