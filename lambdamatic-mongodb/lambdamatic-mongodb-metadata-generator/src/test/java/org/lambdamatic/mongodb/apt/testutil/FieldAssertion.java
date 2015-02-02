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

import org.assertj.core.api.AbstractAssert;
import org.lambdamatic.mongodb.annotations.DocumentField;

/**
 * Specific assertJ {@link AbstractAssert} for {@link DocumentField} objects.
 * 
 * @author Xavier Coulon
 *
 */
public class FieldAssertion extends AbstractAssert<FieldAssertion, Field> {

	protected FieldAssertion(final Field actual) {
		super(actual, FieldAssertion.class);
	}

	public static FieldAssertion assertThat(final Field actual) {
		return new FieldAssertion(actual);
	}

	public FieldAssertion isType(final String expectedType) {
		isNotNull();
		final String actualTypeName = actual.getType().getName();
		if (!actualTypeName.equals(expectedType)) {
			failWithMessage("Expected field <%s> to be of type <%s> but it was <%s>", actual.getName(), expectedType,
					actualTypeName);
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

	public FieldAssertion isNotFinal() {
		isNotNull();
		if ((actual.getModifiers() & Modifier.FINAL) > 0) {
			failWithMessage("Expected field <%s> *NOT* to be final", actual.getName());
		}
		return this;
	}
	
	public <T extends Annotation> AnnotationAssertion hasAnnotation(final Class<T> annotationClass) {
		return new AnnotationAssertion(actual.getAnnotation(annotationClass));
	}
	
}

