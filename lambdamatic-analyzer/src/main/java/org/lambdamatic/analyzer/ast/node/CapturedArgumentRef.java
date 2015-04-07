/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

import java.lang.invoke.SerializedLambda;

/**
 * Not an actual {@link CapturedArgument}, but just a reference (for further processing)
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class CapturedArgumentRef extends Expression {

	/** index of the {@link CapturedArgument} in the {@link SerializedLambda}. */
	private final int index;
	
	/** the actual captured argument value. */
	private final Object value;

	/**
	 * Constructor
	 * 
	 * <p>
	 * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
	 * </p>
	 * @param index the captured value index
	 * @param value
	 *            the actual captured argument value
	 */
	public CapturedArgumentRef(final int index, final Object value) {
		this(generateId(), index, value, false);
	}

	/**
	 * Full constructor with given id
	 * 
	 * @param id
	 *            the synthetic id of this {@link Expression}.
	 * @param value
	 *            the actual captured argument value
	 * @param inverted
	 *            the inversion flag of this {@link Expression}.
	 */
	public CapturedArgumentRef(final int id, final int index, final Object value, final boolean inverted) {
		super(id, inverted);
		this.index = index;
		this.value = value;
	}

	@Override
	public ComplexExpression getParent() {
		return (ComplexExpression)super.getParent();
	}

	/**
	 * {@inheritDoc}
	 * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate(int)
	 */
	@Override
	public CapturedArgumentRef duplicate(int id) {
		return new CapturedArgumentRef(id, this.index, this.value, isInverted());
	}

	/**
	 * {@inheritDoc}
	 * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate()
	 */
	@Override
	public CapturedArgumentRef duplicate() {
		return duplicate(generateId());
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lambdamatic.analyzer.ast.node.Expression#getValue()
	 */
	@Override
	public Object getValue() {
		return value;
	}
	
	/**
	 * @return the index of the captured argument
	 * @see SerializedLambda#getCapturedArg(int)
	 */
	public int getArgumentIndex() {
		return index;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.CAPTURED_ARGUMENT_REF;
	}

	/**
	 * {@inheritDoc}
	 * @see org.lambdamatic.analyzer.ast.node.Expression#getJavaType()
	 */
	@Override
	public Class<?> getJavaType() {
		return value.getClass();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lambdamatic.analyzer.ast.node.Expression#inverse()
	 */
	@Override
	public Expression inverse() {
		throw new UnsupportedOperationException(this.getClass().getName() + " does not support inversion.");
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.lambdamatic.analyzer.ast.node.Expression#canBeInverted()
	 */
	@Override
	public boolean canBeInverted() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "<CapturedArgument#" + index + ">";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
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
		CapturedArgumentRef other = (CapturedArgumentRef) obj;
		if (index != other.index)
			return false;
		return true;
	}

}

