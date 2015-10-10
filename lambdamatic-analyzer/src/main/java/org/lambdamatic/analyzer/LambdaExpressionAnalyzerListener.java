/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer;

import org.lambdamatic.analyzer.ast.SerializedLambdaInfo;
import org.lambdamatic.analyzer.ast.node.LambdaExpression;

/**
 * A listener for events sent by the {@link LambdaExpressionAnalyzer} when analyzing the bytecode.
 * 
 * @author Xavier Coulon
 *
 */
public interface LambdaExpressionAnalyzerListener {

  /**
   * Notifies when the internal cache for a {@link LambdaExpression} located at the given
   * <code>methodImplementationId</code> was <strong>missed</strong>.
   * 
   * @param methodImplementationId the fully qualified name and descriptor of the method
   *        implementing the user Lambda Expression
   * @see SerializedLambdaInfo#getImplMethodId()
   */
  public void cacheMissed(final String methodImplementationId);

  /**
   * Notifies when the internal cache for a {@link LambdaExpression} located at the given
   * <code>methodImplementationId</code> was <strong>hit</strong>.
   * 
   * @param methodImplementationId the fully qualified name and descriptor of the method
   *        implementing the user Lambda Expression
   * @see SerializedLambdaInfo#getImplMethodId()
   */
  public void cacheHit(final String methodImplementationId);

}
