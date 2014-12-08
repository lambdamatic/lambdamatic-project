package org.lambdamatic.analyzer;

import org.lambdamatic.FilterExpression;

import com.sample.model.TestPojo;

public class LambdaExpressionFactory {

	/**
	 * Builds a lambda {@link FilterExpression} from the given "foo" parameter
	 * 
	 * @param foo
	 *            the foo value
	 * @return the lambda expression
	 */
	public FilterExpression<TestPojo> buildLambdaExpression(final String foo) {
		return (TestPojo t) -> t.getStringValue().equals(foo);
	}

	/**
	 * Builds a lambda {@link FilterExpression} from the given "foo" parameter
	 * 
	 * @param foo
	 *            the foo value
	 * @return the lambda expression
	 */
	public static FilterExpression<TestPojo> staticBuildLambdaExpression(final String foo) {
		return (TestPojo t) -> t.getStringValue().equals(foo);
	}

	
	

}
