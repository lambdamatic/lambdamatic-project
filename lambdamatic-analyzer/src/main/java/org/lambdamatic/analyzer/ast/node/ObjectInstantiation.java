/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

import java.util.Arrays;
import java.util.List;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class ObjectInstantiation extends Expression {

	/** the expression on which the method call is applied. */
	private final Class<?> instanceType;

	/** arguments the arguments passed as parameters during the call. */
	private final List<Expression> arguments;

	/**
	 * Full constructor.
	 * <p>
	 * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
	 * </p>
	 * @param instanceType the type of the instance to build
	 * @param arguments the arguments to pass to the {@code <init>} method
	 */
	public ObjectInstantiation(final Class<?> instanceType, final Expression... arguments) {
		this(generateId(), instanceType, Arrays.asList(arguments), false);
	}

	/**
	 * Full constructor.
	 * <p>
	 * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
	 * </p>
	 * @param instanceType the type of the instance to build
	 * @param arguments the arguments to pass to the {@code <init>} method
	 */
	public ObjectInstantiation(final Class<?> instanceType, final List<Expression> arguments) {
		this(generateId(), instanceType, arguments, false);
	}
	
	/**
	 * Full constructor.
	 * <p>
	 * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
	 * </p>
	 * @param id the id of this expression
	 * @param instanceType the type of the instance to build
	 * @param arguments the arguments to pass to the {@code <init>} method
	 */
	public ObjectInstantiation(final int id, final Class<?> instanceType, final List<Expression> arguments, boolean inverted) {
		super(id, inverted);
		this.instanceType = instanceType;
		this.arguments = arguments;
	}

	/**
	 * @see org.lambdamatic.analyzer.ast.node.Expression#getExpressionType()
	 */
	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.OBJECT_INSTANTIATION;
	}

	/**
	 * @see org.lambdamatic.analyzer.ast.node.Expression#getJavaType()
	 */
	@Override
	public Class<?> getJavaType() {
		return instanceType;
	}

	/**
	 * @return the list of arguments to pass to the {@code <init>} method when creating the new object.
	 */
	public List<Expression> getArguments() {
		return arguments;
	}
	
	/**
	 * @see org.lambdamatic.analyzer.ast.node.Expression#inverse()
	 */
	@Override
	public Expression inverse() {
		throw new UnsupportedOperationException(this.getClass().getName() + " does not support inversion.");
	}

	/*$
	 * @see org.lambdamatic.analyzer.ast.node.Expression#canBeInverted()
	 */
	@Override
	public boolean canBeInverted() {
		return false;
	}

	/**
	 * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate(int)
	 */
	@Override
	public Expression duplicate(int id) {
		return new ObjectInstantiation(id, instanceType, arguments, isInverted());
	}

	/**
	 * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate()
	 */
	@Override
	public Expression duplicate() {
		return new ObjectInstantiation(instanceType, arguments);
	}

	@Override
	public String toString() {
		return this.instanceType.getName() + "(" + this.arguments + ")";
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arguments == null) ? 0 : arguments.hashCode());
		result = prime * result + ((instanceType == null) ? 0 : instanceType.hashCode());
		return result;
	}

	/* (non-Javadoc)
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
		ObjectInstantiation other = (ObjectInstantiation) obj;
		if (arguments == null) {
			if (other.arguments != null)
				return false;
		} else if (!arguments.equals(other.arguments))
			return false;
		if (instanceType == null) {
			if (other.instanceType != null)
				return false;
		} else if (!instanceType.equals(other.instanceType))
			return false;
		return true;
	}
	
}
