/**
 * 
 */
package org.bytesparadise.lambdamatic.internal.ast.node;

/**
 * Abstract base class of AST nodes that represent (bytecode) statements.
 *
 * @author xcoulon
 *
 */
public abstract class Expression extends ASTNode {

	/** The parent {@link Expression} (or null if this expression is root of an Expression tree or not part of an Expression tree) */
	private Expression parent;
	
	public enum ExpressionType {
		BOOLEAN_LITERAL, CHARACTER_LITERAL, NUMBER_LITERAL, NULL_LITERAL, STRING_LITERAL, METHOD_INVOCATION, FIELD_ACCESS, INFIX, INSTANCE_OF, VARIABLE, CAPTURED_ARGUMENT;
	}

	public abstract ExpressionType getExpressionType();

	/**
	 * The given visitor is notified of the type of {@link Expression} is it
	 * currently visiting. The visitor may also accept to visit the children
	 * {@link Expression} of this node.
	 * 
	 * @param visitor
	 *            the {@link Expression} visitor
	 */
	public void accept(final ExpressionVisitor visitor) {
		visitor.visit(this);
	}

	/** A flag to indicate if the method invocation result should be inversed. */
	boolean inversed = false;

	/**
	 * @return an inverted instance of the current element.
	 */
	public abstract Expression inverse();

	/**
	 * Removes the given operand from the current {@link Expression}. This
	 * method may be overridden by subclasses of {@link Expression}
	 * 
	 * @param the
	 *            operand to remove
	 * @return {@code this} if the current expression is not the given
	 *         expression, or a {@link BooleanLiteral} with value set to
	 *         {@code TRUE} if this expression is equal to the given
	 *         operand.
	 * @see InfixExpression#factorize()
	 * 
	 */
	public Expression remove(final Expression operand) {
		if(this.equals(operand)) {
			return new BooleanLiteral(Boolean.TRUE);
		}
		return this;
	}
	
	/**
	 * Sets the link with the parent {@link Expression}
	 * @param parent the parent Expression
	 */
	public void setParent(final Expression parent) {
		this.parent = parent;
	}
	
	/**
	 * @return the parent {@link Expression} (or null if this expression is root of an Expression tree or not part of an Expression tree)
	 */
	public Expression getParent() {
		return parent;
	}
	
	/**
	 * Replaces {@code this} {@link Expression} with the one given in parameter in the whole tree.
	 * @param expression the expression to replace {@code this}.
	 * 
	 * @see Expression#setParent(Expression)
	 */
	public void replaceWith(final Expression expression) {
		if(getParent() != null) {
			getParent().replace(this, expression);
		}
	}

	/**
	 * Replaces the given oldExpression with the given newExpression.
	 * @param oldExpression the Expression to replace
	 * @param newExpression the Expression to use as a replacement
	 */
	void replace(Expression oldExpression, Expression newExpression) {
		// by default, do nothing.
	}

	/**
	 * Evaluate the value of {@code this} Expression
	 * @return the expression value
	 */
	public Object eval() {
		throw new UnsupportedOperationException("Call to Expression#eval() is not supported for " + this.getClass().getName());
	}
	
	
}
