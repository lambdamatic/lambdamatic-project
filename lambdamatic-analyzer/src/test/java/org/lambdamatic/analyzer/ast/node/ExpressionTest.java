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

import com.sample.model.EnumPojo;
import com.sample.model.TestPojo;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class ExpressionTest {

	@Test
	public void infixExpressionsWithSameOrderShouldBeEqual() {
		// given
		final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsFooMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("foo"));
		final MethodInvocation equalsBarMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("bar"));
		final MethodInvocation equalsBazMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("baz"));
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
		final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsFooMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("foo"));
		final MethodInvocation equalsBarMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("bar"));
		final MethodInvocation equalsBazMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("baz"));
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
		final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsFooMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("foo"));
		final MethodInvocation equalsBarMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("bar"));
		final MethodInvocation equalsBazMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("baz"));
		final MethodInvocation equalsBazzMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("bazz"));
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
		final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsFooMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("foo"));
		final MethodInvocation equalsBarMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("bar"));
		final MethodInvocation equalsBazMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("baz"));
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
		final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsFooMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("foo"));
		final MethodInvocation equalsBarMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("bar"));
		final MethodInvocation equalsBazMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("baz"));
		final MethodInvocation equalsBazzMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("bazz"));
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
		final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
		// when
		final MethodInvocation expressionA = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("foo"));
		final MethodInvocation expressionB = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("foo"));
		// then
		assertThat(expressionA).isEqualTo(expressionB);
		assertThat(expressionA.hashCode()).isEqualTo(expressionB.hashCode());
	}

	@Test
	public void methodInvocationExpressionsShouldNotBeEqual() {
		// given
		final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
		// when
		final MethodInvocation expressionA = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("foo"));
		final MethodInvocation expressionB = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("bar"));
		// then
		assertThat(expressionA).isNotEqualTo(expressionB);
		assertThat(expressionA.hashCode()).isNotEqualTo(expressionB.hashCode());
	}

	@Test
	public void inversedMethodInvocationExpressionsShouldNotBeEqual() {
		// given
		final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
		// when
		final MethodInvocation expressionA = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("foo"));
		final MethodInvocation expressionB = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("foo")).inverse();
		// then
		assertThat(expressionA).isNotEqualTo(expressionB);
		assertThat(expressionA.hashCode()).isNotEqualTo(expressionB.hashCode());
	}
	
	@Test
	public void infixExpressionWithMethodInvocationOperandsShouldBeFurtherSimplifiable() {
		// given
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final InfixExpression primitiveIntValueEquals42Expression = new InfixExpression(InfixOperator.EQUALS, new FieldAccess(var, "primitiveIntValue"), new NumberLiteral(42));
		final InfixExpression fieldEqualsFooExpression = new InfixExpression(InfixOperator.EQUALS, new FieldAccess(var, "field"), new StringLiteral("FOO"));
		final InfixExpression enumPojoEqualsFOOExpression = new InfixExpression(InfixOperator.EQUALS, new FieldAccess(var, "enumPojo"), new EnumLiteral(EnumPojo.FOO));
		// when
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR,
				new InfixExpression(InfixOperator.CONDITIONAL_AND, primitiveIntValueEquals42Expression,
				new InfixExpression(InfixOperator.CONDITIONAL_OR, fieldEqualsFooExpression.inverse(), enumPojoEqualsFOOExpression.inverse())),
				new InfixExpression(InfixOperator.CONDITIONAL_AND, fieldEqualsFooExpression, enumPojoEqualsFOOExpression));
		// then
		assertThat(expression.canFurtherSimplify()).isEqualTo(true);
	}

	@Test
	public void infixExpressionWithMethodInvocationOperandsShouldNotBeFurtherSimplifiable() {
		// given
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final InfixExpression primitiveIntValueEquals42Expression = new InfixExpression(InfixOperator.EQUALS, new FieldAccess(var, "primitiveIntValue"), new NumberLiteral(42));
		final InfixExpression fieldEqualsFooExpression = new InfixExpression(InfixOperator.EQUALS, new FieldAccess(var, "field"), new StringLiteral("FOO"));
		final InfixExpression enumPojoEqualsFOOExpression = new InfixExpression(InfixOperator.EQUALS, new FieldAccess(var, "enumPojo"), new EnumLiteral(EnumPojo.FOO));
		// when
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR,
				primitiveIntValueEquals42Expression,
				new InfixExpression(InfixOperator.CONDITIONAL_AND, fieldEqualsFooExpression, enumPojoEqualsFOOExpression));
		// then
		assertThat(expression.canFurtherSimplify()).isEqualTo(false);
	}

	@Test
	public void infixExpressionWithInfixExpressionOperandsShouldBeFurtherSimplifiable() {
		// given
		final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsFooMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("foo"));
		final MethodInvocation equalsBarMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("bar"));
		final MethodInvocation equalsBazMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("baz"));
		// when
		final Expression operandExpressionA = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsFooMethod, equalsBarMethod, equalsBazMethod);
		final Expression operandExpressionB = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsFooMethod, equalsBarMethod, equalsBazMethod);
		final Expression expression = new InfixExpression(InfixOperator.CONDITIONAL_AND, operandExpressionA, operandExpressionB);
		// then
		assertThat(expression.canFurtherSimplify()).isEqualTo(true);
	}
	
	@Test
	public void infixExpressionWithMethodInvocationsShouldNotBeFurtherSimplifiable() {
		// given
		final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsFooMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("foo"));
		final MethodInvocation equalsBarMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("bar"));
		final MethodInvocation equalsBazMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("baz"));
		// when
		final InfixExpression operandExpression = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsFooMethod, equalsBarMethod);
		final Expression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR, operandExpression, equalsBazMethod);
		// then
		assertThat(expression.canFurtherSimplify()).isEqualTo(false);
	}

	@Test
	public void infixExpressionWithDuplicateMethodInvocationOperandsShouldBeFurtherSimplifiable() {
		// given
		final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsFooMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("foo"));
		final MethodInvocation equalsBarMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("bar"));
		final MethodInvocation equalsBazMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("baz"));
		// when
		final Expression operandExpressionA = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsFooMethod, equalsBarMethod, equalsBazMethod);
		final Expression operandExpressionB = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsFooMethod, equalsBarMethod, equalsBazMethod);
		final Expression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR, operandExpressionA, operandExpressionB);
		// then
		assertThat(expression.canFurtherSimplify()).isEqualTo(true);
	}

	@Test
	public void infixExpressionWithDuplicateMethodInvocationOperandShouldBeFurtherSimplifiable() {
		// given
		final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsFooMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("foo"));
		final MethodInvocation equalsBarMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("bar"));
		final MethodInvocation equalsBazMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("baz"));
		// when
		final Expression expressionB = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsFooMethod, equalsBarMethod, equalsBazMethod);
		final Expression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsFooMethod, expressionB);
		// then
		assertThat(expression.canFurtherSimplify()).isEqualTo(true);
	}

	@Test
	public void infixExpressionWithNestExpressionWithSameOperatorShouldBeFurtherSimplifiable() {
		// given
		final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsFooMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("foo"));
		final MethodInvocation equalsBarMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("bar"));
		final MethodInvocation equalsBazMethod = new MethodInvocation(testPojo, "equals", Boolean.class,  new StringLiteral("baz"));
		// when
		final Expression expressionB = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsBarMethod, equalsBazMethod);
		final Expression expression = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsFooMethod, expressionB);
		// then
		assertThat(expression.canFurtherSimplify()).isEqualTo(true);
	}
}

