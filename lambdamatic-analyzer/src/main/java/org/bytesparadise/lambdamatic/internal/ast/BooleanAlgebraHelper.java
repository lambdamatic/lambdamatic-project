/**
 * 
 */
package org.bytesparadise.lambdamatic.internal.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bytesparadise.lambdamatic.internal.ast.node.BooleanLiteral;
import org.bytesparadise.lambdamatic.internal.ast.node.Expression;
import org.bytesparadise.lambdamatic.internal.ast.node.InfixExpression;
import org.bytesparadise.lambdamatic.internal.ast.node.Expression.ExpressionType;
import org.bytesparadise.lambdamatic.internal.ast.node.InfixExpression.InfixOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xcoulon
 *
 */
public class BooleanAlgebraHelper {

	/** The usual logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(BooleanAlgebraHelper.class);

	/** The Universal Operator. */
	private static final BooleanLiteral UNIVERSAL_OPERATOR = new BooleanLiteral(Boolean.TRUE);

	/** The EmptySet Operator. */
	private static final BooleanLiteral EMPTY_SET_OPERATOR = new BooleanLiteral(Boolean.FALSE);

	/**
	 * Helper class constructor
	 */
	private BooleanAlgebraHelper() {

	}

	/**
	 * Applies Boolean algebra simplifications rules on given expression
	 * {@link InfixExpression}. The following patterns are searched and when a
	 * match is found, the nodes are replaces with their simplified version:
	 * <ul>
	 * <li>A + AB => A</li>
	 * <li>A + !A B => A + B</li>
	 * <li>(A + B) (A + C) => A + BC</li>
	 * </ul>
	 * 
	 * @return a new {@link Expression} where possible simplifications were
	 *         performed.
	 */
	public static Expression simplify(final InfixExpression expression) {
		InfixExpression simplifiedExpression = simplifyOperands(expression);
		simplifiedExpression = applyRedundancyLaws(simplifiedExpression);
		simplifiedExpression = applyAbsorptionLaw(simplifiedExpression);
		simplifiedExpression = applyMutuallyDistributiveLaw(simplifiedExpression);
		simplifiedExpression = applyAssociativeLaws(simplifiedExpression);
		simplifiedExpression = applyIdempotentLaws(simplifiedExpression);
		simplifiedExpression = applyUnaryOperationLaws(simplifiedExpression);
		simplifiedExpression = applyEmptySetLaws(simplifiedExpression);
		simplifiedExpression = applyUniversalSetLaws(simplifiedExpression);

		if (simplifiedExpression.equals(expression)) {
			return expression;
		}
		LOGGER.debug("Simplified {} to {}", expression, simplifiedExpression);
		LOGGER.trace("* Trying to simplify again {} ", simplifiedExpression);
		return simplify(simplifiedExpression);

	}

	/**
	 * @return a new {@link InfixExpression} whose operands have been themselves
	 *         simplified (if/when it was possible).
	 */
	private static InfixExpression simplifyOperands(final InfixExpression expression) {
		final List<Expression> simplifiedOperands = new ArrayList<Expression>();
		// asking each operand to simplify itself if it is an InfixExpression
		for (Expression operand : expression.getOperands()) {
			switch (operand.getExpressionType()) {
			case INFIX:
				simplifiedOperands.add(simplify((InfixExpression) operand));
				break;
			default:
				simplifiedOperands.add(operand);
				break;
			}
		}
		final InfixExpression resultExpression = new InfixExpression(expression.getOperator(), simplifiedOperands);
		LOGGER.trace(" Result of operands simplification: {} -> {}", expression, resultExpression);
		return resultExpression;
	}

	/**
	 * Apply Idempotent Laws:
	 * <ul>
	 * <li>{@code a + a = a}</li>
	 * <li>{@code a.a = a}</li>
	 * </ul>
	 * 
	 * @return a new simplified {@link InfixExpression}, or the equivalent of
	 *         the given {@link InfixExpression} if no change was performed.
	 */
	public static InfixExpression applyIdempotentLaws(final InfixExpression expression) {
		if (expression.getOperands().size() > 1) {
			for (Expression operand : expression.getOperands()) {
				final List<Expression> otherOperands = getOtherOperands(expression, operand);
				final List<Expression> operandsToKeep = otherOperands.stream().filter(o -> !o.equals(operand))
						.collect(Collectors.toList());
				if (operandsToKeep.size() == otherOperands.size()) {
					continue;
				}
				final List<Expression> simplifiedOperands = new ArrayList<>(operandsToKeep);
				simplifiedOperands.add(operand);
				final InfixExpression resultExpression = new InfixExpression(expression.getOperator(),
						simplifiedOperands);
				LOGGER.trace(" Result of Idempotent Law application: {} -> {}", expression, resultExpression);
				return applyIdempotentLaws(resultExpression);
			}
		}
		return expression;
	}

