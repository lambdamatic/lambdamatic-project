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
package org.lambdamatic.mongodb.apt;

/**
 * Exception thrown when something wrong happened during the code generation.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class MetadataGenerationException extends RuntimeException {

	/** the generqted serialVersionUID. */
	private static final long serialVersionUID = -149849519526425611L;

	/**
	 * Exception constructor
	 * 
	 * @param message
	 *            the exception message
	 */
	public MetadataGenerationException(final String message) {
		super(message);
	}

	/**
	 * Exception constructor
	 * 
	 * @param cause
	 *            the underlying cause
	 */
	public MetadataGenerationException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor
	 * @param message the exception message
	 * @param cause the underlying cause
	 */
	public MetadataGenerationException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
}
