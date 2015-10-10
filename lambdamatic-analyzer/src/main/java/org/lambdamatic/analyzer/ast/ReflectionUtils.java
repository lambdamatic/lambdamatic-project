/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lambdamatic.analyzer.exception.AnalyzeException;

/**
 * Utility class to around Reflection API.
 * 
 * @author Xavier Coulon
 *
 */
public class ReflectionUtils {

  /**
   * Finds and returns the closest matching method with the given name and given argument types on
   * the given {@code source} Object.
   * 
   * @param source the Class instance on which the method should be found
   * @param methodName the name of the declared method to find
   * @param argTypes the argument types
   * @return the {@link Method}
   * @throws AnalyzeException if no method matched
   */
  public static Method findJavaMethod(final Object source, final String methodName,
      final Class<?>... argTypes) {
    if (source instanceof Class) {
      return findJavaMethod((Class<?>) source, methodName, argTypes);
    }
    return findJavaMethod(source.getClass(), methodName, argTypes);
  }

  /**
   * Finds and returns the closest matching method with the given name and given argument types on
   * the given {@code sourceClass} {@link Class}.
   * 
   * @param sourceClass the Class on which the method should be found
   * @param methodName the name of the declared method to find
   * @param argTypes the argument types
   * @return the {@link Method}
   * @throws AnalyzeException if no method matched
   */
  public static Method findJavaMethod(final Class<?> sourceClass, final String methodName,
      final Class<?>... argTypes) {
    methods_loop: for (Method method : sourceClass.getMethods()) {
      if (!method.getName().equals(methodName)
          || method.getParameterTypes().length != argTypes.length) {
        continue methods_loop;
      }
      // perfect match or check if superclass/superinterfaces of the given parameter types could
      // match
      for (int i = 0; i < argTypes.length; i++) {
        final Class<?> givenParameterType = argTypes[i];
        final Class<?> methodParameterType = method.getParameterTypes()[i];
        final boolean isSameType = methodParameterType.equals(givenParameterType);
        final boolean isSubType = methodParameterType.isAssignableFrom(givenParameterType);
        final boolean isMatchingVarArg = (i == argTypes.length - 1) && methodParameterType.isArray()
            && (methodParameterType.getComponentType().equals(givenParameterType)
                || methodParameterType.getComponentType().isAssignableFrom(givenParameterType));
        if (!isSameType && !isSubType && !isMatchingVarArg) {
          continue methods_loop;
        }
      }
      // the method matches
      return method;
    }
    throw new AnalyzeException("Could not find a method named '" + methodName + "' in class "
        + sourceClass.getName() + " with parameters matching "
        + String.join(", ", Stream.of(argTypes).map(Class::getName).collect(Collectors.toList())));
  }

  /**
   * Looks for the Java {@link Method} matching the given arguments
   * 
   * @param javaType the Java {@link Class} to which the method belongs to.
   * @param name the name of the method to find
   * @param parameterTypes the type of the parameters
   * @return the Java {@link Method}
   * @throws AnalyzeException if the {@link Method} was not found or in case of
   *         {@link SecurityException}.
   */
  public static Method findJavaMethod(final Class<?> javaType, final String name,
      final List<Class<?>> parameterTypes) {
    try {
      return javaType.getMethod(name, parameterTypes.toArray(new Class<?>[0]));
    } catch (NoSuchMethodException | SecurityException e) {
      throw new AnalyzeException("Failed to find method named '" + name + "' on class '"
          + javaType.getName() + "' with parameter types: " + parameterTypes, e);
    }
  }

  /**
   * Finds and returns the method with the given name and given argument types on the given
   * {@code source} Object.
   * 
   * @param fieldName the name of the field to find
   * @param source the source (class instance) on which the method should be found, or {@code null}
   *        if the method to find is static
   * @return the {@link Method}
   * @throws NoSuchFieldException if no field with the given name could be find
   */
  public static Field getFieldToInvoke(final Object source, final String fieldName)
      throws NoSuchFieldException {
    if (source instanceof Class) {
      return getFieldToInvoke((Class<?>) source, fieldName);
    }
    return getFieldToInvoke(source.getClass(), fieldName);
  }

  // FIXME: we should cover all cases: method in superclass and all variants of superclass of
  // any/all arguments.
  private static Field getFieldToInvoke(final Class<?> clazz, final String fieldName)
      throws NoSuchFieldException {
    return clazz.getField(fieldName);
  }


}
