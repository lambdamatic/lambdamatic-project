/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class NumberLiteral extends ObjectInstance {

	/**
	 * Full constructor
	 * <p>
	 * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
	 * </p>
	 * 
	 * @param value
	 *            the literal value
	 */
	public NumberLiteral(final Number value) {
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
	public NumberLiteral(final int id, final Number value, final boolean inverted) {
		super(id, value, inverted);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate(int)
	 */
	@Override
	public NumberLiteral duplicate(int id) {
		return new NumberLiteral(id, getValue(), isInverted());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lambdamatic.analyzer.ast.node.Expression#getExpressionType()
	 */
	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.NUMBER_LITERAL;
	}

	@Override
	public Number getValue() {
		return (Number) super.getValue();
	}

}