	/**
	 * Apply Universal Laws:
	 * <ul>
	 * <li>{@code I + a = I}</li>
	 * <li>{@code I.a = a}</li>
	 * </ul>
	 * 
	 * @return a new simplified {@link InfixExpression}, or the equivalent of
	 *         the given expression if no change was performed.
	 */
	public static InfixExpression applyUniversalSetLaws(final InfixExpression expression) {
		if (expression.getOperator() == InfixOperator.CONDITIONAL_AND && expression.getOperands().size() > 1) {
			// removes 'I' if it is part of the expression operands
			if (expression.getOperands().contains(UNIVERSAL_OPERATOR)) {
				final InfixExpression resultExpression = new InfixExpression(expression.getOperator(), expression
						.getOperands().stream().filter(m -> !m.equals(UNIVERSAL_OPERATOR)).collect(Collectors.toList()));
				LOGGER.trace(" Result of Universal Set Law application: {} -> {}", expression, resultExpression);
				return resultExpression;
			}
		} else if (expression.getOperator() == InfixOperator.CONDITIONAL_OR && expression.getOperands().size() > 1) {
			// removes other operands if 'I' is part of the expression operands
			if (expression.getOperands().contains(UNIVERSAL_OPERATOR)) {
				final InfixExpression resultExpression = new InfixExpression(expression.getOperator(),
						UNIVERSAL_OPERATOR);
				LOGGER.trace(" Result of Universal Set Law application: {} -> {}", expression, resultExpression);
				return resultExpression;
			}
		}
		// if nothing could be done.
		return expression;
	}

	/**
	 * Apply Empty Set Laws:
	 * <ul>
	 * <li>{@code O + a = a}</li>
	 * <li>{@code O.a = O}</li>
	 * </ul>
	 * 
	 * @return a new simplified {@link InfixExpression}, or the given expression
	 *         if no change was performed.
	 */
	public static InfixExpression applyEmptySetLaws(final InfixExpression expression) {
		if (expression.getOperator() == InfixOperator.CONDITIONAL_AND && expression.getOperands().size() > 1) {
			// removes other operands if 'O' is part of the expression operands
			if (expression.getOperands().contains(EMPTY_SET_OPERATOR)) {
				final InfixExpression resultExpression = new InfixExpression(expression.getOperator(),
						EMPTY_SET_OPERATOR);
				LOGGER.trace(" Result of Empty Set Law application: {} -> {}", expression, resultExpression);
				return resultExpression;
			}
		} else if (expression.getOperator() == InfixOperator.CONDITIONAL_OR && expression.getOperands().size() > 1) {
			// removes 'I' if it is part of the expression operands
			if (expression.getOperands().contains(EMPTY_SET_OPERATOR)) {
				final InfixExpression resultExpression = new InfixExpression(expression.getOperator(), expression
						.getOperands().stream().filter(m -> !m.equals(EMPTY_SET_OPERATOR)).collect(Collectors.toList()));
				LOGGER.trace(" Result of UniversalSet Law application: {} -> {}", expression, resultExpression);
				return resultExpression;
			}
		}
		// if nothing could be done.
		return expression;
	}

	/**
	 * Apply Unary Operation Laws:
	 * <ul>
	 * <li>{@code a + !a = I}</li>
	 * <li>{@code a.!a = O}</li>
	 * </ul>
	 * 
	 * @return a new simplified {@link InfixExpression}, or the given expression
	 *         if no change was performed.
	 */
	public static InfixExpression applyUnaryOperationLaws(final InfixExpression expression) {
		if (expression.getOperator() == InfixOperator.CONDITIONAL_AND && expression.getOperands().size() > 1) {
			for (Expression operand : expression.getOperands()) {
				final List<Expression> otherOperands = getOtherOperands(expression, operand);
				if (otherOperands.contains(operand.inverse())) {
					final InfixExpression resultExpression = new InfixExpression(expression.getOperator(),
							new BooleanLiteral(Boolean.FALSE));
					LOGGER.trace(" Result of Unary Operation Law application: {} -> {}", expression, resultExpression);
					return resultExpression;
				}
			}
		} else if (expression.getOperator() == InfixOperator.CONDITIONAL_OR && expression.getOperands().size() > 1) {
			for (Expression operand : expression.getOperands()) {
				final List<Expression> otherOperands = getOtherOperands(expression, operand);
				if (otherOperands.contains(operand.inverse())) {
					final InfixExpression resultExpression = new InfixExpression(expression.getOperator(),
							new BooleanLiteral(Boolean.TRUE));
					LOGGER.trace(" Result of Unary Operation Law application: {} -> {}", expression, resultExpression);
					return resultExpression;
				}
			}
		}
		// if nothing could be done.
		return expression;
	}

