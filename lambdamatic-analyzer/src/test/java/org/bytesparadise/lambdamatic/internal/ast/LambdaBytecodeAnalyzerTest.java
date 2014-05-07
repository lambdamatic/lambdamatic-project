package org.bytesparadise.lambdamatic.internal.ast;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Collections;

import org.bytesparadise.lambdamatic.internal.ast.node.Expression;
import org.bytesparadise.lambdamatic.internal.ast.node.InfixExpression;
import org.bytesparadise.lambdamatic.internal.ast.node.InfixExpression.InfixOperator;
import org.bytesparadise.lambdamatic.internal.ast.node.LocalVariable;
import org.bytesparadise.lambdamatic.internal.ast.node.MethodInvocation;
import org.bytesparadise.lambdamatic.internal.ast.node.NumberLiteral;
import org.bytesparadise.lambdamatic.internal.ast.node.StringLiteral;
import org.bytesparadise.lambdamatic.model.TestPojo;
import org.bytesparadise.lambdamatic.query.FilterExpression;
import org.bytesparadise.lambdamatic.util.TestWatcher;
import org.junit.Rule;
import org.junit.Test;

/**
 * Created by xcoulon on 12/15/13.
 */
public class LambdaBytecodeAnalyzerTest {

	private final LambdaExpressionAnalyzer analyzer = new LambdaExpressionAnalyzer();

	@Rule
	public TestWatcher watcher = new TestWatcher();

