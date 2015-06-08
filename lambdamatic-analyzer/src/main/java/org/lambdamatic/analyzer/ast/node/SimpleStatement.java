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
 * A {@link Statement} that wraps a single {@link Expression}
 * @author xcoulon
 *
 */
public abstract class SimpleStatement extends Statement {

	/** The actual Expression.*/
	final Expression expression;

	/**
	 * Full constructor
	 * @param expression The actual Expression.
	 */
	public SimpleStatement(final Expression expression) {
		this.expression = expression;
	}
	
	/**
	 * @return the {@link Statement}'s {@link Expression}
	 */
	public Expression getExpression() {
		return expression;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
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
		SimpleStatement other = (SimpleStatement) obj;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		return true;
	}

	
	
}
