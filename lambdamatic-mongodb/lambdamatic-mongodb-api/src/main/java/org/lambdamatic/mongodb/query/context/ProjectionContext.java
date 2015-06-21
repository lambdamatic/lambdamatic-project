/**
 * 
 */
package org.lambdamatic.mongodb.query.context;

/**
 * Terminal context for the find operation.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public interface ProjectionContext<DomainType> extends SkipContext<DomainType> {


	/**
	 * Skips the first elements 
	 * @param skip the number of elements to skip
	 * @return 
	 */
	public abstract SkipContext<DomainType> skip(final int skip);
}
