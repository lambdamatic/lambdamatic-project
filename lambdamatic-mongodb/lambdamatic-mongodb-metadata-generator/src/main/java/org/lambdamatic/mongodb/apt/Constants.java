/*******************************************************************************
 * Copyright (c) 2015 Red Hat.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat - Initial Contribution
 *******************************************************************************/
package org.lambdamatic.mongodb.apt;

import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.QueryMetadata;
import org.lambdamatic.mongodb.metadata.UpdateMetadata;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public interface Constants {

	/** constant to identify fully qualified name of the processor . */
	public static String PROCESSOR_CLASS_NAME = "processorClassName";

	/** constant to identify the fully qualified name of the domain class. */
	public static String DOMAIN_CLASS_NAME = "domainClassName";
	
	/** constant to identify the package name. */
	public static String PACKAGE_NAME = "packageName";
	
	/** constant to identify the simple name of the class to generate. */
	public static String SIMPLE_CLASS_NAME = "simpleClassName";
	
	/** constant to identify the extra import statements. */
	public static String IMPORT_STATEMENTS = "imports";
	
	/** constant to identify the template fields. */
	public static String TEMPLATE_FIELDS = "fields";
	
	/** constant to identify the template methodss. */
	public static String TEMPLATE_METHODS = "arrayElementAccessorMethods";
	
	/**
	 * constant to identify the fully qualified name of the {@link QueryMetadata} implementation class in the template
	 * context.
	 */
	public static String QUERY_METADATA_CLASS_NAME = "queryMetadataClassName";
	
	/** Prefix to use for the generated {@link QueryMetadata} classes. */
	@Deprecated
	public static String QUERY_METADATA_CLASSNAME_PREFIX = "Q";
	
	/** Name of the template file for {@link QueryMetadata} classes. */
	public static String QUERY_METADATA_TEMPLATE = "query_metadata_template.mustache";
	
	/**
	 * constant to identify the fully qualified name of the {@link ProjectionMetadata} implementation class in the
	 * template context.
	 */
	public static String PROJECTION_METADATA_CLASS_NAME = "projectionMetadataClassName";
	
	/** Suffix to use for the generated {@link ProjectionMetadata} classes. */
	public static String PROJECTION_METADATA_CLASSNAME_PREFIX = "P";
	
	/** Name of the template file for {@link ProjectionMetadata} classes. */
	public static String PROJECTION_METADATA_TEMPLATE = "projection_metadata_template.mustache";
	
	/**
	 * constant to identify the fully qualified name of the {@link UpdateMetadata} implementation class in the
	 * template context.
	 */
	public static String UPDATE_METADATA_CLASS_NAME = "updateMetadataClassName";
	
	/** Suffix to use for the generated {@link UpdateMetadata} classes. */
	public static String UPDATE_METADATA_CLASSNAME_PREFIX = "U";
	
	/** Name of the template file for {@link UpdateMetadata} classes. */
	public static String UPDATE_METADATA_TEMPLATE = "update_metadata_template.mustache";
	
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
