/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

/**
 * {@link Expression} holding an instance of an Object
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class ObjectInstance extends Expression {

	private final Object value;
	
	/**
	 * Full constructor
	 * <p>
	 * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
	 * </p>
	 * 
	 * @param value
	 *            the literal value
	 */
	public ObjectInstance(final Object value) {
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
	public ObjectInstance(final int id, final Object value, final boolean inverted) {
		super(id, inverted);
		this.value = value;
	}
	
	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.OBJECT_VALUE;
	}

	@Override
	public Class<?> getJavaType() {
		return this.value != null ? this.value.getClass() : Void.class;
	}

	/**
	 * {@inheritDoc}
	 * @see org.lambdamatic.analyzer.ast.node.Expression#inverse()
	 */
	@Override
	public Expression inverse() {
		throw new UnsupportedOperationException(this.getClass().getName() + " does not support inversion.");
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.lambdamatic.analyzer.ast.node.Expression#canBeInverted()
	 */
	@Override
	public boolean canBeInverted() {
		return false;
	}

	@Override
	public Expression duplicate(int id) {
		return new ObjectInstance(id, value, isInverted());
	}

	@Override
	public Expression duplicate() {
		return duplicate(generateId());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value != null ? value.toString() : "null";
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
		ObjectInstance other = (ObjectInstance) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	

}
