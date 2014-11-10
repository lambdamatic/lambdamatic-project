/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.lambdamatic.analyzer.ast.node.InfixExpression.InfixOperator;

import com.sample.model.TestPojo;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class ExpressionTest {

	@Test
	public void infixExpressionsWithSameOrderShouldBeEqual() {
		// given
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation equalsFooMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("foo"));
		final MethodInvocation equalsBarMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("bar"));
		final MethodInvocation equalsBazMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("baz"));
		// when
		final Expression expressionA = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsFooMethod, equalsBarMethod, equalsBazMethod);
		final Expression expressionB = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsFooMethod, equalsBarMethod, equalsBazMethod);
		// then
		assertThat(expressionA).isEqualTo(expressionB);
		assertThat(expressionA.hashCode()).isEqualTo(expressionB.hashCode());
	}

	@Test
	public void infixExpressionsWithDifferentOrderShouldBeEqual() {
		// given
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation equalsFooMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("foo"));
		final MethodInvocation equalsBarMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("bar"));
		final MethodInvocation equalsBazMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("baz"));
		// when
		final Expression expressionA = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsFooMethod, equalsBarMethod, equalsBazMethod);
		final Expression expressionB = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsBazMethod, equalsFooMethod, equalsBarMethod);
		// then
		assertThat(expressionA).isEqualTo(expressionB);
		assertThat(expressionA.hashCode()).isEqualTo(expressionB.hashCode());
	}

	@Test
	public void infixExpressionsShouldNotBeEqual() {
		// given
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation equalsFooMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("foo"));
		final MethodInvocation equalsBarMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("bar"));
		final MethodInvocation equalsBazMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("baz"));
		final MethodInvocation equalsBazzMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("bazz"));
		// when
		final Expression expressionA = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsFooMethod, equalsBarMethod, equalsBazMethod);
		final Expression expressionB = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsBarMethod, equalsFooMethod,
				equalsBazzMethod);
		// then
		assertThat(expressionA).isNotEqualTo(expressionB);
		assertThat(expressionA.hashCode()).isNotEqualTo(expressionB.hashCode());
	}

	@Test
	public void infixExpressionsWithNestedInfixShouldBeEqual() {
		// given
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation equalsFooMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("foo"));
		final MethodInvocation equalsBarMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("bar"));
		final MethodInvocation equalsBazMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("baz"));
		// when
		final Expression expressionA = new InfixExpression(InfixOperator.CONDITIONAL_OR, new InfixExpression(InfixOperator.CONDITIONAL_AND,
				equalsFooMethod, equalsBarMethod), equalsBazMethod);
		final Expression expressionB = new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsBazMethod, new InfixExpression(
				InfixOperator.CONDITIONAL_AND, equalsBarMethod, equalsFooMethod));
		// then
		assertThat(expressionA).isEqualTo(expressionB);
		assertThat(expressionA.hashCode()).isEqualTo(expressionB.hashCode());
	}

	@Test
	public void infixExpressionsNestedInfixShouldNotBeEqual() {
		// given
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation equalsFooMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("foo"));
		final MethodInvocation equalsBarMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("bar"));
		final MethodInvocation equalsBazMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("baz"));
		final MethodInvocation equalsBazzMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("bazz"));
		// when
		final Expression expressionA = new InfixExpression(InfixOperator.CONDITIONAL_OR, new InfixExpression(InfixOperator.CONDITIONAL_AND,
				equalsFooMethod, equalsBarMethod), equalsBazMethod);
		final Expression expressionB = new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsBazzMethod, new InfixExpression(
				InfixOperator.CONDITIONAL_AND, equalsBarMethod, equalsFooMethod));
		// then
		assertThat(expressionA).isNotEqualTo(expressionB);
		assertThat(expressionA.hashCode()).isNotEqualTo(expressionB.hashCode());
	}

	@Test
	public void methodInvocationExpressionsShouldBeEqual() {
		// given
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		// when
		final MethodInvocation expressionA = new MethodInvocation(testPojo, "equals", new StringLiteral("foo"));
		final MethodInvocation expressionB = new MethodInvocation(testPojo, "equals", new StringLiteral("foo"));
		// then
		assertThat(expressionA).isEqualTo(expressionB);
		assertThat(expressionA.hashCode()).isEqualTo(expressionB.hashCode());
	}

	@Test
	public void methodInvocationExpressionsShouldNotBeEqual() {
		// given
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		// when
		final MethodInvocation expressionA = new MethodInvocation(testPojo, "equals", new StringLiteral("foo"));
		final MethodInvocation expressionB = new MethodInvocation(testPojo, "equals", new StringLiteral("bar"));
		// then
		assertThat(expressionA).isNotEqualTo(expressionB);
		assertThat(expressionA.hashCode()).isNotEqualTo(expressionB.hashCode());
	}

	@Test
	public void inversedMethodInvocationExpressionsShouldNotBeEqual() {
		// given
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		// when
		final MethodInvocation expressionA = new MethodInvocation(testPojo, "equals", new StringLiteral("foo"));
		final MethodInvocation expressionB = new MethodInvocation(testPojo, "equals", new StringLiteral("foo")).inverse();
		// then
		assertThat(expressionA).isNotEqualTo(expressionB);
		assertThat(expressionA.hashCode()).isNotEqualTo(expressionB.hashCode());
	}
	
	@Test
	public void infixExpressionShouldBeFurtherSimplifiable() {
		// given
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation equalsFooMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("foo"));
		final MethodInvocation equalsBarMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("bar"));
		final MethodInvocation equalsBazMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("baz"));
		// when
		final Expression operandExpressionA = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsFooMethod, equalsBarMethod, equalsBazMethod);
		final Expression operandExpressionB = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsFooMethod, equalsBarMethod, equalsBazMethod);
		final Expression expression = new InfixExpression(InfixOperator.CONDITIONAL_AND, operandExpressionA, operandExpressionB);
		// then
		assertThat(expression.canFurtherSimplify()).isEqualTo(true);
	}

	@Test
	public void infixExpressionShouldNotBeFurtherSimplifiable() {
		// given
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation equalsFooMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("foo"));
		final MethodInvocation equalsBarMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("bar"));
		final MethodInvocation equalsBazMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("baz"));
		// when
		final InfixExpression operandExpression = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsFooMethod, equalsBarMethod);
		final Expression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR, operandExpression, equalsBazMethod);
		// then
		assertThat(expression.canFurtherSimplify()).isEqualTo(false);
	}

	@Test
	public void infixExpressionWithDuplicateOperandsShouldBeFurtherSimplifiable() {
		// given
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation equalsFooMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("foo"));
		final MethodInvocation equalsBarMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("bar"));
		final MethodInvocation equalsBazMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("baz"));
		// when
		final Expression operandExpressionA = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsFooMethod, equalsBarMethod, equalsBazMethod);
		final Expression operandExpressionB = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsFooMethod, equalsBarMethod, equalsBazMethod);
		final Expression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR, operandExpressionA, operandExpressionB);
		// then
		assertThat(expression.canFurtherSimplify()).isEqualTo(true);
	}

	@Test
	public void infixExpressionWithDuplicateOperandShouldBeFurtherSimplifiable() {
		// given
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation equalsFooMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("foo"));
		final MethodInvocation equalsBarMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("bar"));
		final MethodInvocation equalsBazMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("baz"));
		// when
		final Expression expressionB = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsFooMethod, equalsBarMethod, equalsBazMethod);
		final Expression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsFooMethod, expressionB);
		// then
		assertThat(expression.canFurtherSimplify()).isEqualTo(true);
	}

	@Test
	public void infixExpressionWithNestExpressionWithSameOperatorShouldBeFurtherSimplifiable() {
		// given
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation equalsFooMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("foo"));
		final MethodInvocation equalsBarMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("bar"));
		final MethodInvocation equalsBazMethod = new MethodInvocation(testPojo, "equals", new StringLiteral("baz"));
		// when
		final Expression expressionB = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsBarMethod, equalsBazMethod);
		final Expression expression = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsFooMethod, expressionB);
		// then
		assertThat(expression.canFurtherSimplify()).isEqualTo(true);
	}
}

