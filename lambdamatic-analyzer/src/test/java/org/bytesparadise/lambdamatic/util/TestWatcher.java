package org.bytesparadise.lambdamatic.util;

import org.junit.rules.TestName;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestWatcher extends TestName {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestWatcher.class);

	@Override
	protected void starting(Description d) {
		LOGGER.warn("*************************");
		LOGGER.warn("Starting {}.{}", d.getClassName(), d.getMethodName());
		LOGGER.warn("*************************");
	}

	@Override
	protected void finished(Description d) {
		LOGGER.warn("*************************");
		LOGGER.warn("Finished {}.{}", d.getClassName(), d.getMethodName());
		LOGGER.warn("*************************");
	}

};
