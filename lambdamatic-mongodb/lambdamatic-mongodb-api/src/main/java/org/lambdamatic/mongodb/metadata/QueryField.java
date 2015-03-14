/**
 * 
 */
package org.lambdamatic.mongodb.metadata;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public interface QueryField<T> {

	/**
	 * Specifies equality condition. The {@link QueryField#eq(Object)} $eq operator matches documents where the value of a field equals the specified value.
	 * @param value the specified value to match.
	 */
	@MongoOperation(MongoOperator.EQUALS)
	public abstract void eq(T value);
	
	/**
	 * @deprecated Use the {@link QueryField#eq(Object)} method to specify an equality condition.
	 */
	@Override
	@Deprecated
	@MongoOperation(MongoOperator.EQUALS)
	public boolean equals(Object obj);
}
