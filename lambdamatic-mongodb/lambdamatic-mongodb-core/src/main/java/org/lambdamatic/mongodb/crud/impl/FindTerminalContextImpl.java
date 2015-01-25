/**
 * 
 */
package org.lambdamatic.mongodb.crud.impl;

import java.util.ArrayList;
import java.util.List;

import org.lambdamatic.mongodb.FindTerminalContext;

import com.mongodb.client.FindFluent;

/**
 * Terminal context implementation for the find operations.
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class FindTerminalContextImpl<T> implements FindTerminalContext<T> {

	private final FindFluent<T> find;
	
	/**
	 * Constructor
	 * @param find
	 */
	public FindTerminalContextImpl(final FindFluent<T> find) {
		this.find = find;
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
