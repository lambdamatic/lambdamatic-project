/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Abstract base class of AST nodes that represent (bytecode) statements.
 *
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public abstract class Expression extends ASTNode implements Comparable<Expression> {

	/**
	 * The parent {@link Expression} (or null if this expression is root of an Expression tree or not part of an
	 * Expression tree)
	 */
	private Expression parent;

	public enum ExpressionType {
		BOOLEAN_LITERAL, CHARACTER_LITERAL, CLASS_LITERAL, NUMBER_LITERAL, NULL_LITERAL, STRING_LITERAL, OBJECT_INSTANCIATION, OBJECT_INSTANCE, ARRAY_VARIABLE, METHOD_INVOCATION, FIELD_ACCESS, COMPOUND, INSTANCE_OF, LOCAL_VARIABLE, CAPTURED_ARGUMENT, CAPTURED_ARGUMENT_REF, ENUM_LITERAL, LAMBDA_EXPRESSION, ASSIGNMENT, OPERATION, ARRAY_ELEMENT_ACCESS;
	}

	/** synthetic id generator based on {@link AtomicInteger}. */
	private static AtomicInteger sequence = new AtomicInteger();

	/**
	 * @return a new synthetic ID
	 */
	protected static int generateId() {
		final int id = sequence.incrementAndGet();
		return id;
	}

	/**
	 * @return the current synthetic ID
	 */
	static int currentId() {
		return sequence.get();
	}

	/** the synthetic Id for this expression. */
	private final int id;

	/** A flag to indicate if the method invocation result should be inverted. */
	private final boolean inverted;

	/**
	 * Full constructor
	 * 
	 * @param id
	 *            the synthetic id of this {@link Expression}.
	 * @param inverted
	 *            the inversion flag of this {@link Expression}.
	 */
	public Expression(final int id, final boolean inverted) {
		this.id = id;
		this.inverted = inverted;
	}

	/**
	 * @return the internal Id of this {@link Expression}.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the {@link ExpressionType} of this {@link Expression}
	 */
	public abstract ExpressionType getExpressionType();

	/**
	 * Checks if any {@link Expression} element of this {@link ComplexExpression} is of the given {@link ExpressionType}
	 * .
	 * 
	 * @param type
	 *            the {@link ExpressionType} to look for
	 * @return {@code true} if any matches, {@code false} otherwise.
	 */
	public boolean anyElementMatches(final ExpressionType type) {
		return getExpressionType() == type;
	}

	/**
	 * @return the type of the underlying Java element of this {@link Expression}
	 */
	public abstract Class<?> getJavaType();

	/**
	 * The given visitor is notified of the type of {@link Expression} is it currently visiting. The visitor may also
	 * accept to visit the children {@link Expression} of this node.
	 * 
	 * @param visitor
	 *            the {@link Expression} visitor
	 */
	public void accept(final ExpressionVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * @return the inversion flag.
	 */
	public boolean isInverted() {
		return inverted;
	}

	/**
	 * @return an inverted instance of the current element.
	 */
	public Expression inverse() {
		throw new UnsupportedOperationException(this.getClass().getName() + " does not support inversion.");
	}
	
	
	/**
	 * @return {@code true} if this {@link Expression} can be inverted, {@code false} otherwise.
	 *         <p>
	 *         This method always should be called before {@link Expression#inverse()}.
	 */
	public abstract boolean canBeInverted();

	/**
	 * Duplicates {@code this} {@link Expression} and sets the given {@code id}.
	 * 
	 * @param id
	 *            the id to use for the duplicate version of this {@link Expression}.
	 * @return the duplicate {@link Expression}.
	 */
	public abstract Expression duplicate(final int id);

	/**
	 * Duplicates {@code this} {@link Expression} and without any specific {@code id}.
	 * 
	 * @return the duplicate {@link Expression}.
	 */
	public abstract Expression duplicate();

	/**
	 * @return the {@code absolute} version of this {@link Expression}, ie, the non-inverted form if it is inverted,
	 *         {@code this} otherwise.
	 */
	public Expression getAbsolute() {
		if (inverted) {
			return this.inverse();
		}
		return this;
	}

	/**
	 * Sets the link with the parent {@link Expression}
	 * 
	 * @param parent
	 *            the parent Expression
	 */
	public void setParent(final Expression parent) {
		this.parent = parent;
	}

	/**
	 * @return the parent {@link Expression} (or null if this expression is root of an Expression tree or not part of an
	 *         Expression tree)
	 */
	public Expression getParent() {
		return parent;
	}

	/**
	 * @return the <strong>root</strong> parent {@link Expression}
	 */
	public Expression getRoot() {
		if (getParent() == null) {
			return this;
		}
		return getParent().getRoot();
	}

	/**
	 * @return {@code true} if {@code this} {@link Expression} is the <strong>root</strong> node in the Expression tree,
	 *         ie, it has no parent Expression.
	 */
	public boolean isRoot() {
		return getParent() == null;
	}

	/**
	 * Computes all variants of {@code this} {@link Expression}.
	 * 
	 * <p>
	 * By default, does nothing.
	 * </p>
	 * 
	 * @param monitor
	 *            the {@link ExpressionSimplificationMonitor} that keeps track of the changes and new variants of this
	 *            expression (to avoid duplicate computations).
	 * @return a {@link List} of {@link Expression} being all new variant forms of {@code this} expression, ordered by
	 *         increasing complexity of the resulting expressions
	 */
	protected List<Expression> computeVariants(final ExpressionSimplificationMonitor monitor) {
		// does nothing
		monitor.registerExpression(this);
		return Collections.emptyList();
	}

	/**
	 * Apply all boolean laws on {@code this} expression.
	 * 
	 * <p>
	 * By default, does nothing.
	 * </p>
	 * 
	 * @param monitor
	 *            the {@link ExpressionSimplificationMonitor} that keeps track of the changes and new variants of this
	 *            expression (to avoid duplicate computations).
	 * @return a {@link List} of {@link Expression} being all new variant forms of {@code this} expression, ordered by
	 *         increasing complexity of the resulting expressions
	 */
	protected List<Expression> applyBooleanLaws(final ExpressionSimplificationMonitor monitor) {
		// does nothing
		return Collections.emptyList();
	}

	/**
	 * @return the value of {@code this} Expression
	 */
	public Object getValue() {
		throw new UnsupportedOperationException(
				"Call to Expression#getValue() is not supported for " + this.getClass().getName());
	}

	@Override
	public int compareTo(Expression other) {
		return this.getComplexity() - other.getComplexity();
	}

	/**
	 * Computes and returns the complexity for this {@link Expression}. Simple expressions have a complexity of
	 * {@code 1}. See overridden methods for more complex Expressions.
	 * 
	 * @see CompoundExpression.
	 * @return
	 */
	public int getComplexity() {
		return 1;
	}

	/**
	 * @return {@code true} if the current {@link CompoundExpression} can be further simplified, {@code false} otherwise
	 *         <p>
	 *         Returns {@code false} by default, since simple expression cannot be further simplified.
	 *         </p>
	 */
	protected boolean canFurtherSimplify() {
		return false;
	}

	/**
	 * duplicates the given {@link Expression}, but <strong>does not modify the parent reference</strong>.
	 * 
	 * @return a duplicate {@link List} of the {@link Expression} arguments
	 */
	public static List<Expression> duplicateExpressions(final List<Expression> sourceExpressions) {
		return sourceExpressions.stream().map(e -> {
			return e.duplicate();
		} ).collect(Collectors.toList());
	}

}
