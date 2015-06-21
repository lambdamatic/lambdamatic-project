/**
 * 
 */
package org.lambdamatic.mongodb.metadata;

import java.util.List;
import java.util.Set;

import org.lambdamatic.mongodb.FilterExpression;
import org.lambdamatic.mongodb.LambdamaticMongoCollection;

/**
 * MongoDB operation available on a given Document field of type Array in MongoDB (mapped as a {@link List} or
 * {@link Set} in Java) to specify a query projection.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public interface ProjectionArray<DomainType> extends ProjectionField {

	/**
	 * The positional {@link MongoOperator#FIRST} operator limits the contents of an array from the query results to
	 * contain only the first element matching the query document.
	 * 
	 * Use {@link ProjectionArray#first()} in the projection document of the
	 * {@link LambdamaticMongoCollection#filter(FilterExpression)} method when you only need one particular array
	 * element in selected documents.
	 * 
	 * @see http://docs.mongodb.org/manual/reference/operator/projection/positional/#proj._S_
	 */
	@MongoOperation(MongoOperator.FIRST)
	public void first();

	/**
	 * The {@link MongoOperator#ELEMEMT_MATCH} operator limits the contents of an array field from the query results to
	 * contain only the <strong>first element</strong> matching the given {@code expression} condition.
	 * 
	 * @param expression
	 *            the condition to specify which elements should be returned
	 * 
	 * @see http://docs.mongodb.org/manual/reference/operator/projection/elemMatch/#proj._S_elemMatch
	 */
	@MongoOperation(MongoOperator.ELEMEMT_MATCH)
	public ProjectionField elementMatch(final FilterExpression<DomainType> expression);

}
