package org.lambdamatic.analyzer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lambdamatic.testutils.JavaMethods.ArrayUtil_toArray;
import static org.lambdamatic.testutils.JavaMethods.Object_equals;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_elementMatch;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.lambdamatic.SerializableFunction;
import org.lambdamatic.analyzer.ast.node.ArrayVariable;
import org.lambdamatic.analyzer.ast.node.ClassLiteral;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.LambdaExpression;
import org.lambdamatic.analyzer.ast.node.LocalVariable;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.lambdamatic.analyzer.ast.node.StringLiteral;
import org.lambdamatic.testutils.TestWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sample.model.TestPojo;

/**
 * Parameterized tests with many use cases of comparison.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 */
@RunWith(Parameterized.class)
public class SerializableFunctionExpressionBytecodeAnalyzerTest {

	private final LambdaExpressionAnalyzer analyzer = LambdaExpressionAnalyzer.getInstance();

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SerializableFunctionExpressionBytecodeAnalyzerTest.class);

	@Rule
	public TestWatcher watcher = new TestWatcher();

	@Parameters(name = "[{index}] expect {1}")
	public static Object[][] data() throws NoSuchMethodException, SecurityException {
		final String foo = "foo";
		final TestPojo anotherPojo = new TestPojo();
		final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
		final FieldAccess testPojo_dot_field = new FieldAccess(testPojo, "field");
		final FieldAccess testPojo_dot_dateValue = new FieldAccess(testPojo, "dateValue");
		final FieldAccess testPojo_dot_elementList = new FieldAccess(testPojo, "elementList");
		final FieldAccess e_dot_field = new FieldAccess(new LocalVariable(0, "e", TestPojo.class), "field");
		final MethodInvocation e_dot_field_equals_foo = new MethodInvocation(e_dot_field, Object_equals,
				new StringLiteral("foo"));

		return new Object[][] {
				new Object[] {
						(SerializableFunction<TestPojo, Object[]>) ((TestPojo t) -> ArrayUtil.toArray(t.stringValue,
								t.dateValue)),
				new MethodInvocation(new ClassLiteral(ArrayUtil.class), ArrayUtil_toArray,
						new ArrayVariable(Object[].class, new FieldAccess(testPojo, "stringValue"),
								new FieldAccess(testPojo, "dateValue"))) },
				new Object[] { (SerializableFunction<TestPojo, Object[]>) ((TestPojo t) -> ArrayUtil.toArray(t.field)),
						new MethodInvocation(new ClassLiteral(ArrayUtil.class), ArrayUtil_toArray,
								new ArrayVariable(Object[].class, testPojo_dot_field)) },
				new Object[] {
						(SerializableFunction<TestPojo, Object[]>) ((TestPojo t) -> ArrayUtil.toArray(t.field,
								t.dateValue, t.elementList)),
						new MethodInvocation(new ClassLiteral(ArrayUtil.class), ArrayUtil_toArray,
								new ArrayVariable(Object[].class, testPojo_dot_field, testPojo_dot_dateValue,
										testPojo_dot_elementList)) },
				// Verify nested Lambda expression
				new Object[] {
						(SerializableFunction<TestPojo, Boolean>) ((TestPojo t) -> t
								.elementMatch(e -> e.field.equals("foo"))),
						new MethodInvocation(testPojo, TestPojo_elementMatch, e_dot_field_equals_foo) },
				new Object[] {
						(SerializableFunction<TestPojo, Boolean>) ((TestPojo t) -> t
								.elementMatch(e -> e.field.equals(foo))),
						new MethodInvocation(testPojo, TestPojo_elementMatch, e_dot_field_equals_foo) },
				new Object[] {
						(SerializableFunction<TestPojo, Boolean>) ((TestPojo t) -> t
								.elementMatch(e -> e.field.equals(anotherPojo.getStringValue()))),
						new MethodInvocation(testPojo, TestPojo_elementMatch, e_dot_field_equals_foo) },
				new Object[] {
						(SerializableFunction<TestPojo, Boolean>) ((TestPojo t) -> t
								.elementMatch(e -> e.field.equals(anotherPojo.stringValue))),
						new MethodInvocation(testPojo, TestPojo_elementMatch, e_dot_field_equals_foo) },

		};
	}

	@Parameter(value = 0)
	public SerializableFunction<TestPojo, Object[]> expression;

	@Parameter(value = 1)
	public Expression expectation;

	@Test
	public void shouldParseLambdaExpression() throws IOException {
		// when
		final LambdaExpression resultExpression = analyzer.analyzeExpression(expression);
		// then
		LOGGER.info("Result: {}", resultExpression);
		assertThat(resultExpression.getExpression()).isEqualTo(expectation);
	}

}