	/**
	 * Associative Laws:
	 * <ul>
	 * <li>{@code a.(b.c) = a.b.c}</li>
	 * <li> {@code a + (b + c) = a + b + c}</li></u>
	 * 
	 * @return a new simplified {@link InfixExpression}, or the given expression
	 *         if no change was performed.
	 */
	public static InfixExpression applyAssociativeLaws(final InfixExpression expression) {
		if (expression.getOperands().size() > 1 && expression.getOperator() == InfixOperator.CONDITIONAL_AND) {
			final List<Expression> simplifiedOperands = new ArrayList<>();
			boolean match = false;
			for (Expression operand : expression.getOperands()) {
				if (operand.getExpressionType() == ExpressionType.INFIX) {
					final InfixExpression infixOperand = (InfixExpression) operand;
					if(infixOperand.getOperator() == expression.getOperator()) {
						simplifiedOperands.addAll(infixOperand.getOperands());
						match = true;
					} else { 
						simplifiedOperands.add(infixOperand);
					}
				} else {
					simplifiedOperands.add(operand);
				}
			}
			if(match) {
				final InfixExpression resultExpression = new InfixExpression(expression.getOperator(), simplifiedOperands);
				LOGGER.trace(" Result of Associative Laws application: {} -> {}", expression, resultExpression);
				return applyAssociativeLaws(resultExpression);
			}
		} else if (expression.getOperands().size() > 1 && expression.getOperator() == InfixOperator.CONDITIONAL_OR) {
			final List<Expression> simplifiedOperands = new ArrayList<>();
			boolean match = false;
			for (Expression operand : expression.getOperands()) {
				if (operand.getExpressionType() == ExpressionType.INFIX) {
					final InfixExpression infixOperand = (InfixExpression) operand;
					if(infixOperand.getOperator() == expression.getOperator()) {
						simplifiedOperands.addAll(infixOperand.getOperands());
						match = true;
					} else { 
						simplifiedOperands.add(infixOperand);
					}
				} else {
					simplifiedOperands.add(operand);
				}
			}
			if(match) {
				final InfixExpression resultExpression = new InfixExpression(expression.getOperator(), simplifiedOperands);
				LOGGER.trace(" Result of Associative Laws application: {} -> {}", expression, resultExpression);
				return applyAssociativeLaws(resultExpression);
			}
		}
		return expression;
	}

	/**
	 * Apply Absorption Laws:
	 * <ul>
	 * <li>{@code a.(a + b).(a + c) = a}</li>
	 * <li>{@code a + (a.b) + (a.c) = a}</li>
	 * </ul>
	 * 
	 * @return a new simplified {@link InfixExpression}, or the given expression
	 *         if no change was performed.
	 */
	public static InfixExpression applyAbsorptionLaw(final InfixExpression expression) {
		if (expression.getOperands().size() > 1 && expression.getOperator() == InfixOperator.CONDITIONAL_AND) {
			final List<Expression> simplifiedOperands = new ArrayList<>(expression.getOperands());
			for (Expression operand : expression.getOperands()) {
				// removes from 'simplifiedOperands' all expression that
				// match '(a + b)' where 'a' is the operand to match
				final List<Expression> operandsToRemove = simplifiedOperands
						.stream()
						.filter(m -> !m.equals(operand) && m.getExpressionType() == ExpressionType.INFIX
								&& ((InfixExpression) m).getOperator() == InfixOperator.CONDITIONAL_OR
								&& ((InfixExpression) m).getOperands().contains(operand)).collect(Collectors.toList());
				// if there's no match
				if (operandsToRemove.isEmpty()) {
					continue;
				}
				// otherwise, apply the change and recursively try to apply
				// the same law again
				simplifiedOperands.removeAll(operandsToRemove);
				final InfixExpression resultExpression = new InfixExpression(InfixOperator.CONDITIONAL_AND,
						simplifiedOperands);
				LOGGER.trace(" Result of Absorption Law application: {} -> {}", expression, resultExpression);
				return applyAbsorptionLaw(resultExpression);
			}
		} else if (expression.getOperands().size() > 1 && expression.getOperator() == InfixOperator.CONDITIONAL_OR) {
			final List<Expression> simplifiedOperands = new ArrayList<>(expression.getOperands());
			for (Expression operand : expression.getOperands()) {
				// removes from 'simplifiedOperands' all expression that
				// match '(a + b)' where 'a' is the operand to match
				final List<Expression> operandsToRemove = simplifiedOperands
						.stream()
						.filter(m -> !m.equals(operand) && m.getExpressionType() == ExpressionType.INFIX
								&& ((InfixExpression) m).getOperator() == InfixOperator.CONDITIONAL_AND
								&& ((InfixExpression) m).getOperands().contains(operand)).collect(Collectors.toList());
				// if there's no match
				if (operandsToRemove.isEmpty()) {
					continue;
				}
				// otherwise, apply the change and recursively try to apply
				// the same law again
				simplifiedOperands.removeAll(operandsToRemove);
				final InfixExpression resultExpression = new InfixExpression(InfixOperator.CONDITIONAL_OR,
						simplifiedOperands);
				LOGGER.trace(" Result of Absorption Law application: {} -> {}", expression, resultExpression);
				return applyAbsorptionLaw(resultExpression);
			}
		}
		// if nothing could be done.
		return expression;
	}

