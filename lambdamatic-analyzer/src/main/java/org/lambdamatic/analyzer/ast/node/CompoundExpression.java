/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

import static org.lambdamatic.analyzer.ast.node.BooleanLiteral.EMPTY_SET_OPERATOR;
import static org.lambdamatic.analyzer.ast.node.BooleanLiteral.UNIVERSAL_OPERATOR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.lambdamatic.analyzer.exception.AnalyzeException;
import org.lambdamatic.analyzer.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Infix expression AST node type.
 * {@code Expression CompoundExpressionOperator Expression [CompoundExpressionOperator Expression]*}
 * 
 * <strong>Note</strong>: If the {@code CompoundExpressionOperator} is the same between all
 * {@code Expression} items of this {@link CompoundExpression}
 *
 */
public class CompoundExpression extends ComplexExpression {

  /** The usual logger. */
  private static final Logger LOGGER = LoggerFactory.getLogger(CompoundExpression.class);

  /**
   * The base complexity for any {@link CompoundExpression}.
   */
  private static final int BASE_COMPLEXITY = 10;

  /**
   * {@link CompoundExpression} operators.
   * 
   * <pre>
   * CompoundExpressionOperator:<code>
   *    <b>*</b>  TIMES
   *    <b>/</b>  DIVIDE
   *    <b>%</b>  REMAINDER
   *    <b>+</b>  PLUS
   *    <b>-</b>  MINUS
   *    <b>&lt;&lt;</b>  LEFT_SHIFT
   *    <b>&gt;&gt;</b>  RIGHT_SHIFT_SIGNED
   *    <b>&gt;&gt;&gt;</b>  RIGHT_SHIFT_UNSIGNED
   *    <b>&lt;</b>  LESS
   *    <b>&gt;</b>  GREATER
   *    <b>&lt;=</b>  LESS_EQUALS
   *    <b>&gt;=</b>  GREATER_EQUALS
   *    <b>==</b>  EQUALS
   *    <b>!=</b>  NOT_EQUALS
   *    <b>^</b>  XOR
   *    <b>&amp;</b>  AND
   *    <b>|</b>  OR
   *    <b>&amp;&amp;</b>  CONDITIONAL_AND
   *    <b>||</b>  CONDITIONAL_OR</code>
   * </pre>
   */
  public enum CompoundExpressionOperator {
    /** "times" (*) operator. */
    TIMES("*"), /** "divide" (/) operator. */
    DIVIDE("/"), /** "remainder" (%) operator. */
    REMAINDER("%"), /** "plus" (+) operator. */
    PLUS("+"), /** "minus" (-) operator. */
    MINUS("-"), /** "less" (&lt;) operator. */
    LESS("<"), /** "greater"(&gt;) operator. */
    GREATER(">"), /** "less or equals" (&lt;=) operator. */
    LESS_EQUALS("<="), /** "greater or equals" (&gt;=) operator. */
    GREATER_EQUALS(">="), /** "equals" (==) operator. */
    EQUALS("=="), /** "not equals" (!=) operator. */
    NOT_EQUALS("!="), /** "xor" (^) operator. */
    XOR("^"), /** "and" (&amp;) operator. */
    AND("&"), /** "or" (|) operator. */
    OR("|"), /** "conditional and" (&amp;&amp;) operator. */
    CONDITIONAL_AND("&&"), /** "conditional or" (||) operator. */
    CONDITIONAL_OR("||"), /** "left shift" (&lt;&lt;) operator. */
    LEFT_SHIFT("<<"), /** "right shift" (&gt;&gt;) operator. */
    RIGHT_SHIFT(">>"), /** "right shift, unassigned" (&gt;&gt;&gt;) operator. */
    RIGHT_SHIFT_UNSIGNED(">>>");

    final String value;

    private CompoundExpressionOperator(final String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return this.value;
    }

    /**
     * @return the inverse of {@link CompoundExpressionOperator} of {@code this} operator
     * @throws AnalyzeException if there is not inverse operator defined for {@code this}.
     */
    public CompoundExpressionOperator inverse() {
      switch (this) {
        case EQUALS:
          return NOT_EQUALS;
        case NOT_EQUALS:
          return EQUALS;
        case GREATER:
          return LESS_EQUALS;
        case GREATER_EQUALS:
          return LESS;
        case LESS:
          return GREATER_EQUALS;
        case LESS_EQUALS:
          return GREATER;
        case CONDITIONAL_AND:
          return CONDITIONAL_OR;
        case CONDITIONAL_OR:
          return CONDITIONAL_AND;
        default:
          throw new AnalyzeException(
              "Inverse operator for '" + this.toString() + "' is not defined :/");
      }
    }

  }

  /** The Infix expression operands. */
  private final List<Expression> operands;

  /** The Infix expression operator. */
  private final CompoundExpressionOperator operator;

  /**
   * the measured complexity of this {@link CompoundExpression}.
   */
  private int complexity;

  /**
   * Full constructor
   * 
   * @param operator the Infix expression operator.
   * @param operands the Infix expression operands.
   */
  public CompoundExpression(final CompoundExpressionOperator operator,
      final Expression... operands) {
    this(generateId(), operator, Arrays.asList(operands), false);
  }

  /**
   * Full constructor.
   * 
   * @param id the synthetic ID of this {@link CompoundExpression}
   * @param operator the Infix expression operator
   * @param operands the Infix expression operands
   */
  public CompoundExpression(final int id, final CompoundExpressionOperator operator,
      final Expression... operands) {
    this(id, operator, Arrays.asList(operands), false);
  }

