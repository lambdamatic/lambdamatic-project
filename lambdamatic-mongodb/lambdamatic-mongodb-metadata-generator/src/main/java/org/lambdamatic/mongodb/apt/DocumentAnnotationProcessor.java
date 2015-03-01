/**
 * 
 */
package org.lambdamatic.mongodb.apt;

import java.io.IOException;
import java.util.Map;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import org.lambdamatic.mongodb.LambdamaticMongoCollection;
import org.lambdamatic.mongodb.annotations.BaseDocument;
import org.lambdamatic.mongodb.annotations.Document;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.stringtemplate.v4.ST;

/**
 * Processor for classes annotated with {@code Document} or {@link BaseDocument}. Generates their associated metadata
 * Java classes in the target folder given in the constructor.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({ "org.lambdamatic.mongodb.annotations.BaseDocument",
		"org.lambdamatic.mongodb.annotations.Document" })
public class DocumentAnnotationProcessor extends EmbeddedDocumentAnnotationProcessor {

	/** constant to identify the fully qualified name of the collection producer class in the template properties. */
	protected static final String MONGO_COLLECTION_PRODUCER_CLASS_NAME = "mongoCollectionProducerClassName";

	/** constant to identify the fully qualified name of the collection class in the template properties. */
	protected static final String MONGO_COLLECTION_CLASS_NAME = "mongoCollectionClassName";

	/** constant to identify the fully qualified name of the collection name in the template properties. */
	private static final String MONGO_COLLECTION_NAME = "mongoCollectionName";

	/** Name of the template file for {@link ProjectionMetadata} classes. */
	private static final String PROJECTION_METADATA_TEMPLATE = "projection_metadata_template.st";

	/** Name of the template file for the {@link LambdamaticMongoCollection} implementation classes. */
	private static final String MONGO_COLLECTION_TEMPLATE = "mongo_collection_template.st";

	/** Suffix to use for the generated {@link LambdamaticMongoCollection} implementation classes. */
	private static String MONGO_COLLECTION_CLASSNAME_SUFFIX = "Collection";

	/** Name of the template file for the {@link LambdamaticMongoCollection} implementation producer classes. */
	private static final String MONGO_COLLECTION_PRODUCER_TEMPLATE = "mongo_collection_producer_template.st";

	/** Suffix to use for the generated {@link LambdamaticMongoCollection} implementation producer classes. */
	private static String MONGO_COLLECTION_PRODUCER_CLASSNAME_SUFFIX = "CollectionProducer";

	/** StringTemplate for the {@link LambdamaticMongoCollection} implementation classes. */
	private ST mongoCollectionTemplate;

	/** StringTemplate for {@link LambdamaticMongoCollection} implementation producer classes. */
	private ST mongoCollectionProducerTemplate;

	/** StringTemplate for the {@link ProjectionMetadata} classes. */
	private ST projectionMetadataTemplate;

	/**
	 * Constructor
	 * 
	 * @throws IOException
	 *             if templates could not be loaded.
	 */
	public DocumentAnnotationProcessor() throws IOException {
		super();
		this.projectionMetadataTemplate = getStringTemplate(PROJECTION_METADATA_TEMPLATE);
		this.mongoCollectionTemplate = getStringTemplate(MONGO_COLLECTION_TEMPLATE);
		this.mongoCollectionProducerTemplate = getStringTemplate(MONGO_COLLECTION_PRODUCER_TEMPLATE);
	}

	@Override
	protected void doProcess(final Map<String, Object> templateContextProperties) throws IOException {
		super.doProcess(templateContextProperties);
		generateMongoCollectionSourceCode(templateContextProperties);
		generateMongoCollectionProducerSourceCode(templateContextProperties);
	}

	@Override
	protected Map<String, Object> initializeTemplateContextProperties(final TypeElement domainElement) {
		final Map<String, Object> properties = super.initializeTemplateContextProperties(domainElement);
		final Document documentAnnotation = domainElement.getAnnotation(Document.class);
		properties.put(MONGO_COLLECTION_NAME, documentAnnotation.collection());
		properties.put(MONGO_COLLECTION_CLASS_NAME, generateMongoCollectionSimpleClassName(domainElement));
		properties.put(MONGO_COLLECTION_PRODUCER_CLASS_NAME,
				generateMongoCollectionProviderSimpleClassName(domainElement));
		return properties;
	}

	/**
	 * Generates the {@code ProjectionMetadata} implementation source code for the annotated class currently being
	 * processed.
	 * 
	 * @param allContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	@Override
	protected void generateProjectionMetadataSourceCode(final Map<String, Object> templateContextProperties) throws IOException {
		final String targetClassName = templateContextProperties.get(PACKAGE_NAME) + "."
				+ templateContextProperties.get(PROJECTION_METADATA_CLASS_NAME);
		generateSourceCode(targetClassName, projectionMetadataTemplate, templateContextProperties);
	}
	
	/**
	 * Generates the {@code LambdamaticMongoCollection} implementation source code for the underlying MongoDB
	 * collection.
	 * 
	 * @param templateContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	protected void generateMongoCollectionSourceCode(final Map<String, Object> templateContextProperties) throws IOException {
		final String targetClassName = templateContextProperties.get("packageName") + "."
				+ templateContextProperties.get(MONGO_COLLECTION_CLASS_NAME);
		generateSourceCode(targetClassName, mongoCollectionTemplate, templateContextProperties);
	}

	/**
	 * Generates the CDI Producer for the {@code LambdamaticMongoCollection} implementation.
	 * 
	 * @param templateContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	protected void generateMongoCollectionProducerSourceCode(final Map<String, Object> templateContextProperties)
			throws IOException {
		final String targetClassName = templateContextProperties.get("packageName") + "."
				+ templateContextProperties.get(MONGO_COLLECTION_PRODUCER_CLASS_NAME);
		generateSourceCode(targetClassName, mongoCollectionProducerTemplate, templateContextProperties);
	}

	/**
	 * Builds the simple name of the {@link LambdamaticMongoCollection} class associated with the given
	 * {@code typeElement}
	 * 
	 * @param typeElement
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, followed by
	 *         {@link DocumentAnnotationProcessor#MONGO_COLLECTION_CLASSNAME_SUFFIX}.
	 */
	public static String generateMongoCollectionSimpleClassName(final TypeElement typeElement) {
		return typeElement.getSimpleName().toString() + MONGO_COLLECTION_CLASSNAME_SUFFIX;
	}

	/**
	 * Builds the simple name of the {@link LambdamaticMongoCollection} provider class associated with the given
	 * {@code typeElement}
	 * 
	 * @param typeElement
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, followed by
	 *         {@link DocumentAnnotationProcessor#MONGO_COLLECTION_PRODUCER_CLASSNAME_SUFFIX}.
	 */
	public static String generateMongoCollectionProviderSimpleClassName(final TypeElement typeElement) {
		return typeElement.getSimpleName().toString() + MONGO_COLLECTION_PRODUCER_CLASSNAME_SUFFIX;
	}

}
