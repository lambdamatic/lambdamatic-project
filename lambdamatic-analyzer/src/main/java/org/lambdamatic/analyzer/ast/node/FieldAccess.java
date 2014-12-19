/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

import org.lambdamatic.analyzer.exception.AnalyzeException;

/**
 * A field access: {@code sourceExpression.fieldName}
 * @author xcoulon
 *
 */
public class FieldAccess extends Expression {

	/** the Expression on containing the field to access.*/
	private final Expression sourceExpression;
	
	/** the name of the accessed field. */
	private final String fieldName;
	
	/**
	 * Full constructor
	  * <p>
	 * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
	 * </p>
	 * @param sourceExpression the sourceExpression containing the field to access
	 * @param fieldName the name of the accessed field
	 */
	public FieldAccess(final Expression sourceExpression, final String fieldName) {
		this(generateId(), sourceExpression, fieldName, false);
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
	public FieldAccess(final int id, final Expression expression, final String fieldName, final boolean inverted) {
		super(id, inverted);
		this.sourceExpression = expression;
		this.fieldName = fieldName;
	}
	
	@Override
	public ComplexExpression getParent() {
		return (ComplexExpression) super.getParent();
	}
	
	/**
	 * @return the sourceExpression on containing the field to access.
	 */
	public Expression getSourceExpression() {
		return sourceExpression;
	}

	/**
	 * {@inheritDoc}
	 * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate(int)
	 */
	@Override
	public FieldAccess duplicate(int id) {
		return new FieldAccess(id, getSourceExpression(), getFieldName(), isInverted());
	}

	/**
	 * {@inheritDoc}
	 * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate()
	 */
	@Override
	public FieldAccess duplicate() {
		return duplicate(generateId());
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.lambdamatic.analyzer.ast.node.Expression#getExpressionType()
	 */
	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.FIELD_ACCESS;
	}

	/**
	 * {@inheritDoc}
	 * @see org.lambdamatic.analyzer.ast.node.Expression#getJavaType()
	 */
	@Override
	public Class<?> getJavaType() {
		try {
			return sourceExpression.getJavaType().getField(fieldName).getType();
		} catch (NoSuchFieldException | SecurityException e) {
			throw new AnalyzeException("Failed to retrieve field '" + fieldName + "' in " + sourceExpression, e);
		}
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
	 * @see org.lambdamatic.analyzer.ast.node.Expression#canBeInverted()
	 */
	@Override
	public boolean canBeInverted() {
		return false;
	}

	@Override
	public String toString() {
		return this.sourceExpression.toString() + "." + this.fieldName;
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getExpressionType() == null) ? 0 : getExpressionType().hashCode());
		result = prime * result + ((sourceExpression == null) ? 0 : sourceExpression.hashCode());
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
		if (sourceExpression == null) {
			if (other.sourceExpression != null)
				return false;
		} else if (!sourceExpression.equals(other.sourceExpression))
			return false;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		return true;
	}
	
}

