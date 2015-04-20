/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class StringLiteral extends ObjectInstance {

	/**
	 * Full constructor
	 * <p>
	 * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
	 * </p>
	 * 
	 * @param value
	 *            the literal value
	 */
	public StringLiteral(final String value) {
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
	public StringLiteral(final int id, final String value, final boolean inverted) {
		super(id, value, inverted);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate(int)
	 */
	@Override
	public StringLiteral duplicate(int id) {
		return new StringLiteral(id, getValue(), isInverted());
	}

	@Override
	public String getValue() {
		return (String) super.getValue();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lambdamatic.analyzer.ast.node.Expression#getExpressionType()
	 */
	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.STRING_LITERAL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "\"" + getValue() + "\"";
	}

}