	/**
	 * Apply Redundancy Law (derived from other laws):
	 * <ul>
	 * <li>{@code a.(!a + b).(!a + c) = a.b.c}</li>
	 * <li>{@code a + (!a.b) + (!a.c) = a + b + c}</li> </p>
	 * </ul>
	 * in some cases, the law does apply on *some* operands but not *all* of
	 * them. Eg:
	 * <ul>
	 * <li>{@code a.(!a + b).e = a.b.e}</li>
	 * <li>{@code a + (!a.b) + e = a + b + e}</li> </p>
	 * </ul>
	 *
	 * @param expression
	 *            the expression to simplify
	 * @return a simplified {@link InfixExpression} or the given
	 *         {@link InfixExpression} if none of the 2 laws was applicable.
	 */
	public static InfixExpression applyRedundancyLaws(final InfixExpression expression) {
		if (expression.getOperands().size() > 1 && expression.getOperator() == InfixOperator.CONDITIONAL_AND) {
			for (Expression operandMember : expression.getOperands()) {
				final List<Expression> matchingOperands = expression
						.getOperands()
						.stream()
						.filter(o -> o != operandMember)
						.filter(o -> (o.getExpressionType() == ExpressionType.INFIX && ((InfixExpression) o)
								.getOperator() == InfixOperator.CONDITIONAL_OR)
								&& ((InfixExpression) o).getOperands().contains(operandMember.inverse()))
						.collect(Collectors.toList());

				if (matchingOperands.size() > 0) {
					final List<Expression> simplifiedOperands = new ArrayList<>(expression.getOperands());
					simplifiedOperands.removeAll(matchingOperands);
					simplifiedOperands.addAll(matchingOperands.stream()
							.map(o -> ((InfixExpression) o).remove(operandMember.inverse()))
							.collect(Collectors.toList()));
					// attempts to apply the same Law again
					final InfixExpression resultExpression = new InfixExpression(InfixOperator.CONDITIONAL_AND,
							simplifiedOperands);
					LOGGER.trace(" Result of Redundancy Law application: {} -> {}", expression, resultExpression);
					return applyRedundancyLaws(resultExpression);
				}
			}
		} else if (expression.getOperands().size() > 1 && expression.getOperator() == InfixOperator.CONDITIONAL_OR) {
			for (Expression operandMember : expression.getOperands()) {
				final List<Expression> matchingOperands = expression
						.getOperands()
						.stream()
						.filter(o -> o != operandMember)
						.filter(o -> (o.getExpressionType() == ExpressionType.INFIX && ((InfixExpression) o)
								.getOperator() == InfixOperator.CONDITIONAL_AND)
								&& ((InfixExpression) o).getOperands().contains(operandMember.inverse()))
						.collect(Collectors.toList());
				if (matchingOperands.size() > 0) {
					final List<Expression> simplifiedOperands = new ArrayList<>(expression.getOperands());
					simplifiedOperands.removeAll(matchingOperands);
					simplifiedOperands.addAll(matchingOperands.stream()
							.map(o -> ((InfixExpression) o).remove(operandMember.inverse()))
							.collect(Collectors.toList()));
					// attempts to apply the same Law again
					final InfixExpression resultExpression = new InfixExpression(InfixOperator.CONDITIONAL_OR,
							simplifiedOperands);
					LOGGER.trace(" Result of Redundancy Law application: {} -> {}", expression, resultExpression);
					return applyRedundancyLaws(resultExpression);
				}
			}
		}
		return expression;
	}

