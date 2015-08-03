/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.lambdamatic.analyzer.exception.AnalyzeException;

/**
 * A field access: {@code source.fieldName}
 * @author xcoulon
 *
 */
public class FieldAccess extends ComplexExpression {

	/** the Expression on containing the field to access (may change if it is evaluated).*/
	private Expression source;
	
	/** the name of the accessed field. */
	private final String fieldName;
	
	/**
	 * Full constructor
	 * <p>
	 * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
	 * </p>
	 * @param sourceExpression the source containing the field to access
	 * @param fieldName the name of the accessed field
	 */
	public FieldAccess(final Expression sourceExpression, final String fieldName) {
		this(sourceExpression, fieldName, false);
	}

	/**
	 * Full constructor
	 * <p>
	 * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
	 * </p>
	 * @param sourceExpression the source containing the field to access
	 * @param fieldName the name of the accessed field
	 * @param inverted if this {@link FieldAccess} expression is inverted
	 */
	public FieldAccess(final Expression sourceExpression, final String fieldName, final boolean inverted) {
		this(generateId(), sourceExpression, fieldName, inverted);
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
		setSourceExpression(expression);
		this.fieldName = fieldName;
	}

	private void setSourceExpression(final Expression expression) {
		this.source = expression;
		this.source.setParent(this);
	}
	
	@Override
	public ComplexExpression getParent() {
		return (ComplexExpression) super.getParent();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(final ExpressionVisitor visitor) {
		source.accept(visitor);
		visitor.visit(this);
	}
	
	@Override
	public void replaceElement(final Expression oldSourceExpression, final Expression newSourceExpression) {
		if(oldSourceExpression == this.source) {
			setSourceExpression(newSourceExpression);
		}
	}

	@Override
	public boolean anyElementMatches(ExpressionType type) {
		return source.anyElementMatches(type);
	}
	
	/**
	 * @return the source on containing the field to access.
	 */
	public Expression getSource() {
		return source;
	}

	/**
	 * {@inheritDoc}
	 * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate(int)
	 */
	@Override
	public FieldAccess duplicate(int id) {
		return new FieldAccess(id, getSource().duplicate(), getFieldName(), isInverted());
	}

	/**
	 * @return an inverted instance of the current element, only if the underlying Java type is {@link Boolean}
	 */
	@Override
	public FieldAccess inverse() {
		if(getJavaType() == Boolean.class || getJavaType() == boolean.class) {
			return new FieldAccess(source, fieldName, !isInverted()); 
		}
		throw new UnsupportedOperationException("Field access on '" + getFieldName() + "' with Java type '" + getJavaType() + "' does not support inversion.");
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
			return source.getJavaType().getField(fieldName).getType();
		} catch (NoSuchFieldException | SecurityException e) {
			throw new AnalyzeException("Failed to retrieve field '" + fieldName + "' in " + source, e);
		}
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Returns the underlying Java field 
	 */
	@Override
	public Object getValue() {
		try {
			return FieldUtils.getField(source.getJavaType(), getFieldName());
		} catch (IllegalArgumentException e) {
			throw new AnalyzeException("Cannot retrieve field named '" + fieldName + "' on class " + source.getJavaType(), e);
		}
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
		return this.source.toString() + "." + this.fieldName;
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
		result = prime * result + ((source == null) ? 0 : source.hashCode());
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
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		return true;
	}
	
}

