/**
 * 
 */
package org.lambdamatic.mongodb;

import org.lambdamatic.SerializablePredicate;

/**
 * A {@link FilterExpression} which specifies which {@code DonainType} should be part of the find/update/delete operation.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
@FunctionalInterface
public interface FilterExpression<DomainType> extends SerializablePredicate<DomainType> {

}
