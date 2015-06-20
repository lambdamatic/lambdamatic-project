/*******************************************************************************
 * Copyright (c) 2015 Red Hat.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.lambdamatic.analyzer.exception.AnalyzeException;

/**
 * @author xcoulon
 *
 */
public class MathUtils {

	/**
	 * Private constructor for this utility class
	 */
	private MathUtils() {
	}

	/**
	 * Adds the <code>right</code> value to the <code>left</code> value.
	 * @param left the left value in the addition
	 * @param right the right value in the addition
	 * @return the addition result as a {@link BigDecimal} or a {@link BigInteger} 
	 */
	public static Number add(final Number left, final Number right) {
		if (isDecimal(left) || isDecimal(right)) {
			return new BigDecimal(left.toString()).add(new BigDecimal(right.toString()));
		} else {
			return new BigInteger(left.toString()).add(new BigInteger(right.toString()));
		}
	}
	
	/**
	 * Subtracts the <code>right</code> value to the <code>left</code> value.
	 * @param left the left value in the addition
	 * @param right the right value in the addition
	 * @return the subtraction result as a {@link BigDecimal} or a {@link BigInteger} 
	 */
	public static Number subtract(final Number left, final Number right) {
		if (isDecimal(left) || isDecimal(right)) {
			return new BigDecimal(left.toString()).subtract(new BigDecimal(right.toString()));
		} else {
			return new BigInteger(left.toString()).subtract(new BigInteger(right.toString()));
		}
	}
	
	/**
	 * Multiplies the <code>left</code> value with the <code>right</code> value.
	 * @param left the left value in the addition
	 * @param right the right value in the addition
	 * @return the addition result as a {@link BigDecimal} or a {@link BigInteger} 
	 */
	public static Number multiply(final Number left, final Number right) {
		if (isDecimal(left) || isDecimal(right)) {
			return new BigDecimal(left.toString()).multiply(new BigDecimal(right.toString()));
		} else {
			return new BigInteger(left.toString()).multiply(new BigInteger(right.toString()));
		}
	}
	
	/**
	 * Divides the <code>left</code> value with the <code>right</code> value.
	 * @param left the left value in the addition
	 * @param right the right value in the addition
	 * @return the addition result as a {@link BigDecimal} or a {@link BigInteger} 
	 */
	public static Number divide(final Number left, final Number right) {
		if (isDecimal(left) || isDecimal(right)) {
			return new BigDecimal(left.toString()).divide(new BigDecimal(right.toString()));
		} else {
			return new BigInteger(left.toString()).divide(new BigInteger(right.toString()));
		}
	}
	
	/**
	 * @param number the number convert
	 * @return the opposite value of the given number, in the same class wrapper.
	 */
	public static Number opposite(final Number number) {
		if(number instanceof Short) {
			return new Short((short) (number.shortValue() * -1));
		} else if(number instanceof Integer) {
			return new Integer(number.intValue() * -1);
		} else if(number instanceof Long) {
			return new Long(number.longValue() * -1);
		} else if(number instanceof Float) {
			return new Float(number.floatValue() * -1);
		} else if(number instanceof Double) {
			return new Double(number.doubleValue() * -1);
		} else {
			throw new AnalyzeException("Unexpected type of number to negate: " + number.getClass().getName());
		}
		
	}
	
	private static boolean isDecimal(final Number n) {
		return n instanceof BigDecimal || n instanceof Double || n instanceof Float;
	}

}
