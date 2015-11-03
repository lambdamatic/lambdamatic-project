/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.apt.template;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Function;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.apache.commons.lang3.ClassUtils;

/**
 * Utility class related to {@link TypeMirror} and {@link TypeElement}.
 */
public class ElementUtils {

  private ElementUtils() {}

  /**
   * Returns the value of the given annotation using a Function.
   * 
   * @param <A> the type of annotation to analyze
   * @param <V> the type of value to return
   * @param annotation the annotation to analyze
   * @param valueRetriever the retrieval {@link Function} to apply if the given {@link Annotation}
   *        is not null.
   * @param defaultValue the default value to return if the annotation is null or if the given
   *        Function did not return any value.
   * @return the annotation value or <code>null</code> if it is empty or null.
   */
  public static <A extends Annotation, V> V getAnnotationValue(final A annotation,
      final Function<A, V> valueRetriever, final V defaultValue) {
    if (annotation != null) {
      final V value = valueRetriever.apply(annotation);
      // do not allow empty String (if applicable)
      if (defaultValue.getClass().equals(String.class) && !String.class.cast(value).isEmpty()) {
        return value;
      } else if (!defaultValue.getClass().equals(String.class) && value != null) {
        return value;
      }
    }
    return defaultValue;
  }

  /**
   * Checks if the given {@link Element} is, implements or extends the given target type.
   * 
   * @param type the element to analyze
   * @param targetType the type to check
   * @return <code>true</code> if the given {@link Element} corresponds to a type that implements
   *         {@link List}, <code>false</code> otherwise.
   */
  public static boolean isAssignable(final DeclaredType type, final Class<?> targetType) {
    if (type instanceof NoType) {
      return false;
    }
    if (type.asElement().toString().equals(targetType.getName())) {
      return true;
    }
    final TypeElement element = (TypeElement) type.asElement();
    final boolean implementation = element.getInterfaces().stream()
        .filter(interfaceMirror -> interfaceMirror.getKind() == TypeKind.DECLARED)
        .map(interfaceMirror -> (DeclaredType) interfaceMirror)
        .map(declaredInterface -> declaredInterface.asElement())
        .anyMatch(declaredElement -> declaredElement.toString().equals(targetType.getName()));
    if (implementation) {
      return true;
    }
    if (element.getSuperclass().getKind() == TypeKind.DECLARED) {
      return isAssignable((DeclaredType)(element.getSuperclass()), targetType);
    } 
    return false;
  }

  /**
   * Attempts to load the Java {@link Class} associated with the given {@link TypeMirror}. This may
   * not be possible if the {@link TypeMirror} corresponds to a user class that has not been
   * compiled yet, in which case the method returns <code>null</code>.
   * 
   * @param variableType the {@link TypeMirror} to analyze
   * @return the Java {@link Class} or <code>null</code> if it could not be loaded.
   */
  public static Class<?> getVariableType(final TypeMirror variableType) {
    try {
      return ClassUtils.getClass(variableType.toString());
    } catch (ClassNotFoundException e) {
      return null;
    }
  }

  /**
   * Attempts to load the Java {@link Class} associated with the given {@code className}. This may
   * not be possible if the {@link TypeMirror} corresponds to a user class that has not been
   * compiled yet, in which case the method returns <code>null</code>.
   * 
   * @param className the fully qualified name of the class to load.
   * @return the Java {@link Class} or <code>null</code> if it could not be loaded.
   */
  public static Class<?> toClass(final String className) {
    try {
      return ClassUtils.getClass(className);
    } catch (ClassNotFoundException e) {
      return null;
    }
  }

}
