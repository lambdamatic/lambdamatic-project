/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.analyzer.exception;


/**
 * {@link RuntimeException} to throw when something wrong happens while analyzing a Lambda Expression.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class AnalyzeException extends RuntimeException {

	/** serialVersionUID */
	private static final long serialVersionUID = -4410187600260761123L;

	/**
	 * Constructor when something wrong happened underneath
	 * @param message the message to report
	 * @param cause the underlying cause
	 */
	public AnalyzeException(final String message, final Exception cause) {
		super(message, cause);
	}

	/**
	 * Constructor when something wrong happened in our code
	 * @param message the message to report
	 */
	public AnalyzeException(final String message) {
		super(message);
	}

}

