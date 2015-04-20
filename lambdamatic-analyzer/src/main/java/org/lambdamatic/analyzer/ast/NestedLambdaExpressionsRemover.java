/**
 * 
 */
package org.lambdamatic.analyzer.ast;

import org.lambdamatic.analyzer.ast.node.CapturedArgument;
import org.lambdamatic.analyzer.ast.node.ComplexExpression;
import org.lambdamatic.analyzer.ast.node.ExpressionVisitor;
import org.lambdamatic.analyzer.ast.node.LambdaExpression;

/**
 * {@link ExpressionVisitor} that removes nested {@link LambdaExpression} with their
 * {@link LambdaExpression#getExpression()} when they are visited. This visitor is used when there is no
 * {@link CapturedArgument} in the user lambda, but still, there might be nested {@link LambdaExpression} that we don't
 * need at the end of the bytecode analysis.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class NestedLambdaExpressionsRemover extends ExpressionVisitor {

	@Override
	public boolean visitLambdaExpression(final LambdaExpression lambdaExpression) {
		final ComplexExpression parentExpression = lambdaExpression.getParent();
		parentExpression.replaceElement(lambdaExpression, lambdaExpression.getExpression());
		return true;
	}
}
