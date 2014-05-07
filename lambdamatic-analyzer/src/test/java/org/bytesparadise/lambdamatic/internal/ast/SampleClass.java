package org.bytesparadise.lambdamatic.internal.ast;

import org.bytesparadise.lambdamatic.model.TestPojo;
import org.bytesparadise.lambdamatic.query.FilterExpression;
import org.bytesparadise.lambdamatic.query.Query;

public class SampleClass {

	public void sampleMethod() {
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getStringValue().equals("foo");
		new Query(TestPojo.class).filter(expression);
	}
}
