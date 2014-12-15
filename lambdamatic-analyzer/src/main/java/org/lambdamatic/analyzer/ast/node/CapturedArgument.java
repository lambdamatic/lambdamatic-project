/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

/**
 * @author xcoulon
 *
 */
public class CapturedArgument extends Expression {

	private final Object value;

	/**
	 * Constructor
	 * 
	  * <p>
	 * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
	 * </p>
	 * @param value
	 *            the captured argument
	 */
	public CapturedArgument(final Object value) {
		this(generateId(), value, false);
	}

	/**
	 * Full constructor with given id
	 * 
	 * @param id
	 *            the synthetic id of this {@link Expression}.
	 * @param value
	 *            the literal value
	 * @param inverted
	 *            the inversion flag of this {@link Expression}.
	 */
	public CapturedArgument(final int id, final Object value, final boolean inverted) {
		super(id, inverted);
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
	public CapturedArgument duplicate(int id) {
		return new CapturedArgument(id, getValue(), isInverted());
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
	 * {@inheritDoc}
	 */
	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.CAPTURED_ARGUMENT;
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



	@Override
	public String toString() {
		return (value != null) ? value.toString() : null;
	}

}

