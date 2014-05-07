/**
 * 
 */
package org.bytesparadise.lambdamatic.internal.ast.node;

/**
 * @author xcoulon
 *
 */
public class CapturedArgument extends Expression {

	private final Object value;
	
	/**
	 * Constructor
	 * @param value the captured argument
	 */
	public CapturedArgument(Object value) {
		super();
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	/**
	 * {@inheritDoc}
	 * @see org.bytesparadise.lambdamatic.internal.ast.node.Expression#eval()
	 */
	@Override
	public Object eval() {
		return getValue();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.CAPTURED_ARGUMENT;
	}

	/**
	 * {@inheritDoc}
	 * @see org.bytesparadise.lambdamatic.internal.ast.node.Expression#inverse()
	 */
	@Override
	public Expression inverse() {
		throw new UnsupportedOperationException(this.getClass().getName() + " does not support inversion.");
	}
	
	@Override
	public String toString() {
		return value.getClass().getName();
	}

}
