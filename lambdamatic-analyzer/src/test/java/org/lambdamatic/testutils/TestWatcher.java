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
package org.lambdamatic.testutils;

import org.junit.rules.TestName;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestWatcher extends TestName {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestWatcher.class);

  @Override
  protected void starting(final Description description) {
    LOGGER.warn("*************************");
    LOGGER.warn("Starting {}.{}", description.getClassName(), description.getMethodName());
    LOGGER.warn("*************************");
  }

  @Override
  protected void finished(final Description description) {
    LOGGER.warn("*************************");
    LOGGER.warn("Finished {}.{}", description.getClassName(), description.getMethodName());
    LOGGER.warn("*************************");
  }

}

