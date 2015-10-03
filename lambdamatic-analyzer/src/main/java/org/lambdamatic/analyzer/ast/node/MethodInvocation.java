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

import org.lambdamatic.analyzer.exception.AnalyzeException;

/**
 * A method call: {@code expression.methodName(arguments)}
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class MethodInvocation extends ComplexExpression {

	/** the expression on which the method call is applied (may change if evaluated). */
	private Expression source;

	/** the underlying Java method that is called. */
	private final Method javaMethod;

	/** the arguments passed as parameters during the call. */
	private final List<Expression> arguments;
	
	/** the actual return type (preserving data from generics if available) */
	private final Class<?> returnType;

	/**
	 * Full constructor.
	 * 
	 * @param source
	 *            the expression on which the method call is applied.
	 * @param methodName
	 *            the name of the called method.
	 * @param arguments
	 *            the arguments passed as parameters during the call.
	 */
	public MethodInvocation(final Expression sourceExpression, final Method javaMethod, final Expression... arguments) {
		this(sourceExpression, javaMethod, javaMethod.getReturnType(), Arrays.asList(arguments));
	}

	/**
	 * Full constructor.
	 * 
	 * @param source
	 *            the expression on which the method call is applied.
	 * @param methodName
	 *            the name of the called method.
	 * @param returnType
	 *            the returned Java type of the underlying method.
	 * @param arguments
	 *            the arguments passed as parameters during the call.
	 */
	public MethodInvocation(final Expression sourceExpression, final Method javaMethod, final Class<?> returnType, final List<Expression> arguments) {
		this(generateId(), sourceExpression, javaMethod, returnType, arguments, false);
	}

	/**
	 * Full constructor.
	 * 
	 * @param id
	 *            the synthetic id of this {@link Expression}.
	 * @param source
	 *            the expression on which the method call is applied.
	 * @param javaMethod
	 *            the actual Java {@link Method} being called.
	 * @param returnType
	 *            the returned Java type of the underlying method.
	 * @param arguments
	 *            the arguments passed as parameters during the call.
	 */
	public MethodInvocation(final int id, final Expression sourceExpression, final Method javaMethod, final Class<?> returnType, final List<Expression> arguments,
			final boolean inverted) {
		super(id, inverted);
		setSourceExpression(sourceExpression);
		this.javaMethod = javaMethod;
		this.returnType = returnType;
		this.arguments = arguments;
		this.arguments.stream().forEach(e -> e.setParent(this));
	}

	private void setSourceExpression(final Expression sourceExpression) {
		this.source = sourceExpression;
		this.source.setParent(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate(int)
	 */
	@Override
	public MethodInvocation duplicate(int id) {
		return new MethodInvocation(id, getSource().duplicate(), this.javaMethod, this.returnType,
				Expression.duplicateExpressions(this.arguments), isInverted());
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
		return source.anyElementMatches(type)
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
		return this.returnType;
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
	public Expression getSource() {
		return source;
	}

	/**
	 * @return the name of the method
	 */
	public String getMethodName() {
		return this.javaMethod.getName();
	}

	/**
	 * @return the arguments passed during the method call
	 */
	public List<Expression> getArguments() {
		return arguments;
	}

	/**
	 * @return the underlying Java {@link Method}
	 * @throws AnalyzeException if not method was found.
	 */
	public Method getJavaMethod() {
		return this.javaMethod;
	}

	@Override
	public int getNumberOfBytecodeInstructions() {
		int length = 1 + this.getSource().getNumberOfBytecodeInstructions();
		for (Expression arg : arguments) {
			length += arg.getNumberOfBytecodeInstructions();
		}
		return length;
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
		final Object source = this.source.getValue();
		try {
			for (int i = 0; i < this.arguments.size(); i++) {
				final Object methodArgValue = arguments.get(i).getValue();
				args.add(methodArgValue);
				argTypes[i] = arguments.get(i).getJavaType();
			}
			this.javaMethod.setAccessible(true);
			return javaMethod.invoke(source, args.toArray());
		} catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new AnalyzeException("Failed to invoke method '" + javaMethod.getName() + "' on '" + source + "'", e);
		}
	}
	
	/**
	 * @return the value of {@code this} Expression
	 */
	public Object getValue() {
		if(this.source.getExpressionType() == ExpressionType.CLASS_LITERAL) {
			return evaluate();
		} else {
			return evaluate();
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
		source.accept(visitor);
		visitor.visit(this);
	}

	/**
	 * Replace the given {@code oldArgumoldExpressionent} with the given {@code newExpression} if it is part of this
	 * {@link MethodInvocation} arguments or if it is the source.
	 * 
	 * {@inheritDoc}
	 */
	public void replaceElement(final Expression oldExpression, final Expression newExpression) {
		final int oldExpressionIndex = this.arguments.indexOf(oldExpression);
		if (oldExpressionIndex > -1) {
			this.arguments.set(oldExpressionIndex, newExpression);
			newExpression.setParent(this);
		} else if(oldExpression == this.source) {
			setSourceExpression(newExpression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MethodInvocation inverse() {
		return new MethodInvocation(generateId(), source, javaMethod, this.returnType,
				Expression.duplicateExpressions(this.arguments), !isInverted());
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
		return (isInverted() ? "!" : "") + source.toString() + '.' + javaMethod.getName() + "(" + String.join(", ", args) + ")";
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
		result = prime * result + ((javaMethod == null) ? 0 : javaMethod.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
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
		if (javaMethod == null) {
			if (other.javaMethod != null)
				return false;
		} else if (!javaMethod.getName().equals(other.javaMethod.getName()))
			return false;
		else if (!Arrays.deepEquals(javaMethod.getParameterTypes(), other.javaMethod.getParameterTypes()))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
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
				getParent().replaceElement(this, getSource().inverse());
			} else {
				getParent().replaceElement(this, getSource());
			}
		}
	}

}

