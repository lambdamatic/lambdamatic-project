/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.apt.template;

import java.util.function.Function;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.apache.commons.lang3.ClassUtils;
import org.lambdamatic.mongodb.apt.BaseAnnotationProcessor;

/**
 * Base template context.
 */
public abstract class BaseTemplateContext {

  /**
   * {@link Function} to generate the package name for a given {@link TypeElement}.
   */
  public static final Function<TypeMirror, String> elementToPackageName =
      type -> ClassUtils.getPackageCanonicalName(type.toString());

  /** the domain type associated with this template context. */
  protected final DeclaredType domainType;

  /** the parent annotation annotationProcessor. */
  private final BaseAnnotationProcessor annotationProcessor;

  /**
   * Private constructor.
   * 
   * @param domainType the {@link DeclaredType} to work on
   * @param annotationProcessor the annotation processor used
   */
  public BaseTemplateContext(final DeclaredType domainType,
      final BaseAnnotationProcessor annotationProcessor) {
    this.domainType = domainType;
    this.annotationProcessor = annotationProcessor;
  }

  /**
   * @return the package name for the class to generate.
   */
  public String getPackageName() {
    return elementToPackageName.apply(this.domainType);
  }

  /**
   * @return the simple name of the associated {@code domainType} class (in the same package).
   */
  public String getDomainClassName() {
    return this.domainType.asElement().getSimpleName().toString();
  }

  /**
   * @return the fully qualified name of the parent Annotation Processor.
   */
  public String getProcessorClassName() {
    return this.annotationProcessor.getClass().getName();
  }

  /**
   * @return the filename of the template to use to generate the source code.
   */
  public abstract String getTemplateFileName();

  /**
   * @return the fully qualified name of the target class to generate.
   */
  public abstract String getFullyQualifiedClassName();

  /**
   * @return the simple name of the target class to generate.
   */
  public abstract String getSimpleClassName();

}
