/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class NullLiteral extends ObjectInstance {

	/**
	 * Full constructor
	 * <p>
	 * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
	 * </p>
	 * 
	 * @param value
	 *            the literal value
	 */
	public NullLiteral() {
		this(generateId(), false);
	}

	/**
	 * Full constructor with given id
	 * 
	 * @param id
	 *            the synthetic id of this {@link Expression}.
	 * @param inverted
	 *            the inversion flag of this {@link Expression}.
	 */
	public NullLiteral(final int id, final boolean inverted) {
		super(id, null, inverted);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate(int)
	 */
	@Override
	public NullLiteral duplicate(int id) {
		return new NullLiteral(id, isInverted());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lambdamatic.analyzer.ast.node.Expression#getExpressionType()
	 */
	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.NULL_LITERAL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "null";
	}

}
