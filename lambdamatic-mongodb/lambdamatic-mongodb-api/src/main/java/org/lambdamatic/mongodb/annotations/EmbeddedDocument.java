/**
 * 
 */
package org.lambdamatic.mongodb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mandatory annotation for any embedded document of a user-domain class annotated with {@link Document}.
 * 
 * @author Xavier Coulon
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EmbeddedDocument {

}
