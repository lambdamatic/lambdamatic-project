/**
 * 
 */
package org.lambdamatic.mongodb;

import org.lambdamatic.SerializablePredicate;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
@FunctionalInterface
public interface FilterExpression<T> extends SerializablePredicate<T> {

}
