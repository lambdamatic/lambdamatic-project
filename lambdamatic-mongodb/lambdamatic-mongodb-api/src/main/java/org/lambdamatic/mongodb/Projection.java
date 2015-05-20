/**
 * 
 */
package org.lambdamatic.mongodb;

import org.lambdamatic.mongodb.metadata.ExcludeFields;
import org.lambdamatic.mongodb.metadata.IncludeFields;
import org.lambdamatic.mongodb.metadata.ProjectionField;

/**
 * A {@link Projection} which specifies which {@link ProjectionField} should be included or excluded
 * from the document(s) returned by the query.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public interface Projection {
	
	@IncludeFields
	public static void include(final ProjectionField... fields) {}

	@ExcludeFields
	public static void exclude(final ProjectionField... fields) {}

	
}
