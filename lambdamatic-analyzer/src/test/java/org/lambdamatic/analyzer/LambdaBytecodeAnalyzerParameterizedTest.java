package org.lambdamatic.analyzer;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.lambdamatic.FilterExpression;
import org.lambdamatic.LambdaExpression;
import org.lambdamatic.analyzer.ast.node.BooleanLiteral;
import org.lambdamatic.analyzer.ast.node.CharacterLiteral;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.InfixExpression;
import org.lambdamatic.analyzer.ast.node.InfixExpression.InfixOperator;
import org.lambdamatic.analyzer.ast.node.LocalVariable;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.lambdamatic.analyzer.ast.node.NullLiteral;
import org.lambdamatic.analyzer.ast.node.NumberLiteral;
import org.lambdamatic.analyzer.ast.node.StringLiteral;
import org.lambdamatic.testutils.TestWatcher;

import com.sample.model.TestPojo;

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

	@Parameters(name = "[{index}] {1}")
	public static Object[][] data() {
		final TestPojo anotherPojo = new TestPojo();
		@SuppressWarnings("unused") // looks like a bug in Eclipse, this variable is actually used.
		final boolean booleanValue_true = true;
		final byte byteValue_0 = (byte) 0;
		final byte byteValue_1 = (byte) 1;
		final byte byteValue_42 = (byte) 42;
		final short shortValue_0 = (short) 0;
		final short shortValue_1 = (short) 1;
		final short shortValue_42 = (short) 42;
		final int intValue_0 = 0;
		final int intValue_1 = 1;
		final int intValue_42 = 42;
		final long longValue_0 = 0L;
		final long longValue_1 = 1L;
		final long longValue_42 = 42L;
		final float floatValue_0 = 0f;
		final float floatValue_1 = 1f;
		final float floatValue_42 = 42.0f;
		final double doubleValue_0 = 0d;
		final double doubleValue_1 = 1d;
		final double doubleValue_42 = 42d;
		final char charValue_a = 'a';
		final char charValue_z = 'z';
		final String stringValue_bar = "bar";
		final String stringValue_null = null;
		final LocalVariable var_t = new LocalVariable("t", TestPojo.class);
		final MethodInvocation t_dot_getPrimitiveBooleanValue = new MethodInvocation(var_t, "getPrimitiveBooleanValue",
				int.class);
		final MethodInvocation t_dot_getBooleanValue = new MethodInvocation(var_t, "getBooleanValue", Boolean.class);
		final MethodInvocation t_dot_getPrimitiveByteValue = new MethodInvocation(var_t, "getPrimitiveByteValue",
				byte.class);
		final MethodInvocation t_dot_getByteValue = new MethodInvocation(var_t, "getByteValue", Byte.class);
		final MethodInvocation t_dot_getPrimitiveShortValue = new MethodInvocation(var_t, "getPrimitiveShortValue",
				short.class);
		final MethodInvocation t_dot_getShortValue = new MethodInvocation(var_t, "getShortValue", Short.class);
		final MethodInvocation t_dot_getPrimitiveIntValue = new MethodInvocation(var_t, "getPrimitiveIntValue",
				int.class);
		final MethodInvocation t_dot_getIntegerValue = new MethodInvocation(var_t, "getIntegerValue", Integer.class);
		final MethodInvocation t_dot_getPrimitiveLongValue = new MethodInvocation(var_t, "getPrimitiveLongValue",
				long.class);
		final MethodInvocation t_dot_getLongValue = new MethodInvocation(var_t, "getLongValue", Long.class);
		final MethodInvocation t_dot_getPrimitiveCharValue = new MethodInvocation(var_t, "getPrimitiveCharValue",
				char.class);
		final MethodInvocation t_dot_getCharacterValue = new MethodInvocation(var_t, "getCharacterValue",
				Character.class);
		final MethodInvocation t_dot_getPrimitiveFloatValue = new MethodInvocation(var_t, "getPrimitiveFloatValue",
				float.class);
		final MethodInvocation t_dot_getFloatValue = new MethodInvocation(var_t, "getFloatValue", Float.class);
		final MethodInvocation t_dot_getPrimitiveDoubleValue = new MethodInvocation(var_t, "getPrimitiveDoubleValue",
				double.class);
		final MethodInvocation t_dot_getDoubleValue = new MethodInvocation(var_t, "getDoubleValue", Double.class);
		final MethodInvocation t_dot_getStringValue = new MethodInvocation(var_t, "getStringValue", String.class);
		final MethodInvocation t_dot_getStringValue_dot_equals_foo = new MethodInvocation(t_dot_getStringValue,
				"equals", Boolean.class, new StringLiteral("foo"));
		final MethodInvocation t_dot_getStringValue_dot_equals_bar = new MethodInvocation(t_dot_getStringValue,
				"equals", Boolean.class, new StringLiteral("bar"));
		final MethodInvocation t_dot_getStringValue_dot_equals_baz = new MethodInvocation(t_dot_getStringValue,
				"equals", Boolean.class, new StringLiteral("baz"));
		final FieldAccess t_dot_field = new FieldAccess(var_t, "field");
		final MethodInvocation t_dot_field_dot_equals_foo = new MethodInvocation(t_dot_field, "equals", Boolean.class,
				new StringLiteral("foo"));
		final MethodInvocation t_dot_field_dot_equals_bar = new MethodInvocation(t_dot_field, "equals", Boolean.class,
				new StringLiteral("bar"));
		final MethodInvocation t_dot_field_dot_equals_baz = new MethodInvocation(t_dot_field, "equals", Boolean.class,
				new StringLiteral("baz"));
		final InfixExpression t_dot_field_equals_foo = new InfixExpression(InfixOperator.EQUALS, t_dot_field,
				new StringLiteral("foo"));
		final InfixExpression t_dot_field_not_equals_foo = new InfixExpression(InfixOperator.NOT_EQUALS, t_dot_field,
				new StringLiteral("foo"));
		return new Object[][] {
				// primitive boolean (comparisons are pretty straightforward in
				// the bytecode)
				// in any case, the Lambda expression analysis returns an InfixExpression rather than a simple MethodInvocation
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveBooleanValue()),
						t_dot_getPrimitiveBooleanValue },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> !t.getPrimitiveBooleanValue()),
						t_dot_getPrimitiveBooleanValue.inverse() },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveBooleanValue() == true),
						t_dot_getPrimitiveBooleanValue },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveBooleanValue() != booleanValue_true),
						t_dot_getPrimitiveBooleanValue.inverse() },
				// java.lang.Boolean (comparisons are pretty straightforward in
				// the bytecode)
				// in any case, the Lambda expression analysis returns an InfixExpression rather than a simple MethodInvocation
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getBooleanValue()),
						t_dot_getBooleanValue },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> !t.getBooleanValue()),
						t_dot_getBooleanValue.inverse() },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getBooleanValue() == Boolean.TRUE),
						t_dot_getBooleanValue },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getBooleanValue() != booleanValue_true),
						t_dot_getBooleanValue.inverse() },

				// primitive byte
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveByteValue() > (byte) 0),
						new InfixExpression(InfixOperator.GREATER, t_dot_getPrimitiveByteValue, new NumberLiteral((byte) 0)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveByteValue() < byteValue_0),
						new InfixExpression(InfixOperator.LESS, t_dot_getPrimitiveByteValue, new NumberLiteral((byte) 0)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveByteValue() >= (byte) 1),
						new InfixExpression(InfixOperator.GREATER_EQUALS, t_dot_getPrimitiveByteValue,
								new NumberLiteral(1)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveByteValue() <= byteValue_1),
						new InfixExpression(InfixOperator.LESS_EQUALS, t_dot_getPrimitiveByteValue, new NumberLiteral(
								intValue_1)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveByteValue() == (byte) 42),
						new InfixExpression(InfixOperator.EQUALS, t_dot_getPrimitiveByteValue, new NumberLiteral(42)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveByteValue() != byteValue_42),
						new InfixExpression(InfixOperator.NOT_EQUALS, t_dot_getPrimitiveByteValue, new NumberLiteral(
								intValue_42)) },
				// java.lang.Byte
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getByteValue() > (byte) 0),
						new InfixExpression(InfixOperator.GREATER, t_dot_getByteValue, new NumberLiteral((byte) 0)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getByteValue() < byteValue_0),
						new InfixExpression(InfixOperator.LESS, t_dot_getByteValue, new NumberLiteral((byte) 0)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getByteValue() >= (byte) 1),
						new InfixExpression(InfixOperator.GREATER_EQUALS, t_dot_getByteValue, new NumberLiteral(1)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getByteValue() <= byteValue_1),
						new InfixExpression(InfixOperator.LESS_EQUALS, t_dot_getByteValue,
								new NumberLiteral(intValue_1)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getByteValue() == (byte) 42),
						new InfixExpression(InfixOperator.EQUALS, t_dot_getByteValue, new NumberLiteral(42)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getByteValue() != byteValue_42),
						new InfixExpression(InfixOperator.NOT_EQUALS, t_dot_getByteValue,
								new NumberLiteral(intValue_42)) },

				// primitive short (comparing with NumberLiterals with int
				// values - see bytecode instructions)
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveShortValue() > (short) 0),
						new InfixExpression(InfixOperator.GREATER, t_dot_getPrimitiveShortValue, new NumberLiteral((short)0)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveShortValue() < shortValue_0),
						new InfixExpression(InfixOperator.LESS, t_dot_getPrimitiveShortValue, new NumberLiteral((short)0)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveShortValue() >= (short) 1),
						new InfixExpression(InfixOperator.GREATER_EQUALS, t_dot_getPrimitiveShortValue,
								new NumberLiteral(1)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveShortValue() <= shortValue_1),
						new InfixExpression(InfixOperator.LESS_EQUALS, t_dot_getPrimitiveShortValue, new NumberLiteral(
								intValue_1)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveShortValue() == (short) 42),
						new InfixExpression(InfixOperator.EQUALS, t_dot_getPrimitiveShortValue, new NumberLiteral(42)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveShortValue() != shortValue_42),
						new InfixExpression(InfixOperator.NOT_EQUALS, t_dot_getPrimitiveShortValue, new NumberLiteral(
								intValue_42)) },

				// java.lang.Short (comparing with NumberLiterals with int
				// values - see bytecode instructions)
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getShortValue() > (short) 0),
						new InfixExpression(InfixOperator.GREATER, t_dot_getShortValue, new NumberLiteral((short)0)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getShortValue() < shortValue_0),
						new InfixExpression(InfixOperator.LESS, t_dot_getShortValue, new NumberLiteral((short)0)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getShortValue() >= 1),
						new InfixExpression(InfixOperator.GREATER_EQUALS, t_dot_getShortValue, new NumberLiteral(1)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getShortValue() <= shortValue_1),
						new InfixExpression(InfixOperator.LESS_EQUALS, t_dot_getShortValue, new NumberLiteral(
								intValue_1)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getShortValue() == 42),
						new InfixExpression(InfixOperator.EQUALS, t_dot_getShortValue, new NumberLiteral(42)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getShortValue() != intValue_42),
						new InfixExpression(InfixOperator.NOT_EQUALS, t_dot_getShortValue, new NumberLiteral(
								intValue_42)) },
				// primitive char
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveCharValue() > charValue_a),
						new InfixExpression(InfixOperator.GREATER, t_dot_getPrimitiveCharValue, new CharacterLiteral(
								charValue_a)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveCharValue() < charValue_a),
						new InfixExpression(InfixOperator.LESS, t_dot_getPrimitiveCharValue, new CharacterLiteral(
								charValue_a)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveCharValue() >= charValue_a),
						new InfixExpression(InfixOperator.GREATER_EQUALS, t_dot_getPrimitiveCharValue,
								new CharacterLiteral(charValue_a)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveCharValue() <= charValue_a),
						new InfixExpression(InfixOperator.LESS_EQUALS, t_dot_getPrimitiveCharValue,
								new CharacterLiteral(charValue_a)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveCharValue() == charValue_z),
						new InfixExpression(InfixOperator.EQUALS, t_dot_getPrimitiveCharValue, new CharacterLiteral(
								charValue_z)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveCharValue() != charValue_z),
						new InfixExpression(InfixOperator.NOT_EQUALS, t_dot_getPrimitiveCharValue,
								new CharacterLiteral(charValue_z)) },
				// java.lang.Character
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getCharacterValue() > charValue_a),
						new InfixExpression(InfixOperator.GREATER, t_dot_getCharacterValue, new CharacterLiteral(
								charValue_a)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getCharacterValue() < charValue_a),
						new InfixExpression(InfixOperator.LESS, t_dot_getCharacterValue, new CharacterLiteral(
								charValue_a)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getCharacterValue() >= charValue_a),
						new InfixExpression(InfixOperator.GREATER_EQUALS, t_dot_getCharacterValue,
								new CharacterLiteral(charValue_a)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getCharacterValue() <= charValue_a),
						new InfixExpression(InfixOperator.LESS_EQUALS, t_dot_getCharacterValue, new CharacterLiteral(
								charValue_a)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getCharacterValue() == charValue_z),
						new InfixExpression(InfixOperator.EQUALS, t_dot_getCharacterValue, new CharacterLiteral(
								charValue_z)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getCharacterValue() != charValue_z),
						new InfixExpression(InfixOperator.NOT_EQUALS, t_dot_getCharacterValue, new CharacterLiteral(
								charValue_z)) },

				// primitive int
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveIntValue() > 0),
						new InfixExpression(InfixOperator.GREATER, t_dot_getPrimitiveIntValue, new NumberLiteral(0)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveIntValue() < intValue_0),
						new InfixExpression(InfixOperator.LESS, t_dot_getPrimitiveIntValue, new NumberLiteral(0)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveIntValue() >= 1),
						new InfixExpression(InfixOperator.GREATER_EQUALS, t_dot_getPrimitiveIntValue,
								new NumberLiteral(1)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveIntValue() <= intValue_1),
						new InfixExpression(InfixOperator.LESS_EQUALS, t_dot_getPrimitiveIntValue, new NumberLiteral(
								intValue_1)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveIntValue() == 42),
						new InfixExpression(InfixOperator.EQUALS, t_dot_getPrimitiveIntValue, new NumberLiteral(42)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveIntValue() != intValue_42),
						new InfixExpression(InfixOperator.NOT_EQUALS, t_dot_getPrimitiveIntValue, new NumberLiteral(
								intValue_42)) },
				// java.lang.Integer
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getIntegerValue() > 0),
						new InfixExpression(InfixOperator.GREATER, t_dot_getIntegerValue, new NumberLiteral(0)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getIntegerValue() < intValue_0),
						new InfixExpression(InfixOperator.LESS, t_dot_getIntegerValue, new NumberLiteral(0)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getIntegerValue() >= 1),
						new InfixExpression(InfixOperator.GREATER_EQUALS, t_dot_getIntegerValue, new NumberLiteral(1)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getIntegerValue() <= intValue_1),
						new InfixExpression(InfixOperator.LESS_EQUALS, t_dot_getIntegerValue, new NumberLiteral(
								intValue_1)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getIntegerValue() == 42),
						new InfixExpression(InfixOperator.EQUALS, t_dot_getIntegerValue, new NumberLiteral(42)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getIntegerValue() != intValue_42),
						new InfixExpression(InfixOperator.NOT_EQUALS, t_dot_getIntegerValue, new NumberLiteral(
								intValue_42)) },
				// primitive long
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveLongValue() > 0L),
						new InfixExpression(InfixOperator.GREATER, t_dot_getPrimitiveLongValue, new NumberLiteral(0L)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveLongValue() < longValue_0),
						new InfixExpression(InfixOperator.LESS, t_dot_getPrimitiveLongValue, new NumberLiteral(0L)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveLongValue() >= 1L),
						new InfixExpression(InfixOperator.GREATER_EQUALS, t_dot_getPrimitiveLongValue,
								new NumberLiteral(1L)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveLongValue() <= longValue_1),
						new InfixExpression(InfixOperator.LESS_EQUALS, t_dot_getPrimitiveLongValue, new NumberLiteral(
								longValue_1)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveLongValue() == 42L),
						new InfixExpression(InfixOperator.EQUALS, t_dot_getPrimitiveLongValue, new NumberLiteral(42L)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveLongValue() != longValue_42),
						new InfixExpression(InfixOperator.NOT_EQUALS, t_dot_getPrimitiveLongValue, new NumberLiteral(
								longValue_42)) },
				// java.lang.Long
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getLongValue() > 0L),
						new InfixExpression(InfixOperator.GREATER, t_dot_getLongValue, new NumberLiteral(0L)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getLongValue() < longValue_0),
						new InfixExpression(InfixOperator.LESS, t_dot_getLongValue, new NumberLiteral(0L)) },
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
				// primitive float
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveFloatValue() > 0f),
						new InfixExpression(InfixOperator.GREATER, t_dot_getPrimitiveFloatValue, new NumberLiteral(0f)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveFloatValue() < floatValue_0),
						new InfixExpression(InfixOperator.LESS, t_dot_getPrimitiveFloatValue, new NumberLiteral(0f)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveFloatValue() >= 1f),
						new InfixExpression(InfixOperator.GREATER_EQUALS, t_dot_getPrimitiveFloatValue,
								new NumberLiteral(1f)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveFloatValue() <= floatValue_1),
						new InfixExpression(InfixOperator.LESS_EQUALS, t_dot_getPrimitiveFloatValue, new NumberLiteral(
								floatValue_1)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveFloatValue() == 42f),
						new InfixExpression(InfixOperator.EQUALS, t_dot_getPrimitiveFloatValue, new NumberLiteral(42f)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveFloatValue() != floatValue_42),
						new InfixExpression(InfixOperator.NOT_EQUALS, t_dot_getPrimitiveFloatValue, new NumberLiteral(
								floatValue_42)) },
				// java.lang.Float
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getFloatValue() > 0f),
						new InfixExpression(InfixOperator.GREATER, t_dot_getFloatValue, new NumberLiteral(0f)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getFloatValue() < floatValue_0),
						new InfixExpression(InfixOperator.LESS, t_dot_getFloatValue, new NumberLiteral(0f)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getFloatValue() >= 1f),
						new InfixExpression(InfixOperator.GREATER_EQUALS, t_dot_getFloatValue, new NumberLiteral(1f)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getFloatValue() <= floatValue_1),
						new InfixExpression(InfixOperator.LESS_EQUALS, t_dot_getFloatValue, new NumberLiteral(
								floatValue_1)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getFloatValue() == 42f),
						new InfixExpression(InfixOperator.EQUALS, t_dot_getFloatValue, new NumberLiteral(42f)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getFloatValue() != floatValue_42),
						new InfixExpression(InfixOperator.NOT_EQUALS, t_dot_getFloatValue, new NumberLiteral(
								floatValue_42)) },

				// primitive double
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveDoubleValue() > 0d),
						new InfixExpression(InfixOperator.GREATER, t_dot_getPrimitiveDoubleValue, new NumberLiteral(0d)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveDoubleValue() < doubleValue_0),
						new InfixExpression(InfixOperator.LESS, t_dot_getPrimitiveDoubleValue, new NumberLiteral(0d)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveDoubleValue() >= 1f),
						new InfixExpression(InfixOperator.GREATER_EQUALS, t_dot_getPrimitiveDoubleValue,
								new NumberLiteral(1d)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveDoubleValue() <= doubleValue_1),
						new InfixExpression(InfixOperator.LESS_EQUALS, t_dot_getPrimitiveDoubleValue,
								new NumberLiteral(doubleValue_1)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveDoubleValue() == 42d),
						new InfixExpression(InfixOperator.EQUALS, t_dot_getPrimitiveDoubleValue, new NumberLiteral(42d)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getPrimitiveDoubleValue() != doubleValue_42),
						new InfixExpression(InfixOperator.NOT_EQUALS, t_dot_getPrimitiveDoubleValue, new NumberLiteral(
								doubleValue_42)) },
				// java.lang.Double
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getDoubleValue() > 0d),
						new InfixExpression(InfixOperator.GREATER, t_dot_getDoubleValue, new NumberLiteral(0d)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getDoubleValue() < doubleValue_0),
						new InfixExpression(InfixOperator.LESS, t_dot_getDoubleValue, new NumberLiteral(0d)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getDoubleValue() >= 1d),
						new InfixExpression(InfixOperator.GREATER_EQUALS, t_dot_getDoubleValue, new NumberLiteral(1d)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getDoubleValue() <= doubleValue_1),
						new InfixExpression(InfixOperator.LESS_EQUALS, t_dot_getDoubleValue, new NumberLiteral(
								doubleValue_1)) },
				new Object[] { (FilterExpression<TestPojo>) ((TestPojo t) -> t.getDoubleValue() == 42d),
						new InfixExpression(InfixOperator.EQUALS, t_dot_getDoubleValue, new NumberLiteral(42d)) },
				new Object[] {
						(FilterExpression<TestPojo>) ((TestPojo t) -> t.getDoubleValue() != doubleValue_42),
						new InfixExpression(InfixOperator.NOT_EQUALS, t_dot_getDoubleValue, new NumberLiteral(
								doubleValue_42)) },

				// java.lang.String
				new Object[] { (FilterExpression<TestPojo>) (t -> t.getStringValue().equals("foo")),
						t_dot_getStringValue_dot_equals_foo },
				new Object[] {
						(FilterExpression<TestPojo>) (t -> t.getStringValue().equals(stringValue_bar)),
						new MethodInvocation(t_dot_getStringValue, "equals", Boolean.class, new StringLiteral(
								stringValue_bar)) },
				new Object[] { (FilterExpression<TestPojo>) (t -> t.getStringValue().equals(null)),
						new MethodInvocation(t_dot_getStringValue, "equals", Boolean.class, new NullLiteral()) },
				new Object[] {
						(FilterExpression<TestPojo>) (t -> !t.getStringValue().equals(stringValue_null)),
						new MethodInvocation(t_dot_getStringValue, "equals", Boolean.class, new NullLiteral())
								.inverse() },
				new Object[] {
						(FilterExpression<TestPojo>) (t -> t.getStringValue().equals(anotherPojo.getStringValue())),
						t_dot_getStringValue_dot_equals_foo },
				new Object[] { new LambdaExpressionFactory().buildLambdaExpression("foo"),
						t_dot_getStringValue_dot_equals_foo },
				new Object[] { LambdaExpressionFactory.staticBuildLambdaExpression("foo"),
						t_dot_getStringValue_dot_equals_foo },
				// java.lang.Date

				// Enumeration

				// mixes with multiple operands
				new Object[] {
						(FilterExpression<TestPojo>) (t -> t.getStringValue().equals("foo")
								|| t.getStringValue().equals("bar") || t.getStringValue().equals("baz")),
						new InfixExpression(InfixOperator.CONDITIONAL_OR, t_dot_getStringValue_dot_equals_foo,
								t_dot_getStringValue_dot_equals_bar, t_dot_getStringValue_dot_equals_baz) },
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
								InfixOperator.CONDITIONAL_AND, t_dot_field_dot_equals_foo, t_dot_field_dot_equals_bar),
								t_dot_field_dot_equals_baz.inverse()) },
				new Object[] { (FilterExpression<TestPojo>) (t -> t.field.equals("foo") && t.field.equals("foo")),
						t_dot_field_dot_equals_foo },
				new Object[] { (FilterExpression<TestPojo>) (t -> t.field == "foo"), t_dot_field_equals_foo },
				new Object[] { (FilterExpression<TestPojo>) (t -> t.field != "foo"), t_dot_field_not_equals_foo },

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
