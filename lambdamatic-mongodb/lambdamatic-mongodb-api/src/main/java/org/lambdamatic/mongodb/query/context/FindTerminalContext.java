/**
 * 
 */
package org.lambdamatic.mongodb.query.context;

import java.util.List;

/**
 * Terminal context for the find operation.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public interface FindTerminalContext<T> {

	/**
	 * @return a {@link List} of documents of type {@code T}.
	 */
	List<T> toList();

	/**
	 * @return a single document of type {@code T}.
	 */
	T first();

}
