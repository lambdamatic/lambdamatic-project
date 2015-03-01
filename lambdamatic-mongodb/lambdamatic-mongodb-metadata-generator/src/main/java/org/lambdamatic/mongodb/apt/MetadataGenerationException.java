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

	public MetadataGenerationException(String message) {
		super(message);
	}

}
