/**
 * 
 */
package org.lambdamatic.mongodb.metadata;

import java.util.List;
import java.util.Set;

import org.lambdamatic.SerializablePredicate;

/**
 * MongoDB operation available on a given Document field of type Array in MongoDB (mapped as a {@link List} or
 * {@link Set} in Java to specify a query filter.
 * 
 * @param T
 *            can be a {@link QueryMetadata} or a simple Java Type (String, Enum, etc.)
 *
 * @author Xavier Coulon <xcoulon@redhat.com>
 */
// TODO: verify that query operation such as below work (ie: compile and convert)
// db.test.insert({tags:[{name:"foo", score:1}, {name:"bar", score:2}]} )
// db.test.find({"tags.name":"bar", "tags.score":{$gt:1}}, {"tags.$":1}) // find tag with 'name' == 'bar' OR 'score' > 1
// and db.test.find({tags : { $elemMatch : { name : "foo", score : 1}}})
public interface QueryArray<T> {

	/**
	 * Matches arrays that contain all elements specified in parameter.
	 * 
	 * @param values
	 *            the specified values to match.
	 * @see <a href="http://docs.mongodb.org/manual/reference/operator/query/all/#op._S_all">MongoDB documentation</a>
	 */
	@MongoOperation(MongoOperator.ALL)
	public boolean matchesAll(Object values); // FIXME: use matchesAll(T... values) instead ?

	/**
	 * Matches documents that contain an array field with at least one element that matches the specified query
	 * expression.
	 * 
	 * @param expression
	 *            the query in the form of a lambda expression
	 * @see <a href="http://docs.mongodb.org/manual/reference/operator/query/elemMatch/#op._S_elemMatch">MongoDB
	 *      documentation</a>
	 */
	@MongoOperation(MongoOperator.ELEMEMT_MATCH)
	public boolean elementMatch(final SerializablePredicate<T> expression);

	/**
	 * Matches any array with the number of elements specified by the argument.
	 * 
	 * @param size
	 *            the number of elements to match
	 * @see <a href="http://docs.mongodb.org/manual/reference/operator/query/size/#op._S_size">MongoDB documentation</a>
	 */
	@MongoOperation(MongoOperator.SIZE)
	public boolean hasSize(long size);

}
