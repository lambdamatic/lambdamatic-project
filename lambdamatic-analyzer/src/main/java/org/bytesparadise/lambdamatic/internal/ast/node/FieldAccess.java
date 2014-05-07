/**
 * 
 */
package org.bytesparadise.lambdamatic.internal.ast.node;

/**
 * A field access: {@code expression.fieldName}
 * @author xcoulon
 *
 */
public class FieldAccess extends Expression {

	/** the expression on containing the field to access.*/
	private final Expression expression;
	
	/** the name of the accessed field. */
	private final String fieldName;
	
	/**
	 * Full constructor
	 * @param expression the expression on containing the field to access
	 * @param fieldName the name of the accessed field
	 */
	public FieldAccess(final Expression expression, final String fieldName) {
		super();
		this.expression = expression;
		this.fieldName = fieldName;
	}

	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.FIELD_ACCESS;
	}
	
	/**
	 * @return the expression
	 */
	public Expression getExpression() {
		return expression;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	@Override
	public Expression inverse() {
		throw new UnsupportedOperationException(this.getClass().getName() + " does not support inversion.");
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		return result;
	}

	/* (non-Javadoc)
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
		FieldAccess other = (FieldAccess) obj;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		return true;
	}
	
}
