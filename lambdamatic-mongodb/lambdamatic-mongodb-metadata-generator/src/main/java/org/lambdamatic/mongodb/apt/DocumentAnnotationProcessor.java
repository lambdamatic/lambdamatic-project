/**
 * 
 */
package org.lambdamatic.mongodb.apt;

import java.io.IOException;
import java.util.HashMap;
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
	protected void doProcess(final TypeElement domainElement, final Map<String, Object> templateContextProperties)
			throws IOException {
		generateQueryMetadataSourceCode(domainElement, templateContextProperties);
		generateProjectionMetadataSourceCode(domainElement, templateContextProperties);
		generateUpdateMetadataSourceCode(domainElement, templateContextProperties);
		generateMongoCollectionSourceCode(domainElement, templateContextProperties);
		generateMongoCollectionProducerSourceCode(domainElement, templateContextProperties);
	}

	// @Override
	// protected Map<String, Object> initializeTemplateContextProperties(final TypeElement domainElement) {
	// final Map<String, Object> templateContextProperties = super.initializeTemplateContextProperties(domainElement);
	// final Map<Class<?>, List<? extends FieldMetadata>> allFields = getMetadataFields(domainElement);
	// templateContextProperties.put(Constants.QUERY_FIELDS, allFields.get(QueryMetadata.class));
	// templateContextProperties.put(Constants.QUERY_METADATA_CLASS_NAME,
	// generateQueryMetadataSimpleClassName(domainElement));
	// templateContextProperties.put(Constants.QUERY_ARRAY_METADATA_CLASS_NAME,
	// generateQueryArrayMetadataSimpleClassName(domainElement));
	// templateContextProperties.put(Constants.PROJECTION_FIELDS, allFields.get(ProjectionMetadata.class));
	// templateContextProperties.put(Constants.PROJECTION_METADATA_CLASS_NAME,
	// generateProjectionMetadataSimpleClassName(domainElement));
	// templateContextProperties.put(Constants.UPDATE_FIELDS, allFields.get(UpdateMetadata.class));
	// templateContextProperties.put(Constants.UPDATE_METADATA_CLASS_NAME,
	// generateUpdateMetadataSimpleClassName(domainElement));
	// templateContextProperties.put(Constants.UPDATE_ARRAY_METADATA_CLASS_NAME,
	// generateUpdateArrayMetadataSimpleClassName(domainElement));
	// final Document documentAnnotation = domainElement.getAnnotation(Document.class);
	// templateContextProperties.put(Constants.MONGO_COLLECTION_NAME, documentAnnotation.collection());
	// templateContextProperties.put(Constants.MONGO_COLLECTION_CLASS_NAME,
	// generateMongoCollectionSimpleClassName(domainElement));
	// templateContextProperties.put(Constants.MONGO_COLLECTION_PRODUCER_CLASS_NAME,
	// generateMongoCollectionProducerSimpleClassName(domainElement));
	// return templateContextProperties;
	// }
	//
	/**
	 * Generates the {@code LambdamaticMongoCollection} implementation source code for the underlying MongoDB
	 * collection.
	 * 
	 * @param domainElement
	 *            the type element from which the name will be generated
	 * @param templateContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * @param template
	 *            the {@link Mustache} template to use
	 * 
	 * @throws IOException
	 */
	private void generateMongoCollectionSourceCode(final TypeElement domainElement,
			final Map<String, Object> baseTemplateContext) throws IOException {
		final Map<String, Object> templateContext = new HashMap<>(baseTemplateContext);
		final Document documentAnnotation = domainElement.getAnnotation(Document.class);
		templateContext.put(Constants.GENERATED_SIMPLE_CLASS_NAME,
				generateMongoCollectionSimpleClassName(domainElement));
		templateContext.put(Constants.MONGO_COLLECTION_NAME, documentAnnotation.collection());
		templateContext.put(Constants.QUERY_METADATA_CLASS_NAME, createQueryMetadataSimpleClassName(domainElement));
		templateContext.put(Constants.PROJECTION_METADATA_CLASS_NAME, createProjectionMetadataSimpleClassName(domainElement));
		templateContext.put(Constants.UPDATE_METADATA_CLASS_NAME, createUpdateMetadataSimpleClassName(domainElement));
		final Mustache template = getTemplate(Constants.MONGO_COLLECTION_TEMPLATE);
		generateSourceCode(template, templateContext);
	}

	/**
	 * Generates the CDI Producer for the {@code LambdamaticMongoCollection} implementation.
	 * 
	 * @param domainElement
	 *            the type element from which the name will be generated
	 * @param templateContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * @param template
	 *            the {@link Mustache} template to use
	 * 
	 * @throws IOException
	 */
	private void generateMongoCollectionProducerSourceCode(final TypeElement domainElement,
			final Map<String, Object> baseTemplateContext) throws IOException {
		final Map<String, Object> templateContext = new HashMap<>(baseTemplateContext);
		final Document documentAnnotation = domainElement.getAnnotation(Document.class);
		templateContext.put(Constants.MONGO_COLLECTION_NAME, documentAnnotation.collection());
		templateContext.put(Constants.MONGO_COLLECTION_CLASS_NAME, generateMongoCollectionSimpleClassName(domainElement));
		templateContext.put(Constants.GENERATED_SIMPLE_CLASS_NAME,
				generateMongoCollectionProducerSimpleClassName(domainElement));
		final Mustache template = getTemplate(Constants.MONGO_COLLECTION_PRODUCER_TEMPLATE);
		generateSourceCode(template, templateContext);
	}

	/**
	 * Builds the simple name of the LambdamaticMongoCollection class associated with the given {@code typeElement}
	 * 
	 * @param typeElement
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, followed by
	 *         {@link Constants#MONGO_COLLECTION_CLASSNAME_SUFFIX}.
	 */
	private static String generateMongoCollectionSimpleClassName(final TypeElement typeElement) {
		return typeElement.getSimpleName().toString() + Constants.MONGO_COLLECTION_CLASSNAME_SUFFIX;
	}

	/**
	 * Builds the simple name of the LambdamaticMongoCollection producer class associated with the given
	 * {@code typeElement}
	 * 
	 * @param typeElement
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, followed by
	 *         {@link Constants#MONGO_COLLECTION_PRODUCER_CLASSNAME_SUFFIX}.
	 */
	private static String generateMongoCollectionProducerSimpleClassName(final TypeElement typeElement) {
		return typeElement.getSimpleName().toString() + Constants.MONGO_COLLECTION_PRODUCER_CLASSNAME_SUFFIX;
	}

}
