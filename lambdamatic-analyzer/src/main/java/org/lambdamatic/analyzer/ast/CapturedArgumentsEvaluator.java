/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast;

import static org.lambdamatic.analyzer.ast.node.Expression.ExpressionType.CAPTURED_ARGUMENT_REF;
import static org.lambdamatic.analyzer.ast.node.Expression.ExpressionType.LOCAL_VARIABLE;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.util.List;

import org.lambdamatic.analyzer.ast.node.CapturedArgument;
import org.lambdamatic.analyzer.ast.node.CapturedArgumentRef;
import org.lambdamatic.analyzer.ast.node.ComplexExpression;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.Expression.ExpressionType;
import org.lambdamatic.analyzer.ast.node.ExpressionFactory;
import org.lambdamatic.analyzer.ast.node.ExpressionVisitor;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.LambdaExpression;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.lambdamatic.analyzer.ast.node.ObjectInstance;
import org.lambdamatic.analyzer.exception.AnalyzeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ExpressionVisitor} that will replace the given {@link CapturedArgument} with their
 * actual values in the visited {@link Expression}.
 * 
 */
public class CapturedArgumentsEvaluator extends ExpressionVisitor {

  /** The logger. */
  private static final Logger LOGGER = LoggerFactory.getLogger(CapturedArgumentsEvaluator.class);

  /**
   * the captured arguments provided by the {@link SerializedLambda}.
   */
  private final List<Object> capturedArgs;

  /**
   * Constructor.
   * 
   * @param capturedArgs the captured arguments provided by the {@link SerializedLambda}
   */
  public CapturedArgumentsEvaluator(final List<Object> capturedArgs) {
    this.capturedArgs = capturedArgs;
  }

  @Override
  public boolean visitCapturedArgumentRef(final CapturedArgumentRef capturedArgumentRef) {
    final int index = capturedArgumentRef.getArgumentIndex();
    final Object capturedArgument = this.capturedArgs.get(index);
    final Expression replacement = ExpressionFactory.getExpression(capturedArgument);
    capturedArgumentRef.getParent().replaceElement(capturedArgumentRef, replacement);
    // no need to process further
    return false;
  }

  @Override
  public boolean visitObjectValue(ObjectInstance expr) {
    return super.visitObjectValue(expr);
  }

  @Override
  public boolean visitMethodInvocationExpression(final MethodInvocation methodInvocation) {
    // only methods *not* using (unresolved) Captured Arg reference or Local Variable can be
    // evaluated
    if (methodInvocation.anyElementMatches(CAPTURED_ARGUMENT_REF)
        || methodInvocation.anyElementMatches(LOCAL_VARIABLE)) {
      return true;
    }
    final Object replacement = methodInvocation.evaluate();
    final ComplexExpression parentExpression = methodInvocation.getParent();
    if (parentExpression != null) {
      parentExpression.replaceElement(methodInvocation,
          ExpressionFactory.getExpression(replacement));
    }
    return false;
  }

  @Override
  public boolean visitFieldAccessExpression(final FieldAccess fieldAccess) {
    if (fieldAccess.getSource().getExpressionType() == ExpressionType.OBJECT_INSTANCE) {
      final String fieldName = fieldAccess.getFieldName();
      try {
        final Object source = fieldAccess.getSource().getValue();
        final Field f = ReflectionUtils.getFieldToInvoke(source, fieldName);
        f.setAccessible(true);
        final Object replacement = f.get(source);
        final ComplexExpression parentExpression = fieldAccess.getParent();
        if (parentExpression != null) {
          final Expression fieldAccessReplacement = ExpressionFactory.getExpression(replacement);
          LOGGER.trace(" replacing {} ({}) with {} ({})", fieldAccess,
              fieldAccess.getExpressionType(), fieldAccessReplacement,
              fieldAccessReplacement.getExpressionType());
          parentExpression.replaceElement(fieldAccess, fieldAccessReplacement);
        }
        // no further visiting on this (obsolete) branch of the expression tree.
        return false;
      } catch (NoSuchFieldException | SecurityException | IllegalAccessException
          | IllegalArgumentException e) {
        throw new AnalyzeException("Failed to execute method '" + fieldName
            + "' on captured argument '" + fieldAccess.getSource().getValue() + "'", e);
      }
    }
    return super.visitFieldAccessExpression(fieldAccess);
  }

  @Override
  public boolean visitLambdaExpression(final LambdaExpression lambdaExpression) {
    // run another CapturedArgumentsEvaluator on the given lambdaExpression
    lambdaExpression.getBody().stream()
        .forEach(s -> s.accept(new StatementExpressionsDelegateVisitor(
            new CapturedArgumentsEvaluator(this.capturedArgs))));
    // final ComplexExpression parentExpression = lambdaExpression.getParent();
    // parentExpression.replaceElement(lambdaExpression, new LambdaExpression(evaluatedExpression,
    // lambdaExpression.getArgumentType(), lambdaExpression.getArgumentName()));
    // no need to further visit this expression.
    return false;
  }
}
