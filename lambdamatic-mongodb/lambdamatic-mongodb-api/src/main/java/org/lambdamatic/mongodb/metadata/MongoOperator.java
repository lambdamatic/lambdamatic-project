/**
 * 
 */
package org.lambdamatic.mongodb.metadata;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public enum MongoOperator {

	OR("$or"), AND("$and"), NOT("$not"), EQUALS("$eq"), NOT_EQUALS("$ne"), GREATER("$gt"), GREATER_EQUALS("$gte"), LESS(
			"$lt"), LESS_EQUALS("$lte"), IN("$in"), NOT_IN("$nin"), GEO_WITHIN("$geoWithin"), ALL(
					"$all"), ELEMEMT_MATCH("$elemMatch"), SIZE("$size"), FIRST("$"), PUSH("$push");

	private final String literal;

	private MongoOperator(final String literal) {
		this.literal = literal;
	}

	public String getLiteral() {
		return literal;
	}

}
