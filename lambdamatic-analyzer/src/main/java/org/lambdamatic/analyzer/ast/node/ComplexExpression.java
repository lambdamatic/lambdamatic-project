/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public abstract class ComplexExpression extends Expression {

	/**
	 * Full constructor
	 * @param id the synthetic id of this {@link Expression}.
	 * @param inverted the inversion flag of this {@link Expression}.
	 */
	public ComplexExpression(int id, boolean inverted) {
		super(id, inverted);
	}

	/**
	 * Replaces the given oldExpression with the given newExpression.
	 * @param oldExpression the Expression to replace
	 * @param newExpression the Expression to use as a replacement
	 */
	public abstract void replaceElement(final Expression oldExpression, final Expression newExpression);

}
