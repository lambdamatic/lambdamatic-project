package org.lambdamatic.analyzer;

import java.util.function.Predicate;

import org.lambdamatic.SerializablePredicate;

import com.sample.model.TestPojo;

/**
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class SerializablePredicateFactory {

	/**
	 * Builds a lambda {@link Predicate} from the given "foo" parameter
	 * 
	 * @param foo
	 *            the foo value
	 * @return the lambda expression
	 */
	public SerializablePredicate<TestPojo> buildLambdaExpression(final String foo) {
		return (TestPojo t) -> t.getStringValue().equals(foo);
	}

	/**
	 * Builds a lambda {@link Predicate} from the given "foo" parameter
	 * 
	 * @param foo
	 *            the foo value
	 * @return the lambda expression
	 */
	public static SerializablePredicate<TestPojo> staticBuildLambdaExpression(final String foo) {
		return (TestPojo t) -> t.getStringValue().equals(foo);
	}

	
	

}
