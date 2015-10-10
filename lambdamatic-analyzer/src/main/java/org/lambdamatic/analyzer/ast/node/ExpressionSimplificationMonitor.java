/*******************************************************************************
 * Copyright (c) 2014, 2015 Red Hat. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.lambdamatic.analyzer.utils.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to monitor the progress of the simplification of an {@link Expression}.
 * 
 * @author Xavier Coulon
 *
 */
public class ExpressionSimplificationMonitor {

  /** The Logger. */
  @SuppressWarnings("unused")
  private static final Logger LOGGER =
      LoggerFactory.getLogger(ExpressionSimplificationMonitor.class);

  /**
   * keeping the hash value of the previous forms of this expression while trying to simplify it, to
   * avoid endless loops.
   */
  private final Map<Integer, Set<Integer>> allHashes = new HashMap<>();

  /** Indentation counter to display log messages. */
  private int indentation = 0;

  /** keeping the simplest form for a given expression while trying to simplify it. */
  private final Map<Integer, Set<Tuple<Expression, Integer>>> allVariantForms = new HashMap<>();

  /** Whether this monitor is stopped (ie, best simplification was found). */
  private boolean stopped = false;

  /**
   * The original {@link CompoundExpression} that is being simplified.
   */
  private final Expression expressionToSimplify;

  /**
   * Construtor.
   * 
   * @param expressionToSimplify the expression to simplify
   */
  public ExpressionSimplificationMonitor(final Expression expressionToSimplify) {
    this.expressionToSimplify = expressionToSimplify;
  }

  /**
   * @return the expression to simplify.
   */
  public Expression getExpressionToSimplify() {
    return this.expressionToSimplify;
  }


  /**
   * Checks of the given {@link Expression} was already processed.
   * 
   * @param expression the expression to check
   * @return <code>true</code> if the given expression was already processed, <code>false</code>
   *         otherwise
   */
  public boolean isExpressionFormKnown(final Expression expression) {
    return this.allHashes.containsKey(expression.getId())
        && this.allHashes.get(expression.getId()).contains(expression.hashCode());
  }

  /**
   * Registers the given {@link Expression} in the list of processed expressions.
   * 
   * @param expression the expression to register
   */
  public void registerExpression(final Expression expression) {
    registerVariantExpression(expression, expression);
  }

  /**
   * Register the variant form of a given Expression, to track all combination that were found and
   * avoid passing twice on the same simplification path.
   * 
   * @param expression the original {@link Expression}
   * @param variantForm its variant form.
   */
  public void registerVariantExpression(final Expression expression, final Expression variantForm) {
    if (!this.allHashes.containsKey(expression.getId())) {
      this.allHashes.put(expression.getId(), new HashSet<>());
    }
    this.allHashes.get(expression.getId()).add(variantForm.hashCode());
    if (!this.allVariantForms.containsKey(expression.getId())) {
      this.allVariantForms.put(expression.getId(), new HashSet<>());
    }
    this.allVariantForms.get(expression.getId())
        .add(new Tuple<>(variantForm, variantForm.getComplexity()));
  }

  /**
   * @return the indentation after having incremented it.
   */
  public int incrementIndentation() {
    return ++this.indentation;
  }

  /**
   * @return the indentation after having decremented it.
   */
  public int decrementIndentation() {
    return --this.indentation;
  }

  /**
   * @return the indentation to apply when printing some logs.
   */
  public String getIndentation() {
    final StringBuilder indentationBuilder = new StringBuilder();
    for (int i = 0; i < this.indentation; i++) {
      indentationBuilder.append(' ');
    }
    return indentationBuilder.toString();
  }

  /**
   * Stop the simplification.
   */
  public void stop() {
    this.stopped = true;
  }

  /**
   * @return <code>true</code> if the simplification is stopped, <code>false</code> otherwise.
   */
  public boolean isStopped() {
    return this.stopped;
  }

}

