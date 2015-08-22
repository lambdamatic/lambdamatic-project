/**
 * 
 */
package org.lambdamatic.mongodb.metadata;

import java.util.List;
import java.util.Set;

/**
 * MongoDB operation available on a given Document field of type Array in MongoDB (mapped as a {@link List} or
 * {@link Set} in Java to specify an update.
 * 
 * @param T
 *            can be a {@link UpdateMetadata} or a simple Java Type (String, Enum, etc.)
 *
 * @author Xavier Coulon <xcoulon@redhat.com>
 */
public interface UpdateArray<T> {

	/**
	 * The $push operator appends a specified value to an array.
	 * 
	 * @param element
	 *            the element to push in the array
	 */
	@MongoOperation(MongoOperator.PUSH)
	public void push(final T element);

}
