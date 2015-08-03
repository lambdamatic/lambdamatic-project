/**
 * 
 */
package org.lambdamatic.mongodb.metadata;

import org.lambdamatic.mongodb.exceptions.ConversionException;

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
	
	public MongoOperator inverse() {
		switch(this) {
		case EQUALS:
			return NOT_EQUALS;
		case GREATER:
			return LESS_EQUALS;
		case GREATER_EQUALS:
			return LESS;
		case IN:
			return NOT_IN;
		case LESS:
			return GREATER_EQUALS;
		case LESS_EQUALS:
			return GREATER;
		case NOT_EQUALS:
			return EQUALS;
		case NOT_IN:
			return IN;
		default:
			throw new ConversionException("No opposite operator is defined for " + this.literal);
		
		}
	}

}
