package org.lambdamatic.analyzer.utils;

/**
 * A generic-purpose tuple to retain a pair of objects.
 * 
 *
 * @param <X>
 * @param <Y>
 * 
 * @see http://stackoverflow.com/questions/2670982/using-pairs-or-2-tuples-in-java
 */
public class Tuple<X, Y> {
	public final X x;
	public final Y y;

	public Tuple(final X x, final Y y) {
		this.x = x;
		this.y = y;
	}
}
