/**
 * 
 */
package org.lambdamatic.mongodb.metadata;

import java.util.Map;

import org.lambdamatic.mongodb.FilterExpression;

/**
 * MongoDB operation available on a given Document field of type Array in MongoDB mapped as a {@link Map} in Java.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 */
public interface QueryMap<K,V> {

	/**
	 * Matches any array with the number of elements specified by the argument.
	 * 
	 * @param size
	 *            the number of elements to match
	 * @see <a href="http://docs.mongodb.org/manual/reference/operator/query/size/#op._S_size">MongoDB documentation</a>
	 */
	@MongoOperation(MongoOperator.SIZE)
	public boolean hasSize(long size);
	
	/**
	 * Checks if any value indexed with <code>index</code> in the Map matches the given <code>expression</code>
	 * @param expression
	 * @return
	 */
	public boolean elementMatch(final K index, final FilterExpression<V> expression);

	/**
	 * Checks if any value indexed with <code>index</code> in the Map matches the given <code>expression</code>
	 * @param expression
	 * @return
	 */
	public boolean elementMatch(final int position, final K index, final FilterExpression<V> expression);

}
