/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

import java.util.List;


/**
 * Variable passed during the call to the Lambda Expression serialized method.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class LocalVariable extends Expression {

	/** The variable index (in the bytecode). */
	private final int index;
	
	/** The variable name. */
	private final String name;
	
	/** The variable type's associated {@link Class}. */
	private final Class<?> type;

	/**
	 * Constructor when local variable is provided
	 * <p>
	 * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
	 * </p>
	 * 
	 * @param index
	 *            The variable index (as specified in the method bytecode).
	 * @param name
	 *            The variable name.
	 * @param value
	 *            The variable value.
	 */
	public LocalVariable(final int index, final String name, final Class<?> type) {
		this(generateId(), index, name, type, false);
		if(type == null) {
			throw new IllegalArgumentException("Type of local variable '" + name + "' must not be null");
		}

	}

	/**
	 * Full constructor
	 * 
	 * @param id
	 *            the synthetic id of this {@link Expression}.
	 * @param index the variable index (as defined in the bytecode)
	 * @param name
	 *            The variable name
	 * @param value
	 *            the local variable value
	 * @param type the fully qualified type name of the variable.
	 * @param inverted
	 *            the inversion flag of this {@link Expression}.
	 */
	public LocalVariable(final int id, final int index, final String name, final Class<?> type, final boolean inverted) {
		super(id, inverted);
		this.index = index;
		this.name = name;
		this.type = type;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate(int)
	 */
	@Override
	public LocalVariable duplicate(int id) {
		return new LocalVariable(id, this.index, this.name, this.type, isInverted());
	}

	/**
	 * {@inheritDoc}
	 * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate()
	 */
	@Override
	public LocalVariable duplicate() {
		return duplicate(generateId());
	}
	
	/**
	 * @return index the variable index (as defined in the bytecode)
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * @return the variable name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the variable type
	 */
	public Class<?> getType() {
		return type;
	}
	
	public Object getValue(final List<Object> localVariables) {
		return localVariables.get(index);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lambdamatic.analyzer.ast.node.Expression#getExpressionType()
	 */
	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.LOCAL_VARIABLE;
	}
	
	@Override
	public ComplexExpression getParent() {
		return (ComplexExpression)super.getParent();
	}

	/**
	 * {@inheritDoc}
	 * @see org.lambdamatic.analyzer.ast.node.Expression#getJavaType()
	 */
	@Override
	public Class<?> getJavaType() {
		return type;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return name;
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
		result = prime * result + ((getExpressionType() == null) ? 0 : getExpressionType().hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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


