/**
 * 
 */
package org.lambdamatic.mongodb.internal;

import java.util.ArrayList;
import java.util.List;

import org.lambdamatic.SerializableFunction;
import org.lambdamatic.mongodb.metadata.Projection;
import org.lambdamatic.mongodb.query.context.FindContext;
import org.lambdamatic.mongodb.query.context.FindTerminalContext;

import com.mongodb.client.FindIterable;

/**
 * {@link FindContext} implementation.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class FindContextImpl<T, PM> implements FindContext<T, PM> {
	
	/** the underlying Mongo {@link FindFluent}.*/ 
	private final FindIterable<T> find;
	
	/**
	 * Constructor
	 * @param find the {@link FindFluent} element built with a Filter argument.
	 */
	public FindContextImpl(final FindIterable<T> find) {
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
