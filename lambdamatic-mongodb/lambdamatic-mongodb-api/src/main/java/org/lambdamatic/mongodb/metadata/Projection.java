/**
 * 
 */
package org.lambdamatic.mongodb.metadata;

import java.util.Arrays;
import java.util.List;


/**
 * A {@link Projection} which specifies which {@link ProjectionField} should be included or excluded
 * from the document(s) returned by the query.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class Projection {
	
	public enum ProjectionType {
		INCLUDE, EXCLUDE;
	}
	
	/**
	 * Specifies which {@link ProjectionField} should be <strong>included</strong>
	 * @param fields the list of {@link ProjectionField} to include
	 * @return a {@link Projection} instance
	 */
	public static Projection include(final ProjectionField... fields) {
		return new Projection(ProjectionType.INCLUDE, fields);
	}

	/**
	 * Specifies which {@link ProjectionField} should be <strong>excluded</strong>
	 * @param fields the list of {@link ProjectionField} to include
	 * @return a {@link Projection} instance
	 */
	public static Projection exclude(final ProjectionField... fields) {
		return new Projection(ProjectionType.EXCLUDE, fields);
	}

	/** the type of projection. */
	private final ProjectionType type;
	
	/** the fields to include or to exclude. */
	private final List<ProjectionField> fields;
	
	/**
	 * Private constructor: use {@link Projection#include(ProjectionField...)} or {@link Projection#exclude(ProjectionField...)}
	 * @param type the type of projection
	 * @param fields the list of {@link ProjectionField} to include or to exclude
	 * 
	 */
	private Projection(final ProjectionType type, final ProjectionField... fields) {
		this.type = type;
		this.fields = Arrays.asList(fields);
	}
	
	/**
	 * @return the projection type.
	 */
	public ProjectionType getType() {
		return type;
	}
	
	/**
	 * @return the fields to include or to exclude.
	 */
	public List<ProjectionField> getFields() {
		return fields;
	}

	@Override
	public String toString() {
		return type.toString().toLowerCase() + " " + fields.toString();
	}
}
