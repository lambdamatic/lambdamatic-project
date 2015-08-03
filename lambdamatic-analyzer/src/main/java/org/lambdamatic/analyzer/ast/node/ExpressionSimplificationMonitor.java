/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lambdamatic.analyzer.utils.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class ExpressionSimplificationMonitor {

	private static final class TupleComparator implements Comparator<Tuple<Expression, Integer>> {
		@Override
		public int compare(Tuple<Expression, Integer> o1, Tuple<Expression, Integer> o2) {
			return o1.y - o2.y;
		}
	}

	/** The Logger. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ExpressionSimplificationMonitor.class);

	/**
	 * keeping the hash value of the previous forms of this expression while trying to simplify it, to avoid endless loops.
	 */
	private final Map<Integer, Set<Integer>> allHashes = new HashMap<>();

	/** Indentation counter to display log messages. */
	private int indentationCount = 0;
	
	/** keeping the simplest form for a given expression while trying to simplify it. */
	private final Map<Integer, Set<Tuple<Expression, Integer>>> allVariantForms = new HashMap<>();

	/** Whether this monitor is stopped (ie, best simplification was found).*/
	private boolean stopped = false;

	/** The original {@link CompoundExpression} that is being simplified.*/
	private final Expression expressionToSimplify;

	public ExpressionSimplificationMonitor(final Expression expressionToSimplify) {
		this.expressionToSimplify = expressionToSimplify;
	}

	public boolean isExpressionFormKnown(final Expression expression) {
		return allHashes.containsKey(expression.getId()) && allHashes.get(expression.getId()).contains(expression.hashCode());
	}

	public void registerExpression(final Expression expression) {
		registerVariantExpression(expression, expression);
	}

	public void registerVariantExpression(final Expression expression, final Expression variantForm) {
		if(!allHashes.containsKey(expression.getId())) {
			allHashes.put(expression.getId(), new HashSet<>());
		}
		allHashes.get(expression.getId()).add(variantForm.hashCode());
		if(!allVariantForms.containsKey(expression.getId())) {
			allVariantForms.put(expression.getId(), new HashSet<>());
		}
		allVariantForms.get(expression.getId()).add(new Tuple<>(variantForm, variantForm.getComplexity()));
	}
	
	/**
	 * Returns a list of all known variant forms of the given {@link Expression}, ordered by increasing complexity
	 * @param expression the expression whose variant forms are requested
	 * @return the ordered list of variant forms.
	 */
	@Deprecated
	public List<Expression> getVariantExpressions(final Expression expression) {
		final List<Expression> orderedVariantForms = new ArrayList<>();
		if(!allHashes.containsKey(expression.getId())) {
			return Collections.emptyList();
		}
		final List<Tuple<Expression, Integer>> variantForms = new ArrayList<Tuple<Expression,Integer>>(allVariantForms.get(expression.getId()));
		Collections.sort(variantForms, new TupleComparator());
		for(Tuple<Expression, Integer> variantForm : variantForms) {
			orderedVariantForms.add(variantForm.x);
		}
		return Collections.unmodifiableList(orderedVariantForms);
	}
	
	@Deprecated
	public Expression getSimplestForm(final Expression expression) {
		if(!allVariantForms.containsKey(expression.getId())) {
			return expression;
		}
		final List<Tuple<Expression, Integer>> expressionVariants = new ArrayList<Tuple<Expression,Integer>>(allVariantForms.get(expression.getId()));
		Collections.sort(expressionVariants, new TupleComparator());
		return expressionVariants.get(0).x;
	}

	public int getHashcode(final Expression expression) {
		return expression.hashCode();
	}
	
	public int getIndentationCount() {
		return indentationCount;
	}
	
	public int incrementIndentationCount() {
		return ++indentationCount;
	}
	
	public int decrementIndentationCount() {
		return --indentationCount;
	}
	
	public String getIndentation() {
		final StringBuilder indentationBuilder = new StringBuilder();
		for(int i = 0; i < indentationCount; i++) {
			indentationBuilder.append(' ');
		}
		return indentationBuilder.toString();
	}

	public void stop() {
		this.stopped  = true;
	}
	public boolean isStopped() {
		return stopped;
	}

	public Expression getExpressionToSimplify() {
		return expressionToSimplify;
	}

}

