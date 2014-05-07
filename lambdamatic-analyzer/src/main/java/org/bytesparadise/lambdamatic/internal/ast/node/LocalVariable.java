/**
 * 
 */
package org.bytesparadise.lambdamatic.internal.ast.node;

/**
 * @author xcoulon
 *
 */
public class LocalVariable extends Expression {

	/** The variable's name.*/
	private final String name;
	
	/** The variable type's fully qualified name. */
	private final String type;
	
	/**
	 * Full constructor
	 * @param name The variable's name.
	 * @param type The variable type's fully qualified name.
	 */
	public LocalVariable(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}

	/**
	 * {@inheritDoc}
	 * @see org.bytesparadise.lambdamatic.internal.ast.node.Expression#getExpressionType()
	 */
	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.VARIABLE;
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
		return name;
	}
	
	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		LocalVariable other = (LocalVariable) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	
}
