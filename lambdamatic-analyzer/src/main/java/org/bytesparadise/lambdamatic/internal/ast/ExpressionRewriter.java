package org.bytesparadise.lambdamatic.internal.ast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bytesparadise.lambdamatic.internal.ast.node.CapturedArgument;
import org.bytesparadise.lambdamatic.internal.ast.node.Expression;
import org.bytesparadise.lambdamatic.internal.ast.node.ExpressionVisitor;
import org.bytesparadise.lambdamatic.internal.ast.node.MethodInvocation;
import org.bytesparadise.lambdamatic.internal.ast.node.StringLiteral;
import org.bytesparadise.lambdamatic.internal.ast.node.Expression.ExpressionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a new {@link Expression} where any method on a {@link CapturedArgument} is converted to its actual literal value.
 * 
 * @author xcoulon
 *
 */
public class ExpressionRewriter extends ExpressionVisitor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExpressionRewriter.class); 
	
	@Override
	public boolean visitMethodInvocationExpression(final MethodInvocation methodInvocation) {
		if(methodInvocation.getSourceExpression().getExpressionType() == ExpressionType.CAPTURED_ARGUMENT) {
			final CapturedArgument capturedSourceArgument = (CapturedArgument) methodInvocation.getSourceExpression(); 
			final String methodName = methodInvocation.getMethodName();
			try {
				final Object source = capturedSourceArgument.getValue();
				final List<Object> args = new ArrayList<>();
				final List<Class<?>> argTypes = new ArrayList<>();
				for(Expression methodArgument : methodInvocation.getArguments()) {
					final Object methodArgValue = methodArgument.eval();
					args.add(methodArgValue);
					argTypes.add(methodArgValue.getClass());
				}
				final Method m = getMethodToInvoke(source, methodName, argTypes);
				m.setAccessible(true);
				final Object replacement = m.invoke(source, args.toArray());
				methodInvocation.replaceWith(getLiteral(replacement));
				// no further visiting on this branch of the expression tree.
				return false;
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				LOGGER.error("Failed to execute method '{}' on captured argument '{}'", methodName, capturedSourceArgument.getValue());
			}
		}
		return true;
	}

	/**
	 * 
	 * @param methodName the name of the declared method to find
	 * @param source the source (class instance) on which the method should be found, or {@code null} if the method to find is static
	 * @param argTypes the argument types
	 * @return the {@link Method} 
	 * @throws NoSuchMethodException
	 */
	private Method getMethodToInvoke(final Object source, final String methodName, 
			final List<Class<?>> argTypes) throws NoSuchMethodException {
		if(source instanceof Class) {
			return getMethodToInvoke((Class<?>)source, methodName, argTypes);
		}
		return getMethodToInvoke(source.getClass(), methodName, argTypes);
	}

	// FIXME: we should cover all cases: method in superclass and all variants of superclass of any/all arguments.
	private Method getMethodToInvoke(final Class<?> clazz, final String methodName, final List<Class<?>> argTypes)
			throws NoSuchMethodException {
		return clazz.getDeclaredMethod(methodName, argTypes.toArray(new Class<?>[argTypes.size()]));
	}
	
	/**
	 * @return a literal Expression for the given value, depending on its type.
	 * @param value the literal value to examine and wrap into an Expression
	 */
	private Expression getLiteral(Object value) {
		if(value instanceof String) {
			return new StringLiteral((String) value);
		}
		//FIXME: complete with other value types (number, null, etc.).
		return null;
	}

	/**
	 * Occurs when the {@link CapturedArgument} is not the source expression of a {@link MethodInvocation}. 
	 * {@inheritDoc}
	 * @see org.bytesparadise.lambdamatic.internal.ast.node.ExpressionVisitor#visitCapturedArgument(org.bytesparadise.lambdamatic.internal.ast.node.CapturedArgument)
	 */
	@Override
	public boolean visitCapturedArgument(final CapturedArgument capturedArgument) {
		capturedArgument.replaceWith(getLiteral(capturedArgument.getValue()));
		// no need to keep on visiting the current branch after the replacement with a literal.
		return false;
	}
}