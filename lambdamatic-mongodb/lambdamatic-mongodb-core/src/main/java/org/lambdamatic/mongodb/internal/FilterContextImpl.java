/**
 * 
 */
package org.lambdamatic.mongodb.internal;

import org.bson.BsonDocument;
import org.lambdamatic.mongodb.FilterExpression;
import org.lambdamatic.mongodb.ProjectionExpression;
import org.lambdamatic.mongodb.UpdateExpression;
import org.lambdamatic.mongodb.annotations.Document;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.UpdateMetadata;
import org.lambdamatic.mongodb.query.context.FilterContext;
import org.lambdamatic.mongodb.query.context.ProjectionContext;

import com.mongodb.client.MongoCollection;

/**
 * {@link CollectionContext} implementation.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 * @param <DomainType>
 *            the Domain Type annotated with {@link Document}
 * @param <ProjectionType>
 *            the {@link ProjectionMetadata} associated with Domain Type
 * @param <UpdateType>
 *            the {@link UpdateMetadata} associated with Domain Type
 *
 */
class FilterContextImpl<DomainType, QueryType, ProjectionType, UpdateType> extends ProjectionContextImpl<DomainType>
		implements FilterContext<DomainType, ProjectionType, UpdateType> {

	/**
	 * Constructor
	 * 
	 * @param mongoCollection
	 *            the {@link MongoCollection} to query or update
	 */
	FilterContextImpl(final MongoCollection<DomainType> mongoCollection) {
		super(mongoCollection);
	}

	/**
	 * Constructor
	 * 
	 * @param mongoCollection
	 *            the {@link MongoCollection} to query or update
	 * @param filterExpression
	 *            the {@link FilterExpression} to determine which elements to find, update or remove.
	 */
	FilterContextImpl(final MongoCollection<DomainType> mongoCollection,
			final FilterExpression<DomainType> filterExpression) {
		super(mongoCollection, filterExpression);
	}

	/**
	 * Constructor
	 * 
	 * @param mongoCollection
	 *            the {@link MongoCollection} to query or update
	 * @param filterDocument
	 *            the {@link BsonDocument} to determine which elements to find, update or remove.
	 */
	FilterContextImpl(final MongoCollection<DomainType> mongoCollection, final BsonDocument filterDocument) {
		super(mongoCollection, filterDocument);
	}

	@Override
	public ProjectionContext<DomainType> projection(final ProjectionExpression<ProjectionType> projectionExpression) {
		final BsonDocument projectionDocument = BsonUtils.asBsonDocument(projectionExpression);
		return new ProjectionContextImpl<DomainType>(getFindIterable().projection(projectionDocument));
	}

	@Override
	public void forEach(final UpdateExpression<UpdateType> updateExpression) {
		final BsonDocument updateDocument = BsonUtils.asBsonDocument(updateExpression);
		getMongoCollection().updateMany(getFilterDocument(), updateDocument);
	}

	@Override
	public void remove() {
		getMongoCollection().deleteMany(getFilterDocument());
	}

}
