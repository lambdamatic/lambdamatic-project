/**
 * 
 */
package org.lambdamatic.mongodb.apt;

import org.lambdamatic.mongodb.metadata.ProjectionField;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.QueryArray;
import org.lambdamatic.mongodb.metadata.QueryField;
import org.lambdamatic.mongodb.metadata.QueryMetadata;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public interface Constants {

	/** constant to identify the list of {@link QueryField} in the template properties. */
	public static String QUERY_FIELDS = "queryFields";
	
	/** constant to identify the list of {@link ProjectionField} in the template properties. */
	public static String PROJECTION_FIELDS = "projectionFields";
	
	/**
	 * constant to identify the fully qualified name of the {@link QueryMetadata} implementation class in the template
	 * properties.
	 */
	public static String QUERY_METADATA_CLASS_NAME = "queryMetadataClassName";
	
	/**
	 * constant to identify the fully qualified name of the {@link QueryArray} implementation class in the template
	 * properties.
	 */
	public static String QUERY_ARRAY_METADATA_CLASS_NAME = "queryArrayMetadataClassName";
	
	/**
	 * constant to identify the fully qualified name of the {@link ProjectionMetadata} implementation class in the
	 * template properties.
	 */
	public static String PROJECTION_METADATA_CLASS_NAME = "projectionMetadataClassName";
	
	/** constant to identify the package name in the template properties. */
	public static String PACKAGE_NAME = "packageName";
	
	/** constant to identify the extra import statements in the template properties. */
	public static String IMPORT_STATEMENTS = "imports";
	
	/** Name of the template file for {@link ProjectionMetadata} classes. */
	public static String EMBEDDED_PROJECTION_METADATA_TEMPLATE = "embedded_projection_metadata_template.mustache";
	
	/** Name of the template file for {@link QueryMetadata} classes. */
	public static String QUERY_METADATA_TEMPLATE = "query_metadata_template.mustache";
	
	/** Name of the template file for {@link QueryMetadata} classes. */
	public static String QUERY_ARRAY_METADATA_TEMPLATE = "query_array_metadata_template.mustache";
	
	/** constant to identify the fully qualified name of the collection producer class in the template properties. */
	public static String MONGO_COLLECTION_PRODUCER_CLASS_NAME = "mongoCollectionProducerClassName";
	
	/** constant to identify the fully qualified name of the collection class in the template properties. */
	public static String MONGO_COLLECTION_CLASS_NAME = "mongoCollectionClassName";
	
	/** constant to identify the fully qualified name of the collection name in the template properties. */
	public static String MONGO_COLLECTION_NAME = "mongoCollectionName";
	
	/** Name of the template file for {@link ProjectionMetadata} classes. */
	public static String PROJECTION_METADATA_TEMPLATE = "projection_metadata_template.mustache";
	
	/** Name of the template file for the LambdamaticMongoCollection implementation classes. */
	public static String MONGO_COLLECTION_TEMPLATE = "mongo_collection_template.mustache";
	
	/** Suffix to use for the generated LambdamaticMongoCollection implementation classes. */
	public static String MONGO_COLLECTION_CLASSNAME_SUFFIX = "Collection";
	
	/** Name of the template file for the LambdamaticMongoCollection implementation producer classes. */
	public static String MONGO_COLLECTION_PRODUCER_TEMPLATE = "mongo_collection_producer_template.mustache";
	
	/** Suffix to use for the generated LambdamaticMongoCollection implementation producer classes. */
	public static String MONGO_COLLECTION_PRODUCER_CLASSNAME_SUFFIX = "CollectionProducer";

}
