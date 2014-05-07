/**
 * 
 */
package org.bytesparadise.lambdamatic.internal.ast.node;


/**
 * @author xcoulon
 *
 */
public class NumberLiteral extends Expression {

	/** The literal value.*/
	private final Number value;
	
	/**
	 * Full constructor
	 * @param value the literal value
	 */
	public NumberLiteral(Number value) {
		super();
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public Number getValue() {
		return value;
	}

	/**
	 * {@inheritDoc}
	 * @see org.bytesparadise.lambdamatic.internal.ast.node.Expression#eval()
	 */
	@Override
	public Number eval() {
		return getValue();
	}

	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.NUMBER_LITERAL;
	}
	
	@Override
	public Expression inverse() {
		throw new UnsupportedOperationException(this.getClass().getName() + " does not support inversion.");
	}

	@Override
	public String toString() {
		return getValue().toString();
	}
	/**
	 * {@inheritDoc}
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
		NumberLiteral other = (NumberLiteral) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}



}
