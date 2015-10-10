/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.exceptions;

/**
 * {@link Exception} thrown when a Database operation failed.
 * 
 */
public class OperationException extends RuntimeException {

  private static final long serialVersionUID = -5160855055994674139L;

  /**
   * Constructor.
   * 
   * @param message reason of the operation failure
   */
  public OperationException(final String message) {
    super(message);
  }

  /**
   * Full constructor.
   * 
   * @param message the message explaining the operation that failed
   * @param cause underlying reason of the database operation failure
   */
  public OperationException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
