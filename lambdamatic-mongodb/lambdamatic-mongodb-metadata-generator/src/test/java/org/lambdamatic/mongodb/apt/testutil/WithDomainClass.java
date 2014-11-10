/**
 * 
 */
package org.lambdamatic.mongodb.apt.testutil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate the class that should be processed by the {@link CompilationAndAnnotationProcessingRule}
 * 
 * @author Xavier Coulon
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(WithDomainClasses.class)
public @interface WithDomainClass{

	public Class<?> value();
}

