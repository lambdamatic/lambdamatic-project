/**
 * 
 */
package org.bytesparadise.lambdamatic.internal.ast.node;

/**
 * A Boolean Literal Expression.
 * 
 * @author xcoulon
 *
 */
public class BooleanLiteral extends Expression {

	/** The literal value. */
	private final Boolean value;

	/**
	 * Full constructor
	 * 
	 * @param value
	 *            the literal value
	 */
	public BooleanLiteral(Boolean value) {
		super();
		this.value = value;
	}

	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.BOOLEAN_LITERAL;
	}

	/**
	 * @return the value
	 */
	public Boolean getValue() {
		return value;
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.bytesparadise.lambdamatic.internal.ast.node.Expression#eval()
	 */
	@Override
	public Boolean eval() {
		return getValue();
	}

	@Override
	public Expression inverse() {
		return new BooleanLiteral(!getValue());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
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
		BooleanLiteral other = (BooleanLiteral) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
