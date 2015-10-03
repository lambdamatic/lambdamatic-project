/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.apt.testutil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.assertj.core.api.AbstractAssert;

/**
 * Specific assertJ {@link AbstractAssert} for {@link Method}s.
 * 
 * @author Xavier Coulon
 *
 */
public class MethodAssertion extends AbstractAssert<MethodAssertion, Method> {

	private MethodAssertion(final Method method) {
		super(method, MethodAssertion.class);
	}

	public static MethodAssertion assertThat(final Class<?> targetClass, final String methodName, final Class<?>... parameterTypes) {
		try {
			return new MethodAssertion(targetClass.getDeclaredMethod(methodName, parameterTypes));
		} catch (NoSuchMethodException | SecurityException e) {
			return new MethodAssertion(null);
		}
	}

	public MethodAssertion isStatic() {
		isNotNull();
		if ((actual.getModifiers() & Modifier.STATIC) == 0) {
			failWithMessage("Expected field <%s> to be static", actual.getName());
		}
		return this;
	}

	public MethodAssertion isNotStatic() {
		isNotNull();
		if ((actual.getModifiers() & Modifier.STATIC) > 0) {
			failWithMessage("Expected field <%s> *NOT* to be static", actual.getName());
		}
		return this;
	}
	
	public MethodAssertion isFinal() {
		isNotNull();
		if ((actual.getModifiers() & Modifier.FINAL) == 0) {
			failWithMessage("Expected field <%s> to be final", actual.getName());
		}
		return this;
	}
	
	public MethodAssertion isNotFinal() {
		isNotNull();
		if ((actual.getModifiers() & Modifier.FINAL) > 0) {
			failWithMessage("Expected field <%s> *NOT* to be final", actual.getName());
		}
		return this;
	}
	
	public MethodAssertion hasNoAnnotation() {
		isNotNull();
		if (actual.getAnnotations().length > 0) {
			failWithMessage("Expected no annotation on field <%s>", actual.getName());
		}
		return this;
	}

	public <T extends Annotation> MethodAssertion hasAnnotation(final Class<T> annotationClass) {
		new AnnotationAssertion(actual.getAnnotation(annotationClass)).isNotNull();
		return this;
	}

	public <T extends Annotation> MethodAssertion hasAnnotation(final Class<T> annotationClass, final String attributeName, final String expectedValue) {
		new AnnotationAssertion(actual.getAnnotation(annotationClass)).isNotNull().hasAttributeValue(attributeName, expectedValue);
		return this;
	}
	
	public void isNotDefined() {
		isNull();
	}

	public MethodAssertion hasReturnType(final Class<?> returnType) {
		isNotNull();
		if(!actual.getReturnType().equals(returnType)) {
			failWithMessage("Expected return type to be <%s> but it was <%s>", returnType.getName(), actual.getReturnType().getName());
		}
		return this;
	}

}

