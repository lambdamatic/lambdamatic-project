/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

/**
 * A Boolean Literal Expression.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class BooleanLiteral extends ObjectInstance {

	/** The Universal Operator (or Boolean TRUE) */
	public static final BooleanLiteral UNIVERSAL_OPERATOR = new BooleanLiteral(true);

	/** The EmptySet Operator (or Boolean FALSE). */
	public static final BooleanLiteral EMPTY_SET_OPERATOR = new BooleanLiteral(false);

	/**
	 * Full constructor
	 * 
	 * <p>
	 * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
	 * </p>
	 * 
	 * @param value
	 *            the literal value
	 */
	public BooleanLiteral(final Boolean value) {
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
	public BooleanLiteral(final int id, final Boolean value, final boolean inverted) {
		super(id, value, inverted);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate(int)
	 */
	@Override
	public BooleanLiteral duplicate(int id) {
		return new BooleanLiteral(id, getValue(), isInverted());
	}

	/**
	 * {@inheritDoc}
	 * @see org.lambdamatic.analyzer.ast.node.Expression#getExpressionType()
	 */
	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.BOOLEAN_LITERAL;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lambdamatic.analyzer.ast.node.Expression#getValue()
	 */
	@Override
	public Boolean getValue() {
		return (Boolean) super.getValue();
	}

	/**
	 * {@inheritDoc}
	 * @see org.lambdamatic.analyzer.ast.node.Expression#inverse()
	 */
	@Override
	public Expression inverse() {
		return new BooleanLiteral(!getValue());
	}

	/**
	 * {@inheritDoc}
	 * @see org.lambdamatic.analyzer.ast.node.Expression#canBeInverted()
	 */
	@Override
	public boolean canBeInverted() {
		return true;
	}

}

