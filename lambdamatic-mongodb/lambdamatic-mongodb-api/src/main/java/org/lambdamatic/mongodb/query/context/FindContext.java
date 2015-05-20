/**
 * 
 */
package org.lambdamatic.mongodb.query.context;

import org.lambdamatic.mongodb.Projection;
import org.lambdamatic.mongodb.ProjectionExpression;

/**
 * Terminal context for the find operation.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public interface FindContext<T, PM> extends FindTerminalContext<T> {

	/**
	 * Reduce the fields to be <strong>included<strong> or <strong>excluded</strong> in the returned to the given {@link Projection} expression.
	 *
	 * @param projectionExpression
	 *            the set of fields to include in the returned document
	 * @return the {@link FindContext} to optionally specify more settings
	 */
	public FindTerminalContext<T> projection(final ProjectionExpression<PM> projectionExpression);
	
}