	@Test
	public void shouldParseLambdaExpressionWithStringLiteralConstant() throws IOException {
		// operation
		final Expression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getStringValue().equals(
				"foo"));
		// expectations
		final LocalVariable testPojo = new LocalVariable("t", "Lorg/bytesparadise/lambdamatic/model/TestPojo;");
		final MethodInvocation getStringValueMethod = new MethodInvocation(testPojo, "getStringValue",
				Collections.emptyList());
		final MethodInvocation expectedExpression = new MethodInvocation(getStringValueMethod, "equals",
				new StringLiteral("foo"));
		// verification
		assertThat(resultExpression).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionAsVariableWithStringLiteralConstant() throws IOException {
		// operation
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getStringValue().equals("foo");
		final Expression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// expectations
		final LocalVariable testPojo = new LocalVariable("t", "Lorg/bytesparadise/lambdamatic/model/TestPojo;");
		final MethodInvocation getStringValueMethod = new MethodInvocation(testPojo, "getStringValue",
				Collections.emptyList());
		final MethodInvocation expectedExpression = new MethodInvocation(getStringValueMethod, "equals",
				new StringLiteral("foo"));
		// verification
		assertThat(resultExpression).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionWithStringLiteralParameter() throws IOException {
		// operation
		final String value = "foo";
		final Expression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getStringValue().equals(
				value));
		// expectations
		final LocalVariable testPojo = new LocalVariable("t", "Lorg/bytesparadise/lambdamatic/model/TestPojo;");
		final MethodInvocation getStringValueMethod = new MethodInvocation(testPojo, "getStringValue",
				Collections.emptyList());
		final MethodInvocation expectedExpression = new MethodInvocation(getStringValueMethod, "equals",
				new StringLiteral("foo"));
		// verification
		assertThat(resultExpression).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionAsVariableWithStringLiteralParameter() throws IOException {
		// operation
		final String value = "foo";
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getStringValue().equals(value);
		final Expression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// expectations
		final LocalVariable testPojo = new LocalVariable("t", "Lorg/bytesparadise/lambdamatic/model/TestPojo;");
		final MethodInvocation getStringValueMethod = new MethodInvocation(testPojo, "getStringValue",
				Collections.emptyList());
		final MethodInvocation expectedExpression = new MethodInvocation(getStringValueMethod, "equals",
				new StringLiteral("foo"));
		// verification
		assertThat(resultExpression).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionWithCapturedArgumentParameter() throws IOException {
		// operation
		final TestPojo anotherPojo = new TestPojo();
		final Expression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getStringValue().equals(
				anotherPojo.getStringValue()));
		// expectations
		final LocalVariable testPojo = new LocalVariable("t", "Lorg/bytesparadise/lambdamatic/model/TestPojo;");
		final MethodInvocation getStringValueMethod = new MethodInvocation(testPojo, "getStringValue",
				Collections.emptyList());
		final MethodInvocation expectedExpression = new MethodInvocation(getStringValueMethod, "equals",
				new StringLiteral("foo"));
		// verification
		assertThat(resultExpression).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionAsVariableWithCapturedArgumentParameter() throws IOException {
		// operation
		final TestPojo anotherPojo = new TestPojo();
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getStringValue().equals(
				anotherPojo.getStringValue());
		final Expression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// expectations
		final LocalVariable testPojo = new LocalVariable("t", "Lorg/bytesparadise/lambdamatic/model/TestPojo;");
		final MethodInvocation getStringValueMethod = new MethodInvocation(testPojo, "getStringValue",
				Collections.emptyList());
		final MethodInvocation expectedExpression = new MethodInvocation(getStringValueMethod, "equals",
				new StringLiteral("foo"));
		// verification
		assertThat(resultExpression).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionWithGreaterThanNumberConstantValue() throws IOException {
		// operation
		final Expression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getIntValue() > 1);
		// expectations
		final LocalVariable testPojo = new LocalVariable("t", "Lorg/bytesparadise/lambdamatic/model/TestPojo;");
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getIntValue",
				Collections.emptyList());
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.GREATER, getIntValueMethod,
				new NumberLiteral(1));
		// verification
		assertThat(resultExpression).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionAsVariableWithGreaterThanNumberConstantValue() throws IOException {
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getIntValue() > 1;
		// operation
		final Expression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// expectations
		final LocalVariable testPojo = new LocalVariable("t", "Lorg/bytesparadise/lambdamatic/model/TestPojo;");
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getIntValue",
				Collections.emptyList());
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.GREATER, getIntValueMethod,
				new NumberLiteral(1));
		// verification
		assertThat(resultExpression).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionWithGreaterOrEqualThanNumberConstantValue() throws IOException {
		// operation
		final Expression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getIntValue() >= 1);
		// expectations
		final LocalVariable testPojo = new LocalVariable("t", "Lorg/bytesparadise/lambdamatic/model/TestPojo;");
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getIntValue",
				Collections.emptyList());
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.GREATER_EQUALS, getIntValueMethod,
				new NumberLiteral(1));
		// verification
		assertThat(resultExpression).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionAsVariableWithGreaterOrEqualThanNumberConstantValue() throws IOException {
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getIntValue() >= 1;
		// operation
		final Expression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// expectations
		final LocalVariable testPojo = new LocalVariable("t", "Lorg/bytesparadise/lambdamatic/model/TestPojo;");
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getIntValue",
				Collections.emptyList());
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.GREATER_EQUALS, getIntValueMethod,
				new NumberLiteral(1));
		// verification
		assertThat(resultExpression).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionWithLessThanNumberConstantValue() throws IOException {
		// operation
		final Expression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getIntValue() < 1);
		// expectations
		final LocalVariable testPojo = new LocalVariable("t", "Lorg/bytesparadise/lambdamatic/model/TestPojo;");
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getIntValue",
				Collections.emptyList());
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.LESS, getIntValueMethod,
				new NumberLiteral(1));
		// verification
		assertThat(resultExpression).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionAsVariableWithLessThanNumberConstantValue() throws IOException {
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getIntValue() < 1;
		// operation
		final Expression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// expectations
		final LocalVariable testPojo = new LocalVariable("t", "Lorg/bytesparadise/lambdamatic/model/TestPojo;");
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getIntValue",
				Collections.emptyList());
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.LESS, getIntValueMethod,
				new NumberLiteral(1));
		// verification
		assertThat(resultExpression).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionWithLessOrEqualThanNumberConstantValue() throws IOException {
		// operation
		final Expression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getIntValue() <= 1);
		// expectations
		final LocalVariable testPojo = new LocalVariable("t", "Lorg/bytesparadise/lambdamatic/model/TestPojo;");
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getIntValue",
				Collections.emptyList());
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.LESS_EQUALS, getIntValueMethod,
				new NumberLiteral(1));
		// verification
		assertThat(resultExpression).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionAsVariableWithLessOrEqualThanNumberConstantValue() throws IOException {
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getIntValue() <= 1;
		// operation
		final Expression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// expectations
		final LocalVariable testPojo = new LocalVariable("t", "Lorg/bytesparadise/lambdamatic/model/TestPojo;");
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getIntValue",
				Collections.emptyList());
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.LESS_EQUALS, getIntValueMethod,
				new NumberLiteral(1));
		// verification
		assertThat(resultExpression).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionWithMultipleStringLiteralConstantComparisons() throws IOException {
		// operation
		final Expression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> (t.getStringValue()
				.equals("foo") || t.getStringValue().equals("bar") || t.getStringValue().equals("baz")));
		// expectations
		final LocalVariable testPojo = new LocalVariable("t", "Lorg/bytesparadise/lambdamatic/model/TestPojo;");
		final MethodInvocation getStringValueMethod = new MethodInvocation(testPojo, "getStringValue",
				Collections.emptyList());
		final MethodInvocation ifEqualsFooExpression = new MethodInvocation(getStringValueMethod, "equals",
				new StringLiteral("foo"));
		final MethodInvocation ifEqualsBarExpression = new MethodInvocation(getStringValueMethod, "equals",
				new StringLiteral("bar"));
		final MethodInvocation ifEqualsBazExpression = new MethodInvocation(getStringValueMethod, "equals",
				new StringLiteral("baz"));
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.CONDITIONAL_OR,
				ifEqualsFooExpression, ifEqualsBarExpression, ifEqualsBazExpression);
		// verification
		assertThat(resultExpression).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionAsVariableWithMultipleStringLiteralConstantComparisons() throws IOException {
		final FilterExpression<TestPojo> expression = (TestPojo t) -> (t.getStringValue().equals("foo") || t
				.getStringValue().equals("bar") || t.getStringValue().equals("baz"));
		// operation
		final Expression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// expectations
		final LocalVariable testPojo = new LocalVariable("t", "Lorg/bytesparadise/lambdamatic/model/TestPojo;");
		final MethodInvocation getStringValueMethod = new MethodInvocation(testPojo, "getStringValue",
				Collections.emptyList());
		final MethodInvocation ifEqualsFooExpression = new MethodInvocation(getStringValueMethod, "equals",
				new StringLiteral("foo"));
		final MethodInvocation ifEqualsBarExpression = new MethodInvocation(getStringValueMethod, "equals",
				new StringLiteral("bar"));
		final MethodInvocation ifEqualsBazExpression = new MethodInvocation(getStringValueMethod, "equals",
				new StringLiteral("baz"));
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.CONDITIONAL_OR,
				ifEqualsFooExpression, ifEqualsBarExpression, ifEqualsBazExpression);
		// verification
		assertThat(resultExpression).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionBuiltFromInvokeSpecialMethod() throws IOException {
		// operation
		final Expression resultExpression = analyzer.analyzeLambdaExpression(buildLambdaExpression("foo"));
		// expectations
		final LocalVariable testPojo = new LocalVariable("t", "Lorg/bytesparadise/lambdamatic/model/TestPojo;");
		final MethodInvocation getStringValueMethod = new MethodInvocation(testPojo, "getStringValue",
				Collections.emptyList());
		final MethodInvocation expectedExpression = new MethodInvocation(getStringValueMethod, "equals",
				new StringLiteral("foo"));
		// verification
		assertThat(resultExpression).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionAsVariableBuiltFromInvokeSpecialMethod() throws IOException {
		// operation
		final FilterExpression<TestPojo> expression = buildLambdaExpression("foo");
		final Expression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// expectations
		final LocalVariable testPojo = new LocalVariable("t", "Lorg/bytesparadise/lambdamatic/model/TestPojo;");
		final MethodInvocation getStringValueMethod = new MethodInvocation(testPojo, "getStringValue",
				Collections.emptyList());
		final MethodInvocation expectedExpression = new MethodInvocation(getStringValueMethod, "equals",
				new StringLiteral("foo"));
		// verification
		assertThat(resultExpression).isEqualTo(expectedExpression);
	}

	/**
	 * Builds a lambda {@link FilterExpression} from the given "foo" parameter
	 * 
	 * @param foo
	 *            the foo value
	 * @return the lambda expression
	 */
	private FilterExpression<TestPojo> buildLambdaExpression(final String foo) {
		return (TestPojo t) -> t.getStringValue().equals(foo);
	}

	@Test
	public void shouldParseLambdaExpressionBuiltFromInvokeStaticMethod() throws IOException {
		// operation
		final Expression resultExpression = analyzer.analyzeLambdaExpression(staticBuildLambdaExpression("foo"));
		// expectations
		final LocalVariable testPojo = new LocalVariable("t", "Lorg/bytesparadise/lambdamatic/model/TestPojo;");
		final MethodInvocation getStringValueMethod = new MethodInvocation(testPojo, "getStringValue",
				Collections.emptyList());
		final MethodInvocation expectedExpression = new MethodInvocation(getStringValueMethod, "equals",
				new StringLiteral("foo"));
		// verification
		assertThat(resultExpression).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionAsVariableBuiltFromInvokeStaticMethod() throws IOException {
		// operation
		final FilterExpression<TestPojo> expression = staticBuildLambdaExpression("foo");
		final Expression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// expectations
		final LocalVariable testPojo = new LocalVariable("t", "Lorg/bytesparadise/lambdamatic/model/TestPojo;");
		final MethodInvocation getStringValueMethod = new MethodInvocation(testPojo, "getStringValue",
				Collections.emptyList());
		final MethodInvocation expectedExpression = new MethodInvocation(getStringValueMethod, "equals",
				new StringLiteral("foo"));
		// verification
		assertThat(resultExpression).isEqualTo(expectedExpression);
	}

	/**
	 * Builds a lambda {@link FilterExpression} from the given "foo" parameter
	 * 
	 * @param foo
	 *            the foo value
	 * @return the lambda expression
	 */
	private static FilterExpression<TestPojo> staticBuildLambdaExpression(final String foo) {
		return (TestPojo t) -> t.getStringValue().equals(foo);
	}

}
