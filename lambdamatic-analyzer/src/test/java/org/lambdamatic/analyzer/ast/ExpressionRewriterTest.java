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
import org.lambdamatic.analyzer.ast.node.ClassLiteral;
import org.lambdamatic.analyzer.ast.node.EnumLiteral;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.InfixExpression;
import org.lambdamatic.analyzer.ast.node.NumberLiteral;
import org.lambdamatic.analyzer.ast.node.InfixExpression.InfixOperator;
import org.lambdamatic.analyzer.ast.node.LocalVariable;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.lambdamatic.analyzer.ast.node.StringLiteral;

import com.sample.model.EnumPojo;
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
	
	@Test
	public void shouldSubstituteTwoEnumLiterals() {
		// given '(foo + (!foo.bar) + (!foo.bar.!baz)'
		final LocalVariable var = new LocalVariable("t", TestPojo.class);
		final InfixExpression getPrimitiveIntMethodEquals42_1 = new InfixExpression(InfixOperator.EQUALS, new MethodInvocation(var, "getPrimitiveIntValue", int.class), new NumberLiteral(42));
		final InfixExpression getPrimitiveIntMethodEquals42_2 = new InfixExpression(InfixOperator.NOT_EQUALS, new MethodInvocation(var, "getPrimitiveIntValue", int.class), new NumberLiteral(42));
		final InfixExpression getPrimitiveIntMethodEquals42_3 = new InfixExpression(InfixOperator.NOT_EQUALS, new MethodInvocation(var, "getPrimitiveIntValue", int.class), new NumberLiteral(42));
		final InfixExpression getEnumPojoMethodEqualsFieldBar_1 = new InfixExpression(InfixOperator.EQUALS, new MethodInvocation(var, "getEnumPojo", EnumPojo.class), new FieldAccess(new ClassLiteral(EnumPojo.class), "BAR"));
		final InfixExpression getEnumPojoMethodEqualsFieldBar_2 = new InfixExpression(InfixOperator.NOT_EQUALS, new MethodInvocation(var, "getEnumPojo", EnumPojo.class), new FieldAccess(new ClassLiteral(EnumPojo.class), "BAR"));
		final InfixExpression getStringValueMethodEqualsFoo1 = new InfixExpression(InfixOperator.EQUALS, new MethodInvocation(var, "getStringValue", String.class), new StringLiteral("foo"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR, getPrimitiveIntMethodEquals42_1, 
				new InfixExpression(InfixOperator.CONDITIONAL_AND, getPrimitiveIntMethodEquals42_2, getEnumPojoMethodEqualsFieldBar_1), 
				new InfixExpression(InfixOperator.CONDITIONAL_AND, getPrimitiveIntMethodEquals42_3, getEnumPojoMethodEqualsFieldBar_2.inverse(), getStringValueMethodEqualsFoo1));
		// when
		final ExpressionRewriter expressionRewriter = new ExpressionRewriter();
		expression.accept(expressionRewriter);
		// then expect (foo || bar || baz)
		final InfixExpression getEnumPojoMethodEqualsEnumBar_1 = new InfixExpression(InfixOperator.EQUALS, new MethodInvocation(var, "getEnumPojo", EnumPojo.class), new EnumLiteral(EnumPojo.BAR));
		final InfixExpression getEnumPojoMethodEqualsEnumBar_2 = new InfixExpression(InfixOperator.EQUALS, new MethodInvocation(var, "getEnumPojo", EnumPojo.class), new EnumLiteral(EnumPojo.BAR));
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.CONDITIONAL_OR, getPrimitiveIntMethodEquals42_1, 
				new InfixExpression(InfixOperator.CONDITIONAL_AND, getPrimitiveIntMethodEquals42_2, getEnumPojoMethodEqualsEnumBar_1), 
				new InfixExpression(InfixOperator.CONDITIONAL_AND, getPrimitiveIntMethodEquals42_3, getEnumPojoMethodEqualsEnumBar_2, getStringValueMethodEqualsFoo1));
		assertThat(expression).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldSubstituteTwoEnumLiteralsFromDuplicateExpressions() {
		// given '(foo + (!foo.bar) + (!foo.bar.!baz)'
		final LocalVariable var = new LocalVariable("t", TestPojo.class);
		final InfixExpression getPrimitiveIntMethodEquals42 = new InfixExpression(InfixOperator.EQUALS, new MethodInvocation(var, "getPrimitiveIntValue", int.class), new NumberLiteral(42));
		final InfixExpression getEnumPojoMethodEqualsFieldBar = new InfixExpression(InfixOperator.EQUALS, new MethodInvocation(var, "getEnumPojo", EnumPojo.class), new FieldAccess(new ClassLiteral(EnumPojo.class), "BAR"));
		final InfixExpression getStringValueMethodEqualsFoo = new InfixExpression(InfixOperator.EQUALS, new MethodInvocation(var, "getStringValue", String.class), new StringLiteral("foo"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR, getPrimitiveIntMethodEquals42, 
				new InfixExpression(InfixOperator.CONDITIONAL_AND, getPrimitiveIntMethodEquals42.inverse(), getEnumPojoMethodEqualsFieldBar), 
				new InfixExpression(InfixOperator.CONDITIONAL_AND, getPrimitiveIntMethodEquals42.inverse(), getEnumPojoMethodEqualsFieldBar.inverse(), getStringValueMethodEqualsFoo));
		// when
		final ExpressionRewriter expressionRewriter = new ExpressionRewriter();
		expression.accept(expressionRewriter);
		// then expect (foo || bar || baz)
		final InfixExpression getEnumPojoMethodEqualsEnumBar = new InfixExpression(InfixOperator.EQUALS, new MethodInvocation(var, "getEnumPojo", EnumPojo.class), new EnumLiteral(EnumPojo.BAR));
		final InfixExpression expectedExpression = new InfixExpression(InfixOperator.CONDITIONAL_OR, getPrimitiveIntMethodEquals42, 
				new InfixExpression(InfixOperator.CONDITIONAL_AND, getPrimitiveIntMethodEquals42.inverse(), getEnumPojoMethodEqualsEnumBar), 
				new InfixExpression(InfixOperator.CONDITIONAL_AND, getPrimitiveIntMethodEquals42.inverse(), getEnumPojoMethodEqualsEnumBar.inverse(), getStringValueMethodEqualsFoo));
		assertThat(expression).isEqualTo(expectedExpression);
	}
}

