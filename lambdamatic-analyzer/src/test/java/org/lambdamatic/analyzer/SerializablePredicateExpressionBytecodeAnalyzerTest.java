/*******************************************************************************
 * Copyright (c) 2015 Red Hat.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Red Hat - Initial Contribution
 *******************************************************************************/
package org.lambdamatic.analyzer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lambdamatic.testutils.JavaMethods.Character_valueOf;
import static org.lambdamatic.testutils.JavaMethods.List_size;
import static org.lambdamatic.testutils.JavaMethods.Object_equals;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getBooleanValue;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getByteValue;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getCharacterValue;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getDoubleValue;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getFloatValue;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getIntegerValue;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getLongValue;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getPrimitiveBooleanValue;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getPrimitiveByteValue;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getPrimitiveCharValue;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getPrimitiveDoubleValue;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getPrimitiveFloatValue;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getPrimitiveIntValue;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getPrimitiveLongValue;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getPrimitiveShortValue;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getShortValue;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getStringValue;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_matches_String;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_matches_TestPojo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.lambdamatic.SerializablePredicate;
import org.lambdamatic.analyzer.ast.node.ArrayVariable;
import org.lambdamatic.analyzer.ast.node.CharacterLiteral;
import org.lambdamatic.analyzer.ast.node.ClassLiteral;
import org.lambdamatic.analyzer.ast.node.EnumLiteral;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.CompoundExpression;
import org.lambdamatic.analyzer.ast.node.CompoundExpression.CompoundExpressionOperator;
import org.lambdamatic.analyzer.ast.node.LambdaExpression;
import org.lambdamatic.analyzer.ast.node.LocalVariable;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.lambdamatic.analyzer.ast.node.NullLiteral;
import org.lambdamatic.analyzer.ast.node.NumberLiteral;
import org.lambdamatic.analyzer.ast.node.ObjectInstance;
import org.lambdamatic.analyzer.ast.node.ObjectInstanciation;
import org.lambdamatic.analyzer.ast.node.ReturnStatement;
import org.lambdamatic.analyzer.ast.node.StringLiteral;
import org.lambdamatic.testutils.TestWatcher;

import com.sample.model.EnumPojo;
import com.sample.model.TestPojo;

/**
 * Parameterized tests with many use cases of comparison.
 * 
 * @author Xavier Coulon
 */
@RunWith(Parameterized.class)
public class SerializablePredicateExpressionBytecodeAnalyzerTest {

  private final LambdaExpressionAnalyzer analyzer = LambdaExpressionAnalyzer.getInstance();

  @Rule
  public TestWatcher watcher = new TestWatcher();

  /**
   * Utility method that makes the JUnit parameters declaration much more readable.
   * 
   * @param lambdaExpression the {@link FunctionalInterface} to encode
   * @param expression the expected result
   * @return an array containing the 2 arguments.
   */
  private static Object[] match(final SerializablePredicate<TestPojo> lambdaExpression,
      final Expression expression) {
    return new Object[] {lambdaExpression, expression};
  }

