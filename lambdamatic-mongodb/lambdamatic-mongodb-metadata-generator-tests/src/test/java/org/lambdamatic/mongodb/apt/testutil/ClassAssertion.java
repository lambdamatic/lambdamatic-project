/*******************************************************************************
 * Copyright (c) 2014, 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc. All rights
 * reserved. This program is made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.apt.testutil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.api.AbstractAssert;

/**
 * Specific assertJ {@link AbstractAssert} for {@link TemplateType} objects.
 * 
 * @author Xavier Coulon
 *
 */
public class ClassAssertion extends AbstractAssert<ClassAssertion, Class<?>> {

  protected ClassAssertion(final Class<?> actual) {
    super(actual, ClassAssertion.class);
  }

  public static ClassAssertion assertThat(final Class<?> actual) {
    return new ClassAssertion(actual);
  }

  /**
   * Verifies that the actual class is abstract.
   * @return this {@link ClassAssertion} for fluent linking 
   */
  public ClassAssertion isAbstract() {
    isNotNull();
    if (!Modifier.isAbstract(actual.getModifiers())) {
      failWithMessage("Expected class <%s> to be abstract.", actual.getName());
    }
    return this;
  }

  /**
   * Verifies that the actual class given parameterized interface.
   * @return this {@link ClassAssertion} for fluent linking 
   */
  public ClassAssertion isImplementing(final Class<?> expectedGenericInterface,
      final Class<?>... parameterTypes) {
    isNotNull();
    boolean match = Stream.of(actual.getGenericInterfaces())
        .filter(i -> i instanceof ParameterizedType).map(i -> (ParameterizedType) i)
        .filter(i -> i.getRawType().getTypeName().equals(expectedGenericInterface.getName()))
        .filter(i -> i.getActualTypeArguments().length == parameterTypes.length)
        .anyMatch(i -> Arrays.deepEquals(parameterTypes, i.getActualTypeArguments()));
    if (!match) {
      failWithMessage("Expected field <%s> to implement <%s> but it only implements <%s>.",
          actual.getName(), expectedGenericInterface, actual.getGenericInterfaces());
    }
    actual.getTypeParameters();
    return this;
  }

  /**
   * Verifies that the actual class extends the given superclass.
   * @return this {@link ClassAssertion} for fluent linking 
   */
  public ClassAssertion isExtending(final String expectedSuperClass) {
    isNotNull();
    if (actual.getSuperclass() == null
        || !expectedSuperClass.equals(actual.getSuperclass().getName())) {
      failWithMessage("Expected class <%s> to implement <%s> but it only extends <%s>.",
          actual.getName(), expectedSuperClass, actual.getSuperclass());
    }
    actual.getTypeParameters();
    return this;
  }

  /**
   * Verifies that the actual class extends the given parameterized superclass.
   * @return this {@link ClassAssertion} for fluent linking 
   */
  public ClassAssertion isExtending(final Class<?> expectedSuperClass,
      final Class<?>... parameterTypes) {
    isNotNull();
    final ParameterizedType genericSuperclass = (ParameterizedType) actual.getGenericSuperclass();
    if (genericSuperclass == null
        || !expectedSuperClass.getName().equals(genericSuperclass.getRawType().getTypeName())) {
      failWithMessage("Expected class <%s> to extend <%s> but it only extends <%s>.",
          actual.getName(), expectedSuperClass, actual.getSuperclass());
    }
    if (!Arrays.deepEquals(genericSuperclass.getActualTypeArguments(), parameterTypes)) {
      failWithMessage("Expected class <%s> to have types <%s> but it only has <%s>.",
          actual.getName(), parameterTypes, genericSuperclass.getActualTypeArguments());
    }
    return this;
  }



  /**
   * Verifies that the actual class extends has the given method.
   * @return this {@link ClassAssertion} for fluent linking 
   */
  public ClassAssertion hasMethod(final String methodName, final Class<?>... parameterTypes) {
    isNotNull();
    try {
      actual.getMethod(methodName, parameterTypes);
    } catch (NoSuchMethodException | SecurityException e) {
      failWithMessage("Expected class <%s> to have method %s(%s), but it only containes %s",
          actual.getName(), methodName, (parameterTypes.length > 0 ? parameterTypes : ""),
          actual.getMethods());
    }
    return this;
  }

  /**
   * Verifies that the actual class extends hqs no field.
   * @return this {@link ClassAssertion} for fluent linking 
   */
  public ClassAssertion hasNoField(final String fieldName) {
    isNotNull();
    try {
      actual.getField(fieldName);
      failWithMessage("Did not expect class <%s> to have *public* field %s", actual.getName(),
          fieldName);
    } catch (NoSuchFieldException | SecurityException e) {
      // nothing to do
    }
    return this;
  }

  public <A extends Annotation> AnnotationAssertion hasAnnotation(Class<A> annotationClass) {
    isNotNull();
    return new AnnotationAssertion(actual.getAnnotation(annotationClass));
  }

}

