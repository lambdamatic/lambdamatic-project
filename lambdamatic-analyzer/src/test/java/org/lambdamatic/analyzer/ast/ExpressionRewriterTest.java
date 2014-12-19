/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.analyzer.ast;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.lambdamatic.analyzer.ast.node.CapturedArgument;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.InfixExpression;
import org.lambdamatic.analyzer.ast.node.InfixExpression.InfixOperator;
import org.lambdamatic.analyzer.ast.node.LocalVariable;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.lambdamatic.analyzer.ast.node.StringLiteral;

import com.sample.model.TestPojo;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class ExpressionRewriterTest {

	@Test
	public void shouldSubstituteOneCapturedArgumentInMethodInvocation() {
		// given
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation getStringValueMethod = new MethodInvocation(new CapturedArgument(new TestPojo()), "getStringValue", String.class);
		final MethodInvocation equalsGetStringValue = new MethodInvocation(testPojo, "equals", Boolean.class, getStringValueMethod);
		// when
		final ExpressionRewriter expressionRewriter = new ExpressionRewriter();
		equalsGetStringValue.accept(expressionRewriter);
		// then
		final MethodInvocation expectedResult = new MethodInvocation(testPojo, "equals", Boolean.class, new StringLiteral("foo"));
		assertThat(equalsGetStringValue).isEqualTo(expectedResult);
	}

	@Test
	public void shouldSubstituteCapturedArgumentInTwoMethodInvocations() {
		// given
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation equalsMethodInvocation = new MethodInvocation(testPojo, "equals", Boolean.class, new MethodInvocation(new CapturedArgument(new TestPojo()), "getStringValue", String.class));
		final MethodInvocation equalsFieldAccess = new MethodInvocation(testPojo, "equals", Boolean.class, new FieldAccess(new CapturedArgument(new TestPojo()), "field"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsMethodInvocation, equalsFieldAccess);
		// when
		final ExpressionRewriter expressionRewriter = new ExpressionRewriter();
		expression.accept(expressionRewriter);
		// then
		final InfixExpression expectedResult = new InfixExpression(InfixOperator.CONDITIONAL_OR, new MethodInvocation(testPojo, "equals", Boolean.class, 
				new StringLiteral("foo")), new MethodInvocation(testPojo, "equals", Boolean.class, new StringLiteral("bar")));
		assertThat(expression).isEqualTo(expectedResult);
	}

	@Test
	public void shouldNotSubstituteInfixExpressionOperands() {
		// given
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final MethodInvocation equalsFooMethod = new MethodInvocation(testPojo, "equals", Boolean.class, new StringLiteral("foo"));
		// when
		final ExpressionRewriter expressionRewriter = new ExpressionRewriter();
		equalsFooMethod.accept(expressionRewriter);
		// then
		assertThat(equalsFooMethod).isEqualTo(equalsFooMethod);
	}
}

