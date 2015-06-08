/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

import org.lambdamatic.analyzer.ast.node.Expression.ExpressionType;

/**
 * Checks if the visited {@link Expression} matches the expected {@link ExpressionType}.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class ExpressionTypeMatcher extends ExpressionVisitor {

	private final ExpressionType expectedExpressionType;

	private boolean matchFound = false;
	
	public ExpressionTypeMatcher(final ExpressionType expectedExpressionType) {
		this.expectedExpressionType = expectedExpressionType;
	}
	
	public boolean isMatchFound() {
		return this.matchFound;
	}
	
	@Override
	public boolean visit(final Expression expr) {
		if(expr.getExpressionType() == expectedExpressionType) {
			this.matchFound = true;
			return false;
		}
		return super.visit(expr);
	}

}
