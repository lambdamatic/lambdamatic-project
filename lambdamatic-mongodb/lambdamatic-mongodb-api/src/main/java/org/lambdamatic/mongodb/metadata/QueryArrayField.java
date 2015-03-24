/**
 * 
 */
package org.lambdamatic.mongodb.metadata;

import java.util.List;
import java.util.Set;

/**
 * MongoDB operation available on a given Document field of type Array in MongoDB (mapped as a {@link List} or
 * {@link Set} in Java)..
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public interface QueryArrayField<T> {
	
	/**
	 * Matches arrays that contain all elements specified in parameter.
	 * 
	 * @param values
	 *            the specified values to match.
	 * @see <a href="http://docs.mongodb.org/manual/reference/operator/query/all/#op._S_all">MongoDB documentation</a>
	 */
	@MongoOperation(MongoOperator.ALL)
	public boolean matchesAll(Object values);

	/**
	 * Matches documents that contain an array field with at least one element that matches all the specified query criteria
	 * @param value the specified values to match.
	 * @see <a href="http://docs.mongodb.org/manual/reference/operator/query/elemMatch/#op._S_elemMatch">MongoDB documentation</a>
	 */
	@MongoOperation(MongoOperator.ELEMEMT_MATCH)
	public boolean elementMatch(Object value);

	/**
	 * Matches any array with the number of elements specified by the argument.
	 * @param size the number of elements to match
	 * @see <a href="http://docs.mongodb.org/manual/reference/operator/query/size/#op._S_size">MongoDB documentation</a>
	 */
	@MongoOperation(MongoOperator.SIZE)
	public boolean hasSize(long size);

}
