/**
 * 
 */
package org.lambdamatic;

import java.util.List;

import org.lambdamatic.analyzer.ast.node.Expression;

/**
 * The {@link FilterExpression} and its relevant data in a form that can be manipulated.
 * 
 * @author xcoulon
 *
 */
public class LambdaExpression {

	/** The {@link FilterExpression} AST with captured arguments. */
	private final Expression expression;
	
	/** The type of the element being evaluated in the {@link FilterExpression}. */
	private final Class<?> argumentType;
	
	/**
	 * Constructor
	 * @param expression The {@link FilterExpression} AST with captured arguments.
	 * @param argumentType The type of the element being evaluated in the {@link FilterExpression}.
	 */
	public LambdaExpression(final Expression expression, final Class<?> argumentType) {
		this.expression = expression;
		this.argumentType = argumentType;
	}

	/**
	 * @return The {@link FilterExpression} AST with captured arguments.
	 */
	public Expression getExpression() {
		return expression;
	}

	/**
	 * @return The type of the element being evaluated in the {@link FilterExpression}.
	 */
	public Class<?> getArgumentType() {
		return argumentType;
	}

}
