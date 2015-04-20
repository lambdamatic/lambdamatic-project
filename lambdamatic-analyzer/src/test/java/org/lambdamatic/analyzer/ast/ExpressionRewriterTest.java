/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.analyzer.ast;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lambdamatic.testutils.JavaMethods.Object_equals;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getEnumPojo;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getPrimitiveIntValue;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getStringValue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.lambdamatic.analyzer.ast.node.ClassLiteral;
import org.lambdamatic.analyzer.ast.node.EnumLiteral;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.ExpressionVisitorUtil;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.InfixExpression;
import org.lambdamatic.analyzer.ast.node.InfixExpression.InfixOperator;
import org.lambdamatic.analyzer.ast.node.LocalVariable;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.lambdamatic.analyzer.ast.node.NumberLiteral;
import org.lambdamatic.analyzer.ast.node.StringLiteral;
import org.lambdamatic.testutils.TestWatcher;

import com.sample.model.EnumPojo;
import com.sample.model.TestPojo;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
@RunWith(Parameterized.class)
public class ExpressionRewriterTest {

	@Rule
	public TestWatcher watcher = new TestWatcher();

	@Parameters(name = "[{index}] {1}")
	public static Object[][] data() {
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		// no substitution
		final MethodInvocation expression0 = new MethodInvocation(var, Object_equals,
				new StringLiteral("foo"));
		// substitute 2 enum literals
		final InfixExpression getPrimitiveIntMethodEquals42_1 = new InfixExpression(InfixOperator.EQUALS,
				new MethodInvocation(var, TestPojo_getPrimitiveIntValue), new NumberLiteral(42));
		final InfixExpression getPrimitiveIntMethodEquals42_2 = new InfixExpression(InfixOperator.NOT_EQUALS,
				new MethodInvocation(var, TestPojo_getPrimitiveIntValue), new NumberLiteral(42));
		final InfixExpression getPrimitiveIntMethodEquals42_3 = new InfixExpression(InfixOperator.NOT_EQUALS,
				new MethodInvocation(var, TestPojo_getPrimitiveIntValue), new NumberLiteral(42));
		final InfixExpression getEnumPojoMethodEqualsFieldBar_1 = new InfixExpression(InfixOperator.EQUALS,
				new MethodInvocation(var, TestPojo_getEnumPojo), new FieldAccess(new ClassLiteral(
						EnumPojo.class), "BAR"));
		final InfixExpression getEnumPojoMethodEqualsFieldBar_2 = new InfixExpression(InfixOperator.NOT_EQUALS,
				new MethodInvocation(var, TestPojo_getEnumPojo), new FieldAccess(new ClassLiteral(
						EnumPojo.class), "BAR"));
		final InfixExpression getStringValueMethodEqualsFoo1 = new InfixExpression(InfixOperator.EQUALS,
				new MethodInvocation(var, TestPojo_getStringValue), new StringLiteral("foo"));
		final InfixExpression expression1 = new InfixExpression(InfixOperator.CONDITIONAL_OR,
				getPrimitiveIntMethodEquals42_1, new InfixExpression(InfixOperator.CONDITIONAL_AND,
						getPrimitiveIntMethodEquals42_2, getEnumPojoMethodEqualsFieldBar_1), new InfixExpression(
						InfixOperator.CONDITIONAL_AND, getPrimitiveIntMethodEquals42_3,
						getEnumPojoMethodEqualsFieldBar_2.inverse(), getStringValueMethodEqualsFoo1));
		final InfixExpression getEnumPojoMethodEqualsEnumBar_1 = new InfixExpression(InfixOperator.EQUALS,
				new MethodInvocation(var, TestPojo_getEnumPojo), new EnumLiteral(EnumPojo.BAR));
		final InfixExpression getEnumPojoMethodEqualsEnumBar_2 = new InfixExpression(InfixOperator.EQUALS,
				new MethodInvocation(var, TestPojo_getEnumPojo), new EnumLiteral(EnumPojo.BAR));
		final InfixExpression expectedExpression1 = new InfixExpression(InfixOperator.CONDITIONAL_OR,
				getPrimitiveIntMethodEquals42_1, new InfixExpression(InfixOperator.CONDITIONAL_AND,
						getPrimitiveIntMethodEquals42_2, getEnumPojoMethodEqualsEnumBar_1), new InfixExpression(
						InfixOperator.CONDITIONAL_AND, getPrimitiveIntMethodEquals42_3,
						getEnumPojoMethodEqualsEnumBar_2, getStringValueMethodEqualsFoo1));
		// Substitute 2 Enum literals from duplicate expressions
		final InfixExpression getPrimitiveIntMethodEquals42 = new InfixExpression(InfixOperator.EQUALS,
				new MethodInvocation(var, TestPojo_getPrimitiveIntValue), new NumberLiteral(42));
		final InfixExpression getEnumPojoMethodEqualsFieldBar = new InfixExpression(InfixOperator.EQUALS,
				new MethodInvocation(var, TestPojo_getEnumPojo), new FieldAccess(new ClassLiteral(
						EnumPojo.class), "BAR"));
		final InfixExpression getStringValueMethodEqualsFoo = new InfixExpression(InfixOperator.EQUALS,
				new MethodInvocation(var, TestPojo_getStringValue), new StringLiteral("foo"));
		final InfixExpression expression2 = new InfixExpression(InfixOperator.CONDITIONAL_OR,
				getPrimitiveIntMethodEquals42, new InfixExpression(InfixOperator.CONDITIONAL_AND,
						getPrimitiveIntMethodEquals42.inverse(), getEnumPojoMethodEqualsFieldBar), new InfixExpression(
						InfixOperator.CONDITIONAL_AND, getPrimitiveIntMethodEquals42.inverse(),
						getEnumPojoMethodEqualsFieldBar.inverse(), getStringValueMethodEqualsFoo));
		final InfixExpression getEnumPojoMethodEqualsEnumBar = new InfixExpression(InfixOperator.EQUALS,
				new MethodInvocation(var, TestPojo_getEnumPojo), new EnumLiteral(EnumPojo.BAR));
		final InfixExpression expectedExpression2 = new InfixExpression(InfixOperator.CONDITIONAL_OR,
				getPrimitiveIntMethodEquals42, new InfixExpression(InfixOperator.CONDITIONAL_AND,
						getPrimitiveIntMethodEquals42.inverse(), getEnumPojoMethodEqualsEnumBar), new InfixExpression(
						InfixOperator.CONDITIONAL_AND, getPrimitiveIntMethodEquals42.inverse(),
						getEnumPojoMethodEqualsEnumBar.inverse(), getStringValueMethodEqualsFoo));

		return new Object[][] { new Object[] { expression0, expression0 },
				new Object[] { expression1, expectedExpression1 }, new Object[] { expression2, expectedExpression2 }, };
	}

	@Parameter(value = 0)
	public Expression expression;

	@Parameter(value = 1)
	public Expression expectedExpression;

	@Test
	public void shouldRewriteExpression() {
		// given
		final ExpressionSanitizer expressionRewriter = new ExpressionSanitizer();
		// when
		ExpressionVisitorUtil.visit(expression, expressionRewriter);
		expression.accept(expressionRewriter);
		// then
		assertThat(expression).isEqualTo(expectedExpression);
	}

}
