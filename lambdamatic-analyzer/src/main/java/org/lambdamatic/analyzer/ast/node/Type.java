/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

/**
 * A fully qualified Java Type.
 * 
 * @author xcoulon
 *
 */
public class Type extends Node {

  /** The type name. */
  private final String fullyQualifiedName;

  /**
   * @param fullyQualifiedName the fully qualified name of this type.
   */
  public Type(final String fullyQualifiedName) {
    super();
    this.fullyQualifiedName = fullyQualifiedName;
  }

  /**
   * @return the fully qualified name of the type.
   */
  public String getFullyQualifiedName() {
    return this.fullyQualifiedName;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((this.fullyQualifiedName == null) ? 0 : this.fullyQualifiedName.hashCode());
    return result;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Type other = (Type) obj;
    if (this.fullyQualifiedName == null) {
      if (other.fullyQualifiedName != null) {
        return false;
      }
    } else if (!this.fullyQualifiedName.equals(other.fullyQualifiedName)) {
      return false;
    }
    return true;
  }

}

