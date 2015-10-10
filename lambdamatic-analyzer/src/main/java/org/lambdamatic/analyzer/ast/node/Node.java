/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

import org.objectweb.asm.tree.LabelNode;


/**
 * the base class for all AST nodes.
 */
public abstract class Node {

  private final String label;

  /**
   * Default constructor.
   */
  public Node() {
    this.label = null;
  }

  /**
   * Constructor with a given {@link LabelNode}.
   * 
   * @param labelNode the underlying {@link LabelNode}
   */
  public Node(final LabelNode labelNode) {
    this.label = (labelNode != null) ? labelNode.getLabel().toString() : null;
  }

  /**
   * @return the label that was given to this node or <code>null</code>.
   */
  public String getLabel() {
    return this.label;
  }

  protected boolean hasLabel() {
    return getLabel() != null;
  }

  /**
   * @return the length of the given node in term of number of bytecode instructions (including
   *         {@link LabelNode}s).
   */
  public int getNumberOfBytecodeInstructions() {
    return 1 + (hasLabel() ? 1 : 0);
  }

}

