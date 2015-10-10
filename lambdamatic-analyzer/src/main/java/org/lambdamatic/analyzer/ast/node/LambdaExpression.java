/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.lambdamatic.analyzer.ast.StatementExpressionsDelegateVisitor;

/**
 * The AST form of the user-defined Lambda Expression and its relevant data in a form that can be
 * further manipulated.
 * 
 * @author Xavier Coulon
 *
 */
public class LambdaExpression extends Expression {

  /**
   * The AST form of the {@link List} of {@link Statement} defined in the Lambda Expression (with
   * captured arguments).
   */
  private final List<Statement> body;

  /**
   * The type of the element being evaluated in the AST form of the user-defined Lambda Expression.
   */
  private final Class<?> argumentType;

  /**
   * The name of the element being evaluated in the AST form of the user-defined Lambda Expression.
   */
  private final String argumentName;

  /**
   * Constructor with a single Statement (that will be wrapped into a {@link List} of
   * {@link Statement}).
   * 
   * @param statement The AST form of the {@link Statement}
   * @param argumentType The type of the Lambda Expression argument
   * @param argumentName the name of the Lambda Expression argument
   */
  public LambdaExpression(final Statement statement, final Class<?> argumentType,
      final String argumentName) {
    this(generateId(), statement, argumentType, argumentName);
  }

  /**
   * Constructor.
   * 
   * @param id the synthetic id
   * @param statement The AST form of the {@link Statement}
   * @param argumentType The type of the Lambda Expression argument
   * @param argumentName the name of the Lambda Expression argument
   */
  public LambdaExpression(final int id, final Statement statement, final Class<?> argumentType,
      final String argumentName) {
    this(id, Arrays.asList(statement), argumentType, argumentName);
  }

  /**
   * Constructor.
   * 
   * @param statements The {@link List} of {@link Statement} defined in the Lambda Expression.
   * @param argumentType The type of the element being evaluated in the AST form of the user-defined
   *        Lambda Expression.
   * @param argumentName the name of the Lambda Expression argument
   */
  public LambdaExpression(final List<Statement> statements, final Class<?> argumentType,
      final String argumentName) {
    super(generateId(), false);
    this.body = statements;
    this.argumentType = argumentType;
    this.argumentName = argumentName;
  }

  /**
   * Constructor with id.
   *
   * @param id the synthetic id of this {@link Expression}
   * @param statements The {@link List} of {@link Statement} defined in the Lambda Expression
   * @param argumentType The type of the element being evaluated in the AST form of the user-defined
   *        Lambda Expression
   * @param argumentName the name of the Lambda Expression argument
   */
  public LambdaExpression(final int id, final List<Statement> statements,
      final Class<?> argumentType, final String argumentName) {
    super(id, false);
    this.body = statements;
    this.argumentType = argumentType;
    this.argumentName = argumentName;
  }

  @Override
  public ExpressionType getExpressionType() {
    return ExpressionType.LAMBDA_EXPRESSION;
  }

  @Override
  public ComplexExpression getParent() {
    return (ComplexExpression) super.getParent();
  }

  @Override
  public Class<?> getJavaType() {
    return this.argumentType;
  }

  @Override
  public boolean canBeInverted() {
    return false;
  }

  @Override
  public LambdaExpression duplicate(int id) {
    final List<Statement> duplicateStatements =
        this.body.stream().map(s -> s.duplicate()).collect(Collectors.toList());
    return new LambdaExpression(id, duplicateStatements, this.argumentType, this.argumentName);
  }

  @Override
  public LambdaExpression duplicate() {
    return this.duplicate(generateId());
  }

  /**
   * @return The AST form of the {@link List} of {@link Statement} defined in the Lambda Expression
   *         (with captured arguments).
   */
  public List<Statement> getBody() {
    return this.body;
  }

  /**
   * @return The type of the element being evaluated in the AST form of the user-defined Lambda
   *         Expression.
   */
  public Class<?> getArgumentType() {
    return this.argumentType;
  }

  /**
   * @return The name of the element being evaluated in the AST form of the user-defined Lambda
   *         Expression.
   */
  public String getArgumentName() {
    return this.argumentName;
  }

  @Override
  public String toString() {
    return this.argumentName + " -> {"
        + String.join(" ", this.body.stream().map(s -> s.toString()).collect(Collectors.toList()))
        + "}";
  }

  @Override
  public boolean anyElementMatches(final ExpressionType type) {
    final ExpressionTypeMatcher matcher = new ExpressionTypeMatcher(type);
    this.body.stream().forEach(s -> s.accept(new StatementExpressionsDelegateVisitor(matcher)));
    return matcher.isMatchFound();

  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.argumentType == null) ? 0 : this.argumentType.hashCode());
    result = prime * result + ((this.body == null) ? 0 : this.body.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final LambdaExpression other = (LambdaExpression) obj;
    if (this.argumentType == null) {
      if (other.argumentType != null) {
        return false;
      }
    } else if (!this.argumentType.getName().equals(other.argumentType.getName())) {
      return false;
    }
    if (this.argumentName == null) {
      if (other.argumentName != null) {
        return false;
      }
    } else if (!this.argumentName.equals(other.argumentName)) {
      return false;
    }
    if (this.body == null) {
      if (other.body != null) {
        return false;
      }
    } else if (!this.body.equals(other.body)) {
      return false;
    }
    return true;
  }

}
