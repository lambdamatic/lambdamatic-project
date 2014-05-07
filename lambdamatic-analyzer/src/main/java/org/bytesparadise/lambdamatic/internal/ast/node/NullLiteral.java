/**
 * 
 */
package org.bytesparadise.lambdamatic.internal.ast.node;


/**
 * @author xcoulon
 *
 */
public class NullLiteral extends Expression {

	/**
	 * Full constructor
	 * @param value the literal value
	 */
	public NullLiteral() {
		super();
	}

	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.CHARACTER_LITERAL;
	}
	
	@Override
	public Expression inverse() {
		throw new UnsupportedOperationException(this.getClass().getName() + " does not support inversion.");
	}

	/**
	 * {@inheritDoc}
	 * @see org.bytesparadise.lambdamatic.internal.ast.node.Expression#eval()
	 */
	@Override
	public Object eval() {
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() { 
		return "null";
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj != null && obj.getClass().equals(this.getClass());
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
