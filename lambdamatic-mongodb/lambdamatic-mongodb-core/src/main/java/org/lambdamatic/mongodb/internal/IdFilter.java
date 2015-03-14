/**
 * 
 */
package org.lambdamatic.mongodb.internal;

import org.lambdamatic.mongodb.annotations.DocumentId;

/**
 * A query filter that will use the attribute of the given {@code domainObject} annotated with {@link DocumentId}.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 * @param <T>
 *
 */
public class IdFilter<T> {

	private final T domainObject;
	
	public IdFilter(final T domainObject) {
		this.domainObject = domainObject;
	}
	
	public T getDomainObject() {
		return domainObject;
	}
}
