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
import org.lambdamatic.mongodb.FilterExpression;
import org.lambdamatic.mongodb.ProjectionExpression;
import org.lambdamatic.mongodb.UpdateExpression;
import org.lambdamatic.mongodb.annotations.Document;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.UpdateMetadata;
import org.lambdamatic.mongodb.query.context.FilterContext;
import org.lambdamatic.mongodb.query.context.FindTerminalContext;

import com.mongodb.client.MongoCollection;

/**
 * {@link FilterContext} implementation.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 * @param <DomainType> the Domain Type annotated with {@link Document}
 * @param <ProjectionType> the {@link ProjectionMetadata} associated with Domain Type
 * @param <UpdateType> the {@link UpdateMetadata} associated with Domain Type
 *
 */
public class FilterContextImpl<DomainType, QueryType, ProjectionType, UpdateType> implements FilterContext<DomainType, ProjectionType, UpdateType> {
	
	/** The mongo collection on which the operation will be performed. */
	private final MongoCollection<DomainType> mongoCollection;
	
	/** the initial filter expression.*/ 
	private final BsonDocument filterDocument;
	
	/** The registry of the custom {@link Codec}.*/
	private final CodecRegistry codecRegistry;

	/** the optional {@link BsonDocument} corresponding to a given {@link ProjectionExpression}. */
	private BsonDocument projectionDocument;
	
	/**
	 * Constructor
	 * @param filter The {@link FindFluent} element built with a Filter argument.
	 * @param codecRegistry The registry of the custom {@link Codec}.
	 */
	public FilterContextImpl(final MongoCollection<DomainType> mongoCollection, final FilterExpression<QueryType> filterExpression,
			final CodecRegistry codecRegistry) {
		this.mongoCollection = mongoCollection;
		this.filterDocument = BsonDocumentWrapper.asBsonDocument(filterExpression, codecRegistry);
		this.codecRegistry = codecRegistry;
	}

	@Override
	public FindTerminalContext<DomainType> projection(final ProjectionExpression<ProjectionType> projectionExpression) {
		this.projectionDocument = BsonDocumentWrapper.asBsonDocument(projectionExpression, codecRegistry);
		return this;
	}
	
	@Override
	public List<DomainType> toList() {
		return mongoCollection.find(filterDocument).projection(projectionDocument).into(new ArrayList<>());
	}

	@Override
	public DomainType first() {
		return mongoCollection.find(filterDocument).projection(projectionDocument).first();
	}
	
	@Override
	public void forEach(final UpdateExpression<UpdateType> updateExpression) {
		final BsonDocument updateDocument = BsonDocumentWrapper.asBsonDocument(updateExpression, codecRegistry);
		mongoCollection.updateMany(filterDocument, updateDocument);
	}

}
