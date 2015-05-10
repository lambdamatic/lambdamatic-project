/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

import java.lang.invoke.SerializedLambda;

import org.lambdamatic.analyzer.exception.AnalyzeException;

/**
 * Not an actual {@link CapturedArgument}, but just a reference (for further processing) to a {@link CapturedArgument}.
 * The raw Expression that will be kept in cache will use these {@link CapturedArgumentRef} until it is evaluated with
 * the actual {@link CapturedArgument}.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class CapturedArgumentRef extends Expression {

	/** index of the {@link CapturedArgument} in the {@link SerializedLambda}. */
	private final int index;
	/** the Java type of the referenced Java value. */
	private final Class<?> javaType;

	/**
	 * Constructor
	 * 
	 * <p>
	 * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
	 * </p>
	 * 
	 * @param index
	 *            the captured value index
	 * @param javaType
	 *            the actual type of the captured argument 
	 */
	public CapturedArgumentRef(final int index, final Class<?> javaType) {
		this(generateId(), index, javaType, false);
	}

	/**
	 * Full constructor with given id
	 * 
	 * @param id
	 *            the synthetic id of this {@link Expression}.
	 * @param javaType
	 *            the actual type of the captured argument 
	 * @param inverted
	 *            the inversion flag of this {@link Expression}.
	 */
	public CapturedArgumentRef(final int id, final int index, final Class<?> javaType, final boolean inverted) {
		super(id, inverted);
		this.index = index;
		this.javaType = javaType;
	}

	@Override
	public ComplexExpression getParent() {
		return (ComplexExpression) super.getParent();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate(int)
	 */
	@Override
	public CapturedArgumentRef duplicate(int id) {
		return new CapturedArgumentRef(id, this.index, this.javaType, isInverted());
	}

	/**
	 * {@inheritDoc}
	 * 
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
		throw new AnalyzeException("Capture Argument reference does not hold any value by itself.");
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
	 * 
	 * @see org.lambdamatic.analyzer.ast.node.Expression#getJavaType()
	 */
	@Override
	public Class<?> getJavaType() {
		return this.javaType;
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
	 * 
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		return result;
	}

	/*
	 * (non-Javadoc)
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
		CapturedArgumentRef other = (CapturedArgumentRef) obj;
		if (index != other.index)
			return false;
		return true;
	}

}
