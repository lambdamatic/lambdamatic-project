/**
 * 
 */
package org.lambdamatic.mongodb.query.context;

import org.lambdamatic.SerializableFunction;
import org.lambdamatic.mongodb.metadata.Projection;

/**
 * Terminal context for the find operation.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public interface FindContext<T, PM> extends FindTerminalContext<T> {

	/**
	 * Reduce the fields to be <strong>included<strong> or <strong>excluded</strong> in the returned to the given {@link SerializableFunction}.
	 *
	 * @param projectionExpression
	 *            the set of fields to include in the returned document
	 * @return the {@link FindContext} to optionally specify more settings
	 */
	public FindTerminalContext<T> projection(final SerializableFunction<PM, Projection> projectionExpression);

}
