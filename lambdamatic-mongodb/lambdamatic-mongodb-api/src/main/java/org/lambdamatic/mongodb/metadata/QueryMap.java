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
public interface QueryMap<Key,DomainMetadata> {

	/**
	 * Matches any array with the number of elements specified by the argument.
	 * 
	 * @param size
	 *            the number of elements to match
	 * @see <a href="http://docs.mongodb.org/manual/reference/operator/query/size/#op._S_size">MongoDB documentation</a>
	 */
	@MongoOperation(MongoOperator.SIZE)
	public boolean size(long size);
	
	/**
	 * Checks if any value indexed with <code>index</code> in the Map matches the given <code>expression</code>
	 * @param expression the {@link FilterExpression}   
	 * @return <code>true</code> if an element matches 
	 */
	@MongoOperation(MongoOperator.ELEMEMT_MATCH)
	public boolean elementMatch(final FilterExpression<DomainMetadata> expression);

	/**
	 * Accessing a specific element in the domain {@link Map}.
	 * @param key the key of the element in the {@link Map}
	 * @return the desired element 
	 */
	@ArrayElementAccessor
	public abstract DomainMetadata get(Key key);

}
