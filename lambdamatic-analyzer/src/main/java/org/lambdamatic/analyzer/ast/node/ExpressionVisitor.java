/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

/**
 * the base class for all {@link Expression} visitors.
 * 
 * @author Xavier Coulon
 *
 */
public abstract class ExpressionVisitor {

  /**
   * Dispatch to the other visitXYZ methods
   * <p>
   * This method should not be called directly. Users should call
   * {@link Expression#accept(ExpressionVisitor)} instead.
   * </p>
   * 
   * @param expr the {@link Expression} to visit
   * @return {@code true} if the visit on the given {@link Expression}'s children should continue,
   *         {@code false} otherwise.
   * 
   * @see Expression#accept(ExpressionVisitor)
   */
  public boolean visit(final Expression expr) {
    if (expr != null) {
      switch (expr.getExpressionType()) {
        case ASSIGNMENT:
          return visitAssignment((Assignment) expr);
        case BOOLEAN_LITERAL:
          return visitBooleanLiteralExpression((BooleanLiteral) expr);
        case CHARACTER_LITERAL:
          return visitCharacterLiteralExpression((CharacterLiteral) expr);
        case FIELD_ACCESS:
          return visitFieldAccessExpression((FieldAccess) expr);
        case COMPOUND:
          return visitInfixExpression((CompoundExpression) expr);
        case INSTANCE_OF:
          return visitInstanceOfExpression((InstanceOf) expr);
        case OBJECT_INSTANCIATION:
          return visitObjectVariableExpression((ObjectInstanciation) expr);
        case ARRAY_VARIABLE:
          return visitArrayVariableExpression((ArrayVariable) expr);
        case METHOD_INVOCATION:
          return visitMethodInvocationExpression((MethodInvocation) expr);
        case NULL_LITERAL:
          return visitNullLiteralExpression((NullLiteral) expr);
        case NUMBER_LITERAL:
          return visitNumberLiteralExpression((NumberLiteral) expr);
        case STRING_LITERAL:
          return visitStringLiteralExpression((StringLiteral) expr);
        case ENUM_LITERAL:
          return visitEnumLiteralExpression((EnumLiteral) expr);
        case CLASS_LITERAL:
          return visitClassLiteralExpression((ClassLiteral) expr);
        case OBJECT_INSTANCE:
          return visitObjectValue((ObjectInstance) expr);
        case CAPTURED_ARGUMENT:
          return visitCapturedArgument((CapturedArgument) expr);
        case CAPTURED_ARGUMENT_REF:
          return visitCapturedArgumentRef((CapturedArgumentRef) expr);
        case LOCAL_VARIABLE:
          return visitLocalVariable((LocalVariable) expr);
        case LAMBDA_EXPRESSION:
          return visitLambdaExpression((LambdaExpression) expr);
        default:
          break;
      }
    }
    return true;
  }

  /**
   * @param expr the {@link Assignment} to visit.
   * @return <code>true</code> to visit any existing child expressions, <code>false</code>
   *         otherwise. <code>true</code> by default. Can be overridden.
   */
  @SuppressWarnings("static-method")
  public boolean visitAssignment(final Assignment expr) {
    return true;
  }

  /**
   * @param expr the {@link LambdaExpression} to visit.
   * @return <code>true</code> to visit any existing child expressions, <code>false</code>
   *         otherwise. <code>true</code> by default. Can be overridden.
   */
  @SuppressWarnings("static-method")
  public boolean visitLambdaExpression(final LambdaExpression expr) {
    return true;
  }

  /**
   * @param expr the {@link LocalVariable} to visit.
   * @return <code>true</code> to visit any existing child expressions, <code>false</code>
   *         otherwise. <code>true</code> by default. Can be overridden.
   */
  @SuppressWarnings("static-method")
  public boolean visitLocalVariable(final LocalVariable expr) {
    return true;
  }

  /**
   * @param expr the {@link ObjectInstance} to visit.
   * @return <code>true</code> to visit any existing child expressions, <code>false</code>
   *         otherwise. <code>true</code> by default. Can be overridden.
   */
  @SuppressWarnings("static-method")
  public boolean visitObjectValue(final ObjectInstance expr) {
    return true;
  }

  /**
   * @param expr the {@link CapturedArgument} to visit.
   * @return <code>true</code> to visit any existing child expressions, <code>false</code>
   *         otherwise. <code>true</code> by default. Can be overridden.
   */
  @SuppressWarnings("static-method")
  public boolean visitCapturedArgument(final CapturedArgument expr) {
    return true;
  }

  /**
   * @param expr the {@link CapturedArgumentRef} to visit.
   * @return <code>true</code> to visit any existing child expressions, <code>false</code>
   *         otherwise. <code>true</code> by default. Can be overridden.
   */
  @SuppressWarnings("static-method")
  public boolean visitCapturedArgumentRef(final CapturedArgumentRef expr) {
    return true;
  }

