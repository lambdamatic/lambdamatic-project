/**
 * 
 */
package org.lambdamatic.mongodb.apt;

import org.lambdamatic.mongodb.metadata.ProjectionField;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.QueryArray;
import org.lambdamatic.mongodb.metadata.QueryField;
import org.lambdamatic.mongodb.metadata.QueryMap;
import org.lambdamatic.mongodb.metadata.QueryMetadata;
import org.lambdamatic.mongodb.metadata.UpdateMetadata;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public interface Constants {

	/** constant to identify the processor class name. */
	public static String PROCESSOR_CLASS_NAME = "processorClassName";

	/** constant to identify the package name. */
	public static String PACKAGE_NAME = "packageName";
	
	/** constant to identify the domain class name. */
	public static String DOMAINE_CLASS_NAME = "domainClassName";
	
	/** constant to identify the simple class name of the class to generate. */
	public static String GENERATED_SIMPLE_CLASS_NAME = "generatedSimpleClassName";
	
	/** constant to identify the extra import statements. */
	public static String IMPORT_STATEMENTS = "imports";
	
	/** constant to identify the fields. An alias for the same result kept in the template context under a different key. */
	public static String TEMPLATE_FIELDS_ALIAS = "templateFieldsAlias";
	
	/** constant to identify the list of {@link QueryField} in the template properties. */
	public static String QUERY_FIELDS = "queryFields";
	
	/**
	 * constant to identify the fully qualified name of the {@link QueryMetadata} implementation class in the template
	 * context.
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
	
	/** Name of the template file for arrays of {@link QueryMetadata} classes. */
	public static String QUERY_MAP_METADATA_TEMPLATE = "query_map_metadata_template.mustache";
	
	/** Suffix to use for the generated {@link QueryMap} classes. */
	public static String QUERY_MAP_METADATA_CLASSNAME_SUFFIX = "Map";
	
	/**
	 * constant to identify the fully qualified names of the {@link QueryMap} type parameters.
	 */
	public static String QUERY_MAP_TYPE_PARAMETER_CLASS_NAMES = "queryMapTypeParameterClassNames";
	
	/** constant to identify the list of {@link ProjectionField}. */
	public static String PROJECTION_FIELDS = "projectionFields";
	
	/**
	 * constant to identify the fully qualified name of the {@link ProjectionMetadata} implementation class in the
	 * template context.
	 */
	public static String PROJECTION_METADATA_CLASS_NAME = "projectionMetadataClassName";
	
	/** Suffix to use for the generated {@link ProjectionMetadata} classes. */
	public static String PROJECTION_METADATA_CLASSNAME_PREFIX = "P";
	
	/** Suffix to use for the generated {@link ProjectionArray} classes. */
	public static String PROJECTION_ARRAY_METADATA_CLASSNAME_SUFFIX = "Array";
	
	/** Suffix to use for the generated {@link ProjectionMap} classes. */
	public static String PROJECTION_MAP_METADATA_CLASSNAME_SUFFIX = "Map";

	/** Name of the template file for {@link ProjectionMetadata} classes. */
	public static String PROJECTION_METADATA_TEMPLATE = "projection_metadata_template.mustache";
	
	/** Name of the template file for {@link ProjectionMetadata} classes. */
	public static String PROJECTION_ARRAY_METADATA_TEMPLATE = "projection_array_metadata_template.mustache";
	
	/** Name of the template file for {@link ProjectionMetadata} classes. */
	public static String PROJECTION_MAP_METADATA_TEMPLATE = "projection_map_metadata_template.mustache";
	
	/** constant to identify the list of {@link UpdateField}. */
	public static String UPDATE_FIELDS = "updateFields";
	
	/**
	 * constant to identify the fully qualified name of the {@link UpdateMetadata} implementation class in the
	 * template context.
	 */
	public static String UPDATE_METADATA_CLASS_NAME = "updateMetadataClassName";
	
	/** Suffix to use for the generated {@link UpdateMetadata} classes. */
	public static String UPDATE_METADATA_CLASSNAME_PREFIX = "U";
	
	/** Suffix to use for the generated {@link QueryArray} classes. */
	public static String UPDATE_ARRAY_METADATA_CLASSNAME_SUFFIX = "Array";
	
	/** Suffix to use for the generated {@link QueryArray} classes. */
	public static String UPDATE_MAP_METADATA_CLASSNAME_SUFFIX = "Map";
	
	/** Name of the template file for {@link UpdateMetadata} classes. */
	public static String UPDATE_METADATA_TEMPLATE = "update_metadata_template.mustache";
	
	/** Name of the template file for arrays of {@link UpdateMetadata} classes. */
	public static String UPDATE_ARRAY_METADATA_TEMPLATE = "update_array_metadata_template.mustache";
	
	/** Name of the template file for arrays of {@link UpdateMetadata} classes. */
	public static String UPDATE_MAP_METADATA_TEMPLATE = "update_map_metadata_template.mustache";
	
	/** constant to identify the fully qualified name of the collection name. */
	public static String MONGO_COLLECTION_NAME = "mongoCollectionName";
	
	/** constant to identify the fully qualified name of the collection class in the template properties. */
	public static String MONGO_COLLECTION_CLASS_NAME = "mongoCollectionClassName";
	
	/** Suffix to use for the generated LambdamaticMongoCollection implementation classes. */
	public static String MONGO_COLLECTION_CLASSNAME_SUFFIX = "Collection";
	
	/** Name of the template file for the LambdamaticMongoCollection implementation classes. */
	public static String MONGO_COLLECTION_TEMPLATE = "mongo_collection_template.mustache";
	
	/** Suffix to use for the generated LambdamaticMongoCollection implementation producer classes. */
	public static String MONGO_COLLECTION_PRODUCER_CLASSNAME_SUFFIX = "CollectionProducer";
	
	/** Name of the template file for the LambdamaticMongoCollection implementation producer classes. */
	public static String MONGO_COLLECTION_PRODUCER_TEMPLATE = "mongo_collection_producer_template.mustache";

}
