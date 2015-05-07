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

import org.lambdamatic.mongodb.annotations.BaseDocument;
import org.lambdamatic.mongodb.annotations.Document;

import com.github.mustachejava.Mustache;

/**
 * Processor for classes annotated with {@code Document} or {@link BaseDocument}. Generates their associated metadata
 * Java classes in the target folder given in the constructor.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({ 
		"org.lambdamatic.mongodb.annotations.Document" })
public class DocumentAnnotationProcessor extends BaseAnnotationProcessor {

	/**
	 * Constructor
	 * 
	 * @throws IOException
	 *             if templates could not be loaded.
	 */
	public DocumentAnnotationProcessor() throws IOException {
		super();
	}

	@Override
	protected void doProcess(final Map<String, Object> templateContextProperties) throws IOException {
		generateQueryMetadataSourceCode(getTemplate(Constants.QUERY_METADATA_TEMPLATE), templateContextProperties);
		generateQueryArrayMetadataSourceCode(getTemplate(Constants.QUERY_ARRAY_METADATA_TEMPLATE), templateContextProperties);
		generateProjectionMetadataSourceCode(getTemplate(Constants.PROJECTION_METADATA_TEMPLATE), templateContextProperties);
		generateMongoCollectionSourceCode(getTemplate(Constants.MONGO_COLLECTION_TEMPLATE), templateContextProperties);
		generateMongoCollectionProducerSourceCode(getTemplate(Constants.MONGO_COLLECTION_PRODUCER_TEMPLATE), templateContextProperties);
	}

	@Override
	protected Map<String, Object> initializeTemplateContextProperties(final TypeElement domainElement) {
		final Map<String, Object> templateContextProperties = super.initializeTemplateContextProperties(domainElement);
		templateContextProperties.put(Constants.QUERY_FIELDS, getQueryFields(domainElement));
		templateContextProperties.put(Constants.PROJECTION_FIELDS, getProjectionFields(domainElement));
		templateContextProperties.put(Constants.QUERY_METADATA_CLASS_NAME, generateQueryMetadataSimpleClassName(domainElement));
		templateContextProperties.put(Constants.QUERY_ARRAY_METADATA_CLASS_NAME, generateQueryArrayMetadataSimpleClassName(domainElement));
		templateContextProperties.put(Constants.PROJECTION_METADATA_CLASS_NAME, generateProjectionSimpleClassName(domainElement));
		final Document documentAnnotation = domainElement.getAnnotation(Document.class);
		templateContextProperties.put(Constants.MONGO_COLLECTION_NAME, documentAnnotation.collection());
		templateContextProperties.put(Constants.MONGO_COLLECTION_CLASS_NAME, generateMongoCollectionSimpleClassName(domainElement));
		templateContextProperties.put(Constants.MONGO_COLLECTION_PRODUCER_CLASS_NAME,
				generateMongoCollectionProviderSimpleClassName(domainElement));
		return templateContextProperties;
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
	protected void generateMongoCollectionSourceCode(final Mustache template, final Map<String, Object> templateContextProperties) throws IOException {
		final String targetClassName = templateContextProperties.get("packageName") + "."
				+ templateContextProperties.get(Constants.MONGO_COLLECTION_CLASS_NAME);
		generateSourceCode(targetClassName, template, templateContextProperties);
	}

	/**
	 * Generates the CDI Producer for the {@code LambdamaticMongoCollection} implementation.
	 * 
	 * @param templateContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	protected void generateMongoCollectionProducerSourceCode(final Mustache template, final Map<String, Object> templateContextProperties)
			throws IOException {
		final String targetClassName = templateContextProperties.get("packageName") + "."
				+ templateContextProperties.get(Constants.MONGO_COLLECTION_PRODUCER_CLASS_NAME);
		generateSourceCode(targetClassName, template, templateContextProperties);
	}

	/**
	 * Builds the simple name of the LambdamaticMongoCollection class associated with the given
	 * {@code typeElement}
	 * 
	 * @param typeElement
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, followed by
	 *         {@link Constants#MONGO_COLLECTION_CLASSNAME_SUFFIX}.
	 */
	public static String generateMongoCollectionSimpleClassName(final TypeElement typeElement) {
		return typeElement.getSimpleName().toString() + Constants.MONGO_COLLECTION_CLASSNAME_SUFFIX;
	}

	/**
	 * Builds the simple name of the LambdamaticMongoCollection provider class associated with the given
	 * {@code typeElement}
	 * 
	 * @param typeElement
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, followed by
	 *         {@link Constants#MONGO_COLLECTION_PRODUCER_CLASSNAME_SUFFIX}.
	 */
	public static String generateMongoCollectionProviderSimpleClassName(final TypeElement typeElement) {
		return typeElement.getSimpleName().toString() + Constants.MONGO_COLLECTION_PRODUCER_CLASSNAME_SUFFIX;
	}

}
