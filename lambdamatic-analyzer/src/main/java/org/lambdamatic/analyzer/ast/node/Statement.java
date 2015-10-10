/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

/**
 * Abstract base class of AST nodes that represent the bytecode statements.
 *
 * @author Xavier Coulon
 *
 */
public abstract class Statement extends Node {

  /**
   * Statement types.
   */
  public enum StatementType {
    /** Expression statement type. */
    EXPRESSION_STMT, /** Control flow statement type. */
    CONTROL_FLOW_STMT, /** Return statement type. */
    RETURN_STMT;
  }

  /** The parent statement in the AST, or null if this statement is the root of the AST. */
  private Statement parent;

  /**
   * @return the {@link StatementType} of this {@link Statement}.
   */
  public abstract StatementType getStatementType();

  /**
   * Sets the parent of this statement in the AST.
   * 
   * @param parent the parent statement
   */
  void setParent(final Statement parent) {
    this.parent = parent;
  }

  /**
   * @return the parent statement in the AST, or null if this statement is the root.
   */
  public Statement getParent() {
    return this.parent;
  }

  /**
   * The given visitor is notified of the type of {@link Statement} is it currently visiting. The
   * visitor may also accept to visit the children {@link Statement} of this node.
   * 
   * @param visitor the {@link Statement} visitor
   */
  public void accept(final StatementVisitor visitor) {
    visitor.visit(this);
  }

  /**
   * @return a copy of this {@link Statement}.
   */
  public abstract Statement duplicate();

}

