/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lamdamatic.jpa.apt.testutils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

import org.assertj.core.api.AbstractAssert;

/**
 * Specific assertJ {@link AbstractAssert} for {@link Annotation} objects.
 * 
 * @author Xavier Coulon
 *
 */
public class AnnotationAssertion extends AbstractAssert<AnnotationAssertion, Annotation> {

	protected AnnotationAssertion(final Annotation actual) {
		super(actual, AnnotationAssertion.class);
	}

	public static AnnotationAssertion assertThat(final Annotation actual) {
		return new AnnotationAssertion(actual);
	}

	public AnnotationAssertion hasAttributeValue(final String attributeName, final Object expectedValue) {
		isNotNull();
		try {
			final Object actualValue = actual.getClass().getMethod(attributeName).invoke(actual);
			if (!expectedValue.equals(actualValue)) {
				failWithMessage("Expected attribute <%s> to have value <%s> but it has value <%s>", attributeName,
						expectedValue, actualValue);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
			failWithMessage("Unable to find value for attribute <%s>: <%s>", attributeName, e.getMessage());
		}
		return this;
	}

}
