/**
 * 
 */
package org.lambdamatic.analyzer;

import org.lambdamatic.analyzer.ast.SerializedLambdaInfo;
import org.lambdamatic.analyzer.ast.node.LambdaExpression;

/**
 * A listener for events sent by the {@link LambdaExpressionAnalyzer} when analyzing the bytecode.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public interface LambdaExpressionAnalyzerListener {

	/**
	 * Notifies when the internal cache for a {@link LambdaExpression} located at the given <code>methodImplementationId</id> was <strong>missed</strong>.
	 * @param methodImplementationId the fully qualified name and descriptor of the method implementing the user Lambda Expression
	 * @see SerializedLambdaInfo#getImplMethodId()
	 */
	public void cacheMissed(final String methodImplementationId);

	/**
	 * Notifies when the internal cache for a {@link LambdaExpression} located at the given <code>methodImplementationId</id> was <strong>hit</strong>.
	 * @param methodImplementationId the fully qualified name and descriptor of the method implementing the user Lambda Expression
	 * @see SerializedLambdaInfo#getImplMethodId()
	 */
	public void cacheHit(final String methodImplementationId);

}
