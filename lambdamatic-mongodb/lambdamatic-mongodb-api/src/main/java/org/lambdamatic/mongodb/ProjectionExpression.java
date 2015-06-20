/**
 * 
 */
package org.lambdamatic.mongodb;

import org.lambdamatic.SerializableConsumer;
import org.lambdamatic.mongodb.metadata.ProjectionField;

/**
 * A {@link ProjectionExpression} which specifies which {@link ProjectionField} should be included or excluded from the
 * document(s) returned by the query.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
@FunctionalInterface
public interface ProjectionExpression<DomainType> extends SerializableConsumer<DomainType> {

}
