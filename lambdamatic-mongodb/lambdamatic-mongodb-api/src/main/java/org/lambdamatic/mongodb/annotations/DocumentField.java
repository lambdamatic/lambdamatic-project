/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Optional annotation for attributes of a class annotated with {@link Document}. Use this
 * annotation to override the default settings.
 * 
 * @author Xavier Coulon
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface DocumentField {

  /**
   * The name of the document field. Empty value (by default) means that the class attribute name is
   * used as the document field name.
   * 
   * @return the name of the field in MongoDB
   */
  public String name() default "";

}
