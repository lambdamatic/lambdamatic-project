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
package org.lambdamatic.analyzer.ast.node;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lambdamatic.testutils.JavaMethods.Object_equals;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getEnumPojo;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getPrimitiveIntValue;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getStringValue;

import java.util.Collection;

import org.junit.Test;
import org.lambdamatic.analyzer.ast.node.CompoundExpression.CompoundExpressionOperator;

import com.sample.model.EnumPojo;
import com.sample.model.TestPojo;

public class CompoundExpressionSimplificationTest {

  @Test
  public void shouldApplyIdempotentLawWithConditionalOrOnTwoOperands() {
    // given '(a + a)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsA1Method =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsA2Method =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final CompoundExpression expression = new CompoundExpression(
        CompoundExpressionOperator.CONDITIONAL_OR, equalsA1Method, equalsA2Method);
    // when
    final Collection<Expression> results = expression.applyIdempotentLaw();
    // then expect '(a)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(equalsA1Method);
  }

  @Test
  public void shouldApplyIdempotentLawWithConditionalOrOnThreeOperands() {
    // given '(a + a + b)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsA1Method =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsA2Method =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final CompoundExpression expression = new CompoundExpression(
        CompoundExpressionOperator.CONDITIONAL_OR, equalsA1Method, equalsA2Method, equalsBMethod);
    // when
    final Collection<Expression> results = expression.applyIdempotentLaw();
    // then expect '(a)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
        equalsA1Method, equalsBMethod));
  }

  @Test
  public void shouldApplyIdempotentLawWithConditionalAndOnTwoOperands() {
    // given '(a.a)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsA1Method =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsA2Method =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final CompoundExpression expression = new CompoundExpression(
        CompoundExpressionOperator.CONDITIONAL_AND, equalsA1Method, equalsA2Method);
    // when
    final Collection<Expression> results = expression.applyIdempotentLaw();
    // then expect '(a)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(equalsA1Method);
  }

  @Test
  public void shouldApplyIdempotentLawWithConditionalAndOnThreeOperands() {
    // given '(a.a.b)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsA1Method =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsA2Method =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final CompoundExpression expression = new CompoundExpression(
        CompoundExpressionOperator.CONDITIONAL_AND, equalsA1Method, equalsA2Method, equalsBMethod);
    // when
    final Collection<Expression> results = expression.applyIdempotentLaw();
    // then expect '(a.b)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
        equalsA1Method, equalsBMethod));
  }

  @Test
  public void shouldApplyAssociativeLawWithConditionalOr() {
    // given '(a + (b + c))'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final MethodInvocation equalsCMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("C"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsBMethod,
                equalsCMethod));
    // when
    final Collection<Expression> results = expression.applyAssociativeLaw();
    // then expect '(a + b + c)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
        equalsAMethod, equalsBMethod, equalsCMethod));
  }

  @Test
  public void shouldNotApplyAssociativeLawWithConditionalOr() {
    // given '(a + (b . c))'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final MethodInvocation equalsCMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("C"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsBMethod,
                equalsCMethod));
    // when
    final Collection<Expression> results = expression.applyAssociativeLaw();
    // then expect no result
    assertThat(results).hasSize(0);
  }

  @Test
  public void shouldApplyAssociativeLawWithConditionalAnd() {
    // given '(a . (b . c))'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final MethodInvocation equalsCMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("C"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsBMethod,
                equalsCMethod));
    // when
    final Collection<Expression> results = expression.applyAssociativeLaw();
    // then expect '(a.b.c)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
        equalsAMethod, equalsBMethod, equalsCMethod));
  }

  @Test
  public void shouldNotApplyAssociativeLawWithConditionalAnd() {
    // given '(a . (b + c))'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final MethodInvocation equalsCMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("C"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsBMethod,
                equalsCMethod));
    // when
    final Collection<Expression> results = expression.applyAssociativeLaw();
    // then expect no result
    assertThat(results).hasSize(0);
  }

  @Test
  public void shouldApplyEmptySetLawsOnConditionalOrOnTwoOperands() {
    // given '(a + O)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final BooleanLiteral emptySetOperator = new BooleanLiteral(false);
    final CompoundExpression expression = new CompoundExpression(
        CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, emptySetOperator);
    // when
    final Collection<Expression> results = expression.applyEmptySetLaw();
    // then expect '(a)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(equalsAMethod);
  }

  @Test
  public void shouldApplyEmptySetLawsOnConditionalOrOnThreeOperands() {
    // given '(a + b + O)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final BooleanLiteral emptySetOperator = new BooleanLiteral(false);
    final CompoundExpression expression = new CompoundExpression(
        CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, equalsBMethod, emptySetOperator);
    // when
    final Collection<Expression> results = expression.applyEmptySetLaw();
    // then expect '(a + b)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
        equalsAMethod, equalsBMethod));
  }

  @Test
  public void shouldApplyEmptySetLawsOnConditionalAndOnTwoOperands() {
    // given '(a.O)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
            BooleanLiteral.EMPTY_SET_OPERATOR);
    // when
    final Collection<Expression> results = expression.applyEmptySetLaw();
    // then expect '(O)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(BooleanLiteral.EMPTY_SET_OPERATOR);
  }

  @Test
  public void shouldApplyEmptySetLawsOnConditionalAndOnThreeOperands() {
    // given '(a.b.O)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
            equalsBMethod, BooleanLiteral.EMPTY_SET_OPERATOR);
    // when
    final Collection<Expression> results = expression.applyEmptySetLaw();
    // then expect '(O)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(BooleanLiteral.EMPTY_SET_OPERATOR);
  }

  @Test
  public void shouldApplyUniversalSetLawsOnConditionalOrOnTwoOperands() {
    // given '(a + 1)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
            BooleanLiteral.UNIVERSAL_OPERATOR);
    // when
    final Collection<Expression> results = expression.applyUniversalSetLaw();
    // then expect '(1)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(BooleanLiteral.UNIVERSAL_OPERATOR);
  }

  @Test
  public void shouldApplyUniversalSetLawsOnConditionalOrOnThreeOperands() {
    // given '(a + b + 1)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
            equalsBMethod, BooleanLiteral.UNIVERSAL_OPERATOR);
    // when
    final Collection<Expression> results = expression.applyUniversalSetLaw();
    // then expect '(1)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(BooleanLiteral.UNIVERSAL_OPERATOR);
  }

  @Test
  public void shouldApplyUniversalSetLawsOnConditionalAndWithTwoOperands() {
    // given '(a.1)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
            BooleanLiteral.UNIVERSAL_OPERATOR);
    // when
    final Collection<Expression> results = expression.applyUniversalSetLaw();
    // then expect '(a)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(equalsAMethod);
    assertThat(results.iterator().next().getId()).isEqualTo(expression.getId());
  }

  @Test
  public void shouldApplyUniversalSetLawsOnConditionalAndWithThreeOperands() {
    // given '(a.b.1)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
            equalsBMethod, BooleanLiteral.UNIVERSAL_OPERATOR);
    // when
    final Collection<Expression> results = expression.applyUniversalSetLaw();
    // then expect '(a.b)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
        equalsAMethod, equalsBMethod));
  }

  @Test
  public void shouldApplyUnaryOperationLawsOnConditionalOrOnTwoOperands() {
    // given '(a + !a)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation notEqualsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A")).inverse();
    final CompoundExpression expression = new CompoundExpression(
        CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, notEqualsAMethod);
    // when
    final Collection<Expression> results = expression.applyUnaryOperationLaw();
    // then expect '(1)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(BooleanLiteral.UNIVERSAL_OPERATOR);
  }

  @Test
  public void shouldApplyUnaryOperationLawsOnConditionalOrOnThreeOperands() {
    // given '(a + !a + b)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation notEqualsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A")).inverse();
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final CompoundExpression expression = new CompoundExpression(
        CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, notEqualsAMethod, equalsBMethod);
    // when
    final Collection<Expression> results = expression.applyUnaryOperationLaw();
    // then expect '(1)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(equalsBMethod);
  }

  @Test
  public void shouldApplyUnaryOperationLawsOnConditionalAndOnTwoOperands() {
    // given '(a . !a)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation notEqualsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A")).inverse();
    final CompoundExpression expression = new CompoundExpression(
        CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, notEqualsAMethod);
    // when
    final Collection<Expression> results = expression.applyUnaryOperationLaw();
    // then expect '(0)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(BooleanLiteral.EMPTY_SET_OPERATOR);
  }

  @Test
  public void shouldApplyUnaryOperationLawsOnConditionalAndOnThreeOperands() {
    // given '(a . !a . b)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation notEqualsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A")).inverse();
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final CompoundExpression expression = new CompoundExpression(
        CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, notEqualsAMethod, equalsBMethod);
    // when
    final Collection<Expression> results = expression.applyUnaryOperationLaw();
    // then expect '(0)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(BooleanLiteral.EMPTY_SET_OPERATOR);
  }

  @Test
  public void shouldApplyAbsorptionLawOnAllOperandsOnConditionalAnd() {
    // given '(a.(a + b).(a + c))'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final MethodInvocation equalsCMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("C"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
                equalsBMethod),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
                equalsCMethod));
    // when
    final Collection<Expression> results = expression.applyAbsorptionLaw();
    // then expect '(a)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(equalsAMethod);
  }

  @Test
  public void shouldNotApplyAbsorptionLawOnAllOperandsOnConditionalAnd() {
    // given '(a.(a.b).(a.c))'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final MethodInvocation equalsCMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("C"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
                equalsBMethod),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
                equalsCMethod));
    // when
    final Collection<Expression> results = expression.applyAbsorptionLaw();
    // then expect no result
    assertThat(results).hasSize(0);
  }

  @Test
  public void shouldApplyAbsorptionLawOnSomeOperandsOnConditionalAnd() {
    // given '(a.(a + b).(a + c).d)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final MethodInvocation equalsCMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("C"));
    final MethodInvocation equalsDMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("D"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
                equalsBMethod),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
                equalsCMethod),
            equalsDMethod);
    // when
    final Collection<Expression> results = expression.applyAbsorptionLaw();
    // then expect '(a.d)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
        equalsAMethod, equalsDMethod));
  }

  @Test
  public void shouldNotApplyAbsorptionLawOnSomeOperandsOnConditionalAnd() {
    // given '(a.(a.b).(a.c).d)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final MethodInvocation equalsCMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("C"));
    final MethodInvocation equalsDMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("D"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
                equalsBMethod),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
                equalsCMethod),
            equalsDMethod);
    // when
    final Collection<Expression> results = expression.applyAbsorptionLaw();
    // then expect no result
    assertThat(results).hasSize(0);
  }

  @Test
  public void shouldApplyAbsorptionLawOnAllOperandsOnConditionalOr() {
    // given '(a + (a.b) + (a.c))'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final MethodInvocation equalsCMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("C"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
                equalsBMethod),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
                equalsCMethod));
    // when
    final Collection<Expression> results = expression.applyAbsorptionLaw();
    // then expect '(a)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(equalsAMethod);
  }

  @Test
  public void shouldNotApplyAbsorptionLawOnAllOperandsOnConditionalOr() {
    // given '(a + (a + b) + (a + c))'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final MethodInvocation equalsCMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("C"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
                equalsBMethod),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
                equalsCMethod));
    // when
    final Collection<Expression> results = expression.applyAbsorptionLaw();
    // then expect no result
    assertThat(results).hasSize(0);
  }

  @Test
  public void shouldApplyAbsorptionLawOnSomeOperandsOnConditionalOr() {
    // given '(a + (a.b) + (a.c) + d)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final MethodInvocation equalsCMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("C"));
    final MethodInvocation equalsDMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("D"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
                equalsBMethod),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
                equalsCMethod),
            equalsDMethod);
    // when
    final Collection<Expression> results = expression.applyAbsorptionLaw();
    // then expect '(a + d)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
        equalsAMethod, equalsDMethod));
  }

  @Test
  public void shouldNotApplyAbsorptionLawOnSomeOperandsOnConditionalOr() {
    // given '(a + (a + b) + (a + c) + d)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final MethodInvocation equalsCMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("C"));
    final MethodInvocation equalsDMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("D"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
                equalsBMethod),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
                equalsCMethod),
            equalsDMethod);
    // when
    final Collection<Expression> results = expression.applyAbsorptionLaw();
    // then expect no result
    assertThat(results).hasSize(0);
  }

  @Test
  public void shouldApplyRedundancyLawOnAllMethodInvocationOperandsOnConditionalAnd() {
    // given '(a.(!a + b).(!a + c))'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation notEqualsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A")).inverse();
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final MethodInvocation equalsCMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("C"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, notEqualsAMethod,
                equalsBMethod),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, notEqualsAMethod,
                equalsCMethod));
    // when
    final Collection<Expression> results = expression.applyRedundancyLaw();
    // then expect '(a.b.c)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
        equalsAMethod, equalsBMethod, equalsCMethod));
  }

  @Test
  public void shouldNotApplyRedundancyLawOnAllOperandsOnConditionalAnd() {
    // given '(a.(a + !b).(a + !c))'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation notEqualsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B")).inverse();
    final MethodInvocation notEqualsCMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("C")).inverse();
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
                notEqualsBMethod),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
                notEqualsCMethod));
    // when
    final Collection<Expression> results = expression.applyRedundancyLaw();
    // then expect no result
    assertThat(results).hasSize(0);
  }

  @Test
  public void shouldApplyRedundancyLawOnSomeOperandsOnConditionalAnd() {
    // given '(a.(!a + b).(!a + c).d)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation notEqualsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A")).inverse();
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final MethodInvocation equalsCMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("C"));
    final MethodInvocation equalsDMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("D"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, notEqualsAMethod,
                equalsBMethod),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, notEqualsAMethod,
                equalsCMethod),
            equalsDMethod);
    // when
    final Collection<Expression> results = expression.applyRedundancyLaw();
    // then expect '(a.b.c.d)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
        equalsAMethod, equalsBMethod, equalsCMethod, equalsDMethod));
  }

  @Test
  public void shouldNotApplyRedundancyLawOnSomeOperandsOnConditionalAnd() {
    // given '(a.(a + b).(a + c).d)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final MethodInvocation equalsCMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("C"));
    final MethodInvocation equalsDMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("D"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
                equalsBMethod),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
                equalsCMethod),
            equalsDMethod);
    // when
    final Collection<Expression> results = expression.applyRedundancyLaw();
    // then expect no result
    assertThat(results).hasSize(0);
  }

  @Test
  public void shouldApplyRedundancyLawOnAllMethodInvocationOperandsOnConditionalOr() {
    // given '(a + (!a.!b) + (!a.c))'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation notEqualsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A")).inverse();
    final MethodInvocation notEqualsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B")).inverse();
    final MethodInvocation equalsCMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("C"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, notEqualsAMethod,
                notEqualsBMethod),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, notEqualsAMethod,
                equalsCMethod));
    // when
    final Collection<Expression> results = expression.applyRedundancyLaw();
    // then expect '(a + !b + c)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
        equalsAMethod, notEqualsBMethod, equalsCMethod));
  }

  @Test
  public void shouldApplyRedundancyLawOnAllComplexInfixExpressionOperandsOnConditionalOr() {
    // given '(p.(!f+!e) + (f.e))' (ie, same as: a.!b + b = a + b)
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final CompoundExpression primitiveIntValueEquals42Expression =
        new CompoundExpression(CompoundExpressionOperator.EQUALS,
            new FieldAccess(var, "primitiveIntValue"), new NumberLiteral(42));
    final CompoundExpression fieldEqualsFooExpression = new CompoundExpression(
        CompoundExpressionOperator.EQUALS, new FieldAccess(var, "field"), new StringLiteral("FOO"));
    final CompoundExpression enumPojoEqualsFooExpression =
        new CompoundExpression(CompoundExpressionOperator.EQUALS, new FieldAccess(var, "enumPojo"),
            new EnumLiteral(EnumPojo.FOO));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
                primitiveIntValueEquals42Expression,
                new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
                    fieldEqualsFooExpression.inverse(), enumPojoEqualsFooExpression.inverse())),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
                fieldEqualsFooExpression, enumPojoEqualsFooExpression));

    // when
    final Collection<Expression> results = expression.applyRedundancyLaw();
    // then expect 'p + (f.e)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
        primitiveIntValueEquals42Expression,
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, fieldEqualsFooExpression,
            enumPojoEqualsFooExpression)));
  }

  @Test
  public void shouldNotApplyRedundancyLawOnAllOperandsOnConditionalOr() {
    // given '(a + (a.b) + (a.c))'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final MethodInvocation equalsCMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("C"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
                equalsBMethod),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
                equalsCMethod));
    // when
    final Collection<Expression> results = expression.applyRedundancyLaw();
    // then expect no result
    assertThat(results).hasSize(0);
  }

  @Test
  public void shouldApplyRedundancyLawOnSomeOperandsOnConditionalOr() {
    // given '(a + (!a.!b) + (!a.c) + d)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation notEqualsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A")).inverse();
    final MethodInvocation notEqualsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B")).inverse();
    final MethodInvocation equalsCMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("C"));
    final MethodInvocation equalsDMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("D"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, notEqualsAMethod,
                notEqualsBMethod),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, notEqualsAMethod,
                equalsCMethod),
            equalsDMethod);
    // when
    final Collection<Expression> results = expression.applyRedundancyLaw();
    // then expect '(a + b + c + d)'
    assertThat(results).hasSize(1);
    assertThat(results).contains(new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
        equalsAMethod, notEqualsBMethod, equalsCMethod, equalsDMethod));
  }

  @Test
  public void shouldNotApplyRedundancyLawOnSomeOperandsOnConditionalOr() {
    // given '(a + (a.b) + (a.c) + d)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final MethodInvocation equalsCMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("C"));
    final MethodInvocation equalsDMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("D"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
                equalsBMethod),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
                equalsCMethod),
            equalsDMethod);
    // when
    final Collection<Expression> results = expression.applyRedundancyLaw();
    // then expect no result
    assertThat(results).hasSize(0);
  }

  @Test
  public void shouldApplyFactorizationLawOnAllOperandsOnConditionalOr() {
    // given '((a.b) + (a.c) + (a.d))'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final MethodInvocation equalsCMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("C"));
    final MethodInvocation equalsDMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("D"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
                equalsBMethod),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
                equalsCMethod),
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
            equalsDMethod));
    // when
    final Collection<Expression> results = expression.applyFactorizationLaw();
    // then expect '(a.(b + c + d))'
    assertThat(results).hasSize(1);
    assertThat(results).contains(new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
        equalsAMethod, new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
            equalsBMethod, equalsCMethod, equalsDMethod)));
  }

  @Test
  public void shouldApplyFactorizationLawOnSomeOperandsOnConditionalOr() {
    // given '((a.b) + (b.d) + (a.c) )'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final MethodInvocation equalsCMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("C"));
    final MethodInvocation equalsDMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("D"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
                equalsBMethod),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
                equalsCMethod),
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsBMethod,
            equalsDMethod));
    // when
    final Collection<Expression> results = expression.applyFactorizationLaw();
    // then expect '(a.(b + c) + (b.d))' and '(b.(a + d) + (a.c))'
    assertThat(results).hasSize(2);
    final CompoundExpression expectedExpression1 =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
                new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsBMethod,
                    equalsCMethod)),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsBMethod,
                equalsDMethod));
    final CompoundExpression expectedExpression2 =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsBMethod,
                new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
                    equalsDMethod)),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
                equalsCMethod));
    assertThat(results).contains(expectedExpression1, expectedExpression2);
  }

  @Test
  public void shouldApplyFactorizationLawOnAllOperandsOnConditionalAnd() {
    // given '((a + b).(a + c).(a + d))'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final MethodInvocation equalsCMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("C"));
    final MethodInvocation equalsDMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("D"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
                equalsBMethod),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
                equalsCMethod),
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
            equalsDMethod));
    // when
    final Collection<Expression> results = expression.applyFactorizationLaw();
    // then expect '(a + (b.c.d))'
    assertThat(results).hasSize(1);
    assertThat(results).contains(new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
        equalsAMethod, new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
            equalsBMethod, equalsCMethod, equalsDMethod)));
  }

  @Test
  public void shouldApplyFactorizationLawOnSomeOperandsOnConditionalAnd() {
    // given '((a + b).(b + d).(a + c))'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsAMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("B"));
    final MethodInvocation equalsCMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("C"));
    final MethodInvocation equalsDMethod =
        new MethodInvocation(var, Object_equals, new StringLiteral("D"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
                equalsBMethod),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
                equalsCMethod),
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsBMethod,
            equalsDMethod));
    // when
    final Collection<Expression> results = expression.applyFactorizationLaw();
    // then expect '((a + (b.c)).(b + d))' and '((b + (a.c)).(a + c))'
    assertThat(results).hasSize(2);
    final CompoundExpression expectedExpression1 =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
                new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsBMethod,
                    equalsCMethod)),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsBMethod,
                equalsDMethod));
    final CompoundExpression expectedExpression2 =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsBMethod,
                new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
                    equalsDMethod)),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
                equalsCMethod));
    assertThat(results).contains(expectedExpression1, expectedExpression2);
  }

  // ************************************************************
  // more global tests including mixtures of expressions.
  // ************************************************************
  @Test
  public void shouldSimplifyMixOfContionalOrIncludingConditionalAndOperandsWithFieldAccesses() {
    // given ((!a.c) + (a.b) + (a.!b.c))
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final FieldAccess fieldA = new FieldAccess(var, "fieldA");
    final FieldAccess fieldB = new FieldAccess(var, "fieldB");
    final FieldAccess fieldC = new FieldAccess(var, "fieldC");
    final MethodInvocation equalsAMethod =
        new MethodInvocation(fieldA, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(fieldB, Object_equals, new StringLiteral("B"));
    final MethodInvocation equalsCMethod =
        new MethodInvocation(fieldC, Object_equals, new StringLiteral("C"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
                equalsAMethod.inverse(), equalsCMethod),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
                equalsBMethod),
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
            equalsBMethod.inverse(), equalsCMethod));
    // when
    final Expression result = expression.simplify();
    // then expect ((a.b) + c)
    final CompoundExpression expectedExpression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
                equalsBMethod),
            equalsCMethod);
    assertThat(result).isEqualTo(expectedExpression);
  }

  @Test
  public void shouldSimplifyUsingFactorizationThenUnaryOperatorLawsOnConditionalOr() {
    // given ((!a.c) + (a.b) + (a.c))
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final FieldAccess fieldA = new FieldAccess(var, "fieldA");
    final FieldAccess fieldB = new FieldAccess(var, "fieldB");
    final FieldAccess fieldC = new FieldAccess(var, "fieldC");
    final MethodInvocation equalsAMethod =
        new MethodInvocation(fieldA, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(fieldB, Object_equals, new StringLiteral("B"));
    final MethodInvocation equalsCMethod =
        new MethodInvocation(fieldC, Object_equals, new StringLiteral("C"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
                equalsAMethod.inverse(), equalsCMethod),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
                equalsBMethod),
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
            equalsCMethod));
    // when
    final Expression result = expression.simplify();
    // then expect ((a.b) || c)
    final CompoundExpression expectedExpression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsCMethod,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
                equalsBMethod));
    assertThat(result).isEqualTo(expectedExpression);
  }

  @Test
  public void shouldSimplifyUsingFactorizationThenUnaryOperatorLawsOnConditionalAnd() {
    // given ((!a + c).(a + b).(a + c))
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final FieldAccess fieldA = new FieldAccess(var, "fieldA");
    final FieldAccess fieldB = new FieldAccess(var, "fieldB");
    final FieldAccess fieldC = new FieldAccess(var, "fieldC");
    final MethodInvocation equalsAMethod =
        new MethodInvocation(fieldA, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(fieldB, Object_equals, new StringLiteral("B"));
    final MethodInvocation equalsCMethod =
        new MethodInvocation(fieldC, Object_equals, new StringLiteral("C"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
                equalsAMethod.inverse(), equalsCMethod),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
                equalsBMethod),
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
            equalsCMethod));
    // when
    final Expression result = expression.simplify();
    // then expect ((a + b).c)
    final CompoundExpression expectedExpression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsCMethod,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
                equalsBMethod));
    assertThat(result).isEqualTo(expectedExpression);
  }

  @Test
  public void shouldNotSimplifyMixOfContionalOrIncludingConditionalAndOperandsWithFieldAccesses() {
    // given '((a . b) + c))'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final FieldAccess fieldA = new FieldAccess(var, "fieldA");
    final FieldAccess fieldB = new FieldAccess(var, "fieldB");
    final FieldAccess fieldC = new FieldAccess(var, "fieldC");
    final MethodInvocation equalsAMethod =
        new MethodInvocation(fieldA, Object_equals, new StringLiteral("A"));
    final MethodInvocation equalsBMethod =
        new MethodInvocation(fieldB, Object_equals, new StringLiteral("B"));
    final MethodInvocation equalsCMethod =
        new MethodInvocation(fieldC, Object_equals, new StringLiteral("C"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsBMethod,
                equalsCMethod));
    // when
    final Expression result = expression.simplify();
    // then expect same expression
    final CompoundExpression expectedExpression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsBMethod,
                equalsCMethod));
    assertThat(result).isEqualTo(expectedExpression);
  }

  @Test
  public void shouldApplyDistributiveLawOnConditionalOrWithTwoMethodInvocationOperands() {
    // given '(foo.(bar + !baz)) + (!foo.!baz)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final FieldAccess fieldF = new FieldAccess(var, "f");
    final MethodInvocation equalsFoo =
        new MethodInvocation(fieldF, Object_equals, new StringLiteral("foo"));
    final MethodInvocation equalsBar =
        new MethodInvocation(fieldF, Object_equals, new StringLiteral("bar"));
    final MethodInvocation equalsBaz =
        new MethodInvocation(fieldF, Object_equals, new StringLiteral("baz"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsFoo,
                new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsBar,
                    equalsBaz.inverse())),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsFoo.inverse(),
                equalsBaz.inverse()));
    // when
    final Collection<Expression> results = expression.applyDistributiveLaw();
    // then expect '(foo.bar + foo.!baz) + (!foo.!baz)'
    final CompoundExpression expectedExpression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
                new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsFoo,
                    equalsBar),
                new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsFoo,
                    equalsBaz.inverse())),
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsFoo.inverse(),
            equalsBaz.inverse()));
    assertThat(results).hasSize(1);
    assertThat(results).contains(expectedExpression);
  }

  @Test
  public void shouldApplyDistributiveLawOnConditionalOrWithTwoInfixExpressionOperands() {
    // given '((p.(f+ (!f.e))) + (!p.e))'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final CompoundExpression primitiveIntValueEquals42Expression =
        new CompoundExpression(CompoundExpressionOperator.EQUALS,
            new FieldAccess(var, "primitiveIntValue"), new NumberLiteral(42));
    final CompoundExpression fieldEqualsFooExpression = new CompoundExpression(
        CompoundExpressionOperator.EQUALS, new FieldAccess(var, "field"), new StringLiteral("FOO"));
    final CompoundExpression enumPojoEqualsFooExpression =
        new CompoundExpression(CompoundExpressionOperator.EQUALS, new FieldAccess(var, "enumPojo"),
            new EnumLiteral(EnumPojo.FOO));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
                primitiveIntValueEquals42Expression,
                new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
                    fieldEqualsFooExpression,
                    new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
                        fieldEqualsFooExpression.inverse(), enumPojoEqualsFooExpression))),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
                primitiveIntValueEquals42Expression.inverse(), enumPojoEqualsFooExpression));

    // when
    final Collection<Expression> results = expression.applyDistributiveLaw();
    // then expect should expect: '((p.f) + (p.!f.e) + (!p.e))'
    final CompoundExpression expectedExpression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
                new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
                    primitiveIntValueEquals42Expression, fieldEqualsFooExpression),
                new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
                    primitiveIntValueEquals42Expression,
                    new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
                        fieldEqualsFooExpression.inverse(), enumPojoEqualsFooExpression))),
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
            primitiveIntValueEquals42Expression.inverse(), enumPojoEqualsFooExpression));
    assertThat(results).hasSize(1);
    assertThat(results).contains(expectedExpression);
  }

  @Test
  public void shouldNotApplyDistributiveLawOnConditionalAndWithThreeOperands() {
    // given '((foo.bar) + (foo.(!bar.!baz)))'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsFoo =
        new MethodInvocation(var, Object_equals, new StringLiteral("foo"));
    final MethodInvocation equalsBar =
        new MethodInvocation(var, Object_equals, new StringLiteral("bar"));
    final MethodInvocation equalsBaz =
        new MethodInvocation(var, Object_equals, new StringLiteral("baz"));
    final CompoundExpression expression = new CompoundExpression(
        CompoundExpressionOperator.CONDITIONAL_OR,
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsFoo, equalsBar),
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsFoo,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsBar.inverse(),
                equalsBaz.inverse())));
    // when
    final Collection<Expression> results = expression.applyDistributiveLaw();
    // then expect no result (associative law may be used, but not in this test)
    assertThat(results).hasSize(0);
  }


  @Test
  public void shouldSimplifyToConditionalOrWithThreeOperands() {
    // given (foo + (!foo.bar) + (!foo.!bar.baz))
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation equalsFoo =
        new MethodInvocation(var, Object_equals, new StringLiteral("foo"));
    final MethodInvocation equalsBar =
        new MethodInvocation(var, Object_equals, new StringLiteral("bar"));
    final MethodInvocation equalsBaz =
        new MethodInvocation(var, Object_equals, new StringLiteral("baz"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsFoo,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsFoo.inverse(),
                equalsBar),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsFoo.inverse(),
                equalsBar.inverse(), equalsBaz));
    // when
    final Expression result = expression.simplify();
    // then expect (foo || bar || baz)
    final CompoundExpression expectedExpression = new CompoundExpression(
        CompoundExpressionOperator.CONDITIONAL_OR, equalsFoo, equalsBar, equalsBaz);
    assertThat(result).isEqualTo(expectedExpression);
  }

  @Test
  public void shouldNotApplyDistributiveLawOnConditionalOrWithThreeOperands() {
    // given '(foo + (!foo.bar) + (!foo.bar.!baz)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation getStringValueMethod =
        new MethodInvocation(var, TestPojo_getStringValue);
    final CompoundExpression equalsFooExpr = new CompoundExpression(
        CompoundExpressionOperator.EQUALS, getStringValueMethod, new StringLiteral("foo"));
    final CompoundExpression equalsBarExpr = new CompoundExpression(
        CompoundExpressionOperator.EQUALS, getStringValueMethod, new StringLiteral("bar"));
    final CompoundExpression equalsBazExpr = new CompoundExpression(
        CompoundExpressionOperator.EQUALS, getStringValueMethod, new StringLiteral("baz"));
    final CompoundExpression expression =
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsFooExpr,
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
                equalsFooExpr.inverse(), equalsBarExpr),
            new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
                equalsFooExpr.inverse(), equalsBarExpr.inverse(), equalsBazExpr));

    // when
    final Collection<Expression> results = expression.applyDistributiveLaw();
    // then expect nothing
    assertThat(results).hasSize(0);
  }

  @Test
  public void shouldSimplifyToConditionalOrWithThreeOperands2() {
    // given '(foo + (!foo.bar) + (!foo.bar.!baz)'
    final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
    final MethodInvocation getStringValueMethod =
        new MethodInvocation(var, TestPojo_getStringValue);
    final MethodInvocation getEnumPojoMethod = new MethodInvocation(var, TestPojo_getEnumPojo);
    final MethodInvocation getPrimitiveIntMethod =
        new MethodInvocation(var, TestPojo_getPrimitiveIntValue);
    final CompoundExpression getStringValueMethodEqualsFoo = new CompoundExpression(
        CompoundExpressionOperator.EQUALS, getStringValueMethod, new StringLiteral("foo"));
    final CompoundExpression getEnumPojoMethodEqualsBar = new CompoundExpression(
        CompoundExpressionOperator.EQUALS, getEnumPojoMethod, new EnumLiteral(EnumPojo.BAR));
    final CompoundExpression getPrimitiveIntMethodEquals42 = new CompoundExpression(
        CompoundExpressionOperator.EQUALS, getPrimitiveIntMethod, new NumberLiteral(42));
    final CompoundExpression expression = new CompoundExpression(
        CompoundExpressionOperator.CONDITIONAL_OR, getPrimitiveIntMethodEquals42,
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
            getPrimitiveIntMethodEquals42.inverse(), getEnumPojoMethodEqualsBar),
        new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
            getPrimitiveIntMethodEquals42.inverse(), getEnumPojoMethodEqualsBar.inverse(),
            getStringValueMethodEqualsFoo));
    // when
    final Expression result = expression.simplify();
    // then expect (foo || bar || baz)
    final CompoundExpression expectedExpression = new CompoundExpression(
        CompoundExpressionOperator.CONDITIONAL_OR, getPrimitiveIntMethodEquals42,
        getEnumPojoMethodEqualsBar, getStringValueMethodEqualsFoo);
    assertThat(result).isEqualTo(expectedExpression);
  }
}

