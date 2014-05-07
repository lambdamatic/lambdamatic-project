package org.bytesparadise.lambdamatic.query;

import java.util.List;

/**
 * Created by xcoulon on 12/16/13.
 */
public class Query {

	private final Class<?> clazz;

	public Query(Class<?> clazz) {

		this.clazz = clazz;
	}

	// FIXME: class parameter type should be exposed in this method
	public <T> List<T> filter(FilterExpression<T> expression) {
		
		return null;
	}
	
	
}
