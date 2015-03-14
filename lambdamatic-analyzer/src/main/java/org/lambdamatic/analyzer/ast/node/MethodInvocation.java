/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.lambdamatic.analyzer.ast.ReflectionUtils;
import org.lambdamatic.analyzer.exception.AnalyzeException;

/**
 * A method call: {@code expression.methodName(arguments)}
 * 
 * @author xcoulon
 *
 */
public class MethodInvocation extends ComplexExpression {

	/** the expression on which the method call is applied (may change if evaluated). */
	private Expression sourceExpression;

	/** the name of the called method. */
	private final String methodName;

	/** arguments the arguments passed as parameters during the call. */
	private final List<Expression> arguments;

	/** the return type of the underlying Java method.*/
	private final Class<?> returnType;
	
	/**
	 * Full constructor.
	 * 
	 * <p>
	 * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
	 * </p>
	 * 
	 * @param sourceExpression
	 *            the expression on which the method call is applied.
	 * @param methodName
	 *            the name of the called method.
	 * @param returnType
	 * 				the returned Java type of the underlying method.
	 * @param arguments
	 *            the arguments passed as parameters during the call.
	 */
	public MethodInvocation(final Expression sourceExpression, final String methodName, final Class<?> returnType, final Expression... arguments) {
		this(generateId(), sourceExpression, methodName, Arrays.asList(arguments), returnType, false);
	}

	/**
	 * Full constructor.
	 * 
	 * @param sourceExpression
	 *            the expression on which the method call is applied.
	 * @param methodName
	 *            the name of the called method.
	 * @param arguments
	 *            the arguments passed as parameters during the call.
	 * @param returnType
	 *            the returned Java type of the underlying method.
	 */
	public MethodInvocation(final Expression sourceExpression, final String methodName, final List<Expression> arguments, final Class<?> returnType) {
		this(generateId(), sourceExpression, methodName, arguments, returnType, false);
	}

	/**
	 * Full constructor.
	 * 
	 * @param id
	 *            the synthetic id of this {@link Expression}.
	 * @param sourceExpression
	 *            the expression on which the method call is applied.
	 * @param methodName
	 *            the name of the called method.
	 * @param arguments
	 *            the arguments passed as parameters during the call.
	 * @param returnType
	 *            the returned Java type of the underlying method.
	 * @param inverted
	 *            the inversion flag of this {@link Expression}.
	 */
	public MethodInvocation(final int id, final Expression sourceExpression, final String methodName, final List<Expression> arguments,
			final Class<?> returnType, final boolean inverted) {
		super(id, inverted);
		setSourceExpression(sourceExpression);
		this.methodName = methodName;
		this.arguments = arguments;
		this.arguments.stream().forEach(e -> e.setParent(this));
		this.returnType = returnType;
	}

