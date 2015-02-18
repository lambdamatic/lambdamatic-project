/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

import java.util.ArrayList;
import java.util.List;

/**
 * Object created and used during the call to the Lambda Expression serialized method.
 *
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class ObjectVariable extends Expression {

	/** the expression on which the method call is applied. */
	private final Class<?> instanceType;

	/** arguments the arguments passed as parameters during the call to &lt;init&gt;. */
	private final List<Expression> arguments = new ArrayList<Expression>();

	/**
	 * Full constructor.
	 * <p>
	 * Note: the synthetic {@code id} is generated and the {@code inversion} flag is set to {@code false}.
	 * </p>
	 * @param instanceType the type of the instance to build
	 */
	public ObjectVariable(final Class<?> instanceType) {
		this(generateId(), instanceType, false);
	}

	/**
	 * Full constructor.
	 * <p>
	 * Note: the synthetic {@code id} is generated.
	 * </p>
	 * @param instanceType the type of the instance to build
	 * @param inverted the inversion flag
	 */
	public ObjectVariable(final Class<?> instanceType, final boolean inverted) {
		this(generateId(), instanceType, inverted);
	}

	/**
	 * Full constructor.
	 * <p>
	 * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
	 * </p>
	 * @param id the id of this expression
	 * @param instanceType the type of the instance to build
	 */
	public ObjectVariable(final int id, final Class<?> instanceType, boolean inverted) {
		super(id, inverted);
		this.instanceType = instanceType;
	}

	/**
	 * Sets the arguments passed during the call to the &lt;init&gt; method. 
	 * 
	 * @param arguments the arguments to pass to the {@code <init>} method
	 */
	public void setInitArguments(final List<Expression> arguments) {
		this.arguments.addAll(arguments);
	}
	
	/**
	 * @see org.lambdamatic.analyzer.ast.node.Expression#getExpressionType()
	 */
	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.OBJECT_VARIABLE;
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
		final ObjectVariable duplicateVariable = new ObjectVariable(id, instanceType, isInverted());
		duplicateVariable.setInitArguments(this.arguments);
		return duplicateVariable;
	}

	/**
	 * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate()
	 */
	@Override
	public Expression duplicate() {
		final ObjectVariable duplicateVariable = new ObjectVariable(instanceType, isInverted());
		duplicateVariable.setInitArguments(this.arguments);
		return duplicateVariable;
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
		ObjectVariable other = (ObjectVariable) obj;
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

