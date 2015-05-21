/**
 * 
 */
package org.lambdamatic.analyzer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class LambdaExpressionAnalyzerListenerImpl implements LambdaExpressionAnalyzerListener {

	/** Number of times when the cache was hit. */
	private AtomicInteger cacheHits = new AtomicInteger();

	/** Number of times when the cache was missed. */
	private AtomicInteger cacheMisses = new AtomicInteger();

	public void resetHitCounters() {
		this.cacheHits.set(0);
		this.cacheMisses.set(0);
	}

	public int getCacheHits() {
		return cacheHits.get();
	}

	public int getCacheMisses() {
		return cacheMisses.get();
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
