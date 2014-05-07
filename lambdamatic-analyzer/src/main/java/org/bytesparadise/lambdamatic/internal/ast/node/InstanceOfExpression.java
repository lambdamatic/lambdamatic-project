/**
 * 
 */
package org.bytesparadise.lambdamatic.internal.ast.node;


/**
 * InstanceOf expression AST node type:
 * {@code Expression instanceof Type} 
 *
 * @author xcoulon
 *
 */
public class InstanceOfExpression extends Expression {

	/** The expression being evaluated. */
	private final Expression expression;
	
	/** The expected expression type.*/
	private final Type type;
	
	/**
	 * Full constructor.
	 * @param expression the expression being evaluated.
	 * @param type the expected expression type.
	 */
	public InstanceOfExpression(Expression expression, Type type) {
		super();
		this.expression = expression;
		this.type = type;
	}

	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.INSTANCE_OF;
	}

	/**
	 * @return the expression
	 */
	public Expression getExpression() {
		return expression;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.bytesparadise.lambdamatic.internal.ast.node.Expression#inverse()
	 */
	@Override
	public Expression inverse() {
		final InstanceOfExpression inversedInstanceOfExpression = new InstanceOfExpression(expression, type);
		inversedInstanceOfExpression.inversed = !this.inversed;
		return inversedInstanceOfExpression;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InstanceOfExpression other = (InstanceOfExpression) obj;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}
