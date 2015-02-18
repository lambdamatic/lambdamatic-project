/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;


/**
 * Array created and used during the call to the Lambda Expression serialized method.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class ArrayVariable extends ComplexExpression {

	/** the expression on which the method call is applied. */
	private final Class<?> instanceType;

	/** the underlying array elements. */
	private final Expression[] elements;

	/**
	 * Initializes an {@link ArrayVariable} from the given elements.
	 */
	public <T> ArrayVariable(final Class<T> instanceType, final Expression... elements) {
		this(instanceType, elements.length);
		System.arraycopy(elements, 0, this.elements, 0, elements.length);
		Arrays.asList(this.elements).stream().forEach(e -> e.setParent(this));
	}
	
	/**
	 * Full constructor.
	 * <p>
	 * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
	 * </p>
	 * @param instanceType the type of the instance to build
	 * @param length the length of the underlying array
	 */
	public ArrayVariable(final Class<?> instanceType, final int length) {
		this(generateId(), instanceType, length, false);
	}

	/**
	 * Full constructor.
	 * <p>
	 * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
	 * </p>
	 * @param id the id of this expression
	 * @param instanceType the type of the instance to build
	 * @param length the length of the underlying array
	 * @param inverted the inversion flag
	 */
	public ArrayVariable(final Class<?> instanceType, final int length, boolean inverted) {
		this(generateId(), instanceType, length, inverted);
	}
	
	/**
	 * Full constructor.
	 * <p>
	 * Note: the synthetic {@code id} is generated and the inversion flag is set to {@code false}.
	 * </p>
	 * @param id the id of this expression
	 * @param instanceType the type of the instance to build
	 * @param length the length of the underlying array
	 * @param inverted the inversion flag
	 */
	public ArrayVariable(final int id, final Class<?> instanceType, final int length, boolean inverted) {
		super(id, inverted);
		this.instanceType = instanceType;
		this.elements = new Expression[length];
	}
	
	/**
	 * @see org.lambdamatic.analyzer.ast.node.Expression#getExpressionType()
	 */
	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.ARRAY_VARIABLE;
	}

	/**
	 * @see org.lambdamatic.analyzer.ast.node.Expression#getJavaType()
	 */
	@Override
	public Class<?> getJavaType() {
		return instanceType;
	}

	/**
	 * @return the {@link Expression} element of this {@link ArrayVariable}
	 */
	@Override
	public Object getValue() {
		final Object[] values = (Object[]) Array.newInstance(instanceType, elements.length);
		for(int i = 0; i < elements.length; i++) {
			values[i] = elements[i].getValue();
		}
		return values;
	}
	
	/**
	 * @return the elements contained in this array.
	 */
	public Expression[] getElements() {
		return elements;
	}
	
	@Override
	public boolean anyElementMatches(ExpressionType type) {
		final List<Expression> elements = Arrays.asList(this.elements);
		return elements.stream().anyMatch(e -> e.anyElementMatches(type));
	}
	
	/**
	 * Sets the given {@code element} at the given {@code index} in the underlying array of {@link Expression}.
	 * @param index the index in the array
	 * @param element the element to add
	 */
	public void setElement(final int index, final Expression element) {
		this.elements[index] = element;
		element.setParent(this);
	}
	
	/**
	 * @see org.lambdamatic.analyzer.ast.node.Expression#inverse()
	 */
	@Override
	public Expression inverse() {
		throw new UnsupportedOperationException(this.getClass().getName() + " does not support inversion.");
	}

	/*$
	 * @see org.lambdamatic.analyzer.ast.node.Expression#canBeInverted()
	 */
	@Override
	public boolean canBeInverted() {
		return false;
	}

	/**
	 * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate(int)
	 */
	@Override
	public Expression duplicate(int id) {
		final ArrayVariable arrayVariable = new ArrayVariable(id, instanceType, this.elements.length, isInverted());
		for(int i = 0; i < this.elements.length; i++) {
			arrayVariable.setElement(i, this.elements[i].duplicate());
		}
		return arrayVariable;
	}

	/**
	 * @see org.lambdamatic.analyzer.ast.node.Expression#duplicate()
	 */
	@Override
	public Expression duplicate() {
		return duplicate(generateId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(final ExpressionVisitor visitor) {
		if (visitor.visit(this)) {
			for (Expression element : this.elements) {
				element.accept(visitor);
			}
		}
	}
	
	@Override
	public void replaceElement(final Expression oldElementExpression, final Expression newElementExpression) {
		final int oldExpressionIndex = ArrayUtils.indexOf(this.elements, oldElementExpression);
		if (oldExpressionIndex > -1) {
			this.elements[oldExpressionIndex] = newElementExpression;
			newElementExpression.setParent(this);
		}
	}

	@Override
	public String toString() {
		return this.instanceType.getName()
				+ Arrays.asList(elements).stream().map(Expression::toString).collect(Collectors.toList());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(elements);
		result = prime * result + ((instanceType == null) ? 0 : instanceType.hashCode());
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
		ArrayVariable other = (ArrayVariable) obj;
		if (!Arrays.equals(elements, other.elements))
			return false;
		if (instanceType == null) {
			if (other.instanceType != null)
				return false;
		} else if (!instanceType.equals(other.instanceType))
			return false;
		return true;
	}

	
}
