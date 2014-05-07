/**
 * 
 */
package org.bytesparadise.lambdamatic.internal.ast.node;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A method call: {@code expression.methodName(arguments)}
 * 
 * @author xcoulon
 *
 */
public class MethodInvocation extends Expression {

	/** the expression on which the method call is applied. */
	private Expression sourceExpression;

	/** the name of the called method. */
	private final String methodName;

	/** arguments the arguments passed as parameters during the call. */
	private final List<Expression> arguments;

	/**
	 * Full constructor.
	 * 
	 * @param sourceExpression
	 *            the expression on which the method call is applied.
	 * @param methodName
	 *            the name of the called method.
	 * @param arguments
	 *            the arguments passed as parameters during the call.
	 */
	public MethodInvocation(final Expression sourceExpression, final String methodName, final Expression... arguments) {
		this(sourceExpression, methodName, Arrays.asList(arguments));
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
	 */
	public MethodInvocation(final Expression sourceExpression, final String methodName, final List<Expression> arguments) {
		super();
		this.sourceExpression = sourceExpression;
		this.sourceExpression.setParent(this);
		this.methodName = methodName;
		this.arguments = arguments;
		this.arguments.stream().forEach(e -> e.setParent(this));
	}

	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.METHOD_INVOCATION;
	}

	/**
	 * @return the source expression, on which this method invocation is
	 *         performed (ie, on which the method is called).
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
	 * @see org.bytesparadise.lambdamatic.internal.ast.node.Expression#eval()
	 */
	@Override
	public Object eval() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(final ExpressionVisitor visitor) {
		if (visitor.visit(this)) {
			sourceExpression.accept(visitor);
			for (Expression arg : this.arguments) {
				arg.accept(visitor);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void replace(final Expression oldExpression, final Expression newExpression) {
		if(this.sourceExpression.equals(oldExpression)) {
			this.sourceExpression = newExpression;
		} else {
			final int oldExpressionIndex = this.arguments.indexOf(oldExpression);
			if(oldExpressionIndex > -1) {
				this.arguments.set(oldExpressionIndex, newExpression);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public MethodInvocation inverse() {
		final MethodInvocation inversedMethodInvocation = new MethodInvocation(sourceExpression, methodName, arguments);
		inversedMethodInvocation.inversed = !this.inversed;
		return inversedMethodInvocation;
	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		List<String> args = arguments.stream().map(Expression::toString).collect(Collectors.toList());
		return (inversed ? "!" : "") + sourceExpression.toString() + '.' + methodName + "(" + String.join(", ", args)
				+ ")";
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
		result = prime * result + ((arguments == null) ? 0 : arguments.hashCode());
		result = prime * result + (inversed ? 1231 : 1237);
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
		if (inversed != other.inversed)
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

}