	/**
	 * Applies (the opposite of) the Mutually Distributive Law on the current
	 * infix expression in case it has the following form:
	 * <ul>
	 * <li>{@code (a.b) + (a.b) + (a.d)} = {@code a.(b + c + d)}</li>
	 * <li>{@code (a + b).(a + b).(a + d)} = {@code a + (b.c.d)}</li>
	 * </ul>
	 * returns the simplified expression or the given expression if Mutually
	 * Distributive Law could not be applied on the given expression.
	 * 
	 * @return a *new* infix expression, or the given expression if the Mutually
	 *         Distributive Law could not be applied .
	 */
	public static InfixExpression applyMutuallyDistributiveLaw(final InfixExpression expression) {
		if (expression.getOperands().size() > 1 && expression.getOperator() == InfixOperator.CONDITIONAL_AND) {
			// only need to compare elements of the first operand with the
			// elements of the other operands
			final Expression currentOperand = expression.getOperands().get(0);
			if (currentOperand.getExpressionType() == ExpressionType.INFIX) {
				final List<Expression> otherOperands = getOtherOperands(expression, currentOperand);
				for (Expression operandElement : ((InfixExpression) currentOperand).getOperands()) {
					boolean allMatch = otherOperands.stream().allMatch(
							o -> o.getExpressionType() == ExpressionType.INFIX
									&& ((InfixExpression) o).getOperator() == InfixOperator.CONDITIONAL_OR
									&& ((InfixExpression) o).getOperands().contains(operandElement));
					if (allMatch) {
						final List<Expression> simplifiedOtherOperands = otherOperands.stream()
								.map(o -> o.remove(operandElement)).collect(Collectors.toList());
						simplifiedOtherOperands.add(currentOperand.remove(operandElement));
						final InfixExpression resultExpression = new InfixExpression(InfixOperator.CONDITIONAL_OR,
								operandElement, new InfixExpression(InfixOperator.CONDITIONAL_AND,
										simplifiedOtherOperands));
						LOGGER.trace(" Result of Mutually Distributive Law application: {} -> {}", expression,
								resultExpression);
						return applyMutuallyDistributiveLaw(resultExpression);
					}
				}
			}
		} else if (expression.getOperands().size() > 1 && expression.getOperator() == InfixOperator.CONDITIONAL_OR) {
			// only need to compare elements of the first operand with the
			// elements of the other operands
			final Expression currentOperand = expression.getOperands().get(0);
			if (currentOperand.getExpressionType() == ExpressionType.INFIX) {
				final List<Expression> otherOperands = getOtherOperands(expression, currentOperand);
				for (Expression operandElement : ((InfixExpression) currentOperand).getOperands()) {
					boolean allMatch = otherOperands.stream().allMatch(
							o -> o.getExpressionType() == ExpressionType.INFIX
									&& ((InfixExpression) o).getOperator() == InfixOperator.CONDITIONAL_AND
									&& ((InfixExpression) o).getOperands().contains(operandElement));
					if (allMatch) {
						final List<Expression> simplifiedOtherOperands = otherOperands.stream()
								.map(o -> o.remove(operandElement)).collect(Collectors.toList());
						simplifiedOtherOperands.add(currentOperand.remove(operandElement));
						final InfixExpression resultExpression = new InfixExpression(InfixOperator.CONDITIONAL_AND,
								operandElement, new InfixExpression(InfixOperator.CONDITIONAL_OR,
										simplifiedOtherOperands));
						LOGGER.trace(" Result of Mutually Distributive Law application: {} -> {}", expression,
								resultExpression);
						return applyMutuallyDistributiveLaw(resultExpression);
					}
				}
			}
		}
		return expression;
	}

	/**
	 * @return the list of {@code Expression} operands other than the given
	 *         {@link Expression} operand in the given {@link InfixExpression}
	 * 
	 * @param expression
	 *            the {@link InfixExpression} to analyze
	 * @param operand
	 *            the {@link Expression} to exclude from the result list.
	 */
	private static List<Expression> getOtherOperands(final InfixExpression expression, final Expression operand) {
		return getOtherOperands(expression.getOperands(), operand);
	}

	/**
	 * @return the list of {@code Expression} operands other than the given
	 *         {@link Expression} operand in the given list of
	 *         {@link Expression}
	 * 
	 * @param expressions
	 *            the list of {@link Expression} to analyze
	 * @param operand
	 *            the {@link Expression} to exclude from the result list.
	 */
	private static List<Expression> getOtherOperands(final List<Expression> expressions, final Expression operand) {
		return expressions.stream().filter(m -> m != operand).collect(Collectors.toList());
	}

}
