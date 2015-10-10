/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.apt.template;

import static org.lambdamatic.mongodb.apt.template.ElementUtils.getAnnotationValue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.annotations.EmbeddedDocument;
import org.lambdamatic.mongodb.apt.MetadataGenerationException;
import org.lambdamatic.mongodb.metadata.ArrayElementAccessor;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.QueryMetadata;
import org.lambdamatic.mongodb.metadata.UpdateMetadata;

/**
 * A method in a template-based source code.
 */
public class TemplateMethod extends TemplateElement {

  /**
   * {@link Function} that creates a {@link List} of {@link QueryMetadata} {@link TemplateMethod}
   * for a given {@link VariableElement}.
   */
  @Deprecated
  public static Function<VariableElement, List<TemplateMethod>> createQueryMethodsFunction =
      field -> {
        // need to retrieve field type and check for list or map
        // need to retrieve field type parameters (1 for list, 2 for map) for return type and
        // parameter name
        // need 'documentFieldName', too
        // no need for annotation support for now
        final TypeMirror fieldType = field.asType();
        if (fieldType instanceof DeclaredType) {
          final DeclaredType declaredType = (DeclaredType) fieldType;
          final TypeElement declaredElement = (TypeElement) declaredType.asElement();
          if (ElementUtils.isList(declaredElement)) {
            final String javaFieldName = field.getSimpleName().toString();
            final List<? extends TypeMirror> parameterTypes = declaredType.getTypeArguments();
            // expect only 1 parameter
            if (parameterTypes == null || parameterTypes.size() != 1) {
              throw new MetadataGenerationException(
                  "Expected a single parameter for the type of the '" + field.toString()
                      + "' field but it was: " + fieldType.toString());
            }
            final String documentFieldType = getAnnotationValue(
                field.getAnnotation(DocumentField.class), a -> a.name(), javaFieldName);
            final List<TemplateAnnotation> annotations = Arrays.asList(
                TemplateAnnotation.Builder.type(DocumentField.class)
                    .attribute("name", documentFieldType).build(),
                TemplateAnnotation.Builder.type(ArrayElementAccessor.class).build());
            final TemplateType returnType =
                TemplateType.getQueryMetadataFieldType(parameterTypes.get(0));
            final TemplateType parameterType = TemplateType.Builder.with(int.class).build();
            return Arrays.asList(
                new TemplateMethod(javaFieldName, returnType, parameterType, "index", annotations));
          } else if (ElementUtils.isMap(declaredElement)) {
            final String javaFieldName = field.getSimpleName().toString();
            final List<? extends TypeMirror> parameterTypes = declaredType.getTypeArguments();
            // expect 2 parameters
            if (parameterTypes == null || parameterTypes.size() != 2) {
              throw new MetadataGenerationException("Expected two parameters for the type of the '"
                  + field.toString() + "' field but it was: " + fieldType.toString());
            }
            final String documentFieldType = getAnnotationValue(
                field.getAnnotation(DocumentField.class), a -> a.name(), javaFieldName);
            final List<TemplateAnnotation> annotations = Arrays.asList(
                TemplateAnnotation.Builder.type(DocumentField.class)
                    .attribute("name", documentFieldType).build(),
                TemplateAnnotation.Builder.type(ArrayElementAccessor.class).build());
            final TemplateType returnType =
                TemplateType.getQueryMetadataFieldType(parameterTypes.get(1));
            final TemplateType parameterType =
                TemplateType.Builder.with(parameterTypes.get(0).toString()).build();
            return Arrays.asList(
                new TemplateMethod(javaFieldName, returnType, parameterType, "key", annotations));
          }
        } else if (fieldType.getKind() == TypeKind.ARRAY) {
          final TypeMirror componentType = ((ArrayType) fieldType).getComponentType();
          if (componentType.getAnnotation(EmbeddedDocument.class) != null) {
            throw new MetadataGenerationException(
                "Unsupported EmbeddedDocument type: " + fieldType);
          } else {
            final String javaFieldName = field.getSimpleName().toString();
            final String documentFieldType = getAnnotationValue(
                field.getAnnotation(DocumentField.class), a -> a.name(), javaFieldName);
            final List<TemplateAnnotation> annotations = Arrays.asList(
                TemplateAnnotation.Builder.type(DocumentField.class)
                    .attribute("name", documentFieldType).build(),
                TemplateAnnotation.Builder.type(ArrayElementAccessor.class).build());
            final TemplateType returnType = TemplateType.getQueryMetadataFieldType(componentType);
            final TemplateType parameterType = TemplateType.Builder.with(int.class).build();
            return Arrays.asList(
                new TemplateMethod(javaFieldName, returnType, parameterType, "index", annotations));
          }

        }
        return Collections.emptyList();
      };

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
