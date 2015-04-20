/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

/**
 * A Character Literal Expression.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class CharacterLiteral extends ObjectInstance {

	/**
	 * Full constructor
	 * <p>
	 * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
	 * </p>
	 * 
	 * @param value
	 *            the literal value
	 */
	public CharacterLiteral(final Character value) {
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
	public CharacterLiteral(final int id, final Character value, final boolean inverted) {
		super(id, value, inverted);
	}

	/**
	 * {@inheritDoc}
	 * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate(int)
	 */
	@Override
	public CharacterLiteral duplicate(int id) {
		return new CharacterLiteral(id, getValue(), isInverted());
	}

	/**
	 * {@inheritDoc}
	 * @see org.lambdamatic.analyzer.ast.node.Expression#getExpressionType()
	 */
	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.CHARACTER_LITERAL;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lambdamatic.analyzer.ast.node.Expression#getValue()
	 */
	@Override
	public Character getValue() {
		return (Character)super.getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "'" + super.getValue().toString() + "'";
	}

}

