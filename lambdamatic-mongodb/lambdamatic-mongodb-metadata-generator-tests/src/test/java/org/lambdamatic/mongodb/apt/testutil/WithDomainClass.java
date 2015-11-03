/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.apt.testutil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TemplateAnnotation to indicate the class that should be processed by the
 * {@link CompilationAndAnnotationProcessingRule}.
 * 
 * @author Xavier Coulon
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(WithDomainClasses.class)
public @interface WithDomainClass {

  /** name of the class to process before test. */
  public Class<?> value();
}