  /**
   * @param expr the {@link BooleanLiteral} to visit.
   * @return <code>true</code> to visit any existing child expressions, <code>false</code>
   *         otherwise. <code>true</code> by default. Can be overridden.
   */
  @SuppressWarnings("static-method")
  public boolean visitBooleanLiteralExpression(final BooleanLiteral expr) {
    return true;
  }

  /**
   * @param expr the {@link CharacterLiteral} to visit.
   * @return <code>true</code> to visit any existing child expressions, <code>false</code>
   *         otherwise. <code>true</code> by default. Can be overridden.
   */
  @SuppressWarnings("static-method")
  public boolean visitCharacterLiteralExpression(final CharacterLiteral expr) {
    return true;
  }

  /**
   * @param expr the {@link FieldAccess} to visit.
   * @return <code>true</code> to visit any existing child expressions, <code>false</code>
   *         otherwise. <code>true</code> by default. Can be overridden.
   */
  @SuppressWarnings("static-method")
  public boolean visitFieldAccessExpression(final FieldAccess expr) {
    return true;
  }

  /**
   * @param expr the {@link CompoundExpression} to visit.
   * @return <code>true</code> to visit any existing child expressions, <code>false</code>
   *         otherwise. <code>true</code> by default. Can be overridden.
   */
  @SuppressWarnings("static-method")
  public boolean visitInfixExpression(final CompoundExpression expr) {
    return true;
  }

  /**
   * @param expr the {@link InstanceOf} to visit.
   * @return <code>true</code> to visit any existing child expressions, <code>false</code>
   *         otherwise. <code>true</code> by default. Can be overridden.
   */
  @SuppressWarnings("static-method")
  public boolean visitInstanceOfExpression(final InstanceOf expr) {
    return true;
  }

  /**
   * @param expr the {@link ObjectInstanciation} to visit.
   * @return <code>true</code> to visit any existing child expressions, <code>false</code>
   *         otherwise. <code>true</code> by default. Can be overridden.
   */
  @SuppressWarnings("static-method")
  public boolean visitObjectVariableExpression(final ObjectInstanciation expr) {
    return true;
  }

  /**
   * @param expr the {@link ArrayVariable} to visit.
   * @return <code>true</code> to visit any existing child expressions, <code>false</code>
   *         otherwise. <code>true</code> by default. Can be overridden.
   */
  @SuppressWarnings("static-method")
  public boolean visitArrayVariableExpression(final ArrayVariable expr) {
    return true;
  }

  /**
   * @param expr the {@link MethodInvocation} to visit.
   * @return <code>true</code> to visit any existing child expressions, <code>false</code>
   *         otherwise. <code>true</code> by default. Can be overridden.
   */
  @SuppressWarnings("static-method")
  public boolean visitMethodInvocationExpression(final MethodInvocation expr) {
    return true;
  }

  /**
   * @param expr the {@link NullLiteral} to visit.
   * @return <code>true</code> to visit any existing child expressions, <code>false</code>
   *         otherwise. <code>true</code> by default. Can be overridden.
   */
  @SuppressWarnings("static-method")
  public boolean visitNullLiteralExpression(final NullLiteral expr) {
    return true;
  }

  /**
   * @param expr the {@link NumberLiteral} to visit.
   * @return <code>true</code> to visit any existing child expressions, <code>false</code>
   *         otherwise. <code>true</code> by default. Can be overridden.
   */
  @SuppressWarnings("static-method")
  public boolean visitNumberLiteralExpression(final NumberLiteral expr) {
    return true;
  }

  /**
   * @param expr the {@link StringLiteral} to visit.
   * @return <code>true</code> to visit any existing child expressions, <code>false</code>
   *         otherwise. <code>true</code> by default. Can be overridden.
   */
  @SuppressWarnings("static-method")
  public boolean visitStringLiteralExpression(final StringLiteral expr) {
    return true;
  }

  /**
   * @param expr the {@link EnumLiteral} to visit.
   * @return <code>true</code> to visit any existing child expressions, <code>false</code>
   *         otherwise. <code>true</code> by default. Can be overridden.
   */
  @SuppressWarnings("static-method")
  public boolean visitEnumLiteralExpression(final EnumLiteral expr) {
    return true;
  }

  /**
   * @param expr the {@link ClassLiteral} to visit.
   * @return <code>true</code> to visit any existing child expressions, <code>false</code>
   *         otherwise. <code>true</code> by default. Can be overridden.
   */
  @SuppressWarnings("static-method")
  public boolean visitClassLiteralExpression(final ClassLiteral expr) {
    return true;
  }
}
