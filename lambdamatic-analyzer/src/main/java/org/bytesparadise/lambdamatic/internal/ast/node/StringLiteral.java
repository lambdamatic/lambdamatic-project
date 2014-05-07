/**
 * 
 */
package org.bytesparadise.lambdamatic.internal.ast.node;


/**
 * @author xcoulon
 *
 */
public class StringLiteral extends Expression {

	/** The literal value.*/
	private final String value;
	
	/**
	 * Full constructor
	 * @param value the literal value
	 */
	public StringLiteral(String value) {
		super();
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.bytesparadise.lambdamatic.internal.ast.node.Expression#eval()
	 */
	@Override
	public Object eval() {
		return getValue();
	}

	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.STRING_LITERAL;
	}

	@Override
	public Expression inverse() {
		throw new UnsupportedOperationException(this.getClass().getName() + " does not support inversion.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() { 
		return "\"" + value + "\"";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StringLiteral other = (StringLiteral) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	

}
