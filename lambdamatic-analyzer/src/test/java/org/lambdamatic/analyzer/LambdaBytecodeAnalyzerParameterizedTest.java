package org.lambdamatic.analyzer;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.lambdamatic.FilterExpression;
import org.lambdamatic.LambdaExpression;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.InfixExpression;
import org.lambdamatic.analyzer.ast.node.LocalVariable;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.lambdamatic.analyzer.ast.node.NullLiteral;
import org.lambdamatic.analyzer.ast.node.NumberLiteral;
import org.lambdamatic.analyzer.ast.node.StringLiteral;
import org.lambdamatic.analyzer.ast.node.InfixExpression.InfixOperator;
import org.lambdamatic.testutils.TestWatcher;

import com.sample.model.TestPojo;
import com.sample.model.User;

/**
 * Parameterized tests with many use cases of comparison.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
@RunWith(Parameterized.class)
public class LambdaBytecodeAnalyzerParameterizedTest {

	private final LambdaExpressionAnalyzer analyzer = new LambdaExpressionAnalyzer();

	@Rule
	public TestWatcher watcher = new TestWatcher();

	@Parameters(name = "{1}")
	public static Object[][] data() {
		final TestPojo anotherPojo = new TestPojo();
		final String stringValue_foo = "foo";
		final String stringValue_bar = "bar";
		final String stringValue_baz = "baz";
		final String stringValue_null = null;
		final int intValue_0 = 0;
		final int intValue_1 = 1;
		final int intValue_42 = 42;
		final long longValue_0 = 0L;
		final long longValue_1 = 1L;
		final long longValue_42 = 42L;
		final LocalVariable var_t = new LocalVariable("t", TestPojo.class);
		final MethodInvocation t_dot_getStringValue = new MethodInvocation(var_t, "getStringValue");
		final MethodInvocation t_dot_getIntValue = new MethodInvocation(var_t, "getIntValue");
		final MethodInvocation t_dot_getLongValue = new MethodInvocation(var_t, "getLongValue");
		final MethodInvocation t_dot_equals_foo = new MethodInvocation(var_t, "equals", new StringLiteral("foo"));
		final MethodInvocation t_dot_equals_bar = new MethodInvocation(var_t, "equals", new StringLiteral("bae"));
		final MethodInvocation t_dot_equals_baz = new MethodInvocation(var_t, "equals", new StringLiteral("baz"));
		final FieldAccess t_dot_field = new FieldAccess(var_t, "field");
		final MethodInvocation t_dot_field_dot_equals_foo = new MethodInvocation(t_dot_field, "equals",
				new StringLiteral("foo"));
		final MethodInvocation t_dot_field_dot_equals_bar = new MethodInvocation(t_dot_field, "equals",
				new StringLiteral("foo"));
		final MethodInvocation t_dot_field_dot_equals_baz = new MethodInvocation(t_dot_field, "equals",
				new StringLiteral("foo"));
		final InfixExpression t_dot_field_equals_foo = new InfixExpression(InfixOperator.EQUALS, t_dot_field, new StringLiteral("foo"));
		final InfixExpression t_dot_field_not_equals_foo = new InfixExpression(InfixOperator.NOT_EQUALS, t_dot_field, new StringLiteral("foo"));
		return new Object[][] {
				// int
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getIntValue() > 0),
						new InfixExpression(InfixOperator.GREATER, t_dot_getIntValue, new NumberLiteral(0)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getIntValue() < intValue_0),
						new InfixExpression(InfixOperator.LESS, t_dot_getIntValue, new NumberLiteral(0)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getIntValue() >= 1),
						new InfixExpression(InfixOperator.GREATER_EQUALS, t_dot_getIntValue, new NumberLiteral(1)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getIntValue() <= intValue_1),
						new InfixExpression(InfixOperator.LESS_EQUALS, t_dot_getIntValue, new NumberLiteral(intValue_1)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getIntValue() == 42),
						new InfixExpression(InfixOperator.EQUALS, t_dot_getIntValue, new NumberLiteral(42)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getIntValue() != intValue_42),
						new InfixExpression(InfixOperator.NOT_EQUALS, t_dot_getIntValue, new NumberLiteral(intValue_42)) },
				// long
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getLongValue() > 0L),
						new InfixExpression(InfixOperator.GREATER, t_dot_getLongValue, new NumberLiteral(0L)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getLongValue() < longValue_0),
						new InfixExpression(InfixOperator.LESS, t_dot_getLongValue, new NumberLiteral(0)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getLongValue() >= 1L),
						new InfixExpression(InfixOperator.GREATER_EQUALS, t_dot_getLongValue, new NumberLiteral(1L)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getLongValue() <= longValue_1),
						new InfixExpression(InfixOperator.LESS_EQUALS, t_dot_getLongValue, new NumberLiteral(
								longValue_1)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getLongValue() == 42L),
						new InfixExpression(InfixOperator.EQUALS, t_dot_getLongValue, new NumberLiteral(42L)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getLongValue() != longValue_42),
						new InfixExpression(InfixOperator.NOT_EQUALS, t_dot_getLongValue, new NumberLiteral(
								longValue_42)) },
				// String
				new Object[] { (FilterExpression<TestPojo>) (t -> t.getStringValue().equals("foo")),
						new MethodInvocation(t_dot_getStringValue, "equals", new StringLiteral("foo")) },
				new Object[] { (FilterExpression<TestPojo>) (t -> t.getStringValue().equals(stringValue_bar)),
						new MethodInvocation(t_dot_getStringValue, "equals", new StringLiteral(stringValue_bar)) },
				new Object[] { (FilterExpression<TestPojo>) (t -> t.getStringValue().equals(null)),
						new MethodInvocation(t_dot_getStringValue, "equals", new NullLiteral()) },
				new Object[] { (FilterExpression<TestPojo>) (t -> !t.getStringValue().equals(stringValue_null)),
						new MethodInvocation(t_dot_getStringValue, "equals", new NullLiteral()).inverse() },
				new Object[] {
						(FilterExpression<TestPojo>) (t -> t.getStringValue().equals(anotherPojo.getStringValue())),
						new MethodInvocation(t_dot_getStringValue, "equals", new StringLiteral("foo")) },
				new Object[] {
						(FilterExpression<TestPojo>) (t -> t.getStringValue().equals("foo")
								|| t.getStringValue().equals("bar") || t.getStringValue().equals("baz")),
						new InfixExpression(InfixOperator.CONDITIONAL_OR, t_dot_equals_foo, t_dot_equals_bar,
								t_dot_equals_baz) },
				new Object[] { new LambdaExpressionFactory().buildLambdaExpression("foo"),
						new MethodInvocation(t_dot_getStringValue, "equals", new StringLiteral("foo")) },
				new Object[] { LambdaExpressionFactory.staticBuildLambdaExpression("foo"),
						new MethodInvocation(t_dot_getStringValue, "equals", new StringLiteral("foo")) },
				new Object[] { (FilterExpression<TestPojo>) (t -> t.field.equals("foo")), t_dot_field_dot_equals_foo },
				new Object[] {
						(FilterExpression<TestPojo>) (t -> (t.field.equals("foo") && t.field.equals("bar"))
								|| t.field.equals("baz")),
						new InfixExpression(InfixOperator.CONDITIONAL_OR, new InfixExpression(
								InfixOperator.CONDITIONAL_AND, t_dot_field_dot_equals_foo, t_dot_field_dot_equals_bar),
								t_dot_field_dot_equals_baz) },
				new Object[] {
						(FilterExpression<TestPojo>) (t -> (t.field.equals("foo") && !t.field.equals("bar"))
								|| t.field.equals("baz")),
						new InfixExpression(InfixOperator.CONDITIONAL_OR, new InfixExpression(
								InfixOperator.CONDITIONAL_AND, t_dot_field_dot_equals_foo,
								t_dot_field_dot_equals_bar.inverse()), t_dot_field_dot_equals_baz) },
				new Object[] {
					(FilterExpression<TestPojo>) (t -> (t.field.equals("foo") && t.field.equals("bar"))
							|| !t.field.equals("baz")),
							new InfixExpression(InfixOperator.CONDITIONAL_OR, new InfixExpression(
									InfixOperator.CONDITIONAL_AND, t_dot_field_dot_equals_foo,
									t_dot_field_dot_equals_bar), t_dot_field_dot_equals_baz.inverse()) },
				new Object[] {
						(FilterExpression<TestPojo>) (t -> t.field.equals("foo") && t.field.equals("foo")),
								t_dot_field_dot_equals_foo },
				new Object[] {
						(FilterExpression<TestPojo>) (t -> t.field == "foo"),
						t_dot_field_equals_foo },
				new Object[] {
						(FilterExpression<TestPojo>) (t -> t.field != "foo"),
						t_dot_field_not_equals_foo },

		// double
		// float
		// char
		// Date
		// Enum
		};
	}

	@Parameter(value = 0)
	public FilterExpression<TestPojo> expression;

	@Parameter(value = 1)
	public Expression expectation;

	@Test
	public void shouldParseLambdaExpression() throws IOException {
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		assertThat(resultExpression.getExpression()).isEqualTo(expectation);
	}

}
