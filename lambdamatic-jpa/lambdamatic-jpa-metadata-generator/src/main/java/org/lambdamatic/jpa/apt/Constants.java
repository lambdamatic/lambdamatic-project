/**
 * 
 */
package org.lambdamatic.jpa.apt;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public interface Constants {

	/** constant to identify the package name in the template properties. */
	public static String PACKAGE_NAME = "packageName";
	
	/** constant to identify the extra import statements in the template properties. */
	public static String IMPORT_STATEMENTS = "imports";
	
	/** constant to identify the list of {@link QueryField} in the template properties. */
	public static String QUERY_FIELDS = "queryFields";
	
	/**
	 * constant to identify the fully qualified name of the {@link QueryMetadata} implementation class in the template
	 * properties.
	 */
	public static String QUERY_METADATA_CLASS_NAME = "queryMetadataClassName";
	
	/** Prefix to use for the generated {@link QueryMetadata} classes. */
	public static String QUERY_METADATA_CLASSNAME_PREFIX = "Q";
	
	/** Name of the template file for {@link QueryMetadata} classes. */
	public static String QUERY_METADATA_TEMPLATE = "query_metadata_template.mustache";
	
	/** Suffix to use for the generated {@link QueryArray} classes. */
	public static String QUERY_ARRAY_METADATA_CLASSNAME_SUFFIX = "Array";
	
	/** Name of the template file for arrays of {@link QueryMetadata} classes. */
	public static String QUERY_ARRAY_METADATA_TEMPLATE = "query_array_metadata_template.mustache";
	
	/**
	 * constant to identify the fully qualified name of the {@link QueryArray} implementation class in the template
	 * properties.
	 */
	public static String QUERY_ARRAY_METADATA_CLASS_NAME = "queryArrayMetadataClassName";
	
	/** constant to identify the list of {@link ProjectionField} in the template properties. */
	public static String PROJECTION_FIELDS = "projectionFields";
	
	/**
	 * constant to identify the fully qualified name of the {@link ProjectionMetadata} implementation class in the
	 * template properties.
	 */
	public static String PROJECTION_METADATA_CLASS_NAME = "projectionMetadataClassName";
	
	/** Suffix to use for the generated {@link ProjectionMetadata} classes. */
	public static String PROJECTION_METADATA_CLASSNAME_PREFIX = "P";
	
	/** Name of the template file for {@link ProjectionMetadata} classes. */
	public static String PROJECTION_METADATA_TEMPLATE = "projection_metadata_template.mustache";
	
	/** Name of the template file for {@link ProjectionMetadata} classes. */
	public static String EMBEDDED_PROJECTION_METADATA_TEMPLATE = "embedded_projection_metadata_template.mustache";
	
	/** constant to identify the list of {@link UpdateField} in the template properties. */
	public static String UPDATE_FIELDS = "updateFields";
	
	/**
	 * constant to identify the fully qualified name of the {@link UpdateMetadata} implementation class in the
	 * template properties.
	 */
	public static String UPDATE_METADATA_CLASS_NAME = "updateMetadataClassName";
	
	/** Suffix to use for the generated {@link UpdateMetadata} classes. */
	public static String UPDATE_METADATA_CLASSNAME_PREFIX = "U";
	
	/** Name of the template file for {@link UpdateMetadata} classes. */
	public static String UPDATE_METADATA_TEMPLATE = "update_metadata_template.mustache";
	
	/**
	 * constant to identify the fully qualified name of the {@link UpdateArray} implementation class in the template
	 * properties.
	 */
	public static String UPDATE_ARRAY_METADATA_CLASS_NAME = "updateArrayMetadataClassName";
	
	/** Name of the template file for arrays of {@link UpdateMetadata} classes. */
	public static String UPDATE_ARRAY_METADATA_TEMPLATE = "update_array_metadata_template.mustache";
	
	/** Suffix to use for the generated {@link QueryArray} classes. */
	public static String UPDATE_ARRAY_METADATA_CLASSNAME_SUFFIX = "Array";
	
	/** constant to identify the fully qualified name of the collection name in the template properties. */
	public static String MONGO_COLLECTION_NAME = "mongoCollectionName";
	
	/** constant to identify the fully qualified name of the collection class in the template properties. */
	public static String MONGO_COLLECTION_CLASS_NAME = "mongoCollectionClassName";
	
	/** Suffix to use for the generated LambdamaticMongoCollection implementation classes. */
	public static String MONGO_COLLECTION_CLASSNAME_SUFFIX = "Collection";
	
	/** Name of the template file for the LambdamaticMongoCollection implementation classes. */
	public static String MONGO_COLLECTION_TEMPLATE = "mongo_collection_template.mustache";
	
	/** constant to identify the fully qualified name of the collection producer class in the template properties. */
	public static String MONGO_COLLECTION_PRODUCER_CLASS_NAME = "mongoCollectionProducerClassName";
	
	/** Suffix to use for the generated LambdamaticMongoCollection implementation producer classes. */
	public static String MONGO_COLLECTION_PRODUCER_CLASSNAME_SUFFIX = "CollectionProducer";
	
	/** Name of the template file for the LambdamaticMongoCollection implementation producer classes. */
	public static String MONGO_COLLECTION_PRODUCER_TEMPLATE = "mongo_collection_producer_template.mustache";

}
