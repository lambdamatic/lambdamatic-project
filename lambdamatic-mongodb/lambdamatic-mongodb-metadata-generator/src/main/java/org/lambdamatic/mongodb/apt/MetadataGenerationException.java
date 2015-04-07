/**
 * 
 */
package org.lambdamatic.mongodb.apt;

/**
 * Exception thrown when something wrong happened during the code generation.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class MetadataGenerationException extends Exception {

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
	
}
