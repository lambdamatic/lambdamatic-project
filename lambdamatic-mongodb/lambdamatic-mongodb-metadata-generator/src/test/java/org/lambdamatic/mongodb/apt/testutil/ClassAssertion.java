/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.apt.testutil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.AbstractAssert;

/**
 * Specific assertJ {@link AbstractAssert} for {@link Type} objects
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

	public ClassAssertion isAbstract() {
		isNotNull();
		if(!Modifier.isAbstract(actual.getModifiers())) {
			failWithMessage("Expected class <%s> to be abstract.", actual.getName());
		}
		return this;
	}
	
	public ClassAssertion isNotAbstract() {
		isNotNull();
		if(Modifier.isAbstract(actual.getModifiers())) {
			failWithMessage("Expected class <%s> NOT to be abstract.", actual.getName());
		}
		return this;
	}
	
	public ClassAssertion isImplementing(final String expectedInterface) {
		isNotNull();
		final List<String> interfaces = Arrays.asList(actual.getInterfaces()).stream().map(Class::getName).collect(Collectors.toList());
		if (!interfaces.contains(expectedInterface)) {
			failWithMessage("Expected class <%s> to implement <%s> but it only implements <%s>.", actual.getName(), expectedInterface, interfaces);
		}
		actual.getTypeParameters();
		return this;
	}

	public ClassAssertion isImplementing(final String expectedGenericInterfaceName, final String parameterTypeName) {
		isNotNull();
		boolean match = Arrays.asList(actual.getGenericInterfaces()).stream()
				.filter(i -> i instanceof ParameterizedType).map(i -> (ParameterizedType) i)
				.filter(i -> i.getActualTypeArguments().length == 1).map(i -> i.getActualTypeArguments()[0])
				.anyMatch(t -> t.getTypeName().equals(parameterTypeName));
		if (!match) {
			failWithMessage("Expected field <%s> to implement <%s> but it only implements <%s>.", actual.getName(), expectedGenericInterfaceName, actual.getGenericInterfaces());
		}
		actual.getTypeParameters();
		return this;
	}
	
	public ClassAssertion isExtending(final String expectedSuperClass) {
		isNotNull();
		if (actual.getSuperclass() == null || !expectedSuperClass.equals(actual.getSuperclass().getName())) {
			failWithMessage("Expected class <%s> to implement <%s> but it only extends <%s>.", actual.getName(), expectedSuperClass, actual.getSuperclass());
		}
		actual.getTypeParameters();
		return this;
	}

	public ClassAssertion isExtending(final Class<?> expectedSuperClass, final Class<?>... parameterTypes) {
		isNotNull();
		final ParameterizedType genericSuperclass = (ParameterizedType) actual.getGenericSuperclass();
		if (genericSuperclass == null || !expectedSuperClass.getName().equals(genericSuperclass.getRawType().getTypeName())) {
			failWithMessage("Expected class <%s> to extend <%s> but it only extends <%s>.", actual.getName(), expectedSuperClass, actual.getSuperclass());
		}
		if (!Arrays.deepEquals(genericSuperclass.getActualTypeArguments(), parameterTypes)) {
			failWithMessage("Expected class <%s> to have types <%s> but it only has <%s>.", actual.getName(), parameterTypes, genericSuperclass.getActualTypeArguments());
		}
		return this;
	}

	

	public ClassAssertion hasMethod(final String methodName, final Class<?>... parameterTypes) {
		isNotNull();
		try {
			actual.getMethod(methodName, parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			failWithMessage("Expected class <%s> to have method %s(%s), but it only containes %s", actual.getName(), methodName, (parameterTypes.length > 0 ? parameterTypes : ""), actual.getMethods());
		}
		return this;
	}

	public ClassAssertion hasField(final String fieldName) {
		isNotNull();
		try {
			actual.getField(fieldName);
		} catch (NoSuchFieldException | SecurityException e) {
			failWithMessage("Expected class <%s> to have *public* field %s, but it only containes %s", actual.getName(), fieldName, actual.getFields());
		}
		
		return this;
	}

	public ClassAssertion hasNoDeclaredField() {
		isNotNull();
		if(actual.getDeclaredFields().length > 0) {
			failWithMessage("Expected class <%s> to have *no* declared field, but found %s", actual.getName(), actual.getDeclaredFields());
		}
		return this;
	}
	
	public <A extends Annotation> AnnotationAssertion hasAnnotation(Class<A> annotationClass) {
		isNotNull();
		return new AnnotationAssertion(actual.getAnnotation(annotationClass));
	}

}

