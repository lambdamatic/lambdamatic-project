/**
 * 
 */
package org.bytesparadise.lambdamatic.internal.ast.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Infix expression AST node type.
 * {@code Expression InfixOperator Expression [InfixOperator Expression]*}
 * 
 * <strong>Note</strong>: If the {@code InfixOperator} is the same between all
 * {@code Expression} items of this {@link InfixExpression}
 * 
 *
 * @author xcoulon
 *
 */
public class InfixExpression extends Expression {

	private static final Logger LOGGER = LoggerFactory.getLogger(InfixExpression.class);
	
	/**
	 * Infix operators (typesafe enumeration).
	 * 
	 * <pre>
	 * InfixOperator:<code>
	 *    <b>*</b>	TIMES
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
	public enum InfixOperator {
		TIMES("*"), DIVIDE("/"), REMAINDER("%"), PLUS("+"), MINUS("-"), LESS("<"), GREATER(">"), LESS_EQUALS("<="), GREATER_EQUALS(
				">="), EQUALS("=="), NOT_EQUALS("!-"), XORE("^"), AND("&"), OR("|"), CONDITIONAL_AND("&&"), CONDITIONAL_OR(
				"||"), LEFT_SHIFT("<<"), RIGHT_SHIFT(">>"), RIGHT_SHIFT_UNSIGNED(">>>");

		private final String value;

		private InfixOperator(final String v) {
			this.value = v;
		}

		@Override
		public String toString() {
			return value;
		}

		/**
		 * @return an {@link InfixOperator} for the given {@link Opcodes} value.
		 *         Let's keep in mind that the compiler generates an opposite
		 *         comparison that should be restored in its actual form.
		 * @param opcode
		 *            the opcode
		 */
		public static InfixOperator from(final int opcode) {
			switch(opcode) {
			case Opcodes.IF_ICMPLE:
				return InfixOperator.GREATER;
			case Opcodes.IF_ICMPLT:
				return InfixOperator.GREATER_EQUALS;
			case Opcodes.IF_ICMPGE:
				return InfixOperator.LESS;
			case Opcodes.IF_ICMPGT:
				return InfixOperator.LESS_EQUALS;
			}
			LOGGER.error("Oops, it seems we did not plan this case: {}", opcode);
			return null;
		}

	}

	/** The Infix expression operands. */
	private final List<Expression> operands;

	/** The Infix expression operator. */
	private final InfixOperator operator;

	/**
	 * Full constructor
	 * 
	 * @param operator
	 *            the Infix expression operator.
	 * @param operands
	 *            the Infix expression operands.
	 */
	public InfixExpression(final InfixOperator operator, final Expression... operands) {
		this(operator, Arrays.asList(operands));
	}

	/**
	 * Full constructor
	 * 
	 * @param operator
	 *            the Infix expression operator.
	 * @param operands
	 *            the list Infix expression operands.
	 */
	public InfixExpression(final InfixOperator operator, final List<Expression> operands) {
		super();
		//TODO: use a single stream to set the parent expression and sort all operands at once.
		this.operands = operands.stream().sorted((x, y) -> x.hashCode() - y.hashCode()).collect(Collectors.toList());
		this.operands.stream().forEach(e -> e.setParent(this));
		this.operator = operator;
	}

	@Override
	public ExpressionType getExpressionType() {
		return ExpressionType.INFIX;
	}

	/**
	 * @return the operands
	 */
	public List<Expression> getOperands() {
		return operands;
	}

	/**
	 * @return the operator
	 */
	public InfixOperator getOperator() {
		return operator;
	}

	/**
	 * Removes the given {@link Expression} from the list of operands of this
	 * {@link InfixExpression}.
	 * 
	 * @param member
	 *            the node to remove.
	 * @return an Expression representing the result of the given member
	 *         removal. If there is only one member left in this
	 *         {@link InfixExpression}, then the result if the remaining
	 *         {@link Expression} itself.
	 */
	@Override
	public Expression remove(final Expression member) {
		final List<Expression> operandsWorkingCopy = new ArrayList<>(this.operands);
		operandsWorkingCopy.remove(member);
		if (operandsWorkingCopy.size() == 1) {
			return operandsWorkingCopy.get(0);
		}
		return new InfixExpression(this.getOperator(), operandsWorkingCopy);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	void replace(final Expression oldExpression, final Expression newExpression) {
		final int oldExpressionIndex = this.operands.indexOf(oldExpression);
		if(oldExpressionIndex > -1) {
			this.operands.set(oldExpressionIndex, newExpression);
		}
	}

	/**
	 * @return {@code true} if the operands contain the given {@link Expression}
	 *         , false otherwise.
	 * @param member
	 *            the member to look for.
	 */
	public boolean contains(final Expression member) {
		return this.operands.contains(member);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.bytesparadise.lambdamatic.internal.ast.node.Expression#inverse()
	 */
	@Override
	public Expression inverse() {
		final InfixExpression inversedInfixExpression = new InfixExpression(operator, operands);
		inversedInfixExpression.inversed = !this.inversed;
		return inversedInfixExpression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(final ExpressionVisitor visitor) {
		if (visitor.visit(this)) {
			for(Expression operand : operands) {
				operand.accept(visitor);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append('(');
		final List<String> operandValues = operands.stream().map(Expression::toString).collect(Collectors.toList());
		builder.append(String.join(' ' + operator.value + ' ', operandValues));
		builder.append(')');
		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((operands == null) ? 0 : operands.hashCode());
		result = prime * result + ((operator == null) ? 0 : operator.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
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
		InfixExpression other = (InfixExpression) obj;
		if (operands == null) {
			if (other.operands != null)
				return false;
		} else if (!operands.equals(other.operands))
			return false;
		if (operator != other.operator)
			return false;
		return true;
	}

}
