/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of {@link LambdaExpressionAnalyzerListener}.
 *
 */
public class LambdaExpressionAnalyzerListenerImpl implements LambdaExpressionAnalyzerListener {

  /** Number of times when the cache was hit. */
  private AtomicInteger cacheHits = new AtomicInteger();

  /** Number of times when the cache was missed. */
  private AtomicInteger cacheMisses = new AtomicInteger();

  /**
   * Resets the hit counters.
   */
  public void resetHitCounters() {
    this.cacheHits.set(0);
    this.cacheMisses.set(0);
  }

  /**
   * @return the number of cache hits.
   */
  public int getCacheHits() {
    return this.cacheHits.get();
  }

  /**
   * @return the number of cache misses.
   */
  public int getCacheMisses() {
    return this.cacheMisses.get();
  }

  @Override
  public void cacheMissed(String methodImplementationId) {
    this.cacheMisses.incrementAndGet();
  }

  @Override
  public void cacheHit(String methodImplementationId) {
    this.cacheHits.incrementAndGet();
  }

}
