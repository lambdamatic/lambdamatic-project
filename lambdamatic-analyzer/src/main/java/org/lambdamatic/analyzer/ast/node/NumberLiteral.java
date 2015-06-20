/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

import org.lambdamatic.analyzer.exception.AnalyzeException;
import org.lambdamatic.analyzer.utils.MathUtils;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class NumberLiteral extends ObjectInstance {

	/** Flag to indicate if the actual value is a primitive type (true) or a primitive wrapper (false).*/
	private final boolean primitiveType;
	
	/**
	 * Full constructor
	 * <p>
	 * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
	 * </p>
	 * 
	 * @param value
	 *            the literal value
	 * @param primitiveType
	 *            Flag to indicate if the actual value is a primitive type (true) or a primitive wrapper (false)
	 */
	public NumberLiteral(final Number value, final boolean primitiveType) {
		this(generateId(), value, primitiveType, false);
	}
	
	/**
	 * Constructor for primitive <code>short</code> value.
	 * @param value the value
	 */
	public NumberLiteral(final short value) {
		this(value, true);
	}

	/**
	 * Constructor for primitive <code>byte</code> value.
	 * @param value the value
	 */
	public NumberLiteral(final byte value) {
		this(value, true);
	}
	
	/**
	 * Constructor for primitive <code>int</code> value.
	 * @param value the value
	 */
	public NumberLiteral(final int value) {
		this(value, true);
	}
	
	/**
	 * Constructor for primitive <code>float</code> value.
	 * @param value the value
	 */
	public NumberLiteral(final float value) {
		this(value, true);
	}
	
	/**
	 * Constructor for primitive <code>long</code> value.
	 * @param value the value
	 */
	public NumberLiteral(final long value) {
		this(value, true);
	}
	
	/**
	 * Constructor for primitive <code>double</code> value.
	 * @param value the value
	 */
	public NumberLiteral(final double value) {
		this(value, true);
	}
	
	/**
	 * Full constructor with given id
	 * 
	 * @param id
	 *            the synthetic id of this {@link Expression}.
	 * @param value
	 *            the literal value
	 * @param primitiveType
	 *            Flag to indicate if the actual value is a primitive type (true) or a primitive wrapper (false)
	 * @param inverted
	 *            the inversion flag of this {@link Expression}.
	 */
	public NumberLiteral(final int id, final Number value, final boolean primitiveType, final boolean inverted) {
		super(id, value, inverted);
		this.primitiveType = primitiveType;
	}
	
	/**
	 * @return Flag to indicate if the actual value is a primitive type (true) or a primitive wrapper (false)
	 */
	public boolean isPrimitiveType() {
		return primitiveType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate(int)
	 */
	@Override
	public NumberLiteral duplicate(int id) {
		return new NumberLiteral(id, getValue(), this.primitiveType, isInverted());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.lambdamatic.analyzer.ast.node.Expression#getExpressionType()
	 */
	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.NUMBER_LITERAL;
	}

	@Override
	public Number getValue() {
		return (Number) super.getValue();
	}

	@Override
	public Class<?> getJavaType() {
		final Class<?> javaType = super.getJavaType();
		if(this.primitiveType) {
			switch(javaType.getName()) {
			case "java.lang.Byte":
				return byte.class;
			case "java.lang.Short":
				return short.class;
			case "java.lang.Integer":
				return int.class;
			case "java.lang.Float":
				return float.class;
			case "java.lang.Double":
				return double.class;
			case "java.lang.Long":
				return long.class;
			default:
				throw new AnalyzeException("Unexpected primitive wrapper type: " + javaType.getName());
			}
		}
		return javaType;
	}
	/**
	 * {@inheritDoc}
	 * @see org.lambdamatic.analyzer.ast.node.Expression#canBeInverted()
	 */
	@Override
	public boolean canBeInverted() {
		return true;
	}

	@Override
	public Expression inverse() {
		return new NumberLiteral(MathUtils.opposite(getValue()), this.primitiveType);
	}


}
