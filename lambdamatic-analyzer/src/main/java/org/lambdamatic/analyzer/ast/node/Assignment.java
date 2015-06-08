/*******************************************************************************
 * Copyright (c) 2015 Red Hat.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

/**
 * An assignment Expression
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class Assignment extends ComplexExpression {

	/** the source {@link Expression}, to which the value will be assigned. */
	private Expression source;
	
	/** the value {@link Expression} that is to be assigned to the source. */
	private Expression assignedValue;

	/**
	 * Constructor
	 * @param id the expression id
	 * @param source the source of the assignment
	 * @param assignedValue the value to assign
	 * @param inverted if the expression is inverted
	 */
	public Assignment(final Expression source, final Expression assignedValue) {
		this(generateId(), source, assignedValue, false);
	}

	/**
	 * Full constructor
	 * @param id the expression id
	 * @param source the source of the assignment
	 * @param assignedValue the value to assign
	 * @param inverted if the expression is inverted
	 */
	public Assignment(final int id, final Expression source, final Expression assignedValue, final boolean inverted) {
		super(id, inverted);
		setSource(source);
		setAssignedValue(assignedValue);
	}

	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.ASSIGNMENT;
	}

	/**
	 * @return the source {@link Expression}
	 */
	public Expression getSource() {
		return source;
	}
	
	private void setSource(final Expression source) {
		this.source = source;
		this.source.setParent(this);
	}
	
	/**
	 * @return the value to be assigned
	 */
	public Expression getAssignedValue() {
		return assignedValue;
	}

	private void setAssignedValue(Expression assignedValue) {
		this.assignedValue = assignedValue;
		this.assignedValue.setParent(this);
	}
	
	@Override
	public Class<?> getJavaType() {
		return source.getJavaType();
	}

	@Override
	public boolean canBeInverted() {
		return false;
	}

	@Override
	public Expression duplicate(int id) {
		return new Assignment(id, source.duplicate(), assignedValue.duplicate(), source.isInverted());
	}

	@Override
	public Expression duplicate() {
		return new Assignment(source.duplicate(), assignedValue.duplicate());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(final ExpressionVisitor visitor) {
		if(visitor.visit(this)) {
			source.accept(visitor);
			assignedValue.accept(visitor);
		}
	}
	
	@Override
	public void replaceElement(final Expression oldExpression, final Expression newExpression) {
		if(oldExpression.equals(this.source)) {
			setSource(newExpression);
		} else if(oldExpression.equals(this.assignedValue)) {
			setAssignedValue(newExpression);
		}
	}
	
	@Override
	public String toString() {
		return source + " = " + assignedValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assignedValue == null) ? 0 : assignedValue.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
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
		Assignment other = (Assignment) obj;
		if (assignedValue == null) {
			if (other.assignedValue != null)
				return false;
		} else if (!assignedValue.equals(other.assignedValue))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}
	
	

}
