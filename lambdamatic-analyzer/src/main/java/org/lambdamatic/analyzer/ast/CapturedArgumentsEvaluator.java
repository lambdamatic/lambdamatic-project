/**
 * 
 */
package org.lambdamatic.analyzer.ast;

import org.lambdamatic.analyzer.ast.node.CapturedArgument;
import org.lambdamatic.analyzer.ast.node.ExpressionVisitor;
import org.lambdamatic.analyzer.ast.node.LiteralFactory;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;

/**
 * Visitor that will replace the {@link CapturedArgument} with their actual value.
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
@Deprecated
public class CapturedArgumentsEvaluator extends ExpressionVisitor {
	
	/**
	 * Occurs when the {@link CapturedArgument} is not the source expression of a {@link MethodInvocation}. {@inheritDoc}
	 * 
	 * @see org.lambdamatic.analyzer.ast.node.ExpressionVisitor#visitCapturedArgument(org.lambdamatic.analyzer.ast.node.CapturedArgument)
	 */
	@Override
	public boolean visitCapturedArgument(final CapturedArgument capturedArgument) {
		capturedArgument.getParent().replaceElement(capturedArgument, LiteralFactory.getLiteral(capturedArgument.getValue()));
		// no need to keep on visiting the current branch after the replacement with a literal.
		return false;
	}
	
}
