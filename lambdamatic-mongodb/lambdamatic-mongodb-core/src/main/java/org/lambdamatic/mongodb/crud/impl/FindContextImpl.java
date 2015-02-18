/**
 * 
 */
package org.lambdamatic.mongodb.crud.impl;

import java.util.ArrayList;
import java.util.List;

import org.lambdamatic.SerializableFunction;
import org.lambdamatic.mongodb.FindContext;
import org.lambdamatic.mongodb.FindTerminalContext;
import org.lambdamatic.mongodb.metadata.Projection;

import com.mongodb.client.FindFluent;

/**
 * {@link FindContext} implementation.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class FindContextImpl<T, PM> implements FindContext<T, PM> {
	
	/** the underlying Mongo {@link FindFluent}.*/ 
	private final FindFluent<T> find;
	
	/**
	 * Constructor
	 * @param find the {@link FindFluent} element built with a Filter argument.
	 */
	public FindContextImpl(final FindFluent<T> find) {
		this.find = find;
	}

	@Override
	public FindTerminalContext<T> projection(final SerializableFunction<PM, Projection> projectionExpression) {
		find.projection(projectionExpression);
		return this;
	}
	
	@Override
	public List<T> toList() {
		return find.into(new ArrayList<>());
	}

	@Override
	public T first() {
		return find.first();
	}

}
