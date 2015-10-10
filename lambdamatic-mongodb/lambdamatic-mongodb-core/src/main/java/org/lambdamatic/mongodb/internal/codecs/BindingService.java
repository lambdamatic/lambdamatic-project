/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.internal.codecs;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.bson.BsonDocument;
import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.annotations.DocumentId;
import org.lambdamatic.mongodb.exceptions.ConversionException;

import com.mongodb.DBObject;

/**
 * Service that binds the Java Class fields to MongoDB Document fields. This class implements the
 * singleton pattern for easy access in any Codec or Encoder classes.
 * 
 * @author Xavier Coulon
 *
 */
public class BindingService {

  /**
   * Cache of bindings to convert domain class instances to/from {@link BsonDocument}s.
   */
  private final Map<Class<?>, Map<String, Field>> bindings = new HashMap<>();

  private static final BindingService instance = new BindingService();

  /**
   * Access to the singleton instance.
   * 
   * @return the singleton instance of the {@link BindingService}.
   */
  public static BindingService getInstance() {
    return instance;
  }

  /**
   * Singleton constructor.
   */
  private BindingService() {
    super();
  }

  /**
   * Checks if the given {@link Field} is annotated with {@link DocumentId}.
   * 
   * @param field the field to analyze
   * @return {@code true} if the given field is annotated with {@link DocumentId}, {@code false}
   *         otherwise.
   */
  public static boolean isIdBinding(final Field field) {
    return field.getAnnotation(DocumentId.class) != null;
  }

  /**
   * Retrieves the value of the given {@link Field} in the given {@code domainObject}.
   * 
   * @param domainObject the object to analyze
   * @param domainField the target {@link Field}
   * @return the field value or {@code null} if it was not set
   * @throws ConversionException if a problem occurred while accessing the field value
   */
  public static Object getFieldValue(final Object domainObject, final Field domainField) {
    domainField.setAccessible(true);
    try {
      return domainField.get(domainObject);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new ConversionException("Failed to retrieve value for field '" + domainField.getName()
          + "' in domain object '" + domainObject + "'", e);
    }
  }

  /**
   * Analyzes and puts in a memory map the bindings to use when converting an incoming
   * {@link DBObject} into an instance of {@code targetClass}. The bindings is a {@link Map} of
   * {@link DocumentField} in the {@code targetClass} indexed by the name of the {@code key} )in the
   * incoming {@link DBObject}.
   * 
   * @param targetClass the {@link Class} for which the {@link Map} bindings should be returned
   * @return the {@link Map} of bindings for the given {@code targetClass}
   */
  public Map<String, Field> getBindings(final Class<?> targetClass) {
    if (!this.bindings.containsKey(targetClass)) {
      final Map<String, Field> classBindings = new TreeMap<>();
      // let's analyze the class' declared fields
      // TODO: verify that inherited private fields are also supported
      for (Field field : targetClass.getDeclaredFields()) {
        final DocumentId documentIdAnnotation = field.getAnnotation(DocumentId.class);
        if (documentIdAnnotation != null) {
          classBindings.put("_id", field);
          continue;
        }
        final DocumentField documentFieldAnnotation = field.getAnnotation(DocumentField.class);
        if (documentFieldAnnotation != null && documentFieldAnnotation.name().isEmpty()) {
          classBindings.put(field.getName(), field);
        } else if (documentFieldAnnotation != null && !documentFieldAnnotation.name().isEmpty()) {
          classBindings.put(documentFieldAnnotation.name(), field);
        } else if (!field.getName().startsWith("$jacoco")) {
          // custom requirement: ignore Jacoco/EclEmma fields introduced at for code coverage
          classBindings.put(field.getName(), field);
        }
      }
      this.bindings.put(targetClass, classBindings);
    }
    return Collections.unmodifiableMap(this.bindings.get(targetClass));
  }

}
