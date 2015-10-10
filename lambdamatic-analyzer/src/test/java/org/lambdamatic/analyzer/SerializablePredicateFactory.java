/*******************************************************************************
 * Copyright (c) 2015 Red Hat.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Red Hat - Initial Contribution
 *******************************************************************************/
package org.lambdamatic.analyzer;

import java.util.function.Predicate;

import org.lambdamatic.SerializablePredicate;

import com.sample.model.TestPojo;

/**
 * Utility class to build {@link SerializablePredicate}.
 */
public class SerializablePredicateFactory {

  /**
   * Builds a lambda {@link Predicate} from the given parameter.
   * 
   * @param foo the foo value
   * @return the lambda expression
   */
  public SerializablePredicate<TestPojo> buildLambdaExpression(final String foo) {
    return (TestPojo test) -> test.getStringValue().equals(foo);
  }

  /**
   * Builds a lambda {@link Predicate} from the given parameter.
   * 
   * @param foo the foo value
   * @return the lambda expression
   */
  public static SerializablePredicate<TestPojo> staticBuildLambdaExpression(final String foo) {
    return (TestPojo test) -> test.getStringValue().equals(foo);
  }



}
