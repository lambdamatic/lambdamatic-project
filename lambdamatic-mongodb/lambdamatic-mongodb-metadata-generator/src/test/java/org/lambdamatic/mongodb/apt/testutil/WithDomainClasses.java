/**
 * 
 */
package org.lambdamatic.mongodb.apt.testutil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Containing annotation to indicate the classes that should be processed by the {@link CompilationAndAnnotationProcessingRule}
 * 
 * @author Xavier Coulon
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithDomainClasses{
	WithDomainClass[] value();
}

