/*******************************************************************************
 * Copyright (c) 2014, 2015 Red Hat. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;


/**
 * {@link Expression} based on other {@link Expression} that can be replaced with
 * simplified/sanitized versions during the byte-code analysis.
 * 
 * @author Xavier Coulon
 *
 */
public abstract class ComplexExpression extends Expression {

  /**
   * Full constructor
   * 
   * @param id the synthetic id of this {@link Expression}.
   * @param inverted the inversion flag of this {@link Expression}.
   */
  public ComplexExpression(int id, boolean inverted) {
    super(id, inverted);
  }

  /**
   * Replaces the given oldExpression with the given newExpression.
   * 
   * @param oldExpression the Expression to replace
   * @param newExpression the Expression to use as a replacement
   */
  public abstract void replaceElement(final Expression oldExpression,
      final Expression newExpression);

}

