package org.lambdamatic.analyzer;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.lambdamatic.FilterExpression;
import org.lambdamatic.LambdaExpression;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.InfixExpression;
import org.lambdamatic.analyzer.ast.node.InfixExpression.InfixOperator;
import org.lambdamatic.analyzer.ast.node.LocalVariable;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.lambdamatic.analyzer.ast.node.NumberLiteral;
import org.lambdamatic.analyzer.ast.node.StringLiteral;
import org.lambdamatic.testutils.TestWatcher;

import com.sample.model.TestPojo;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
@Ignore
public class LambdaBytecodeAnalyzerTest {

	private final LambdaExpressionAnalyzer analyzer = new LambdaExpressionAnalyzer();

	
	@Rule
	public TestWatcher watcher = new TestWatcher();

	@Test
	public void shouldParseLambdaExpressionWithStringLiteralConstant() throws IOException {
		// given
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getStringValue().equals("foo"));
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getStringValueMethod = new MethodInvocation(testPojo, "getStringValue", String.class);
		final MethodInvocation expectedExpression = new MethodInvocation(getStringValueMethod, "equals", Boolean.class,  new StringLiteral("foo"));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionAsVariableWithStringLiteralConstant() throws IOException {
		// given
		// when
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getStringValue().equals("foo");
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getStringValueMethod = new MethodInvocation(testPojo, "getStringValue", String.class);
		final MethodInvocation expectedExpression = new MethodInvocation(getStringValueMethod, "equals", Boolean.class,  new StringLiteral("foo"));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionWithStringLiteralParameter() throws IOException {
		// given
		final String value = "foo";
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getStringValue().equals(value));
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getStringValueMethod = new MethodInvocation(testPojo, "getStringValue", String.class);
		final MethodInvocation expectedExpression = new MethodInvocation(getStringValueMethod, "equals", Boolean.class,  new StringLiteral("foo"));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionAsVariableWithStringLiteralParameter() throws IOException {
		// given
		final String value = "foo";
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getStringValue().equals(value);
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getStringValueMethod = new MethodInvocation(testPojo, "getStringValue", String.class);
		final MethodInvocation expectedExpression = new MethodInvocation(getStringValueMethod, "equals", Boolean.class,  new StringLiteral("foo"));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionWithCapturedArgumentParameter() throws IOException {
		// given
		final TestPojo anotherPojo = new TestPojo();
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getStringValue().equals(
				anotherPojo.getStringValue()));
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getStringValueMethod = new MethodInvocation(testPojo, "getStringValue", String.class);
		final MethodInvocation expectedExpression = new MethodInvocation(getStringValueMethod, "equals", Boolean.class,  new StringLiteral("foo"));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionAsVariableWithCapturedArgumentParameter() throws IOException {
		// given
		final TestPojo anotherPojo = new TestPojo();
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getStringValue().equals(anotherPojo.getStringValue());
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getStringValueMethod = new MethodInvocation(testPojo, "getStringValue", String.class);
		final MethodInvocation expectedExpression = new MethodInvocation(getStringValueMethod, "equals", Boolean.class,  new StringLiteral("foo"));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionWithPrimitiveIntGreaterThanNumberConstantValue() throws IOException {
		// given
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getPrimitiveIntValue() > 42);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getPrimitiveIntValue", int.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.GREATER, getIntValueMethod, new NumberLiteral(42));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionWithPrimitiveIntAsVariableGreaterThanNumberConstantValue() throws IOException {
		// given
		final int value = 42;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getPrimitiveIntValue() > value);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getPrimitiveIntValue", int.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.GREATER, getIntValueMethod, new NumberLiteral(42));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionAsVariableWithPrimitiveIntEqualsNumberConstantValue() throws IOException {
		// given
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getPrimitiveIntValue() == 42;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getPrimitiveIntValue", int.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.EQUALS, getIntValueMethod, new NumberLiteral(42));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionAsVariableWithPrimitiveIntAsVariableEqualsNumberConstantValue() throws IOException {
		// given
		final int value = 42;
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getPrimitiveIntValue() == value;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getPrimitiveIntValue", int.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.EQUALS, getIntValueMethod, new NumberLiteral(42));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionAsVariableWithPrimitiveIntNotEqualsNumberConstantValue() throws IOException {
		// given
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getPrimitiveIntValue() != 42;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getPrimitiveIntValue", int.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.NOT_EQUALS, getIntValueMethod, new NumberLiteral(42));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionAsVariableWithPrimitiveIntAsVariableNotEqualsNumberConstantValue() throws IOException {
		// given
		final int value = 42;
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getPrimitiveIntValue() != value;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getPrimitiveIntValue", int.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.NOT_EQUALS, getIntValueMethod, new NumberLiteral(42));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionAsVariableWithPrimitiveIntGreaterThanNumberConstantValue() throws IOException {
		// given
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getPrimitiveIntValue() > 42;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getPrimitiveIntValue", int.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.GREATER, getIntValueMethod, new NumberLiteral(42));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionAsVariableWithPrimitiveIntAsVariableGreaterThanNumberConstantValue() throws IOException {
		// given
		final int value = 42;
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getPrimitiveIntValue() > value;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getPrimitiveIntValue", int.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.GREATER, getIntValueMethod, new NumberLiteral(42));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionWithPrimitiveIntGreaterOrEqualThanNumberConstantValue() throws IOException {
		// given
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getPrimitiveIntValue() >= 42);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getPrimitiveIntValue", int.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.GREATER_EQUALS, getIntValueMethod,
				new NumberLiteral(42));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionWithPrimitiveIntAsVariableGreaterOrEqualThanNumberConstantValue() throws IOException {
		// given
		final int value = 42;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getPrimitiveIntValue() >= value);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getPrimitiveIntValue", int.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.GREATER_EQUALS, getIntValueMethod,
				new NumberLiteral(42));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionAsVariableWithPrimitiveIntGreaterOrEqualThanNumberConstantValue() throws IOException {
		// given
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getPrimitiveIntValue() >= 42;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getPrimitiveIntValue", int.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.GREATER_EQUALS, getIntValueMethod,
				new NumberLiteral(42));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionAsVariableWithPrimitiveIntAsVariableGreaterOrEqualThanNumberConstantValue() throws IOException {
		// given
		final int value = 42;
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getPrimitiveIntValue() >= value;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getPrimitiveIntValue", int.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.GREATER_EQUALS, getIntValueMethod,
				new NumberLiteral(42));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionWithPrimitiveIntLessThanNumberConstantValue() throws IOException {
		// given
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getPrimitiveIntValue() < 42);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getPrimitiveIntValue", int.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.LESS, getIntValueMethod, new NumberLiteral(42));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionWithPrimitiveIntAsVariableLessThanNumberConstantValue() throws IOException {
		// given
		final int value = 42;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getPrimitiveIntValue() < value);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getPrimitiveIntValue", int.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.LESS, getIntValueMethod, new NumberLiteral(42));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionAsVariableWithPrimitiveIntLessThanNumberConstantValue() throws IOException {
		// given
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getPrimitiveIntValue() < 42;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getPrimitiveIntValue", int.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.LESS, getIntValueMethod, new NumberLiteral(42));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionAsVariableWithPrimitiveIntAsVariableLessThanNumberConstantValue() throws IOException {
		// given
		final int value = 42;
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getPrimitiveIntValue() < value;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getPrimitiveIntValue", int.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.LESS, getIntValueMethod, new NumberLiteral(42));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionWithPrimitiveIntLessOrEqualThanNumberConstantValue() throws IOException {
		// given
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getPrimitiveIntValue() <= 42);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getPrimitiveIntValue", int.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.LESS_EQUALS, getIntValueMethod, new NumberLiteral(42));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionWithPrimitiveIntAsVariableLessOrEqualThanNumberConstantValue() throws IOException {
		// given
		final int value = 42;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getPrimitiveIntValue() <= value);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getPrimitiveIntValue", int.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.LESS_EQUALS, getIntValueMethod, new NumberLiteral(42));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionAsVariableWithPrimitiveIntLessOrEqualThanNumberConstantValue() throws IOException {
		// given
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getPrimitiveIntValue() <= 42;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getPrimitiveIntValue", int.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.LESS_EQUALS, getIntValueMethod, new NumberLiteral(42));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionAsVariableWithPrimitiveIntAsVariableLessOrEqualThanNumberConstantValue() throws IOException {
		// given
		final int value = 42;
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getPrimitiveIntValue() <= value;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getPrimitiveIntValue", int.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.LESS_EQUALS, getIntValueMethod, new NumberLiteral(42));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionWithLongGreaterThanNumberConstantValue() throws IOException {
		// given
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getLongValue() > 42L);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getLongValue", Long.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.GREATER, getIntValueMethod, new NumberLiteral(42L));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionWithLongGreaterThanNumberAsPrimitiveVariable() throws IOException {
		// given
		final long value = 42L;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getLongValue() > value);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getLongValue", Long.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.GREATER, getIntValueMethod, new NumberLiteral(42L));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionWithLongGreaterThanNumberAsLongVariable() throws IOException {
		// given
		final Long value = new Long(42L);
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getLongValue() > value);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getLongValue", Long.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.GREATER, getIntValueMethod, new NumberLiteral(42L));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionAsVariableWithLongEqualsNumberConstantValue() throws IOException {
		// given
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getLongValue() == 42L;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getLongValue", Long.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.EQUALS, getIntValueMethod, new NumberLiteral(42L));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionAsVariableWithLongEqualsNumberAsVariable() throws IOException {
		// given
		final Long value = new Long(42L);
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getLongValue() == value;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getLongValue", Long.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.EQUALS, getIntValueMethod, new NumberLiteral(42L));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionAsVariableWithLongNotEqualsNumberConstantValue() throws IOException {
		// given
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getLongValue() != 42L;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getLongValue", Long.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.NOT_EQUALS, getIntValueMethod, new NumberLiteral(42L));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionAsVariableWithLongNotEqualsNumberAsVariable() throws IOException {
		// given
		final long value = 42L; 
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getLongValue() != value;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getLongValue", Long.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.NOT_EQUALS, getIntValueMethod, new NumberLiteral(42L));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionAsVariableWithLongGreaterThanNumberConstantValue() throws IOException {
		// given
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getLongValue() > 42L;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getLongValue", Long.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.GREATER, getIntValueMethod, new NumberLiteral(42L));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionAsVariableWithLongGreaterThanNumberAsVariable() throws IOException {
		// given
		final long value = 42L; 
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getLongValue() > value;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getLongValue", Long.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.GREATER, getIntValueMethod, new NumberLiteral(42L));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionWithLongGreaterOrEqualThanNumberConstantValue() throws IOException {
		// given
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getLongValue() >= 42L);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getLongValue", Long.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.GREATER_EQUALS, getIntValueMethod,
				new NumberLiteral(42L));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionWithLongGreaterOrEqualThanNumberAsVariable() throws IOException {
		// given
		final Long value = new Long(42L);
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getLongValue() >= value);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getLongValue", Long.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.GREATER_EQUALS, getIntValueMethod,
				new NumberLiteral(42L));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionAsVariableWithLongGreaterOrEqualThanNumberConstantValue() throws IOException {
		// given
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getLongValue() >= 42L;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getLongValue", Long.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.GREATER_EQUALS, getIntValueMethod,
				new NumberLiteral(42L));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionAsVariableWithLongGreaterOrEqualThanNumberAsVariable() throws IOException {
		// given
		final Long value = new Long(42L); 
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getLongValue() >= value;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getLongValue", Long.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.GREATER_EQUALS, getIntValueMethod,
				new NumberLiteral(42L));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionWithLongLessThanNumberConstantValue() throws IOException {
		// given
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getLongValue() < 42L);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getLongValue", Long.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.LESS, getIntValueMethod, new NumberLiteral(42L));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionWithLongLessThanNumberAsVariable() throws IOException {
		// given
		final long value = 42L; 
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getLongValue() < value);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getLongValue", Long.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.LESS, getIntValueMethod, new NumberLiteral(42L));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionAsVariableWithLongLessThanNumberConstantValue() throws IOException {
		// given
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getLongValue() < 42L;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getLongValue", Long.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.LESS, getIntValueMethod, new NumberLiteral(42L));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionAsVariableWithLongLessThanNumberAsVariable() throws IOException {
		// given
		final Long value = new Long(42L); 
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getLongValue() < value;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getLongValue", Long.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.LESS, getIntValueMethod, new NumberLiteral(42L));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionWithLongLessOrEqualThanNumberConstantValue() throws IOException {
		// given
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getLongValue() <= 42L);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getLongValue", Long.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.LESS_EQUALS, getIntValueMethod, new NumberLiteral(42L));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionWithLongLessOrEqualThanNumberAsPrimitiveVariable() throws IOException {
		// given
		final long value = 42L; 
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> t.getLongValue() <= value);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getLongValue", Long.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.LESS_EQUALS, getIntValueMethod, new NumberLiteral(42L));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionAsVariableWithLongLessOrEqualThanNumberConstantValue() throws IOException {
		// given
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getLongValue() <= 42L;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getLongValue", Long.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.LESS_EQUALS, getIntValueMethod, new NumberLiteral(42L));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionAsVariableWithLongLessOrEqualThanNumberAsPrimitiveVariable() throws IOException {
		// given
		final long value = 42L; 
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.getLongValue() <= value;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getIntValueMethod = new MethodInvocation(testPojo, "getLongValue", Long.class);
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.LESS_EQUALS, getIntValueMethod, new NumberLiteral(42L));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionWithMultipleStringLiteralConstantComparisons() throws IOException {
		// given
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression((TestPojo t) -> (t.getStringValue().equals("foo")
				|| t.getStringValue().equals("bar") || t.getStringValue().equals("baz")));
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getStringValueMethod = new MethodInvocation(testPojo, "getStringValue", String.class);
		final MethodInvocation ifEqualsFooExpression = new MethodInvocation(getStringValueMethod, "equals", Boolean.class,  new StringLiteral("foo"));
		final MethodInvocation ifEqualsBarExpression = new MethodInvocation(getStringValueMethod, "equals", Boolean.class,  new StringLiteral("bar"));
		final MethodInvocation ifEqualsBazExpression = new MethodInvocation(getStringValueMethod, "equals", Boolean.class,  new StringLiteral("baz"));
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.CONDITIONAL_OR, ifEqualsFooExpression,
				ifEqualsBarExpression, ifEqualsBazExpression);
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionAsVariableWithMultipleStringLiteralConstantComparisons() throws IOException {
		// given
		final FilterExpression<TestPojo> expression = (TestPojo t) -> (t.getStringValue().equals("foo") || t.getStringValue().equals("bar") || t
				.getStringValue().equals("baz"));
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getStringValueMethod = new MethodInvocation(testPojo, "getStringValue", String.class);
		final MethodInvocation ifEqualsFooExpression = new MethodInvocation(getStringValueMethod, "equals", Boolean.class,  new StringLiteral("foo"));
		final MethodInvocation ifEqualsBarExpression = new MethodInvocation(getStringValueMethod, "equals", Boolean.class,  new StringLiteral("bar"));
		final MethodInvocation ifEqualsBazExpression = new MethodInvocation(getStringValueMethod, "equals", Boolean.class,  new StringLiteral("baz"));
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.CONDITIONAL_OR, ifEqualsFooExpression,
				ifEqualsBarExpression, ifEqualsBazExpression);
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionBuiltFromInvokeSpecialMethod() throws IOException {
		// given
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(buildLambdaExpression("foo"));
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getStringValueMethod = new MethodInvocation(testPojo, "getStringValue", String.class);
		final MethodInvocation expectedExpression = new MethodInvocation(getStringValueMethod, "equals", Boolean.class,  new StringLiteral("foo"));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionAsVariableBuiltFromInvokeSpecialMethod() throws IOException {
		// given
		// when
		final FilterExpression<TestPojo> expression = buildLambdaExpression("foo");
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getStringValueMethod = new MethodInvocation(testPojo, "getStringValue", String.class);
		final MethodInvocation expectedExpression = new MethodInvocation(getStringValueMethod, "equals", Boolean.class,  new StringLiteral("foo"));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
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
		// given
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(staticBuildLambdaExpression("foo"));
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getStringValueMethod = new MethodInvocation(testPojo, "getStringValue", String.class);
		final MethodInvocation expectedExpression = new MethodInvocation(getStringValueMethod, "equals", Boolean.class,  new StringLiteral("foo"));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionAsVariableBuiltFromInvokeStaticMethod() throws IOException {
		// given
		// when
		final FilterExpression<TestPojo> expression = staticBuildLambdaExpression("foo");
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getStringValueMethod = new MethodInvocation(testPojo, "getStringValue", String.class);
		final MethodInvocation expectedExpression = new MethodInvocation(getStringValueMethod, "equals", Boolean.class,  new StringLiteral("foo"));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
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

	@Test
	public void shouldParseMethodCallOnFieldAccess() throws IOException {
		// given
		// when
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.field.equals("foo");
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation expectedExpression = new MethodInvocation(new FieldAccess(testPojo, "field"), "equals", Boolean.class, 
				new StringLiteral("foo"));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionWithSingleMethodOrInfixAndOnTwoMethods() throws IOException {
		// given
		// when
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.field.equals("baz")
				|| (t.field.equals("foo") && t.field.equals("bar"));
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation equalsBazMethod = new MethodInvocation(new FieldAccess(testPojo, "field"), "equals", Boolean.class,  new StringLiteral(
				"baz"));
		final MethodInvocation equalsFooMethod = new MethodInvocation(new FieldAccess(testPojo, "field"), "equals", Boolean.class,  new StringLiteral(
				"foo"));
		final MethodInvocation equalsBarMethod = new MethodInvocation(new FieldAccess(testPojo, "field"), "equals", Boolean.class,  new StringLiteral(
				"bar"));
		final Expression expectedExpression = new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsBazMethod, new InfixExpression(
				InfixOperator.CONDITIONAL_AND, equalsFooMethod, equalsBarMethod));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionWithInfixAndOnTwoMethodsOrSingleMethod() throws IOException {
		// given
		final FilterExpression<TestPojo> expression = (TestPojo t) -> (t.field.equals("foo") && t.field.equals("bar"))
				|| t.field.equals("baz");
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation equalsFooMethod = new MethodInvocation(new FieldAccess(testPojo, "field"), "equals", Boolean.class,  new StringLiteral(
				"foo"));
		final MethodInvocation equalsBarMethod = new MethodInvocation(new FieldAccess(testPojo, "field"), "equals", Boolean.class,  new StringLiteral(
				"bar"));
		final MethodInvocation equalsBazMethod = new MethodInvocation(new FieldAccess(testPojo, "field"), "equals", Boolean.class,  new StringLiteral(
				"baz"));
		final Expression expectedExpression = new InfixExpression(InfixOperator.CONDITIONAL_OR, new InfixExpression(
				InfixOperator.CONDITIONAL_AND, equalsFooMethod, equalsBarMethod), equalsBazMethod);
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldParseLambdaExpressionWithInfixAndOnTwoNotEqualsMethodsOrSingleMethod() throws IOException {
		// given
		final FilterExpression<TestPojo> expression = (TestPojo t) -> (!t.field.equals("foo") && !t.field.equals("bar"))
				|| t.field.equals("baz");
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation equalsFooMethod = new MethodInvocation(new FieldAccess(testPojo, "field"), "equals", Boolean.class,  new StringLiteral(
				"foo")).inverse();
		final MethodInvocation equalsBarMethod = new MethodInvocation(new FieldAccess(testPojo, "field"), "equals", Boolean.class,  new StringLiteral(
				"bar")).inverse();
		final MethodInvocation equalsBazMethod = new MethodInvocation(new FieldAccess(testPojo, "field"), "equals", Boolean.class,  new StringLiteral(
				"baz"));
		final Expression expectedExpression = new InfixExpression(InfixOperator.CONDITIONAL_OR, new InfixExpression(
				InfixOperator.CONDITIONAL_AND, equalsFooMethod, equalsBarMethod), equalsBazMethod);
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionWithInfixAndOnTwoMethodsOrSingleNotEqualsMethod() throws IOException {
		// given
		final FilterExpression<TestPojo> expression = (TestPojo t) -> (t.equals("foo") && t.equals("bar"))
				|| !t.equals("baz");
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation equalsFooMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("foo"));
		final MethodInvocation equalsBarMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("bar"));
		final MethodInvocation equalsBazMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("baz")).inverse();
		final Expression expectedExpression = new InfixExpression(InfixOperator.CONDITIONAL_OR, new InfixExpression(
				InfixOperator.CONDITIONAL_AND, equalsFooMethod, equalsBarMethod), equalsBazMethod);
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionWithInfixAndOnTwoSameMethods() throws IOException {
		// given
		final FilterExpression<TestPojo> expression = (TestPojo t) -> (t.equals("foo") && t.equals("foo"));
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation equalsFooMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("foo"));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(equalsFooMethod);
	}
	
	@Test
	public void shouldParseLambdaExpressionWithStringEqualsValueOperand() throws IOException {
		// given
		final FilterExpression<TestPojo> expression = (TestPojo t) -> (t.field == "foo");
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final FieldAccess fieldAccess = new FieldAccess(testPojo, "field");
		final StringLiteral fooConstant = new StringLiteral("foo");
		final Expression expectedExpression = new InfixExpression(InfixOperator.EQUALS, fieldAccess, fooConstant);
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldParseLambdaExpressionWithStringNotEqualsValueOperand() throws IOException {
		// given
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.field != "foo";
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final FieldAccess fieldAccess = new FieldAccess(testPojo, "field");
		final StringLiteral fooConstant = new StringLiteral("foo");
		final Expression expectedExpression = new InfixExpression(InfixOperator.NOT_EQUALS, fieldAccess, fooConstant);
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldRetainOrderOfMembersInExpression() throws IOException {
		// given
		final FilterExpression<TestPojo> expression = ((TestPojo t) -> t.stringValue.equals("foo") || t.primitiveIntValue == 42);
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation stringValueEqualsFoo = new MethodInvocation(new FieldAccess(testPojo, "stringValue"), "equals", Boolean.class, 
				new StringLiteral("foo"));
		final MethodInvocation primitiveIntValueEquals42 = new MethodInvocation(new FieldAccess(testPojo, "primitiveIntValue"),
				"equals", Boolean.class, new NumberLiteral(42));
		final Expression expectedExpression = new InfixExpression(InfixOperator.CONDITIONAL_OR, stringValueEqualsFoo, primitiveIntValueEquals42);
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(expectedExpression);
	}

	public void shouldVerifyWithBoolean() {
		Assert.fail("Not implemented yet...");
	}
	public void shouldVerifyWithFloat() {
		Assert.fail("Not implemented yet...");
	}
	public void shouldVerifyWithDouble() {
		Assert.fail("Not implemented yet...");
	}
	public void shouldVerifyWithCharacter() {
		Assert.fail("Not implemented yet...");
	}
	public void shouldVerifyWithDate() {
		Assert.fail("Not implemented yet...");
	}
}

