/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer;

import static org.lambdamatic.testutils.JavaMethods.Object_equals;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_elementMatch;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lambdamatic.SerializableConsumer;
import org.lambdamatic.analyzer.ast.node.ExpressionStatement;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.CompoundExpression;
import org.lambdamatic.analyzer.ast.node.CompoundExpression.CompoundExpressionOperator;
import org.lambdamatic.analyzer.ast.node.LambdaExpression;
import org.lambdamatic.analyzer.ast.node.LocalVariable;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.lambdamatic.analyzer.ast.node.ReturnStatement;
import org.lambdamatic.analyzer.ast.node.Statement;
import org.lambdamatic.analyzer.ast.node.StringLiteral;

import com.sample.model.TestPojo;

import net.jcip.annotations.NotThreadSafe;

/**
 * Test on caching while analyzing {@link SerializableConsumer}.
 * 
 * @author Xavier Coulon
 */
@NotThreadSafe
public class SerializableConsumerBytecodeAnalyzeCacheTest {

  private LambdaExpressionAnalyzerListenerImpl listener;

  private LambdaExpressionAnalyzer lambdaAnalyzer;

  /**
   * Register listeners.
   */
  @Before
  public void registerListener() {
    listener = new LambdaExpressionAnalyzerListenerImpl();
    lambdaAnalyzer = LambdaExpressionAnalyzer.getInstance();
    lambdaAnalyzer.addListener(listener);
  }

  @After
  public void unregisterListener() {
    lambdaAnalyzer.removeListener(listener);
  }

  @Test
  public void shouldNotAnalyzeTwice() throws NoSuchMethodException, SecurityException {
    // given
    final LocalVariable e = new LocalVariable(1, "e", TestPojo.class);
    final LocalVariable t = new LocalVariable(2, "test", TestPojo.class);

    // when (first call)
    final LambdaExpression lambdaExpression1 = getLambdaExpression();
    // then
    final MethodInvocation fieldEqualsJohnMethod =
        new MethodInvocation(new FieldAccess(e, "field"), Object_equals, new StringLiteral("john"));
    final MethodInvocation fieldEqualsJackMethod =
        new MethodInvocation(new FieldAccess(e, "field"), Object_equals, new StringLiteral("jack"));
    final CompoundExpression infixExpression = new CompoundExpression(
        CompoundExpressionOperator.CONDITIONAL_OR, fieldEqualsJohnMethod, fieldEqualsJackMethod);
    final LambdaExpression nestedExpression =
        new LambdaExpression(new ReturnStatement(infixExpression), TestPojo.class, "e");
    final MethodInvocation elementMatchMethod =
        new MethodInvocation(t, TestPojo_elementMatch, nestedExpression);
    final Statement expectedStmt = new ExpressionStatement(elementMatchMethod);
    Assertions.assertThat(lambdaExpression1.getBody()).containsExactly(expectedStmt);
    // 2 cache misses: one per lambda expression in: t -> t.elementMatch(e -> e.field.equals("john")
    // || e.field.equals("jack"))
    Assertions.assertThat(listener.getCacheMisses()).isEqualTo(2);
    Assertions.assertThat(listener.getCacheHits()).isEqualTo(0);
    // given
    listener.resetHitCounters();
    // when (second call)
    final LambdaExpression lambdaExpression2 = getLambdaExpression();
    // then
    Assertions.assertThat(lambdaExpression2.getBody()).containsExactly(expectedStmt);
    Assertions.assertThat(listener.getCacheMisses()).isEqualTo(0);
    // one cache hit for the whole Lambda expression (ie, including nested lambda)
    Assertions.assertThat(listener.getCacheHits()).isEqualTo(1);
  }

  @Test
  public void shouldNotAnalyzeTwiceWithCapturedArguments()
      throws NoSuchMethodException, SecurityException {
    // given
    final LocalVariable e = new LocalVariable(0, "e", TestPojo.class);
    final LocalVariable test = new LocalVariable(1, "test", TestPojo.class);

    // when (first call)
    final LambdaExpression lambdaExpression1 = getLambdaExpression("john1", "jack1");
    // then
    final MethodInvocation fieldEqualsJohn1Method = new MethodInvocation(
        new FieldAccess(e, "field"), Object_equals, new StringLiteral("john1"));
    final MethodInvocation fieldEqualsJack1Method = new MethodInvocation(
        new FieldAccess(e, "field"), Object_equals, new StringLiteral("jack1"));
    final CompoundExpression infixExpression = new CompoundExpression(
        CompoundExpressionOperator.CONDITIONAL_OR, fieldEqualsJohn1Method, fieldEqualsJack1Method);
    final LambdaExpression nestedExpression1 =
        new LambdaExpression(new ReturnStatement(infixExpression), TestPojo.class, "e");
    final MethodInvocation elementMatchMethod1 =
        new MethodInvocation(test, TestPojo_elementMatch, nestedExpression1);
    final Statement expectedStmt1 = new ExpressionStatement(elementMatchMethod1);
    Assertions.assertThat(lambdaExpression1.getBody()).containsExactly(expectedStmt1);
    Assertions.assertThat(listener.getCacheMisses()).isEqualTo(2);
    Assertions.assertThat(listener.getCacheHits()).isEqualTo(0);
    // given
    listener.resetHitCounters();
    // when (second call)
    final LambdaExpression lambdaExpression2 = getLambdaExpression("john2", "jack2");
    // then
    final MethodInvocation fieldEqualsJohn2Method = new MethodInvocation(
        new FieldAccess(e, "field"), Object_equals, new StringLiteral("john2"));
    final MethodInvocation fieldEqualsJack2Method = new MethodInvocation(
        new FieldAccess(e, "field"), Object_equals, new StringLiteral("jack2"));
    final CompoundExpression infixExpression2 = new CompoundExpression(
        CompoundExpressionOperator.CONDITIONAL_OR, fieldEqualsJohn2Method, fieldEqualsJack2Method);
    final LambdaExpression nestedExpression2 =
        new LambdaExpression(new ReturnStatement(infixExpression2), TestPojo.class, "e");
    final MethodInvocation elementMatchMethod2 =
        new MethodInvocation(test, TestPojo_elementMatch, nestedExpression2);
    final Statement expectedStmt2 = new ExpressionStatement(elementMatchMethod2);
    Assertions.assertThat(lambdaExpression2.getBody()).containsExactly(expectedStmt2);
    Assertions.assertThat(listener.getCacheMisses()).isEqualTo(0);
    // one cache hit for the whole Lambda expression (ie, including nested lambda)
    Assertions.assertThat(listener.getCacheHits()).isEqualTo(1);

  }

  private LambdaExpression getLambdaExpression() {
    // given
    final SerializableConsumer<TestPojo> expr =
        (SerializableConsumer<TestPojo>) ((TestPojo test) -> test
            .elementMatch(e -> e.field.equals("john") || e.field.equals("jack")));
    // when
    return LambdaExpressionAnalyzer.getInstance().analyzeExpression(expr);
  }

  private LambdaExpression getLambdaExpression(final String stringField1,
      final String stringField2) {
    // given
    final SerializableConsumer<TestPojo> expr =
        (SerializableConsumer<TestPojo>) ((TestPojo test) -> test
            .elementMatch(e -> e.field.equals(stringField1) || e.field.equals(stringField2)));
    // when
    return LambdaExpressionAnalyzer.getInstance().analyzeExpression(expr);
  }

}