  /**
   * Builds the dataset to use for the tests.
   * @return the dataset to use.
   */
  @Parameters(name = "[{index}] {1}")
  public static Object[][] data() {
    final TestPojo anotherPojo = new TestPojo();
    @SuppressWarnings("unused")
    // looks like a bug in Eclipse, this variable is actually used.
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
    final LocalVariable var_t = new LocalVariable(0, "t", TestPojo.class);
    final LocalVariable var_test = new LocalVariable(0, "test", TestPojo.class);
    final MethodInvocation t_dot_getPrimitiveBooleanValue =
        new MethodInvocation(var_t, TestPojo_getPrimitiveBooleanValue);
    final MethodInvocation t_dot_getBooleanValue =
        new MethodInvocation(var_t, TestPojo_getBooleanValue);
    final MethodInvocation t_dot_getPrimitiveByteValue =
        new MethodInvocation(var_t, TestPojo_getPrimitiveByteValue);
    final MethodInvocation t_dot_getByteValue = new MethodInvocation(var_t, TestPojo_getByteValue);
    final MethodInvocation t_dot_getPrimitiveShortValue =
        new MethodInvocation(var_t, TestPojo_getPrimitiveShortValue);
    final MethodInvocation t_dot_getShortValue =
        new MethodInvocation(var_t, TestPojo_getShortValue);
    final MethodInvocation t_dot_getPrimitiveIntValue =
        new MethodInvocation(var_t, TestPojo_getPrimitiveIntValue);
    final MethodInvocation t_dot_getIntegerValue =
        new MethodInvocation(var_t, TestPojo_getIntegerValue);
    final MethodInvocation t_dot_getPrimitiveLongValue =
        new MethodInvocation(var_t, TestPojo_getPrimitiveLongValue);
    final MethodInvocation t_dot_getLongValue = new MethodInvocation(var_t, TestPojo_getLongValue);
    final MethodInvocation t_dot_getPrimitiveCharValue =
        new MethodInvocation(var_t, TestPojo_getPrimitiveCharValue);
    final MethodInvocation t_dot_getCharacterValue =
        new MethodInvocation(var_t, TestPojo_getCharacterValue);
    final MethodInvocation t_dot_getPrimitiveFloatValue =
        new MethodInvocation(var_t, TestPojo_getPrimitiveFloatValue);
    final MethodInvocation t_dot_getFloatValue =
        new MethodInvocation(var_t, TestPojo_getFloatValue);
    final MethodInvocation t_dot_getPrimitiveDoubleValue =
        new MethodInvocation(var_t, TestPojo_getPrimitiveDoubleValue);
    final MethodInvocation t_dot_getDoubleValue =
        new MethodInvocation(var_t, TestPojo_getDoubleValue);
    final FieldAccess t_dot_stringValue = new FieldAccess(var_t, "stringValue");
    final MethodInvocation t_dot_getStringValue =
        new MethodInvocation(var_t, TestPojo_getStringValue);
    final MethodInvocation test_dot_getStringValue =
        new MethodInvocation(var_test, TestPojo_getStringValue);
    final MethodInvocation t_dot_stringValue_dot_equals_foo =
        new MethodInvocation(t_dot_stringValue, Object_equals, new StringLiteral("foo"));
    final MethodInvocation t_dot_getStringValue_dot_equals_foo =
        new MethodInvocation(t_dot_getStringValue, Object_equals, new StringLiteral("foo"));
    final MethodInvocation test_dot_getStringValue_dot_equals_captured_argument_foo =
        new MethodInvocation(test_dot_getStringValue, Object_equals, new StringLiteral("foo"));
    final MethodInvocation t_dot_getStringValue_dot_equals_bar =
        new MethodInvocation(t_dot_getStringValue, Object_equals, new StringLiteral("bar"));
    final MethodInvocation t_dot_getStringValue_dot_equals_baz =
        new MethodInvocation(t_dot_getStringValue, Object_equals, new StringLiteral("baz"));
    final FieldAccess t_dot_field = new FieldAccess(var_t, "field");
    final MethodInvocation t_dot_field_dot_equals_foo =
        new MethodInvocation(t_dot_field, Object_equals, new StringLiteral("foo"));
    final MethodInvocation t_dot_field_dot_equals_bar =
        new MethodInvocation(t_dot_field, Object_equals, new StringLiteral("bar"));
    final MethodInvocation t_dot_field_dot_equals_baz =
        new MethodInvocation(t_dot_field, Object_equals, new StringLiteral("baz"));
    final CompoundExpression t_dot_field_equals_foo = new CompoundExpression(
        CompoundExpressionOperator.EQUALS, t_dot_field, new StringLiteral("foo"));
    final CompoundExpression t_dot_field_not_equals_foo = new CompoundExpression(
        CompoundExpressionOperator.NOT_EQUALS, t_dot_field, new StringLiteral("foo"));
    final FieldAccess t_dot_enumPojo = new FieldAccess(var_t, "enumPojo");
    final CompoundExpression t_dot_enumPojo_equals_foo = new CompoundExpression(
        CompoundExpressionOperator.EQUALS, t_dot_enumPojo, new EnumLiteral(EnumPojo.FOO));
    final CompoundExpression t_dot_enumPojo_not_equals_foo = new CompoundExpression(
        CompoundExpressionOperator.NOT_EQUALS, t_dot_enumPojo, new EnumLiteral(EnumPojo.FOO));
    final MethodInvocation t_dot_enumPojo_dot_equals_foo =
        new MethodInvocation(t_dot_enumPojo, Object_equals, new EnumLiteral(EnumPojo.FOO));
    final FieldAccess t_dot_primitiveIntValue = new FieldAccess(var_t, "primitiveIntValue");
    final CompoundExpression t_dot_primitiveIntValue_equals_42 = new CompoundExpression(
        CompoundExpressionOperator.EQUALS, t_dot_primitiveIntValue, new NumberLiteral(42));

    final FieldAccess t_elementList = new FieldAccess(var_t, "elementList");
    final MethodInvocation t_elements_dot_size = new MethodInvocation(t_elementList, List_size);
    final CompoundExpression t_elements_dot_size_equals_0 = new CompoundExpression(
        CompoundExpressionOperator.EQUALS, t_elements_dot_size, new NumberLiteral(0));
    final List<Object> otherElements = new ArrayList<>();
    final MethodInvocation t_elementList_dot_equals_otherElements =
        new MethodInvocation(t_elementList, Object_equals, new ObjectInstance(otherElements));
    final MethodInvocation t_dot_equals_newTestPojo =
        new MethodInvocation(var_t, Object_equals, new ObjectInstanciation(TestPojo.class));
    final ObjectInstanciation testPojoVariable = new ObjectInstanciation(TestPojo.class);
    testPojoVariable
        .setInitArguments(Arrays.asList(new StringLiteral("foo"), new NumberLiteral(42)));
    final MethodInvocation t_dot_equals_newTestPojo_foo42 =
        new MethodInvocation(var_t, Object_equals, testPojoVariable);
    final TestPojo otherTestPojo = new TestPojo();
    final MethodInvocation t_dot_equals_otherTestPojo =
        new MethodInvocation(var_t, Object_equals, new ObjectInstance(otherTestPojo));

    final String[] arrayOfStrings = new String[] {new String("foo"), new String("bar")};
    final StringLiteral[] arrayOfStringLiterals =
        new StringLiteral[] {new StringLiteral("foo"), new StringLiteral("bar")};
    final TestPojo[] arrayOfTestPojos = new TestPojo[] {new TestPojo(), new TestPojo()};
    final ObjectInstance[] arrayOfTestPojoVariables = new ObjectInstance[] {
        new ObjectInstance(new TestPojo()), new ObjectInstance(new TestPojo())};

    return new Object[][] {
        // primitive boolean (comparisons are pretty straightforward in
        // the bytecode)
        // in any case, the Lambda expression analysis returns an
        // CompoundExpression rather than a simple MethodInvocation
        match(t -> t.getPrimitiveBooleanValue(), t_dot_getPrimitiveBooleanValue),
        match(t -> !t.getPrimitiveBooleanValue(), t_dot_getPrimitiveBooleanValue.inverse()),
        match(t -> t.getPrimitiveBooleanValue() == true, t_dot_getPrimitiveBooleanValue),
        match(t -> t.getPrimitiveBooleanValue() != booleanValue_true,
            t_dot_getPrimitiveBooleanValue.inverse()),
        // java.lang.Boolean (comparisons are pretty straightforward in
        // the bytecode)
        // in any case, the Lambda expression analysis returns an
        // CompoundExpression rather than a simple MethodInvocation
        match(t -> t.getBooleanValue(), t_dot_getBooleanValue),
        match(t -> !t.getBooleanValue(), t_dot_getBooleanValue.inverse()),
        match(t -> t.getBooleanValue() == Boolean.TRUE, t_dot_getBooleanValue),
        match(t -> t.getBooleanValue() != booleanValue_true, t_dot_getBooleanValue.inverse()),

        // primitive byte
        match(t -> t.getPrimitiveByteValue() > (byte) 0,
            new CompoundExpression(CompoundExpressionOperator.GREATER, t_dot_getPrimitiveByteValue,
                new NumberLiteral((byte) 0))),
        match(t -> t.getPrimitiveByteValue() < byteValue_0,
            new CompoundExpression(CompoundExpressionOperator.LESS, t_dot_getPrimitiveByteValue,
                new NumberLiteral((byte) 0))),
        match(t -> t.getPrimitiveByteValue() >= (byte) 1,
            new CompoundExpression(CompoundExpressionOperator.GREATER_EQUALS,
                t_dot_getPrimitiveByteValue, new NumberLiteral(1))),
        match(t -> t.getPrimitiveByteValue() <= byteValue_1,
            new CompoundExpression(CompoundExpressionOperator.LESS_EQUALS,
                t_dot_getPrimitiveByteValue, new NumberLiteral(intValue_1))),
        match(t -> t.getPrimitiveByteValue() == (byte) 42,
            new CompoundExpression(CompoundExpressionOperator.EQUALS, t_dot_getPrimitiveByteValue,
                new NumberLiteral(42))),
        match(t -> t.getPrimitiveByteValue() != byteValue_42,
            new CompoundExpression(CompoundExpressionOperator.NOT_EQUALS,
                t_dot_getPrimitiveByteValue, new NumberLiteral(intValue_42))),

        // java.lang.Byte
        match(t -> t.getByteValue() > (byte) 0,
            new CompoundExpression(CompoundExpressionOperator.GREATER, t_dot_getByteValue,
                new NumberLiteral((byte) 0))),
        match(t -> t.getByteValue() < byteValue_0,
            new CompoundExpression(CompoundExpressionOperator.LESS, t_dot_getByteValue,
                new NumberLiteral((byte) 0))),
        match(t -> t.getByteValue() >= (byte) 1,
            new CompoundExpression(CompoundExpressionOperator.GREATER_EQUALS, t_dot_getByteValue,
                new NumberLiteral(1))),
        match(t -> t.getByteValue() <= byteValue_1,
            new CompoundExpression(CompoundExpressionOperator.LESS_EQUALS, t_dot_getByteValue,
                new NumberLiteral(intValue_1))),
        match(t -> t.getByteValue() == (byte) 42,
            new CompoundExpression(CompoundExpressionOperator.EQUALS, t_dot_getByteValue,
                new NumberLiteral(42))),
        match(t -> t.getByteValue() != byteValue_42,
            new CompoundExpression(CompoundExpressionOperator.NOT_EQUALS, t_dot_getByteValue,
                new NumberLiteral(intValue_42))),

        // primitive short (comparing with NumberLiterals with int
        // values - see bytecode instructions)
        match(t -> t.getPrimitiveShortValue() > (short) 0,
            new CompoundExpression(CompoundExpressionOperator.GREATER, t_dot_getPrimitiveShortValue,
                new NumberLiteral((short) 0))),
        match(t -> t.getPrimitiveShortValue() < shortValue_0,
            new CompoundExpression(CompoundExpressionOperator.LESS, t_dot_getPrimitiveShortValue,
                new NumberLiteral((short) 0))),
        match(t -> t.getPrimitiveShortValue() >= (short) 1,
            new CompoundExpression(CompoundExpressionOperator.GREATER_EQUALS,
                t_dot_getPrimitiveShortValue, new NumberLiteral(1))),
        match(t -> t.getPrimitiveShortValue() <= shortValue_1,
            new CompoundExpression(CompoundExpressionOperator.LESS_EQUALS,
                t_dot_getPrimitiveShortValue, new NumberLiteral(intValue_1))),
        match(t -> t.getPrimitiveShortValue() == (short) 42,
            new CompoundExpression(CompoundExpressionOperator.EQUALS, t_dot_getPrimitiveShortValue,
                new NumberLiteral(42))),
        match(t -> t.getPrimitiveShortValue() != shortValue_42,
            new CompoundExpression(CompoundExpressionOperator.NOT_EQUALS,
                t_dot_getPrimitiveShortValue, new NumberLiteral(intValue_42))),
        // java.lang.Short (comparing with NumberLiterals with int
        // values - see bytecode instructions)
        match(t -> t.getShortValue() > (short) 0,
            new CompoundExpression(CompoundExpressionOperator.GREATER, t_dot_getShortValue,
                new NumberLiteral((short) 0))),
        match(t -> t.getShortValue() < shortValue_0,
            new CompoundExpression(CompoundExpressionOperator.LESS, t_dot_getShortValue,
                new NumberLiteral((short) 0))),
        match(t -> t.getShortValue() >= 1,
            new CompoundExpression(CompoundExpressionOperator.GREATER_EQUALS, t_dot_getShortValue,
                new NumberLiteral(1))),
        match(t -> t.getShortValue() <= shortValue_1,
            new CompoundExpression(CompoundExpressionOperator.LESS_EQUALS, t_dot_getShortValue,
                new NumberLiteral(intValue_1))),
        match(t -> t.getShortValue() == 42,
            new CompoundExpression(CompoundExpressionOperator.EQUALS, t_dot_getShortValue,
                new NumberLiteral(42))),
        match(t -> t.getShortValue() != intValue_42,
            new CompoundExpression(CompoundExpressionOperator.NOT_EQUALS, t_dot_getShortValue,
                new NumberLiteral(intValue_42))),
        // primitive char
        match(t -> t.getPrimitiveCharValue() > charValue_a,
            new CompoundExpression(CompoundExpressionOperator.GREATER, t_dot_getPrimitiveCharValue,
                new CharacterLiteral(charValue_a))),
        match(t -> t.getPrimitiveCharValue() < charValue_a,
            new CompoundExpression(CompoundExpressionOperator.LESS, t_dot_getPrimitiveCharValue,
                new CharacterLiteral(charValue_a))),
        match(t -> t.getPrimitiveCharValue() >= charValue_a,
            new CompoundExpression(CompoundExpressionOperator.GREATER_EQUALS,
                t_dot_getPrimitiveCharValue, new CharacterLiteral(charValue_a))),
        match(t -> t.getPrimitiveCharValue() <= charValue_a,
            new CompoundExpression(CompoundExpressionOperator.LESS_EQUALS,
                t_dot_getPrimitiveCharValue, new CharacterLiteral(charValue_a))),
        match(t -> t.getPrimitiveCharValue() == charValue_z,
            new CompoundExpression(CompoundExpressionOperator.EQUALS, t_dot_getPrimitiveCharValue,
                new CharacterLiteral(charValue_z))),
        match(t -> t.getPrimitiveCharValue() != charValue_z,
            new CompoundExpression(CompoundExpressionOperator.NOT_EQUALS,
                t_dot_getPrimitiveCharValue, new CharacterLiteral(charValue_z))),
        // java.lang.Character
        match(t -> t.getCharacterValue() > charValue_a,
            new CompoundExpression(CompoundExpressionOperator.GREATER, t_dot_getCharacterValue,
                new CharacterLiteral(charValue_a))),
        match(t -> t.getCharacterValue() < charValue_a,
            new CompoundExpression(CompoundExpressionOperator.LESS, t_dot_getCharacterValue,
                new CharacterLiteral(charValue_a))),
        match(t -> t.getCharacterValue() >= charValue_a,
            new CompoundExpression(CompoundExpressionOperator.GREATER_EQUALS,
                t_dot_getCharacterValue, new CharacterLiteral(charValue_a))),
        match(t -> t.getCharacterValue() <= charValue_a,
            new CompoundExpression(CompoundExpressionOperator.LESS_EQUALS, t_dot_getCharacterValue,
                new CharacterLiteral(charValue_a))),
        match(t -> t.getCharacterValue().equals(charValue_z),
            new MethodInvocation(new MethodInvocation(var_t, TestPojo_getCharacterValue),
                Object_equals,
                new MethodInvocation(new ClassLiteral(Character.class), Character_valueOf,
                    new CharacterLiteral(charValue_z)))),
        match(t -> t.getCharacterValue() != charValue_z,
            new CompoundExpression(CompoundExpressionOperator.NOT_EQUALS, t_dot_getCharacterValue,
                new CharacterLiteral(charValue_z))),

        // primitive int
        match(t -> t.getPrimitiveIntValue() > 0,
            new CompoundExpression(CompoundExpressionOperator.GREATER, t_dot_getPrimitiveIntValue,
                new NumberLiteral(0))),
        match(t -> t.getPrimitiveIntValue() < intValue_0,
            new CompoundExpression(CompoundExpressionOperator.LESS, t_dot_getPrimitiveIntValue,
                new NumberLiteral(0))),
        match(t -> t.getPrimitiveIntValue() >= 1,
            new CompoundExpression(CompoundExpressionOperator.GREATER_EQUALS,
                t_dot_getPrimitiveIntValue, new NumberLiteral(1))),
        match(t -> t.getPrimitiveIntValue() <= intValue_1,
            new CompoundExpression(CompoundExpressionOperator.LESS_EQUALS,
                t_dot_getPrimitiveIntValue, new NumberLiteral(intValue_1))),
        match(t -> t.getPrimitiveIntValue() == 42,
            new CompoundExpression(CompoundExpressionOperator.EQUALS, t_dot_getPrimitiveIntValue,
                new NumberLiteral(42))),
        match(t -> t.getPrimitiveIntValue() != intValue_42,
            new CompoundExpression(CompoundExpressionOperator.NOT_EQUALS,
                t_dot_getPrimitiveIntValue, new NumberLiteral(intValue_42))),
        // java.lang.Integer
        match(t -> t.getIntegerValue() > 0,
            new CompoundExpression(CompoundExpressionOperator.GREATER, t_dot_getIntegerValue,
                new NumberLiteral(0))),
        match(t -> t.getIntegerValue() < intValue_0,
            new CompoundExpression(CompoundExpressionOperator.LESS, t_dot_getIntegerValue,
                new NumberLiteral(0))),
        match(t -> t.getIntegerValue() >= 1,
            new CompoundExpression(CompoundExpressionOperator.GREATER_EQUALS, t_dot_getIntegerValue,
                new NumberLiteral(1))),
        match(t -> t.getIntegerValue() <= intValue_1,
            new CompoundExpression(CompoundExpressionOperator.LESS_EQUALS, t_dot_getIntegerValue,
                new NumberLiteral(intValue_1))),
        match(t -> t.getIntegerValue() == 42,
            new CompoundExpression(CompoundExpressionOperator.EQUALS, t_dot_getIntegerValue,
                new NumberLiteral(42))),
        match(t -> t.getIntegerValue() != intValue_42,
            new CompoundExpression(CompoundExpressionOperator.NOT_EQUALS, t_dot_getIntegerValue,
                new NumberLiteral(intValue_42))),
        // primitive long
        match(t -> t.getPrimitiveLongValue() > 0L,
            new CompoundExpression(CompoundExpressionOperator.GREATER, t_dot_getPrimitiveLongValue,
                new NumberLiteral(0L))),
        match(t -> t.getPrimitiveLongValue() < longValue_0,
            new CompoundExpression(CompoundExpressionOperator.LESS, t_dot_getPrimitiveLongValue,
                new NumberLiteral(0L))),
        match(t -> t.getPrimitiveLongValue() >= 1L,
            new CompoundExpression(CompoundExpressionOperator.GREATER_EQUALS,
                t_dot_getPrimitiveLongValue, new NumberLiteral(1L))),
        match(t -> t.getPrimitiveLongValue() <= longValue_1,
            new CompoundExpression(CompoundExpressionOperator.LESS_EQUALS,
                t_dot_getPrimitiveLongValue, new NumberLiteral(longValue_1))),
        match(t -> t.getPrimitiveLongValue() == 42L,
            new CompoundExpression(CompoundExpressionOperator.EQUALS, t_dot_getPrimitiveLongValue,
                new NumberLiteral(42L))),
        match(t -> t.getPrimitiveLongValue() != longValue_42,
            new CompoundExpression(CompoundExpressionOperator.NOT_EQUALS,
                t_dot_getPrimitiveLongValue, new NumberLiteral(longValue_42))),
        // java.lang.Long
        match(t -> t.getLongValue() > 0L,
            new CompoundExpression(CompoundExpressionOperator.GREATER, t_dot_getLongValue,
                new NumberLiteral(0L))),
        match(t -> t.getLongValue() < longValue_0,
            new CompoundExpression(CompoundExpressionOperator.LESS, t_dot_getLongValue,
                new NumberLiteral(0L))),
        match(t -> t.getLongValue() >= 1L,
            new CompoundExpression(CompoundExpressionOperator.GREATER_EQUALS, t_dot_getLongValue,
                new NumberLiteral(1L))),
        match(t -> t.getLongValue() <= longValue_1,
            new CompoundExpression(CompoundExpressionOperator.LESS_EQUALS, t_dot_getLongValue,
                new NumberLiteral(longValue_1))),
        match(t -> t.getLongValue() == 42L,
            new CompoundExpression(CompoundExpressionOperator.EQUALS, t_dot_getLongValue,
                new NumberLiteral(42L))),
        match(t -> t.getLongValue() != longValue_42,
            new CompoundExpression(CompoundExpressionOperator.NOT_EQUALS, t_dot_getLongValue,
                new NumberLiteral(longValue_42))),
        // primitive float
        match(t -> t.getPrimitiveFloatValue() > 0f,
            new CompoundExpression(CompoundExpressionOperator.GREATER, t_dot_getPrimitiveFloatValue,
                new NumberLiteral(0f))),
        match(t -> t.getPrimitiveFloatValue() < floatValue_0,
            new CompoundExpression(CompoundExpressionOperator.LESS, t_dot_getPrimitiveFloatValue,
                new NumberLiteral(0f))),
        match(t -> t.getPrimitiveFloatValue() >= 1f,
            new CompoundExpression(CompoundExpressionOperator.GREATER_EQUALS,
                t_dot_getPrimitiveFloatValue, new NumberLiteral(1f))),
        match(t -> t.getPrimitiveFloatValue() <= floatValue_1,
            new CompoundExpression(CompoundExpressionOperator.LESS_EQUALS,
                t_dot_getPrimitiveFloatValue, new NumberLiteral(floatValue_1))),
        match(t -> t.getPrimitiveFloatValue() == 42f,
            new CompoundExpression(CompoundExpressionOperator.EQUALS, t_dot_getPrimitiveFloatValue,
                new NumberLiteral(42f))),
        match(t -> t.getPrimitiveFloatValue() != floatValue_42,
            new CompoundExpression(CompoundExpressionOperator.NOT_EQUALS,
                t_dot_getPrimitiveFloatValue, new NumberLiteral(floatValue_42))),
        // java.lang.Float
        match(t -> t.getFloatValue() > 0f,
            new CompoundExpression(CompoundExpressionOperator.GREATER, t_dot_getFloatValue,
                new NumberLiteral(0f))),
        match(t -> t.getFloatValue() < floatValue_0,
            new CompoundExpression(CompoundExpressionOperator.LESS, t_dot_getFloatValue,
                new NumberLiteral(0f))),
        match(t -> t.getFloatValue() >= 1f,
            new CompoundExpression(CompoundExpressionOperator.GREATER_EQUALS, t_dot_getFloatValue,
                new NumberLiteral(1f))),
        match(t -> t.getFloatValue() <= floatValue_1,
            new CompoundExpression(CompoundExpressionOperator.LESS_EQUALS, t_dot_getFloatValue,
                new NumberLiteral(floatValue_1))),
        match(t -> t.getFloatValue() == 42f,
            new CompoundExpression(CompoundExpressionOperator.EQUALS, t_dot_getFloatValue,
                new NumberLiteral(42f))),
        match(t -> t.getFloatValue() != floatValue_42,
            new CompoundExpression(CompoundExpressionOperator.NOT_EQUALS, t_dot_getFloatValue,
                new NumberLiteral(floatValue_42))),
        // primitive double
        match(t -> t.getPrimitiveDoubleValue() > 0d,
            new CompoundExpression(CompoundExpressionOperator.GREATER,
                t_dot_getPrimitiveDoubleValue, new NumberLiteral(0d))),
        match(t -> t.getPrimitiveDoubleValue() < doubleValue_0,
            new CompoundExpression(CompoundExpressionOperator.LESS, t_dot_getPrimitiveDoubleValue,
                new NumberLiteral(0d))),
        match(t -> t.getPrimitiveDoubleValue() >= 1f,
            new CompoundExpression(CompoundExpressionOperator.GREATER_EQUALS,
                t_dot_getPrimitiveDoubleValue, new NumberLiteral(1d))),
        match(t -> t.getPrimitiveDoubleValue() <= doubleValue_1,
            new CompoundExpression(CompoundExpressionOperator.LESS_EQUALS,
                t_dot_getPrimitiveDoubleValue, new NumberLiteral(doubleValue_1))),
        match(t -> t.getPrimitiveDoubleValue() == 42d,
            new CompoundExpression(CompoundExpressionOperator.EQUALS, t_dot_getPrimitiveDoubleValue,
                new NumberLiteral(42d))),
        match(t -> t.getPrimitiveDoubleValue() != doubleValue_42,
            new CompoundExpression(CompoundExpressionOperator.NOT_EQUALS,
                t_dot_getPrimitiveDoubleValue, new NumberLiteral(doubleValue_42))),
        // java.lang.Double
        match(t -> t.getDoubleValue() > 0d,
            new CompoundExpression(CompoundExpressionOperator.GREATER, t_dot_getDoubleValue,
                new NumberLiteral(0d))),
        match(t -> t.getDoubleValue() < doubleValue_0,
            new CompoundExpression(CompoundExpressionOperator.LESS, t_dot_getDoubleValue,
                new NumberLiteral(0d))),
        match(t -> t.getDoubleValue() >= 1d,
            new CompoundExpression(CompoundExpressionOperator.GREATER_EQUALS, t_dot_getDoubleValue,
                new NumberLiteral(1d))),
        match(t -> t.getDoubleValue() <= doubleValue_1,
            new CompoundExpression(CompoundExpressionOperator.LESS_EQUALS, t_dot_getDoubleValue,
                new NumberLiteral(doubleValue_1))),
        match(t -> t.getDoubleValue() == 42d,
            new CompoundExpression(CompoundExpressionOperator.EQUALS, t_dot_getDoubleValue,
                new NumberLiteral(42d))),
        match(t -> t.getDoubleValue() != doubleValue_42,
            new CompoundExpression(CompoundExpressionOperator.NOT_EQUALS, t_dot_getDoubleValue,
                new NumberLiteral(doubleValue_42))),
        // java.lang.String
        match(t -> t.field == "foo", t_dot_field_equals_foo),
        match(t -> t.field != "foo", t_dot_field_equals_foo.inverse()),
        match(t -> t.stringValue.equals("foo"), t_dot_stringValue_dot_equals_foo),
        match(t -> !t.stringValue.equals("foo"),
            t_dot_stringValue_dot_equals_foo
                .inverse()),
        match(t -> t.getStringValue().equals("foo"), t_dot_getStringValue_dot_equals_foo),
        match(t -> !t.getStringValue().equals("foo"),
            t_dot_getStringValue_dot_equals_foo.inverse()),
        match(t -> t.getStringValue().equals(stringValue_bar),
            new MethodInvocation(t_dot_getStringValue, Object_equals,
                new StringLiteral(stringValue_bar))),
        match(t -> t.getStringValue().equals(null),
            new MethodInvocation(t_dot_getStringValue, Object_equals, new NullLiteral())),
        match(t -> !t.getStringValue().equals(stringValue_null),
            new MethodInvocation(t_dot_getStringValue, Object_equals, new NullLiteral()).inverse()),
        match(t -> t.field != "foo", t_dot_field_not_equals_foo),
        match(t -> t.getStringValue().equals(anotherPojo.getStringValue()),
            t_dot_getStringValue_dot_equals_foo),
        match(new SerializablePredicateFactory().buildLambdaExpression("foo"),
            test_dot_getStringValue_dot_equals_captured_argument_foo),
        match(SerializablePredicateFactory.staticBuildLambdaExpression("foo"),
            test_dot_getStringValue_dot_equals_captured_argument_foo),
        // Enumeration
        match(t -> t.enumPojo == EnumPojo.FOO, t_dot_enumPojo_equals_foo),
        match(t -> t.enumPojo != EnumPojo.FOO, t_dot_enumPojo_not_equals_foo),
        match(t -> t.enumPojo.equals(EnumPojo.FOO), t_dot_enumPojo_dot_equals_foo),
        match(t -> !t.enumPojo.equals(EnumPojo.FOO), t_dot_enumPojo_dot_equals_foo.inverse()),
        // List (and interface, btw)
        match(t -> t.elementList.size() == 0, t_elements_dot_size_equals_0),
        // Captured argument
        match(t -> t.elementList.equals(otherElements), t_elementList_dot_equals_otherElements),
        match(t -> t.equals(otherTestPojo), t_dot_equals_otherTestPojo),
        match(t -> t.equals(new TestPojo()), t_dot_equals_newTestPojo),
        match(t -> t.equals(new TestPojo("foo", 42)), t_dot_equals_newTestPojo_foo42),
        match(t -> t.matches(arrayOfStrings),
            new MethodInvocation(var_t, TestPojo_matches_String,
                new ArrayVariable(String.class, arrayOfStringLiterals))),
        match(t -> t.matches(arrayOfTestPojos),
            new MethodInvocation(var_t, TestPojo_matches_TestPojo,
                new ArrayVariable(TestPojo.class, arrayOfTestPojoVariables))),

        // mixes with multiple operands
        match(
            t -> t.getStringValue().equals("foo") || t.getStringValue().equals("bar")
                || t.getStringValue().equals("baz"),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
                t_dot_getStringValue_dot_equals_foo, t_dot_getStringValue_dot_equals_bar,
                t_dot_getStringValue_dot_equals_baz)),
        match(t -> t.field.equals("foo"), t_dot_field_dot_equals_foo),
        match(t -> (t.field.equals("foo") && t.field.equals("bar")) || t.field.equals("baz"),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
                new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
                    t_dot_field_dot_equals_foo, t_dot_field_dot_equals_bar),
                t_dot_field_dot_equals_baz)),
        match(t -> (t.field.equals("foo") && !t.field.equals("bar")) || t.field.equals("baz"),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
                new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
                    t_dot_field_dot_equals_foo, t_dot_field_dot_equals_bar.inverse()),
                t_dot_field_dot_equals_baz)),
        match(t -> (t.field.equals("foo") && t.field.equals("bar")) || !t.field.equals("baz"),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
                new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
                    t_dot_field_dot_equals_foo, t_dot_field_dot_equals_bar),
                t_dot_field_dot_equals_baz.inverse())),
        match(
            t -> (t.field.equals("foo") && t.enumPojo == EnumPojo.FOO) || t.primitiveIntValue == 42,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
                new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
                    t_dot_field_dot_equals_foo, t_dot_enumPojo_equals_foo),
                t_dot_primitiveIntValue_equals_42)),
        match(t -> t.field.equals("foo") && t.field.equals("foo"), t_dot_field_dot_equals_foo),
        match(t -> t.getStringValue().equals("foo") || t.getPrimitiveIntValue() == 42,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
                t_dot_getStringValue_dot_equals_foo,
                new CompoundExpression(CompoundExpressionOperator.EQUALS,
                    t_dot_getPrimitiveIntValue, new NumberLiteral(intValue_42))))};
  }

  @Parameter(value = 0)
  public SerializablePredicate<TestPojo> expression;

  @Parameter(value = 1)
  public Expression expectation;

  @Test
  public void shouldParseLambdaExpression() throws IOException {
    // when
    final LambdaExpression resultExpression = analyzer.analyzeExpression(expression);
    // then
    assertThat(resultExpression.getBody()).containsExactly(new ReturnStatement(expectation));
  }

}
