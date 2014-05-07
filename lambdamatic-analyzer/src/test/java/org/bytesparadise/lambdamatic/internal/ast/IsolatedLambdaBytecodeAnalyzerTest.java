package org.bytesparadise.lambdamatic.internal.ast;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Collections;

import org.bytesparadise.lambdamatic.internal.ast.node.Expression;
import org.bytesparadise.lambdamatic.internal.ast.node.LocalVariable;
import org.bytesparadise.lambdamatic.internal.ast.node.MethodInvocation;
import org.bytesparadise.lambdamatic.internal.ast.node.StringLiteral;
import org.bytesparadise.lambdamatic.model.TestPojo;
import org.bytesparadise.lambdamatic.query.FilterExpression;
import org.junit.Test;

/**
 * Running test in an isolated class to simplify the bytecode reading ;)
 * @author xcoulon
 *
 */
public class IsolatedLambdaBytecodeAnalyzerTest {

	private final LambdaExpressionAnalyzer analyzer = new LambdaExpressionAnalyzer();

	@Test
	public void shouldParseMethodCallWithoutParameterWhenExpressionIsVariable() throws IOException {
		// operation
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getStringValue().equals(
				"foo");
		//FIXME: invoke $deserializeLambda$(expression)
		final Expression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// expectations
		final LocalVariable testPojo = new LocalVariable("t", "Lorg/bytesparadise/lambdamatic/model/TestPojo;");
		final MethodInvocation getStringValueMethod = new MethodInvocation(testPojo, "getStringValue",
				Collections.emptyList());
		final StringLiteral fooConstant = new StringLiteral("foo");
		final MethodInvocation expectedExpression = new MethodInvocation(getStringValueMethod, "equals", fooConstant);
		// verification
		assertThat(resultExpression).isEqualTo(expectedExpression);
	}
}
