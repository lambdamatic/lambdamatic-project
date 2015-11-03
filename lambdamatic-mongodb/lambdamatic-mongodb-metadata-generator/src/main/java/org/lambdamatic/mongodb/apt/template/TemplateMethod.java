/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.apt.template;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.lang.model.element.VariableElement;

import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.UpdateMetadata;

/**
 * A method in a template-based source code.
 */
public class TemplateMethod extends TemplateElement {

  /**
   * {@link Function} that creates a {@link List} of {@link ProjectionMetadata}
   * {@link TemplateMethod} for a given {@link VariableElement}.
   */
  public static Function<VariableElement, List<TemplateMethod>> createProjectionMethodsFunction =
      field -> {
        return Collections.emptyList();
      };

  /**
   * {@link Function} that creates a {@link List} of {@link UpdateMetadata} {@link TemplateMethod}
   * for a given {@link VariableElement}.
   */
  public static Function<VariableElement, List<TemplateMethod>> createUpdateMethodsFunction =
      field -> {
        return Collections.emptyList();
      };

  /** the method name. */
  private final String methodName;

  /** the return type. */
  private final TemplateType returnType;

  /** the parameter type. */
  private final TemplateType parameterType;

  /** the parameter name. */
  private final String parameterName;

  /** the method annotations. */
  private final List<TemplateAnnotation> annotations;

  /**
   * Private constructor
   * 
   * @param methodName the method name.
   * @param returnType the return type.
   * @param parameterType the parameter type.
   * @param parameterName the parameter name.
   * @param annotations the method annotations.
   */
  private TemplateMethod(final String methodName, final TemplateType returnType,
      final TemplateType parameterType, final String parameterName,
      final List<TemplateAnnotation> annotations) {
    this.methodName = methodName;
    this.returnType = returnType;
    this.parameterType = parameterType;
    this.parameterName = parameterName;
    this.annotations = annotations;
  }

  /**
   * @return the method name.
   */
  public String getMethodName() {
    return this.methodName;
  }

  /**
   * @return the return type.
   */
  public TemplateType getReturnType() {
    return this.returnType;
  }

  /**
   * @return the parameter type.
   */
  public TemplateType getParameterType() {
    return this.parameterType;
  }

  /**
   * @return the parameter name.
   */
  public String getParameterName() {
    return this.parameterName;
  }

  /**
   * @return the method annotations.
   */
  public List<TemplateAnnotation> getAnnotations() {
    return this.annotations;
  }

  @Override
  public Collection<String> getRequiredJavaTypes() {
    final Set<String> requiredJavaTypes = new HashSet<>();
    requiredJavaTypes.addAll(this.parameterType.getRequiredJavaTypes());
    requiredJavaTypes
        .addAll(this.annotations.stream().map(annotation -> annotation.getRequiredJavaTypes())
            .flatMap(requiredTypes -> requiredTypes.stream()).collect(Collectors.toList()));
    return requiredJavaTypes;
  }

}
