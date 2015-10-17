/*******************************************************************************
 * Copyright (c) 2014, 2015 Red Hat.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lambdamatic.testutils.JavaMethods.Object_equals;

import org.junit.Test;
import org.lambdamatic.analyzer.ast.node.CompoundExpression.CompoundExpressionOperator;

import com.sample.model.TestPojo;

public class CompoundExpressionSimplificationMonitorTest {

  @Test
  public void shouldRecognizeForm() {
    // given
    final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsFooMethod = new MethodInvocation(
        new FieldAccess(testPojo, "field"), Object_equals, new StringLiteral("foo"));
    final MethodInvocation equalsBarMethod = new MethodInvocation(
        new FieldAccess(testPojo, "field"), Object_equals, new StringLiteral("bar"));
    final MethodInvocation equalsBazMethod = new MethodInvocation(
        new FieldAccess(testPojo, "field"), Object_equals, new StringLiteral("baz"));
    final CompoundExpression expression =
        new CompoundExpression(1, CompoundExpressionOperator.CONDITIONAL_OR,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsFooMethod,
                equalsBarMethod),
            equalsBazMethod);
    final ExpressionSimplificationMonitor monitor = new ExpressionSimplificationMonitor(expression);
    monitor.registerExpression(expression);
    // when
    final CompoundExpression similarExpression =
        new CompoundExpression(1, CompoundExpressionOperator.CONDITIONAL_OR, equalsBazMethod,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsBarMethod,
                equalsFooMethod));
    final boolean expressionFormKnown = monitor.isExpressionFormKnown(similarExpression);
    // expect 23 (2 infix expressions + 3 simple expressions)
    assertThat(expressionFormKnown).isEqualTo(true);
  }

}