  /**
   * Full constructor
   * 
   * @param operator the Infix expression operator.
   * @param operands the list Infix expression operands.
   */
  public CompoundExpression(final CompoundExpressionOperator operator,
      final List<Expression> operands) {
    this(generateId(), operator, operands, false);
  }

  /**
   * Full constructor
   * 
   * @param id the synthetic ID of this {@link CompoundExpression}
   * @param operator the Infix expression operator.
   * @param operands the list Infix expression operands.
   * @param inverted the inversion flag of this {@link Expression}.
   */
  public CompoundExpression(final int id, final CompoundExpressionOperator operator,
      final List<Expression> operands, final boolean inverted) {
    super(id, inverted);
    if (operands.size() == 1 && operands.get(0).getExpressionType() == ExpressionType.COMPOUND) {
      final CompoundExpression infixOperand = (CompoundExpression) operands.get(0);
      this.operands = new ArrayList<>(infixOperand.operands);
      this.operator = infixOperand.operator;
    } else {
      this.operands = new ArrayList<>(operands);
      this.operator = operator;
    }
    this.operands.stream().forEach(e -> e.setParent(this));
    this.complexity =
        BASE_COMPLEXITY + this.operands.stream().mapToInt(Expression::getComplexity).sum();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.lambdamatic.analyzer.ast.node.Expression#getParent()
   */
  @Override
  public ComplexExpression getParent() {
    return (ComplexExpression) super.getParent();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate(int)
   */
  @Override
  public CompoundExpression duplicate(int id) {
    return new CompoundExpression(getId(), this.operator,
        Expression.duplicateExpressions(this.operands), isInverted());
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate()
   */
  @Override
  public CompoundExpression duplicate() {
    return duplicate(generateId());
  }

  /**
   * Computes the complexity of this {@link CompoundExpression} using the following rule:
   * <ul>
   * <li>Perform the sum of all operands'complexity</li>
   * <li>Add {@code 10}
   * </ul>
   * {@inheritDoc}
   * 
   * @see org.lambdamatic.analyzer.ast.node.Expression#getComplexity()
   */
  public int getComplexity() {
    return this.complexity;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.lambdamatic.analyzer.ast.node.Expression#getExpressionType()
   */
  @Override
  public ExpressionType getExpressionType() {
    return ExpressionType.COMPOUND;
  }

  /**
   * {@link CompoundExpression} return a {@link Boolean} type. {@inheritDoc}
   * 
   * @see org.lambdamatic.analyzer.ast.node.Expression#getJavaType()
   */
  @Override
  public Class<?> getJavaType() {
    return Boolean.class;
  }

  /**
   * @return the operands.
   */
  public List<Expression> getOperands() {
    return this.operands;
  }

  @Override
  public boolean anyElementMatches(ExpressionType type) {
    return this.operands.stream().anyMatch(e -> e.anyElementMatches(type));
  }

  /**
   * Replaces the current operands with the given ones.
   * 
   * @param operands the new operands
   * @return {@code this}
   */
  private CompoundExpression setOperands(final List<Expression> operands) {
    this.operands.clear();
    this.operands.addAll(operands);
    this.complexity =
        BASE_COMPLEXITY + this.operands.stream().mapToInt(Expression::getComplexity).sum();
    return this;
  }

  /**
   * Add the given {@code operand} to the list of operands of this {@link CompoundExpression}.
   * 
   * @param operand the operand to add.
   * @return {@code this}
   */
  public CompoundExpression addOperand(final Expression operand) {
    this.operands.add(operand);
    Collections.sort(this.operands);
    return this;
  }

  /**
   * @return the operator.
   */
  public CompoundExpressionOperator getOperator() {
    return this.operator;
  }

  /**
   * Removes the given {@link Expression} from the list of operands of this
   * {@link CompoundExpression}.
   * 
   * @param operands the members to remove.
   * @return {@code this} {@link CompoundExpression} after the given members removal. If there is
   *         only one member left in this {@link CompoundExpression}, then the result if the
   *         remaining {@link Expression} itself.
   */
  public Expression removeOperands(final Expression... operands) {
    final List<Expression> operandsWorkingCopy = new ArrayList<>(this.operands);
    operandsWorkingCopy.removeAll(Arrays.asList(operands));
    if (operandsWorkingCopy.size() == 1) {
      return operandsWorkingCopy.get(0).duplicate(getId());
    }
    setOperands(operandsWorkingCopy);
    return this;
  }

  @Override
  public void replaceElement(final Expression oldExpression, final Expression newExpression) {
    final int oldExpressionIndex = this.operands.indexOf(oldExpression);
    if (oldExpressionIndex > -1) {
      this.operands.set(oldExpressionIndex, newExpression);
      this.complexity =
          BASE_COMPLEXITY + this.operands.stream().mapToInt(Expression::getComplexity).sum();
    }
  }

  /**
   * @param member the member to look for.
   * @return {@code true} if the operands contain the given {@link Expression} , false otherwise.
   */
  public boolean contains(final Expression member) {
    return this.operands.contains(member);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.lambdamatic.analyzer.ast.node.Expression#inverse()
   */
  @Override
  public Expression inverse() {
    switch (this.operator) {
      case CONDITIONAL_AND:
      case CONDITIONAL_OR:
        // returns an CompoundExpression with the inverted operator and the a *duplicate inverted
        // version* of all
        // operands, because their parent expression (the one being init and returned) is not *this*
        // The 'inverse' flag remains 'false', though.
        return new CompoundExpression(this.operator.inverse(), duplicateAndInverseOperands());
      default:
        // returns an CompoundExpression with the inverted operator and the a *duplicate version* of
        // all
        // operands, because their parent expression (the one being init and returned) is not *this*
        // The 'inverse' flag remains 'false', and the operands are not inverted, though.
        return new CompoundExpression(this.operator.inverse(),
            Expression.duplicateExpressions(this.operands));
    }
  }


  /**
   * @return a {@link List} containing a duplicate version of all {@link Expression} operands of
   *         {@code this}.
   */
  private List<Expression> duplicateAndInverseOperands() {
    return this.operands.stream().map(e -> {
      return e.duplicate().inverse();
    }).collect(Collectors.toList());
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.lambdamatic.analyzer.ast.node.Expression#canBeInverted()
   */
  @Override
  public boolean canBeInverted() {
    return true;
  }

  @Override
  public void accept(final ExpressionVisitor visitor) {
    if (visitor.visit(this)) {
      getOperands().stream().forEach(operand -> operand.accept(visitor));
    }
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append('(');
    final List<String> operandValues =
        this.operands.stream().map(Expression::toString).collect(Collectors.toList());
    builder.append(String.join(' ' + this.operator.value + ' ', operandValues));
    builder.append(')');
    return builder.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    if (this.operands != null) {
      int operandsHashcode = 0;
      for (Expression e : this.operands) {
        operandsHashcode += e.hashCode();
      }
      result = prime * result + operandsHashcode;
    }
    result = prime * result + ((this.operator == null) ? 0 : this.operator.hashCode());
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
    CompoundExpression other = (CompoundExpression) obj;
    if (this.operator != other.operator) {
      return false;
    }
    if (this.operands == null) {
      if (other.operands != null) {
        return false;
      }
    } else if (!CollectionUtils.equivalent(this.operands, other.operands)) {
      return false;
    }
    return true;
  }

  // ******************************************************************************************
  //
  // Expression simplification using Boolean algebra formulae.
  //
  // ******************************************************************************************

  static enum BooleanLaw {
    ASSOCIATIVE_LAW, REDUNDANCY_LAW, ABSORPTION_LAW, FACTORIZATION_LAW, IDEMPOTENT_LAW,
    UNARY_OPERATION_LAW, EMPTY_SET_LAW, UNIVERSAL_SET_LAW, DISTRIBUTIVE_LAW;
  }

  /**
   * Attempts to simplify this {@link CompoundExpression} by applying some Boolean Algebra rules.
   * 
   * @return a simplified version of this expression
   */
  public Expression simplify() {
    final long startTime = System.currentTimeMillis();
    final ExpressionSimplificationMonitor monitor = new ExpressionSimplificationMonitor(this);
    final List<Expression> variantForms = computeVariants(monitor);
    for (Expression variant : variantForms) {
      LOGGER.trace("  #={} cx={}: {}", variant.hashCode(), variant.getComplexity(),
          variant.toString());
    }
    final Expression simplestForm = variantForms.isEmpty() ? this : variantForms.get(0);
    final long endTime = System.currentTimeMillis();
    LOGGER.debug(" Simplest form for #{}: {} (c={}) in {}ms", simplestForm.getId(),
        simplestForm.toString(), simplestForm.getComplexity(), (endTime - startTime));
    if (simplestForm.getExpressionType() == ExpressionType.COMPOUND) {
      return ((CompoundExpression) simplestForm).reorderOperands(this);
    }
    return simplestForm;
  }

  /**
   * Reorders the operands of {@code this} {@link CompoundExpression} by looking at the first
   * operand of the given {@link CompoundExpression}.
   * 
   * @param sourceExpression the source {@link CompoundExpression}
   * @return {@code this} {@link CompoundExpression}.
   */
  protected CompoundExpression reorderOperands(final CompoundExpression sourceExpression) {
    final List<Expression> orderedOperands = new ArrayList<>();
    // look at each operand in the source expression, and if it exists in the list of operands of
    // this expression, put it in front.
    final List<Expression> pendingOperands = new ArrayList<>(this.operands);
    for (Expression sourceOperand : sourceExpression.operands) {
      if (pendingOperands.contains(sourceOperand)) {
        orderedOperands.add(sourceOperand);
        pendingOperands.remove(sourceOperand);
      }
    }
    // TODO: if 'orderedOperands' is empty, we may need to look deeper into the operands, if they
    // are themselves CompoundExpression
    orderedOperands.addAll(pendingOperands);
    setOperands(orderedOperands);
    return this;

  }

  /**
   * Computes all variant forms of {@code this} {@link CompoundExpression}.
   * 
   * @return a list of {@link Expression} behing all equivalent of {@code this}.
   */
  protected List<Expression> computeVariants(final ExpressionSimplificationMonitor monitor) {
    if (monitor.isStopped()) {
      LOGGER.trace("{}monitor was stopped. No further variants computation",
          monitor.getIndentation());
      return Collections.emptyList();
    }
    monitor.incrementIndentation();
    try {
      LOGGER.trace("{}computing variants on #{} {}", monitor.getIndentation(), this.getId(),
          this.toString());
      final List<Expression> variantFormsOfThis = new ArrayList<>();
      if (monitor.isExpressionFormKnown(this)) {
        LOGGER.trace("{}#{}: this form is already known: {}", monitor.getIndentation(),
            this.getId(), this.toString());
      } else {
        monitor.registerExpression(this);
        variantFormsOfThis.addAll(this.applyBooleanLaws(monitor));
        if (monitor.isStopped()) {
          LOGGER.trace("{}monitor was stopped. No operand processing", monitor.getIndentation());
          return variantFormsOfThis;
        }
        // asking each operand to simplify itself
        for (Expression operand : this.operands) {
          final Collection<Expression> variantFormsOfOperand = operand.computeVariants(monitor);
          for (Expression variantFormOfOperand : variantFormsOfOperand) {
            final CompoundExpression variantFormOfThis = this.duplicate(getId());
            variantFormOfThis.replaceElement(operand, variantFormOfOperand);
            LOGGER.trace("{}variant form: #{} {}", monitor.getIndentation(),
                variantFormOfThis.getId(), variantFormOfThis.toString());
            variantFormsOfThis.add(variantFormOfThis);
            variantFormsOfThis.addAll(variantFormOfThis.computeVariants(monitor));
          }
        }
      }
      Collections.sort(variantFormsOfThis);
      /*
       * if (LOGGER.isTraceEnabled()) { LOGGER.trace("{}#{}: {} variant{} found{}",
       * monitor.getIndentation(), this.getId(), variantFormsOfThis.size(),
       * (variantFormsOfThis.size()>1 ? "s" : ""), (variantFormsOfThis.isEmpty() ? "." : ":")); for
       * (Expression variantFormOfThis : variantFormsOfThis) { LOGGER.trace("{}|- {}",
       * monitor.getIndentation(), variantFormOfThis.toString()); } }
       */
      return variantFormsOfThis;
    } finally {
      monitor.decrementIndentation();
    }
  }

  /**
   * Apply all boolean laws on {@code this} expression.
   * 
   * @return a {@link List} of {@link Expression} being all new variant forms of {@code this}
   *         expression, once each boolean law was applied.
   */
  protected List<Expression> applyBooleanLaws(final ExpressionSimplificationMonitor monitor) {
    if (monitor.getExpressionToSimplify().getId() == this.getId() && !this.canFurtherSimplify()) {
      LOGGER.trace("{}#{} {} cannot be further simplified. Let's stop here.",
          monitor.getIndentation(), this.getId(), this.toString());
      monitor.stop();
      return Arrays.asList(this);
    }
    if (monitor.isStopped()) {
      LOGGER.trace("{}monitor was stopped. No further boolean laws processing",
          monitor.getIndentation());
      return Collections.emptyList();
    }
    try {
      monitor.incrementIndentation();
      final List<Expression> variants = new ArrayList<>();
      LOGGER.trace("{}applying boolean laws on #{} {}", monitor.getIndentation(), this.getId(),
          this.toString());
      boolean_laws_loop: for (BooleanLaw step : BooleanLaw.values()) {
        final Collection<Expression> resultExpressions = executeStep(step);
        for (Expression resultExpression : resultExpressions) {
          if (resultExpression.equals(this)) {
            // LOGGER.trace(" #{}: no simplification after {}. Trying next step...", this.getId(),
            // step);
            // } else if (monitor.isExpressionFormKnown(resultExpression)) {
            // LOGGER.trace("{}#{}: simplified form is already known. Trying next step...",
            // monitor.getIndentation(), this.getId(), step);
          } else {
            LOGGER.trace("{}#{}: {} \n{} -> \n{}", monitor.getIndentation(), this.getId(), step,
                this.toString(), resultExpression.toString());
            variants.add(resultExpression);
            final List<Expression> resultExpressionVariants =
                resultExpression.computeVariants(monitor);
            // monitor may have been stopped during call to 'computeVariants()' above.
            if (monitor.isStopped()) {
              // if monitor got the stop event, we only keep the last results
              variants.clear();
              variants.addAll(resultExpressionVariants);
              break boolean_laws_loop;
            }
            variants.addAll(resultExpressionVariants);
          }
        }
      }
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("{}#{}: {} boolean simplification{} found{}", monitor.getIndentation(),
            this.getId(), variants.size(), (variants.size() > 1 ? "s" : ""),
            (variants.isEmpty() ? "." : ":"));
        /*
         * for (Expression variant : variants) { LOGGER.trace("{} |- {}", monitor.getIndentation(),
         * variant.toString()); }
         */
      }
      Collections.sort(variants);
      return variants;
    } finally {
      monitor.decrementIndentation();
    }

  }

  /**
   * @return {@code true} if the current {@link CompoundExpression} can be further simplified,
   *         {@code false} otherwise.
   */
  @Override
  protected boolean canFurtherSimplify() {
    // check if operands can be further simplified
    if (this.operands.parallelStream().anyMatch(o -> o.canFurtherSimplify())) {
      return true;
    }
    // check if operand contain EMPTY_SET or UNIVERSAL operators
    if (this.operands.parallelStream()
        .anyMatch(e -> e.equals(UNIVERSAL_OPERATOR) || e.equals(EMPTY_SET_OPERATOR))) {
      return true;
    }

    // check if there are nested InfixExpressions with same operator
    if (this.operands.parallelStream()
        .anyMatch(o -> o.getExpressionType() == ExpressionType.COMPOUND
            && ((CompoundExpression) o).operator == this.operator)) {
      return true;
    }
    // if the current expression is something like:
    // (a.b) + (c.d) or a + (c.d) + (e.f.g), etc.
    // (a + b).(c + d) or a.(c + d).(e + f + g), etc.
    // then we can safely assume that the expression is not simplifiable
    // using an Expression visitor to count the unique operands.
    final ExpressionsCounter expressionsCounter = new ExpressionsCounter();
    this.accept(expressionsCounter);
    final Map<Expression, AtomicInteger> counters = expressionsCounter.getCounters();
    for (Entry<Expression, AtomicInteger> entry : counters.entrySet()) {
      if (entry.getValue().get() > 1) {
        return true;
      }
    }

    // by default, let's assume it can be simplified (applying boolean laws will verify that)
    return false;
  }

  /**
   * Executes the given simplification on {@code this} and returns all combinations.
   * 
   * @param law the simplification law to execute
   * @return the result of the law.
   */
  protected List<Expression> executeStep(final BooleanLaw law) {
    final List<Expression> resultExpressions = new ArrayList<>();
    switch (law) {
      case ABSORPTION_LAW:
        resultExpressions.addAll(applyAbsorptionLaw());
        break;
      case ASSOCIATIVE_LAW:
        resultExpressions.addAll(applyAssociativeLaw());
        break;
      case DISTRIBUTIVE_LAW:
        resultExpressions.addAll(applyDistributiveLaw());
        break;
      case EMPTY_SET_LAW:
        resultExpressions.addAll(applyEmptySetLaw());
        break;
      case FACTORIZATION_LAW:
        resultExpressions.addAll(applyFactorizationLaw());
        break;
      case IDEMPOTENT_LAW:
        resultExpressions.addAll(applyIdempotentLaw());
        break;
      case REDUNDANCY_LAW:
        resultExpressions.addAll(applyRedundancyLaw());
        break;
      case UNARY_OPERATION_LAW:
        resultExpressions.addAll(applyUnaryOperationLaw());
        break;
      case UNIVERSAL_SET_LAW:
        resultExpressions.addAll(applyUniversalSetLaw());
        break;
      default:
        resultExpressions.add(this);
        break;
    }
    Collections.sort(resultExpressions);
    return resultExpressions;
  }

  /**
   * @param expressions the list of {@link Expression} to analyze
   * @param operand the {@link Expression} to exclude from the result list.
   * @return the list of {@code Expression} operands other than the given {@link Expression} operand
   *         in the given list of {@link Expression}
   */
  private List<Expression> getOtherOperands(final Expression operand) {
    return this.operands.stream().filter(m -> m != operand).collect(Collectors.toList());
  }

  /**
   * If the given {@link CompoundExpressionOperator} is
   * {@link CompoundExpressionOperator#CONDITIONAL_AND}, then return
   * {@link CompoundExpressionOperator#CONDITIONAL_OR} and the other way around.
   * 
   * @return the opposite operator, or null if the current operator did not match (this should not
   *         happen, though)
   */
  private CompoundExpressionOperator getOppositeOperator() {
    switch (this.operator) {
      case CONDITIONAL_AND:
        return CompoundExpressionOperator.CONDITIONAL_OR;
      case CONDITIONAL_OR:
        return CompoundExpressionOperator.CONDITIONAL_AND;
      default:
        return null;
    }
  }

  /**
   * Associative Law:
   * <ul>
   * <li>{@code a.(b.c) = a.b.c}</li>
   * <li>{@code a + (b + c) = a + b + c}</li>
   * </ul>
   * 
   * @return a {@link Set} with the new {@link Expression} resulting from this boolean law, or empty
   *         list if no change was performed.
   */
  protected Set<Expression> applyAssociativeLaw() {
    final Set<Expression> resultExpressions = new HashSet<>();
    final List<Expression> resultOperands = new ArrayList<>();
    boolean match = false;
    for (Expression operand : this.operands) {
      if (operand.getExpressionType() == ExpressionType.COMPOUND) {
        final CompoundExpression infixOperand = (CompoundExpression) operand;
        if (infixOperand.getOperator() == this.operator) {
          resultOperands.addAll(infixOperand.getOperands());
          match = true;
        } else {
          resultOperands.add(infixOperand);
        }
      } else {
        resultOperands.add(operand);
      }
    }
    if (match) {
      final CompoundExpression resultExpression = new CompoundExpression(this.getId(),
          this.operator, new ArrayList<>(resultOperands), isInverted());
      // LOGGER.trace(" Associative Law: #{} {} -> {}", this.getId(), this.toString(),
      // resultExpression.toString());
      resultExpressions.add(resultExpression);
    }
    return resultExpressions;
  }

  /**
   * Apply Redundancy Law (derived from other laws):
   * <ul>
   * <li>{@code a.(!a + b).(!a + c) = a.b.c}</li>
   * <li>{@code a + (!a.b) + (!a.c) = a + b + c}</li>
   * </ul>
   * in some cases, the law does apply on *some* operands but not *all* of them. Eg:
   * <ul>
   * <li>{@code a.(!a + b).e = a.b.e}</li>
   * <li>{@code a + (!a.b) + e = a + b + e}</li>
   * </ul>
   *
   * @return a {@link Set} with the new {@link Expression} resulting from this boolean law, or empty
   *         list if no change was performed.
   */
  protected Set<Expression> applyRedundancyLaw() {
    final Set<Expression> resultExpressions = new HashSet<>();
    final CompoundExpressionOperator oppositeOperator = getOppositeOperator();
    for (Expression operand : this.operands) {
      if (!operand.canBeInverted()) {
        continue;
      }
      final List<Expression> simplifiedOperands = this.operands.stream().map(expression -> {
        if (expression.getExpressionType() == ExpressionType.COMPOUND
            && ((CompoundExpression) expression).operator == oppositeOperator
            && ((CompoundExpression) expression).contains(operand.inverse())) {
          return ((CompoundExpression) expression).duplicate(getId())
              .removeOperands(operand.inverse());
        }
        return expression;
      }).collect(Collectors.toList());
      if (!simplifiedOperands.equals(this.operands)) {
        final CompoundExpression resultExpression = new CompoundExpression(this.getId(),
            this.operator, new ArrayList<>(simplifiedOperands), isInverted());
        // LOGGER.trace(" Redundancy Law: #{} {} -> {} [match on {}]", this.getId(),
        // this.toString(), resultExpression.toString(), operand);
        resultExpressions.add(resultExpression);
      }
    }
    return resultExpressions;
  }

  /**
   * Apply Absorption Law:
   * <ul>
   * <li>{@code a.(a + b).(a + c) = a}</li>
   * <li>{@code a + (a.b) + (a.c) = a}</li>
   * </ul>
   * 
   * @return a {@link Set} with the new {@link Expression} resulting from this boolean law, or empty
   *         list if no change was performed.
   */
  protected Set<Expression> applyAbsorptionLaw() {
    final Set<Expression> resultExpressions = new HashSet<>();
    final CompoundExpressionOperator oppositeOperator = getOppositeOperator();
    for (Expression operand : this.operands) {
      // removes from 'simplifiedOperands' all expression that
      // match '(a + b)' where 'a' is the operand to match
      final List<Expression> remainingOperands = this.operands.stream()
          .filter(m -> m.equals(operand) || m.getExpressionType() != ExpressionType.COMPOUND
              || ((CompoundExpression) m).getOperator() != oppositeOperator
              || !((CompoundExpression) m).getOperands().contains(operand))
          .collect(Collectors.toList());
      // if there's no match
      if (remainingOperands.size() == this.operands.size()) {
        continue;
      }
      // otherwise, apply the change and recursively try to apply
      // the same law again
      if (remainingOperands.size() == 1) {
        resultExpressions.add(remainingOperands.get(0).duplicate(getId()));
      } else {
        final CompoundExpression resultExpression = new CompoundExpression(this.getId(),
            this.operator, new ArrayList<>(remainingOperands), isInverted());
        // LOGGER.trace(" Absorption Law: #{} {} -> {} (match on {})", this.getId(),
        // this.toString(), resultExpression.toString(), operand);
        resultExpressions.add(resultExpression);
      }
    }
    return resultExpressions;
  }

  /**
   * Applies (the opposite of) the Mutually Distributive Law on the current infix expression in case
   * it has the following form:
   * <ul>
   * <li>{@code (a.b) + (a.c) + (a.d) = a.(b + c + d)}</li>
   * <li>{@code (a + b).(a + c).(a + d) =  a + (b.c.d)}</li>
   * </ul>
   * or if the expression partially matches:
   * <ul>
   * <li>{@code (a.b) + (a.c) + (e.f)} = {@code a.(b + c) + (e.f))}</li>
   * <li>{@code (a + b).(a + c).(e + f)} = {@code (a + (b.c)).(e + f)}</li>
   * </ul>
   * 
   * @return a {@link Set} with the new {@link Expression} resulting from this boolean law, or empty
   *         list if no change was performed.
   */
  protected Set<Expression> applyFactorizationLaw() {
    final Set<Expression> resultExpressions = new HashSet<>();
    final CompoundExpressionOperator oppositeOperator = getOppositeOperator();
    // we keep track of operands that matched before, to avoid performing factorization multiple
    // times for the same operand
    // (because it's obviously repeated multiple times...)
    final Set<Expression> matchOperands = new HashSet<>();
    for (Expression currentOperand : this.operands) {
      if (currentOperand.getExpressionType() == ExpressionType.COMPOUND) {
        final List<Expression> otherOperands = getOtherOperands(currentOperand);
        for (Expression operandElement : ((CompoundExpression) currentOperand).getOperands()) {
          if (matchOperands.contains(operandElement)) {
            continue;
          }
          boolean anyMatch = otherOperands.stream()
              .anyMatch(o -> o.getExpressionType() == ExpressionType.COMPOUND
                  && ((CompoundExpression) o).getOperator() == oppositeOperator
                  && ((CompoundExpression) o).getOperands().contains(operandElement));
          if (anyMatch) {
            matchOperands.add(operandElement);
            final List<Expression> factorizedOperands = this.operands.stream()
                .filter(o -> o.getExpressionType() == ExpressionType.COMPOUND
                    && ((CompoundExpression) o).getOperator() == oppositeOperator
                    && ((CompoundExpression) o).getOperands().contains(operandElement))
                .map(
                    o -> ((CompoundExpression) o).duplicate(getId()).removeOperands(operandElement))
                .collect(Collectors.toList());
            final List<Expression> remainingOperands = otherOperands.stream()
                .filter(o -> !(o.getExpressionType() == ExpressionType.COMPOUND
                    && ((CompoundExpression) o).getOperator() == oppositeOperator
                    && ((CompoundExpression) o).getOperands().contains(operandElement)))
                .collect(Collectors.toList());
            final CompoundExpression factorizedExpression = new CompoundExpression(oppositeOperator,
                operandElement, new CompoundExpression(this.operator, factorizedOperands));
            final CompoundExpression resultExpression =
                new CompoundExpression(this.getId(), this.operator,
                    CollectionUtils.join(factorizedExpression, remainingOperands), isInverted());
            // LOGGER.trace(" Factorization Law: #{} {} -> {} (match on {} of {})", this.getId(),
            // this.toString(), resultExpression.toString(), operandElement, currentOperand);
            resultExpressions.add(resultExpression);
          }
        }
      }
    }
    return resultExpressions;
  }

  /**
   * Apply Idempotent Law:
   * <ul>
   * <li>{@code a + a = a}</li>
   * <li>{@code a + a + b = a + b}</li>
   * <li>{@code a.a = a}</li>
   * <li>{@code a.a.b = a.b}</li>
   * </ul>
   * 
   * @return a {@link Set} with the new {@link Expression} resulting from this boolean law, or empty
   *         list if no change was performed.
   */
  protected Set<Expression> applyIdempotentLaw() {
    final Set<Expression> resultExpressions = new HashSet<>();
    final Set<Expression> matchOperands = new HashSet<>();
    for (Expression operand : this.operands) {
      if (matchOperands.contains(operand)) {
        continue;
      }
      final List<Expression> otherOperands = getOtherOperands(operand);
      // exclude same operands as 'operand' being processed.
      final List<Expression> operandsToKeep =
          otherOperands.stream().filter(o -> !o.equals(operand)).collect(Collectors.toList());
      if (operandsToKeep.size() == otherOperands.size()) {
        continue;
      }
      matchOperands.add(operand);
      operandsToKeep.add(operand);
      if (operandsToKeep.size() == 1) {
        final Expression resultExpression = operand.duplicate(getId());
        // LOGGER.trace(" Idempotent Law: #{} {} -> {} (match on {})", this.getId(),
        // this.toString(), resultExpression.toString(), operand);
        resultExpressions.add(resultExpression);
      } else {
        final CompoundExpression resultExpression = new CompoundExpression(this.getId(),
            this.operator, new ArrayList<>(operandsToKeep), isInverted());
        // LOGGER.trace(" Idempotent Law: #{} {} -> {} (match on {})", this.getId(),
        // this.toString(), resultExpression.toString(), operand);
        resultExpressions.add(resultExpression);
      }
    }
    return resultExpressions;
  }

  /**
   * Apply Unary Operation Law:
   * <ul>
   * <li>{@code a.!a = O}</li>
   * <li>{@code a.!a.b = O}</li>
   * <li>{@code a + !a = I}</li>
   * <li>{@code a + !a + b = b}</li>
   * </ul>
   * 
   * @return a {@link Set} with the new {@link Expression} resulting from this boolean law, or empty
   *         list if no change was performed.
   */
  protected Set<Expression> applyUnaryOperationLaw() {
    final Set<Expression> resultExpressions = new HashSet<>();
    final Set<Expression> matchOperands = new HashSet<>();
    for (Expression operand : this.operands) {
      if (matchOperands.contains(operand)) {
        continue;
      }
      final List<Expression> otherOperands = getOtherOperands(operand);
      if (operand.canBeInverted() && otherOperands.contains(operand.inverse())) {
        matchOperands.add(operand);
        if (this.operator == CompoundExpressionOperator.CONDITIONAL_AND) {
          final Expression resultExpression =
              new BooleanLiteral(getId(), EMPTY_SET_OPERATOR.getValue(), isInverted());
          LOGGER.trace("  Unary Operation Law: {} -> {}", this, resultExpression);
          resultExpressions.add(resultExpression);
        } else if (this.operands.size() == 2) {
          final Expression resultExpression =
              new BooleanLiteral(getId(), UNIVERSAL_OPERATOR.getValue(), isInverted());
          LOGGER.trace("  Unary Operation Law: #{} {} -> {}", this.getId(), this.toString(),
              resultExpression.toString());
          resultExpressions.add(resultExpression);
        } else {
          final Expression resultExpression = removeOperands(operand, operand.inverse());
          LOGGER.trace("  Unary Operation Law: #{} {} -> {}", this.getId(), this.toString(),
              resultExpression.toString());
          resultExpressions.add(resultExpression);
        }
      }
    }
    return resultExpressions;
  }

  /**
   * Apply Empty Set Law:
   * <ul>
   * <li>{@code O + a = a}</li>
   * <li>{@code O + a + b = a + b}</li>
   * <li>{@code O.a = O}</li>
   * <li>{@code O.a.b = O}</li>
   * </ul>
   * 
   * @return a {@link Set} with the new {@link Expression} resulting from this boolean law, or empty
   *         list if no change was performed.
   */
  protected Set<Expression> applyEmptySetLaw() {
    final Set<Expression> resultExpressions = new HashSet<>();
    if (this.operands.contains(EMPTY_SET_OPERATOR)) {
      if (this.operator == CompoundExpressionOperator.CONDITIONAL_AND) {
        final Expression resultExpression =
            new BooleanLiteral(getId(), EMPTY_SET_OPERATOR.getValue(), isInverted());
        // LOGGER.trace(" Empty Set Law: {} -> {}", this, resultExpression);
        resultExpressions.add(resultExpression);
      } else {
        final Expression resultExpression = removeOperands(EMPTY_SET_OPERATOR);
        // LOGGER.trace(" Empty Set Law: #{} {} -> {}", this.getId(), this.toString(),
        // resultExpression.toString());
        resultExpressions.add(resultExpression);
      }
    }
    return resultExpressions;
  }

  /**
   * Apply Universal Law (denoted {@code I}):
   * <ul>
   * <li>{@code I + a = I}</li>
   * <li>{@code I.a = a}</li>
   * </ul>
   * 
   * @return a {@link Set} with the new {@link Expression} resulting from this boolean law, or empty
   *         list if no change was performed.
   */
  protected Set<Expression> applyUniversalSetLaw() {
    final Set<Expression> resultExpressions = new HashSet<>();
    if (this.operands.contains(UNIVERSAL_OPERATOR)) {
      if (this.operator == CompoundExpressionOperator.CONDITIONAL_AND) {
        // eg: 'something' && 'I' -> return 'something', no need to wrap the single operand in an
        // CompoundExpression
        if (this.operands.size() == 2) {
          final Expression remainingOperand =
              this.operands.stream().filter(m -> !m.equals(UNIVERSAL_OPERATOR)).findFirst().get();
          resultExpressions.add(remainingOperand.duplicate(getId()));
        } else {
          // just remove 'I' from the expression operands.
          final Expression resultExpression = this.removeOperands(UNIVERSAL_OPERATOR);
          // LOGGER.trace(" Universal Set Law: {} -> {}", this, resultExpression);
          resultExpressions.add(resultExpression);
        }
      } else if (this.operator == CompoundExpressionOperator.CONDITIONAL_OR) {
        // removes other operands if 'I' is part of the expression operands
        final Expression resultExpression =
            new BooleanLiteral(getId(), UNIVERSAL_OPERATOR.getValue(), isInverted());
        // LOGGER.trace(" Universal Set Law: #{} {} -> {}", this.getId(), this.toString(),
        // resultExpression.toString());
        resultExpressions.add(resultExpression);
      }
    }
    return resultExpressions;
  }

  /**
   * Expands the given {@link Expression} if it meets the following requirements:
   * <ul>
   * <li>it is an {@link CompoundExpression}</li>
   * <li>contains one of the following forms:
   * <ul>
   * <li>{@code infix(infix(a, ...), infix(a, ...), ...)}</li>
   * <li>{@code infix(infix(a, ...), infix(!a, ...), ...)}</li>
   * </ul>
   * </li>
   * </ul>
   * Eg:
   * <ul>
   * <li>{@code ((a.(b+c)) + (!a.(d+e))) = (a.b + a.c + !a.d + !a.e)}</li>
   * </ul>
   * in each case, the presence of the {@code a} or {@code !a} operands associated with an
   * {@link CompoundExpression} is the key to trigger the development.
   * 
   * @return a {@link Set} with the new {@link Expression} resulting from this boolean law, or empty
   *         list if no change was performed.
   */
  protected Set<Expression> applyDistributiveLaw() {
    final Set<Expression> resultExpressions = new HashSet<>();
    for (Expression sourceOperand : this.operands) {
      if (sourceOperand.getExpressionType() != ExpressionType.COMPOUND) {
        continue;
      }
      final List<Expression> otherOperands = getOtherOperands(sourceOperand);
      for (Expression sourceSubOperand : ((CompoundExpression) sourceOperand).getOperands()) {
        final Map<CompoundExpression, Expression> matchingOtherOperands = new HashMap<>();
        if (!sourceSubOperand.canBeInverted()) {
          continue;
        }
        final Expression inversedSourceSubOperand = sourceSubOperand.inverse();
        for (Expression otherOperand : otherOperands) {
          if (otherOperand.getExpressionType() != ExpressionType.COMPOUND) {
            continue;
          }
          final CompoundExpression otherInfixOperand = (CompoundExpression) otherOperand;
          final boolean containsInfixOperands = (otherInfixOperand.getOperands().stream()
              .anyMatch(o -> o.getExpressionType() == ExpressionType.COMPOUND
                  && ((CompoundExpression) o).operator == otherInfixOperand.operator.inverse()));
          if (containsInfixOperands && otherInfixOperand.getOperands().contains(sourceSubOperand)) {
            // match found
            matchingOtherOperands.put(otherInfixOperand, sourceSubOperand);
          } else if (containsInfixOperands
              && otherInfixOperand.getOperands().contains(inversedSourceSubOperand)) {
            // match found
            matchingOtherOperands.put(otherInfixOperand, inversedSourceSubOperand);
          }
        }
        if (!matchingOtherOperands.isEmpty()) {
          final List<Expression> resultOperands = new ArrayList<>();
          resultOperands.add(sourceOperand);
          // add the non-matching other operands
          otherOperands.stream().filter(o -> !matchingOtherOperands.containsKey(o))
              .forEach(o -> resultOperands.add(o));
          // add the matching operands, after they have been expanded
          matchingOtherOperands.entrySet().stream()
              .forEach(entry -> resultOperands.addAll(entry.getKey().distribute(entry.getValue())));

          final CompoundExpression resultExpression =
              new CompoundExpression(this.getId(), this.operator, resultOperands, isInverted());
          if (!resultExpression.equals(this)) {
            // LOGGER.trace(" Distributive Law: #{} {} -> {}", this.getId(), this.toString(),
            // resultExpression.toString());
            resultExpressions.add(resultExpression);
          }
        }
      }
    }
    return resultExpressions;
  }

  /**
   * Expands the given {@link CompoundExpression}:
   * <ul>
   * <li>{@code a.(b + c + d) = (a.b) + (a.c) + (a.d)}</li>
   * <li>{@code a + (b.c.d) = (a + b).(a + c).(a + d)}</li>
   * </ul>
   * 
   * @param sourceOperand the source operand to process.
   * @return a {@link Set} with the new {@link Expression} resulting from this boolean law, or empty
   *         list if no change was performed.
   */
  protected Set<Expression> distribute(final Expression sourceOperand) {
    final Set<Expression> resultExpressions = new HashSet<>();
    final CompoundExpressionOperator oppositeOperator = getOppositeOperator();
    final CompoundExpression resultExpression = new CompoundExpression(this.getId(), this.operator);
    // pick the first other operand that is an CompoundExpression
    final Optional<Expression> optionalOtherInfixOperand = this.operands.stream()
        .filter(e -> !e.equals(sourceOperand) && e.getExpressionType() == ExpressionType.COMPOUND)
        .findFirst();
    if (!optionalOtherInfixOperand.isPresent()) {
      resultExpressions.add(this);
    } else {
      final CompoundExpression otherInfixOperand =
          (CompoundExpression) optionalOtherInfixOperand.get();
      final CompoundExpression distributedConditionalAnd =
          new CompoundExpression(oppositeOperator, Collections.emptyList());
      for (Expression e : otherInfixOperand.getOperands()) {
        distributedConditionalAnd
            .addOperand(new CompoundExpression(this.operator, Arrays.asList(sourceOperand, e)));
      }
      resultExpression.addOperand(distributedConditionalAnd);
      // unmodified operands shall be added to the result expression
      this.operands.stream().filter(m -> (m.equals(sourceOperand) && m.equals(otherInfixOperand)))
          .forEach(e -> resultExpression.addOperand(e));
      if (resultExpression.getOperands().size() == 1) {
        resultExpressions.add(resultExpression.getOperands().get(0).duplicate(getId()));
      }
    }
    return resultExpressions;
  }

}

