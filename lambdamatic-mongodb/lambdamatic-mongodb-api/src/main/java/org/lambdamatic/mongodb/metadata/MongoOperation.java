/**
 * 
 */
package org.lambdamatic.mongodb.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mongo operator binging
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MongoOperation {

	public MongoOperator value();
}
