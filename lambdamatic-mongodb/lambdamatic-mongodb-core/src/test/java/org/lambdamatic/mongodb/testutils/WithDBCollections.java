/**
 * 
 */
package org.lambdamatic.mongodb.testutils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Containing annotation to indicate the MongoDB Collections that should be processed by the {@link CleanDBCollectionsRule}
 * 
 * @author Xavier Coulon
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithDBCollections{
	WithDBCollection[] value();
}

