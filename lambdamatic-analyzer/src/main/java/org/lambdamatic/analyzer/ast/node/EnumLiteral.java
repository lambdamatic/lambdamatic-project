/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class EnumLiteral extends ObjectInstance {

	/**
	 * Full constructor
	 * <p>
	 * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
	 * </p>
	 * 
	 * @param value
	 *            the literal value
	 */
	public EnumLiteral(final Enum<?> value) {
		this(generateId(), value, false);
	}

	/**
	 * Full constructor with given id
	 * 
	 * @param value
	 *            the literal value
	 * @param inverted
	 *            the inversion flag of this {@link Expression}.
	 */
	public EnumLiteral(final int id, final Enum<?> value, final boolean inverted) {
		super(id, value, inverted);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate(int)
	 */
	@Override
	public EnumLiteral duplicate(int id) {
		return new EnumLiteral(id, getValue(), isInverted());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lambdamatic.analyzer.ast.node.Expression#getValue()
	 */
	@Override
	public Enum<?> getValue() {
		return (Enum<?>) super.getValue();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lambdamatic.analyzer.ast.node.Expression#getExpressionType()
	 */
	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.ENUM_LITERAL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getValue().getClass().getName() + "." + getValue().name();
	}

}
