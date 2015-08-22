/*******************************************************************************
 * Copyright (c) 2015 Red Hat.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.internal.codecs.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to simplify the definition of parameterized data in JUnit tests.
 * 
 * @author xcoulon
 *
 */
public class ParameterizedDataset<T> {

	private final List<Object[]> data = new ArrayList<>();

	/**
	 * Utility method that makes the JUnit parameters declaration much more readable.
	 * 
	 * @param title
	 *            the test title
	 * @param object
	 *            the domain object to encode
	 * @param bson
	 *            the expected result
	 * @return
	 */
	public void match(final String title, final T object, final String bson) {
		data.add(new Object[] {
				title, object, bson });
	}

	/**
	 * Utility method that makes the JUnit parameters declaration much more readable.
	 * 
	 * @param object
	 *            the domain object to encode
	 * @param bson
	 *            the expected result
	 * @return
	 */
	public void match(final T object, final String bson) {
		data.add(new Object[] {
				object, bson });
	}

	public Object[][] toArray() {
		return data.toArray(new Object[0][0]);
	}

}
