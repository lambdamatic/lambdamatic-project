package org.lambdamatic.analyzer.ast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.lambdamatic.analyzer.ast.node.CapturedArgument;
import org.lambdamatic.analyzer.ast.node.ClassLiteral;
import org.lambdamatic.analyzer.ast.node.ComplexExpression;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.Expression.ExpressionType;
import org.lambdamatic.analyzer.ast.node.ExpressionVisitor;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.LiteralFactory;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a new {@link Expression} where any method on a {@link CapturedArgument} is converted to its actual literal value.
 * Also, method calls such as {@link Long#longValue()}, etc. are removed because they are not relevant in our case (these are underlying conversion methods)
 * 
 * @author xcoulon
 *
 */
public class ExpressionRewriter extends ExpressionVisitor {

	/** The logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ExpressionRewriter.class);

	@Override
	public boolean visit(final Expression expr) {
		return super.visit(expr);
	}

	@Override
	public boolean visitMethodInvocationExpression(final MethodInvocation methodInvocation) {
		if (methodInvocation.getSourceExpression().getExpressionType() == ExpressionType.CAPTURED_ARGUMENT) {
			final String methodName = methodInvocation.getMethodName();
			final CapturedArgument capturedSourceArgument = (CapturedArgument) methodInvocation.getSourceExpression();
			try {
				final Object source = capturedSourceArgument.getValue();
				final List<Object> args = new ArrayList<>();
				final List<Class<?>> argTypes = new ArrayList<>();
				for (Expression methodArgument : methodInvocation.getArguments()) {
					final Object methodArgValue = methodArgument.getValue();
					args.add(methodArgValue);
					argTypes.add(methodArgValue.getClass());
				}
				final Method m = getMethodToInvoke(source, methodName, argTypes);
				m.setAccessible(true);
				final Object replacement = m.invoke(source, args.toArray());
				final ComplexExpression parentExpression = methodInvocation.getParent();
				if (parentExpression != null) {
					parentExpression.replaceElement(methodInvocation, LiteralFactory.getLiteral(replacement));
				}
				// no further visiting on this (obsolete) branch of the expression tree.
				return false;
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				LOGGER.error("Failed to execute method '{}' on captured argument '{}'", methodName, capturedSourceArgument.getValue());
			}
		} else {
			final Class<?> sourceType = methodInvocation.getSourceExpression().getJavaType();
			final String methodName = methodInvocation.getMethodName();
			if(sourceType.equals(Boolean.class) && methodName.equals("booleanValue")) {
				methodInvocation.delete();
			}
			// drop invocation of Byte#byteValue() method
			else if(sourceType.equals(Byte.class) && methodName.equals("byteValue")) {
				methodInvocation.delete();
			}
			// drop invocation of Short#shortValue() method
			else if(sourceType.equals(Short.class) && methodName.equals("shortValue")) {
				methodInvocation.delete();
			}
			// drop invocation of Integer#intValue() method
			else if(sourceType.equals(Integer.class) && methodName.equals("intValue")) {
				methodInvocation.delete();
			}
			// drop invocation of Long#longValue() method
			else if(sourceType.equals(Long.class) && methodName.equals("longValue")) {
				methodInvocation.delete();
			}
			// drop invocation of Float#floatValue() method
			else if(sourceType.equals(Float.class) && methodName.equals("floatValue")) {
				methodInvocation.delete();
			}
			// drop invocation of Double#doubleValue() method
			else if(sourceType.equals(Double.class) && methodName.equals("doubleValue")) {
				methodInvocation.delete();
			}
			// drop invocation of Character#charValue() method
			else if(sourceType.equals(Character.class) && methodName.equals("charValue")) {
				methodInvocation.delete();
			}
		}
		return true;
	}

	@Override
	public boolean visitFieldAccessExpression(final FieldAccess fieldAccess) {
		if (fieldAccess.getSourceExpression().getExpressionType() == ExpressionType.CAPTURED_ARGUMENT) {
			final CapturedArgument capturedSourceArgument = (CapturedArgument) fieldAccess.getSourceExpression();
			final String fieldName = fieldAccess.getFieldName();
			try {
				final Object source = capturedSourceArgument.getValue();
				final Field f = getFieldToInvoke(source, fieldName);
				f.setAccessible(true);
				final Object replacement = f.get(source);
				final ComplexExpression parentExpression = fieldAccess.getParent();
				if (parentExpression != null) {
					parentExpression.replaceElement(fieldAccess, LiteralFactory.getLiteral(replacement));
				}
				// no further visiting on this (obsolete) branch of the expression tree.
				return false;
			} catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException e) {
				LOGGER.error("Failed to execute method '{}' on captured argument '{}'", fieldName, capturedSourceArgument.getValue());
			}
		} else if (fieldAccess.getSourceExpression().getExpressionType() == ExpressionType.CLASS_LITERAL) {
			final ClassLiteral sourceClass = (ClassLiteral) fieldAccess.getSourceExpression();
			final String fieldName = fieldAccess.getFieldName();
			try {
				final Class<?> source = sourceClass.getValue();
				final Field f = getFieldToInvoke(source, fieldName);
				f.setAccessible(true);
				final Object replacement = f.get(source);
				final ComplexExpression parentExpression = fieldAccess.getParent();
				if (parentExpression != null) {
					parentExpression.replaceElement(fieldAccess, LiteralFactory.getLiteral(replacement));
				}
				// no further visiting on this (obsolete) branch of the expression tree.
				return false;
			} catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException e) {
				LOGGER.error("Failed to execute method '{}' on class '{}'", fieldName, sourceClass.toString());
			}
			
		}
		return super.visitFieldAccessExpression(fieldAccess);
	}

	/**
	 * Finds and returns the method with the given name and given argument types on the given {@code source} Object
	 * 
	 * @param methodName
	 *            the name of the declared method to find
	 * @param source
	 *            the source (class instance) on which the method should be found, or {@code null} if the method to find is static
	 * @param argTypes
	 *            the argument types
	 * @return the {@link Method}
	 * @throws NoSuchMethodException
	 */
	private Method getMethodToInvoke(final Object source, final String methodName, final List<Class<?>> argTypes)
			throws NoSuchMethodException {
		if (source instanceof Class) {
			return getMethodToInvoke((Class<?>) source, methodName, argTypes);
		}
		return getMethodToInvoke(source.getClass(), methodName, argTypes);
	}

	// FIXME: we should cover all cases: method in superclass and all variants of superclass of any/all arguments.
	private Method getMethodToInvoke(final Class<?> clazz, final String methodName, final List<Class<?>> argTypes)
			throws NoSuchMethodException {
		return clazz.getDeclaredMethod(methodName, argTypes.toArray(new Class<?>[argTypes.size()]));
	}

	/**
	 * Finds and returns the method with the given name and given argument types on the given {@code source} Object
	 * 
	 * @param methodName
	 *            the name of the declared method to find
	 * @param source
	 *            the source (class instance) on which the method should be found, or {@code null} if the method to find is static
	 * @param argTypes
	 *            the argument types
	 * @return the {@link Method}
	 * @throws NoSuchMethodException
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	private Field getFieldToInvoke(final Object source, final String fieldName) throws NoSuchFieldException, SecurityException {
		if (source instanceof Class) {
			return getFieldToInvoke((Class<?>) source, fieldName);
		}
		return getFieldToInvoke(source.getClass(), fieldName);
	}

	// FIXME: we should cover all cases: method in superclass and all variants of superclass of any/all arguments.
	private Field getFieldToInvoke(final Class<?> clazz, final String fieldName) throws  NoSuchFieldException,
			SecurityException {
		return clazz.getField(fieldName);
	}

	/**
	 * Occurs when the {@link CapturedArgument} is not the source expression of a {@link MethodInvocation}. {@inheritDoc}
	 * 
	 * @see org.lambdamatic.analyzer.ast.node.ExpressionVisitor#visitCapturedArgument(org.lambdamatic.analyzer.ast.node.CapturedArgument)
	 */
	@Override
	public boolean visitCapturedArgument(final CapturedArgument capturedArgument) {
		capturedArgument.getParent().replaceElement(capturedArgument, LiteralFactory.getLiteral(capturedArgument.getValue()));
		// no need to keep on visiting the current branch after the replacement with a literal.
		return false;
	}
	
}
