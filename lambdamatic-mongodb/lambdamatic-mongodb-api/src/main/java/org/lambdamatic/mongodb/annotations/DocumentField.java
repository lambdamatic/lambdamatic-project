/**
 * 
 */
package org.lambdamatic.mongodb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Optional annotation for attributes of a class annotated with {@link Document}. Use this annotation to override the
 * default settings.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DocumentField {

	/**
	 * The name of the document field. Empty value (by default) means that the class attribute name is used as the
	 * document field name.
	 */
	public String name() default "";

}
