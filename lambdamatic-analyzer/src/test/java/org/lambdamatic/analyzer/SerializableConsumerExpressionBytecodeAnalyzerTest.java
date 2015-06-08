package org.lambdamatic.analyzer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lambdamatic.testutils.JavaMethods.ArrayUtil_toArray;
import static org.lambdamatic.testutils.JavaMethods.Object_equals;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_elementMatch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.lambdamatic.SerializableConsumer;
import org.lambdamatic.analyzer.ast.node.ArrayVariable;
import org.lambdamatic.analyzer.ast.node.Assignment;
import org.lambdamatic.analyzer.ast.node.ClassLiteral;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.ExpressionStatement;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.LambdaExpression;
import org.lambdamatic.analyzer.ast.node.LocalVariable;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.lambdamatic.analyzer.ast.node.NumberLiteral;
import org.lambdamatic.analyzer.ast.node.Operation;
import org.lambdamatic.analyzer.ast.node.ReturnStatement;
import org.lambdamatic.analyzer.ast.node.Statement;
import org.lambdamatic.analyzer.ast.node.StringLiteral;
import org.lambdamatic.analyzer.ast.node.Operation.Operator;
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
public class SerializableConsumerExpressionBytecodeAnalyzerTest {

	private final LambdaExpressionAnalyzer analyzer = LambdaExpressionAnalyzer.getInstance();

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SerializableConsumerExpressionBytecodeAnalyzerTest.class);

	@Rule
	public TestWatcher watcher = new TestWatcher();

	@Parameters(name = "[{index}] expect {1}")
	public static Object[][] data() throws NoSuchMethodException, SecurityException {
		final String foo = "foo";
		final TestPojo anotherPojo = new TestPojo();
		final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
		final FieldAccess testPojo_dot_stringValue = new FieldAccess(testPojo, "stringValue");
		final FieldAccess testPojo_dot_primitiveIntValue = new FieldAccess(testPojo, "primitiveIntValue");
		final FieldAccess testPojo_dot_field = new FieldAccess(testPojo, "field");
		final FieldAccess testPojo_dot_dateValue = new FieldAccess(testPojo, "dateValue");
		final FieldAccess testPojo_dot_elementList = new FieldAccess(testPojo, "elementList");
		final FieldAccess e_dot_field = new FieldAccess(new LocalVariable(0, "e", TestPojo.class), "field");
		final LambdaExpression e_dot_field_equals_foo = new LambdaExpression(
				new ReturnStatement(new MethodInvocation(e_dot_field, Object_equals, new StringLiteral("foo"))),
				TestPojo.class, "e");

		return new Object[][] {
				new Object[] {
						(SerializableConsumer<TestPojo>) ((TestPojo t) -> ArrayUtil.toArray(t.stringValue,
								t.dateValue)),
				new MethodInvocation(new ClassLiteral(ArrayUtil.class), ArrayUtil_toArray,
						new ArrayVariable(Object[].class, new FieldAccess(testPojo, "stringValue"),
								new FieldAccess(testPojo, "dateValue"))) },
				new Object[] { (SerializableConsumer<TestPojo>) ((TestPojo t) -> ArrayUtil.toArray(t.field)),
						new MethodInvocation(new ClassLiteral(ArrayUtil.class), ArrayUtil_toArray,
								new ArrayVariable(Object[].class, testPojo_dot_field)) },
				new Object[] {
						(SerializableConsumer<TestPojo>) ((TestPojo t) -> ArrayUtil.toArray(t.field,
								t.dateValue, t.elementList)),
						new MethodInvocation(new ClassLiteral(ArrayUtil.class), ArrayUtil_toArray,
								new ArrayVariable(Object[].class, testPojo_dot_field, testPojo_dot_dateValue,
										testPojo_dot_elementList)) },
				// Verify nested Lambda expression
				new Object[] {
						(SerializableConsumer<TestPojo>) ((TestPojo t) -> t
								.elementMatch(e -> e.field.equals("foo"))),
						new MethodInvocation(testPojo, TestPojo_elementMatch, e_dot_field_equals_foo) },
				new Object[] {
						(SerializableConsumer<TestPojo>) ((TestPojo t) -> t
								.elementMatch(e -> e.field.equals(foo))),
						new MethodInvocation(testPojo, TestPojo_elementMatch, e_dot_field_equals_foo) },
				new Object[] {
						(SerializableConsumer<TestPojo>) ((TestPojo t) -> t
								.elementMatch(e -> e.field.equals(anotherPojo.getStringValue()))),
						new MethodInvocation(testPojo, TestPojo_elementMatch, e_dot_field_equals_foo) },
				new Object[] {
						(SerializableConsumer<TestPojo>) ((TestPojo t) -> t
								.elementMatch(e -> e.field.equals(anotherPojo.stringValue))),
						new MethodInvocation(testPojo, TestPojo_elementMatch, e_dot_field_equals_foo) },
				// verify assignation statement(s)
				new Object[] {
						(SerializableConsumer<TestPojo>) ((TestPojo t) -> { t.stringValue = "foo"; }),
						new Assignment(testPojo_dot_stringValue, new StringLiteral("foo"))},
				new Object[] {
						(SerializableConsumer<TestPojo>) ((TestPojo t) -> { t.stringValue = "foo"; t.field = "baz"; }),
						Arrays.array(new Assignment(testPojo_dot_stringValue, new StringLiteral("foo")),
								new Assignment(testPojo_dot_field, new StringLiteral("baz")))},
				new Object[] {
						(SerializableConsumer<TestPojo>) ((TestPojo t) -> { t.primitiveIntValue++; }),
						new Assignment(testPojo_dot_primitiveIntValue, new Operation(Operator.ADD, testPojo_dot_primitiveIntValue, new NumberLiteral(1)))},
				
				new Object[] {
						(SerializableConsumer<TestPojo>) ((TestPojo t) -> { t.primitiveIntValue+=2; }),
						new Assignment(testPojo_dot_primitiveIntValue, new Operation(Operator.ADD, testPojo_dot_primitiveIntValue, new NumberLiteral(2)))},
				new Object[] {
						(SerializableConsumer<TestPojo>) ((TestPojo t) -> { t.primitiveIntValue=t.primitiveIntValue+3; }),
						new Assignment(testPojo_dot_primitiveIntValue, new Operation(Operator.ADD, testPojo_dot_primitiveIntValue, new NumberLiteral(3)))},
				new Object[] {
						(SerializableConsumer<TestPojo>) ((TestPojo t) -> { t.primitiveIntValue--; }),
						new Assignment(testPojo_dot_primitiveIntValue, new Operation(Operator.SUBTRACT, testPojo_dot_primitiveIntValue, new NumberLiteral(1)))},
				
				new Object[] {
						(SerializableConsumer<TestPojo>) ((TestPojo t) -> { t.primitiveIntValue-=2; }),
						new Assignment(testPojo_dot_primitiveIntValue, new Operation(Operator.SUBTRACT, testPojo_dot_primitiveIntValue, new NumberLiteral(2)))},
				new Object[] {
						(SerializableConsumer<TestPojo>) ((TestPojo t) -> { t.primitiveIntValue=t.primitiveIntValue-3; }),
						new Assignment(testPojo_dot_primitiveIntValue, new Operation(Operator.SUBTRACT, testPojo_dot_primitiveIntValue, new NumberLiteral(3)))},
				new Object[] {
						(SerializableConsumer<TestPojo>) ((TestPojo t) -> { t.primitiveIntValue*=2; }),
						new Assignment(testPojo_dot_primitiveIntValue, new Operation(Operator.MULTIPLY, testPojo_dot_primitiveIntValue, new NumberLiteral(2)))},
				new Object[] {
						(SerializableConsumer<TestPojo>) ((TestPojo t) -> { t.primitiveIntValue=t.primitiveIntValue*3; }),
						new Assignment(testPojo_dot_primitiveIntValue, new Operation(Operator.MULTIPLY, testPojo_dot_primitiveIntValue, new NumberLiteral(3)))},
				new Object[] {
						(SerializableConsumer<TestPojo>) ((TestPojo t) -> { t.primitiveIntValue/=2; }),
						new Assignment(testPojo_dot_primitiveIntValue, new Operation(Operator.DIVIDE, testPojo_dot_primitiveIntValue, new NumberLiteral(2)))},
				new Object[] {
						(SerializableConsumer<TestPojo>) ((TestPojo t) -> { t.primitiveIntValue=t.primitiveIntValue/3; }),
						new Assignment(testPojo_dot_primitiveIntValue, new Operation(Operator.DIVIDE, testPojo_dot_primitiveIntValue, new NumberLiteral(3)))},
				

		};
	}

	@Parameter(value = 0)
	public SerializableConsumer<TestPojo> expression;

	@Parameter(value = 1)
	public Object expectation;

	@SuppressWarnings("unchecked")
	@Test
	public void shouldParseLambdaExpression() throws IOException {
		// when
		final LambdaExpression resultExpression = analyzer.analyzeExpression(expression);
		// then
		LOGGER.info("Result: {}", resultExpression);
		if(expectation instanceof Expression) {
			final Expression expectationExpression = (Expression) expectation;
			assertThat(resultExpression.getBody()).containsExactly(new ExpressionStatement(expectationExpression));
		} else if (expectation instanceof List) {
			final List<Statement> expectationStatements = new ArrayList<>();
			for(Expression expression : (List<Expression>)expectation) {
				expectationStatements.add(new ExpressionStatement(expression));
			}
			assertThat(resultExpression.getBody()).containsExactly(expectationStatements.toArray(new Statement[0]));
		}
		
	}

}
