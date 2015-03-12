/**
 * 
 */
package org.lambdamatic.mongodb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mandatory annotation for any user-domain class persisted in MongoDB.
 * <p>
 * The {@code collection} attribute specifies the MongoDB Collection in which the associated document is persisted.
 * </p>
 * 
 * @author Xavier Coulon
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Document {

	public String collection();

}
