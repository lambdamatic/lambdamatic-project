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
package org.lambdamatic.analyzer.ast;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lambdamatic.testutils.JavaMethods.Object_equals;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getPrimitiveIntValue;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getStringValue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.lambdamatic.analyzer.ast.node.CapturedArgumentRef;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.CompoundExpression;
import org.lambdamatic.analyzer.ast.node.CompoundExpression.CompoundExpressionOperator;
import org.lambdamatic.analyzer.ast.node.LocalVariable;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.lambdamatic.analyzer.ast.node.NumberLiteral;
import org.lambdamatic.analyzer.ast.node.StringLiteral;

import com.sample.model.TestPojo;

public class CapturedArgumentsEvaluatorTest {

  @Test
  public void shouldSubstituteCapturedArgumentInTwoMethodInvocationSourceExpressions() {
    // given
    final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsMethodInvocation = new MethodInvocation(testPojo, Object_equals,
        new MethodInvocation(new CapturedArgumentRef(0, TestPojo.class), TestPojo_getStringValue));
    final MethodInvocation equalsFieldAccess = new MethodInvocation(testPojo, Object_equals,
        new FieldAccess(new CapturedArgumentRef(0, TestPojo.class), "field"));
    final CompoundExpression expression = new CompoundExpression(
        CompoundExpressionOperator.CONDITIONAL_OR, equalsMethodInvocation, equalsFieldAccess);
    // when
    final CapturedArgumentsEvaluator expressionRewriter =
        new CapturedArgumentsEvaluator(Arrays.asList(new TestPojo(), new TestPojo()));
    expression.accept(expressionRewriter);
    // then
    final CompoundExpression expectedResult =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
            new MethodInvocation(testPojo, Object_equals, new StringLiteral("foo")),
            new MethodInvocation(testPojo, Object_equals, new StringLiteral("bar")));
    assertThat(expression).isEqualTo(expectedResult);
  }

  @Test
  public void shouldSubstituteOneCapturedArgumentInMethodInvocationSourceExpression() {
    // given
    final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation getStringValueMethod =
        new MethodInvocation(new CapturedArgumentRef(0, String.class), TestPojo_getStringValue);
    final MethodInvocation equalsGetStringValue =
        new MethodInvocation(testPojo, Object_equals, getStringValueMethod);
    // when
    final CapturedArgumentsEvaluator expressionRewriter =
        new CapturedArgumentsEvaluator(Arrays.asList(new TestPojo()));
    equalsGetStringValue.accept(expressionRewriter);
    // then
    final MethodInvocation expectedResult =
        new MethodInvocation(testPojo, Object_equals, new StringLiteral("foo"));
    assertThat(equalsGetStringValue).isEqualTo(expectedResult);
  }

  @Test
  public void shouldSubstituteCapturedArgumentRefInMethodInvocationSourceExpression() {
    // given
    final List<Object> capturedArguments = Arrays.asList(new TestPojo());
    final MethodInvocation getStringValueMethod =
        new MethodInvocation(new CapturedArgumentRef(0, TestPojo.class), TestPojo_getStringValue);
    final MethodInvocation equalsGetStringValue =
        new MethodInvocation(getStringValueMethod, Object_equals, getStringValueMethod);
    // when
    final CapturedArgumentsEvaluator expressionRewriter =
        new CapturedArgumentsEvaluator(capturedArguments);
    equalsGetStringValue.accept(expressionRewriter);
    // then
    final MethodInvocation expectedResult =
        new MethodInvocation(new StringLiteral("foo"), Object_equals, new StringLiteral("foo"));
    assertThat(equalsGetStringValue).isEqualTo(expectedResult);
  }

  @Test
  public void shouldSubstituteCapturedArgumentRefInMethodInvocationArguments() {
    // given
    final List<Object> capturedArguments = Arrays.asList("bar", 42);
    final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation getStringValueMethod =
        new MethodInvocation(testPojo, TestPojo_getStringValue);
    final MethodInvocation getStringValueEqualsBar = new MethodInvocation(getStringValueMethod,
        Object_equals, new CapturedArgumentRef(0, String.class));
    final MethodInvocation getPrimitiveIntValueMethod =
        new MethodInvocation(testPojo, TestPojo_getPrimitiveIntValue);
    final MethodInvocation getPrimitiveIntValueEquals42 = new MethodInvocation(
        getPrimitiveIntValueMethod, Object_equals, new CapturedArgumentRef(1, int.class));
    final CompoundExpression actualExpr =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, getStringValueEqualsBar,
            getPrimitiveIntValueEquals42);
    // when
    final CapturedArgumentsEvaluator expressionRewriter =
        new CapturedArgumentsEvaluator(capturedArguments);
    actualExpr.accept(expressionRewriter);
    // then
    final CompoundExpression expectedExpr =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
            new MethodInvocation(getStringValueMethod, Object_equals, new StringLiteral("bar")),
            new MethodInvocation(getPrimitiveIntValueMethod, Object_equals, new NumberLiteral(42)));
    assertThat(actualExpr).isEqualTo(expectedExpr);
  }

}
