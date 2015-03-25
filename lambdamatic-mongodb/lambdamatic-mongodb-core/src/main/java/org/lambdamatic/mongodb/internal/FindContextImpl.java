/**
 * 
 */
package org.lambdamatic.mongodb.internal;

import java.util.ArrayList;
import java.util.List;

import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
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
	
	/** The registry of the custom {@link Codec}.*/
	private final CodecRegistry codecRegistry;
	
	/**
	 * Constructor
	 * @param find The {@link FindFluent} element built with a Filter argument.
	 * @param codecRegistry The registry of the custom {@link Codec}.
	 */
	public FindContextImpl(final FindIterable<T> find, final CodecRegistry codecRegistry) {
		this.find = find;
		this.codecRegistry = codecRegistry;
	}

	@Override
	public FindTerminalContext<T> projection(final SerializableFunction<PM, Projection> projectionExpression) {
		final BsonDocument projectionDocument = BsonDocumentWrapper.asBsonDocument(projectionExpression, codecRegistry);
		find.projection(projectionDocument);
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
