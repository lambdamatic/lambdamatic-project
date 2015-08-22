/**
 * 
 */
package org.lambdamatic.mongodb.apt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.lambdamatic.mongodb.annotations.EmbeddedDocument;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.QueryArray;
import org.lambdamatic.mongodb.metadata.QueryMap;
import org.lambdamatic.mongodb.metadata.QueryMetadata;
import org.lambdamatic.mongodb.metadata.UpdateMetadata;

import com.github.mustachejava.Mustache;

/**
 * Processor for classes annotated with {@code EmbeddedDocument}. Generates their associated {@link QueryMetadata} and
 * {@link ProjectionMetadata} implementation classes in the target folder given in the constructor.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({ "org.lambdamatic.mongodb.annotations.EmbeddedDocument" })
//FIXME: the EmbeddedDocumentAnnotationProcessor should actually prevent having @DocumentId, instead of accepting it 
public class EmbeddedDocumentAnnotationProcessor extends BaseAnnotationProcessor {

	/**
	 * Constructor
	 * 
	 * @throws IOException
	 *             if templates could not be loaded.
	 */
	public EmbeddedDocumentAnnotationProcessor() throws IOException {
		super();
	}

//	@Override
//	protected Map<String, Object> initializeTemplateContextProperties(final TypeElement domainElement) {
//		final Map<String, Object> templateContextProperties = super.initializeTemplateContextProperties(domainElement);
//		final Map<Class<?>, List<? extends FieldMetadata>> allFields = getMetadataFields(domainElement);
//		templateContextProperties.put(Constants.QUERY_FIELDS, allFields.get(QueryMetadata.class));
//		templateContextProperties.put(Constants.QUERY_METADATA_CLASS_NAME, generateQueryMetadataSimpleClassName(domainElement));
//		templateContextProperties.put(Constants.PROJECTION_FIELDS, allFields.get(ProjectionMetadata.class));
//		templateContextProperties.put(Constants.PROJECTION_METADATA_CLASS_NAME, generateProjectionMetadataSimpleClassName(domainElement));
//		templateContextProperties.put(Constants.UPDATE_FIELDS, allFields.get(UpdateMetadata.class));
//		templateContextProperties.put(Constants.UPDATE_METADATA_CLASS_NAME, generateUpdateMetadataSimpleClassName(domainElement));
//		return templateContextProperties;
//	}

	/**
	 * Generates the {@code QueryMetadata}, {@link ProjectionMetadata} and {@link UpdateMetadata} implementation sources for the annotated
	 * class currently being processed.
	 * @param allContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	@Override
	protected void doProcess(final TypeElement domainElement, final Map<String, Object> templateContextProperties) throws IOException {
		generateQueryMetadataSourceCode(domainElement, templateContextProperties);
		//generateQueryArrayMetadataSourceCode(domainElement, templateContextProperties);
		//generateQueryMapMetadataSourceCode(domainElement, templateContextProperties);
		generateProjectionMetadataSourceCode(domainElement, templateContextProperties);
		//generateProjectionArrayMetadataSourceCode(domainElement, templateContextProperties);
		//generateProjectionMapMetadataSourceCode(domainElement, templateContextProperties);
		generateUpdateMetadataSourceCode(domainElement, templateContextProperties);
		//generateUpdateArrayMetadataSourceCode(domainElement, templateContextProperties);
		//generateUpdateMapMetadataSourceCode(domainElement, templateContextProperties);
	}

	
	/**
	 * Builds the simple name of the {@link QueryArray} class associated with the given {@code element} if it is annotated with {@link EmbeddedDocument} only.
	 * 
	 * @param element
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, prefixed by
	 *         {@link Constants#QUERY_METADATA_CLASSNAME_PREFIX} and suffixed with {@link Constants#QUERY_MAP_METADATA_CLASSNAME_SUFFIX} , or <code>null</code> if the given element is not annotated with {@link EmbeddedDocument}.
	 */
	private static String createQueryArrayMetadataSimpleClassName(final Element element) {
		if(element.getAnnotation(EmbeddedDocument.class) != null) {
			return Constants.QUERY_METADATA_CLASSNAME_PREFIX + element.getSimpleName().toString() + Constants.QUERY_ARRAY_METADATA_CLASSNAME_SUFFIX;
		}
		return null;
	}

	/**
	 * Generates the {@code QueryArray} implementation source code for the annotated class currently being processed.
	 * @param allContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	private void generateQueryArrayMetadataSourceCode(final TypeElement domainElement, final Map<String, Object> baseTemplateContext)
			throws IOException {
		final Map<String, Object> templateContext = new HashMap<>(baseTemplateContext);
		templateContext.put(Constants.GENERATED_SIMPLE_CLASS_NAME,
				createQueryArrayMetadataSimpleClassName(domainElement));
		templateContext.put(Constants.QUERY_METADATA_CLASS_NAME, createQueryMetadataSimpleClassName(domainElement));
		final Mustache template = getTemplate(Constants.QUERY_ARRAY_METADATA_TEMPLATE);
		generateSourceCode(template, templateContext);
	}

	/**
	 * Builds the simple name of the {@link QueryMap} class associated with the given {@code element} if it is annotated with {@link EmbeddedDocument} only.
	 * 
	 * @param element
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, prefixed by
	 *         {@link Constants#QUERY_METADATA_CLASSNAME_PREFIX} and suffixed with {@link Constants#QUERY_MAP_METADATA_CLASSNAME_SUFFIX}, or <code>null</code> if the given element is not annotated with {@link EmbeddedDocument}.
	 */
	private static String createQueryMapMetadataSimpleClassName(final Element element) {
		if(element.getAnnotation(EmbeddedDocument.class) != null) {
			return Constants.QUERY_METADATA_CLASSNAME_PREFIX + element.getSimpleName().toString() + Constants.QUERY_MAP_METADATA_CLASSNAME_SUFFIX;
		}
		return null;
	}
	
	/**
	 * Generates the {@code QueryMap} implementation source code for the annotated class currently being processed.
	 * @param allContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	private void generateQueryMapMetadataSourceCode(final TypeElement domainElement, final Map<String, Object> baseTemplateContext)
			throws IOException {
		final Map<String, Object> templateContext = new HashMap<>(baseTemplateContext);
		templateContext.put(Constants.GENERATED_SIMPLE_CLASS_NAME,
				createQueryMapMetadataSimpleClassName(domainElement));
		templateContext.put(Constants.QUERY_MAP_TYPE_PARAMETER_CLASS_NAMES, getQueryMapTypeParameterClassNames(domainElement));
		final Mustache template = getTemplate(Constants.QUERY_MAP_METADATA_TEMPLATE);
		generateSourceCode(template, templateContext);
	}
	
	private List<String> getQueryMapTypeParameterClassNames(final TypeElement domainElement) {
		final List<String> queryMapTypeParameterClassNames = new ArrayList<>();
		return queryMapTypeParameterClassNames;
	}

	/**
	 * Builds the simple name of the {@link ProjectionArray} class associated with the given {@code element} if it is annotated with {@link EmbeddedDocument} only.
	 * 
	 * @param element
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, prefixed by
	 *         {@link Constants#PROJECTION_METADATA_CLASSNAME_PREFIX} and suffixed with {@link Constants#PROJECTION_ARRAY_METADATA_CLASSNAME_SUFFIX}, or <code>null</code> if the given element is not annotated with {@link EmbeddedDocument}.
	 */
	private static String createProjectionArrayMetadataSimpleClassName(final Element element) {
		if(element.getAnnotation(EmbeddedDocument.class) != null) {
			return Constants.PROJECTION_METADATA_CLASSNAME_PREFIX + element.getSimpleName().toString() + Constants.PROJECTION_ARRAY_METADATA_CLASSNAME_SUFFIX;
		}
		return null;
	}

	/**
	 * Generates the {@code ProjectionArray} implementation source code for the annotated class currently being processed.
	 * @param allContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	private void generateProjectionArrayMetadataSourceCode(final TypeElement domainElement, final Map<String, Object> baseTemplateContext)
			throws IOException {
		final Map<String, Object> templateContext = new HashMap<>(baseTemplateContext);
		templateContext.put(Constants.GENERATED_SIMPLE_CLASS_NAME,
				createProjectionArrayMetadataSimpleClassName(domainElement));
		final Mustache template = getTemplate(Constants.PROJECTION_ARRAY_METADATA_TEMPLATE);
		generateSourceCode(template, templateContext);
	}
	
	/**
	 * Builds the simple name of the {@link ProjectionMap} class associated with the given {@code element} if it is annotated with {@link EmbeddedDocument} only.
	 * 
	 * @param element
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, prefixed by
	 *         {@link Constants#PROJECTION_METADATA_CLASSNAME_PREFIX} and suffixed with {@link Constants#PROJECTION_MAP_METADATA_CLASSNAME_SUFFIX}, or <code>null</code> if the given element is not annotated with {@link EmbeddedDocument}.
	 */
	private static String createProjectionMapMetadataSimpleClassName(final Element element) {
		if(element.getAnnotation(EmbeddedDocument.class) != null) {
			return Constants.PROJECTION_METADATA_CLASSNAME_PREFIX + element.getSimpleName().toString() + Constants.PROJECTION_MAP_METADATA_CLASSNAME_SUFFIX;
		}
		return null;
	}
	
	/**
	 * Generates the {@code ProjectionMap} implementation source code for the annotated class currently being processed.
	 * @param allContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	private void generateProjectionMapMetadataSourceCode(final TypeElement domainElement, final Map<String, Object> baseTemplateContext)
			throws IOException {
		final Map<String, Object> templateContext = new HashMap<>(baseTemplateContext);
		templateContext.put(Constants.GENERATED_SIMPLE_CLASS_NAME,
				createProjectionMapMetadataSimpleClassName(domainElement));
		final Mustache template = getTemplate(Constants.PROJECTION_MAP_METADATA_TEMPLATE);
		generateSourceCode(template, templateContext);
	}
	
	/**
	 * Builds the simple name of the {@link UpdateArray} class associated with the given {@code element} if it is annotated with {@link EmbeddedDocument} only.
	 * 
	 * @param element
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, prefixed by
	 *         {@link Constants#QUERY_METADATA_CLASSNAME_PREFIX} and suffixed with {@link Constants#UPDATE_ARRAY_METADATA_CLASSNAME_SUFFIX}, or <code>null</code> if the given element is not annotated with {@link EmbeddedDocument}.
	 */
	private static String createUpdateArrayMetadataSimpleClassName(final Element element) {
		if(element.getAnnotation(EmbeddedDocument.class) != null) {
			return Constants.UPDATE_METADATA_CLASSNAME_PREFIX + element.getSimpleName().toString() + Constants.UPDATE_ARRAY_METADATA_CLASSNAME_SUFFIX;
		}
		return null;
	}

	/**
	 * Generates the {@code UpdateArray} implementation source code for the annotated class currently being processed.
	 * @param allContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	private void generateUpdateArrayMetadataSourceCode(final TypeElement domainElement, final Map<String, Object> baseTemplateContext)
			throws IOException {
		final Map<String, Object> templateContext = new HashMap<>(baseTemplateContext);
		templateContext.put(Constants.GENERATED_SIMPLE_CLASS_NAME,
				createUpdateArrayMetadataSimpleClassName(domainElement));
		final Mustache template = getTemplate(Constants.UPDATE_ARRAY_METADATA_TEMPLATE);
		generateSourceCode(template, templateContext);
	}
	
	/**
	 * Builds the simple name of the {@link UpdateMap} class associated with the given {@code element} if it is annotated with {@link EmbeddedDocument} only.
	 * 
	 * @param element
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, prefixed by
	 *         {@link Constants#QUERY_METADATA_CLASSNAME_PREFIX} and suffixed with {@link Constants#UPDATE_MAP_METADATA_CLASSNAME_SUFFIX}, or <code>null</code> if the given element is not annotated with {@link EmbeddedDocument}.
	 */
	private static String createUpdateMapMetadataSimpleClassName(final Element element) {
		if(element.getAnnotation(EmbeddedDocument.class) != null) {
			return Constants.UPDATE_METADATA_CLASSNAME_PREFIX + element.getSimpleName().toString() + Constants.UPDATE_MAP_METADATA_CLASSNAME_SUFFIX;
		}
		return null;
	}
	
	/**
	 * Generates the {@code UpdateMap} implementation source code for the annotated class currently being processed.
	 * @param allContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	private void generateUpdateMapMetadataSourceCode(final TypeElement domainElement, final Map<String, Object> baseTemplateContext)
			throws IOException {
		final Map<String, Object> templateContext = new HashMap<>(baseTemplateContext);
		templateContext.put(Constants.GENERATED_SIMPLE_CLASS_NAME,
				createUpdateMapMetadataSimpleClassName(domainElement));
		final Mustache template = getTemplate(Constants.UPDATE_MAP_METADATA_TEMPLATE);
		generateSourceCode(template, templateContext);
		
	}

}