	private void setSourceExpression(final Expression sourceExpression) {
		this.sourceExpression = sourceExpression;
		this.sourceExpression.setParent(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate(int)
	 */
	@Override
	public MethodInvocation duplicate(int id) {
		return new MethodInvocation(id, getSourceExpression().duplicate(), getMethodName(), Expression.duplicateExpressions(this.arguments), getReturnType(), isInverted());
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate()
	 */
	@Override
	public MethodInvocation duplicate() {
		return duplicate(generateId());
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.lambdamatic.analyzer.ast.node.Expression#getExpressionType()
	 */
	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.METHOD_INVOCATION;
	}
	
	@Override
	public boolean anyElementMatches(ExpressionType type) {
		return sourceExpression.anyElementMatches(type)
				|| this.arguments.stream().anyMatch(a -> a.anyElementMatches(type));
	}
	


	/**
	 * Returns the return type of the method. If the return type is a primitive
	 * type, its associated Java Wrapper class is returned (eg: 'int' ->
	 * 'java.lang.Integer')
	 * 
	 * @see org.lambdamatic.analyzer.ast.node.Expression#getJavaType()
	 */
	@Override
	public Class<?> getJavaType() {
		return getReturnType();
	}

	/**
	 * @return the returned {@link Class} of the underlying Java {@link Method}.
	 */
	public Class<?> getReturnType() {
		return returnType;
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.lambdamatic.analyzer.ast.node.Expression#getParent()
	 */
	@Override
	public ComplexExpression getParent() {
		return (ComplexExpression) super.getParent();
	}

	/**
	 * @return the source expression, on which this method invocation is performed (ie, on which the method is called).
	 */
	public Expression getSourceExpression() {
		return sourceExpression;
	}

	/**
	 * @return the name of the method
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * @return the arguments passed during the method call
	 */
	public List<Expression> getArguments() {
		return arguments;
	}

	/**
	 * @return the fist Java {@link Method} in the parent {@link Class} that matches the name are parameter types (or supertypes)
	 * @throws AnalyzeException if not method was found.
	 */
	public Method getJavaMethod() {
		final Class<?> sourceClass = this.sourceExpression.getJavaType();
		final Class<?>[] givenParameterTypes = this.arguments.stream().map(a -> a.getJavaType())
				.collect(Collectors.toList()).toArray(new Class<?>[0]);
		methods_loop:
		for(Method method : sourceClass.getMethods()) {
			if(!method.getName().equals(this.methodName) || method.getParameterTypes().length != givenParameterTypes.length) {
				continue methods_loop;
			}
			// perfect match or check if superclass/superinterfaces of the given parameter types could match
			for(int i = 0; i < givenParameterTypes.length; i++) {
				final Class<?> givenParameterType = givenParameterTypes[i];
				final Class<?> methodParameterType = method.getParameterTypes()[i];
				final boolean isSameType = methodParameterType.equals(givenParameterType);
				final boolean isSubType = methodParameterType.isAssignableFrom(givenParameterType);
				final boolean isMatchingVarArg = (i == givenParameterTypes.length - 1) && methodParameterType.isArray() && (methodParameterType.getComponentType().equals(givenParameterType) || methodParameterType.getComponentType().isAssignableFrom(givenParameterType));
				if(isSameType && !isSubType && !isMatchingVarArg) {
					continue methods_loop;
				}
			}
			// the method matches
			return method;
		}
		throw new AnalyzeException("Failed to find the underlying Java method named '" + this.methodName
				+ "' in class " + sourceClass.getName() + " with parameters " + givenParameterTypes);

	}
	
	@Override
	public int getNumberOfBytecodeInstructions() {
		int length = 1 + this.getSourceExpression().getNumberOfBytecodeInstructions();
		for (Expression arg : arguments) {
			length += arg.getNumberOfBytecodeInstructions();
		}
		return length;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lambdamatic.analyzer.ast.node.Expression#getValue()
	 */
	@Override
	public Object getValue() {
		return null;
	}
	
	/**
	 * Will attempt to evaluate this {@link MethodInvocation} and return its result, even if the arguments contains
	 * {@link CapturedArgument}.
	 * 
	 * @param capturedArgs the captured arguments provided by the {@link SerializedLambda} 
	 * @return the underlying Java method result.
	 * @throws AnalyzeException if the underlying Java {@link Method} could not be found or invoked. 
	 */
	public Object evaluate() {
		final List<Object> args = new ArrayList<>();
		final Class<?>[] argTypes = new Class<?>[this.arguments.size()];
		final Object source = this.sourceExpression.getValue();
		try {
			for (int i = 0; i < this.arguments.size(); i++) {
				final Object methodArgValue = arguments.get(i).getValue();
				args.add(methodArgValue);
				argTypes[i] = arguments.get(i).getJavaType();
			}
			final Method m = ReflectionUtils.getMethodToInvoke(source, this.methodName, argTypes);
			m.setAccessible(true);
			return m.invoke(source, args.toArray());
		} catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new AnalyzeException("Failed to invoke method '" + methodName + "' on '" + source + "'", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(final ExpressionVisitor visitor) {
		for (Expression arg : this.arguments) {
			arg.accept(visitor);
		}
		sourceExpression.accept(visitor);
		visitor.visit(this);
	}

	/**
	 * Replace the given {@code oldArgumoldExpressionent} with the given {@code newExpression} if it is part of this
	 * {@link MethodInvocation} arguments or if it is the sourceExpression.
	 * 
	 * {@inheritDoc}
	 */
	public void replaceElement(final Expression oldExpression, final Expression newExpression) {
		final int oldExpressionIndex = this.arguments.indexOf(oldExpression);
		if (oldExpressionIndex > -1) {
			this.arguments.set(oldExpressionIndex, newExpression);
			newExpression.setParent(this);
		} else if(oldExpression == this.sourceExpression) {
			setSourceExpression(newExpression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MethodInvocation inverse() {
		return new MethodInvocation(generateId(), sourceExpression, methodName, Expression.duplicateExpressions(this.arguments), getReturnType(), !isInverted());
	};

	/**
	 * {@inheritDoc}
	 * @see org.lambdamatic.analyzer.ast.node.Expression#canBeInverted()
	 */
	@Override
	public boolean canBeInverted() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		List<String> args = arguments.stream().map(Expression::toString).collect(Collectors.toList());
		return (isInverted() ? "!" : "") + sourceExpression.toString() + '.' + methodName + "(" + String.join(", ", args) + ")";
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getExpressionType() == null) ? 0 : getExpressionType().hashCode());
		result = prime * result + ((arguments == null) ? 0 : arguments.hashCode());
		result = prime * result + (isInverted() ? 1231 : 1237);
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result + ((sourceExpression == null) ? 0 : sourceExpression.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodInvocation other = (MethodInvocation) obj;
		if (arguments == null) {
			if (other.arguments != null)
				return false;
		} else if (!arguments.equals(other.arguments))
			return false;
		if (isInverted() != other.isInverted())
			return false;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		if (sourceExpression == null) {
			if (other.sourceExpression != null)
				return false;
		} else if (!sourceExpression.equals(other.sourceExpression))
			return false;
		return true;
	}

	/**
	 * Deletes this {@link MethodInvocation} from the Expression tree.
	 */
	public void delete() {
		// replace this MethodElement with the source expression if the parent exists
		if(getParent() != null) {
			// preserve the inversion
			if(this.isInverted()) {
				getParent().replaceElement(this, getSourceExpression().inverse());
			} else {
				getParent().replaceElement(this, getSourceExpression());
			}
		}
	}

}

