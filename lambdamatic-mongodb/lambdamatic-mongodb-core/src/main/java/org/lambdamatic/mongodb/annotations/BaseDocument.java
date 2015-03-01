/**
 * 
 */
package org.lambdamatic.mongodb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mandatory annotation for any superclass of a domain class annotated with {@link Document}.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BaseDocument {
	
}

