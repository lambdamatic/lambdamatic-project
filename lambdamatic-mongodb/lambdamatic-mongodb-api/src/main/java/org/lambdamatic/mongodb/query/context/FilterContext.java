/**
 * 
 */
package org.lambdamatic.mongodb.query.context;

import org.lambdamatic.mongodb.ProjectionExpression;
import org.lambdamatic.mongodb.UpdateExpression;
import org.lambdamatic.mongodb.annotations.Document;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.UpdateMetadata;

/**
 * Terminal context for the find operation.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 * 
 * @param <DomainType> the Domain Type annotated with {@link Document}
 * @param <ProjectionType> the {@link ProjectionMetadata} associated with Domain Type
 * @param <UpdateType> the {@link UpdateMetadata} associated with Domain Type
 *
 */
public interface FilterContext<DomainType, ProjectionType, UpdateType> extends FindTerminalContext<DomainType> {

	/**
	 * Reduce the fields to be <strong>included<strong> or <strong>excluded</strong> in the returned to the given {@link ProjectionType} expression.
	 *
	 * @param projectionExpression
	 *            the set of fields to include in the returned document
	 * @return the {@link FilterContext} to optionally specify more settings
	 */
	public FindTerminalContext<DomainType> projection(final ProjectionExpression<ProjectionType> projectionExpression);
	
	/**
	 * Updates <strong>one</strong> {@code domainObject}, performing <strong>a partial replacement</strong> based on the field assignments in the given {@code updateExpression}.
	 * @param updateExpression the field assignments that explain how the document fields should be updated.
	 */
	public void forEach(final UpdateExpression<UpdateType> updateExpression);
	
	
}
