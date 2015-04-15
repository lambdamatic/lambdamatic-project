/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.apt.testutil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.assertj.core.api.AbstractAssert;
import org.lambdamatic.mongodb.annotations.DocumentField;

/**
 * Specific assertJ {@link AbstractAssert} for {@link DocumentField} annotated {@link Field}s.
 * 
 * @author Xavier Coulon
 *
 */
public class FieldAssertion extends AbstractAssert<FieldAssertion, Field> {

	/** An instance of the class on which the field is inspected. */
	private final Object targetObject;
	
	protected FieldAssertion(final Field field) throws InstantiationException, IllegalAccessException {
		super(field, FieldAssertion.class);
		this.targetObject = null;//targetClass.newInstance();
		
	}

	public static FieldAssertion assertThat(final Class<?> targetClass, final String fieldName) throws NoSuchFieldException, SecurityException, InstantiationException, IllegalAccessException {
		final Field field = targetClass.getDeclaredField(fieldName);
		return new FieldAssertion(field);
	}

	public FieldAssertion isParameterizedType(final Class<?> expectedRawType, final Type... expectedTypeArguments) {
		isNotNull();
		if(!(actual.getGenericType() instanceof ParameterizedType)) {
			failWithMessage("Expected field <%s> to be a parameterized type but it was not", actual);
		}
		final ParameterizedType actualType = (ParameterizedType) actual.getGenericType();
		final ParameterizedType expectedParameterizedType = TypeUtils.parameterize(expectedRawType, expectedTypeArguments);
		if (!TypeUtils.equals(actualType, expectedParameterizedType)) {
			failWithMessage("Expected field %s.%s to be of type %s<%s> but it was %s<%s>", actual.getType().getName(), actual.getName(), expectedRawType,
					expectedTypeArguments, actualType.getRawType().getTypeName(), actualType.getActualTypeArguments());
		}
		return this;
	}

	public FieldAssertion isType(final Class<?> expectedType) {
		return isType(expectedType.getName());
	}
	
	public FieldAssertion isType(final String expectedTypeName) {
		isNotNull();
		final String actualTypeName = actual.getType().getName();
		if (!actualTypeName.equals(expectedTypeName)) {
			failWithMessage("Expected field <%s> to be of type <%s> but it was <%s>", actual.getName(), expectedTypeName,
					actualTypeName);
		}
		return this;
	}
	
	public FieldAssertion isStatic() {
		isNotNull();
		if ((actual.getModifiers() & Modifier.STATIC) == 0) {
			failWithMessage("Expected field <%s> to be static", actual.getName());
		}
		return this;
	}

	public FieldAssertion isNotStatic() {
		isNotNull();
		if ((actual.getModifiers() & Modifier.STATIC) > 0) {
			failWithMessage("Expected field <%s> *NOT* to be static", actual.getName());
		}
		return this;
	}
	
	public FieldAssertion isFinal() {
		isNotNull();
		if ((actual.getModifiers() & Modifier.FINAL) == 0) {
			failWithMessage("Expected field <%s> to be final", actual.getName());
		}
		return this;
	}
	
	public FieldAssertion isNotFinal() {
		isNotNull();
		if ((actual.getModifiers() & Modifier.FINAL) > 0) {
			failWithMessage("Expected field <%s> *NOT* to be final", actual.getName());
		}
		return this;
	}
	
	public FieldAssertion hasValue() throws IllegalArgumentException, IllegalAccessException {
		isNotNull();
		if (actual.getInt(targetObject) == 0) {
			failWithMessage("Expected field <%s> to be initialized", actual.getName());
		}
		return this;
	}
	
	public FieldAssertion hasNoAnnotation() {
		isNotNull();
		if (actual.getAnnotations().length > 0) {
			failWithMessage("Expected no annotation on field <%s>", actual.getName());
		}
		return this;
	}

	public <T extends Annotation> AnnotationAssertion hasAnnotation(final Class<T> annotationClass) {
		return new AnnotationAssertion(actual.getAnnotation(annotationClass));
	}
	
	public FieldAssertion hasDefaultValueEquals(final Object expectedValue) {
		isNotNull();
		final Class<?> declaringClass = actual.getDeclaringClass();
		try {
			final Object defaultInstance = declaringClass.newInstance();
			final Object defaultFieldValue = actual.get(defaultInstance);
			if (!defaultFieldValue.equals(expectedValue)) {
				failWithMessage("Expected field <%s> to be have default value <%s>", actual.getName(), expectedValue);
			}
		} catch(InstantiationException | IllegalAccessException e) {
			failWithMessage("Unable to instantiate new object of type <%s>", declaringClass);
			e.printStackTrace();
		}
		return this;
	}

}

