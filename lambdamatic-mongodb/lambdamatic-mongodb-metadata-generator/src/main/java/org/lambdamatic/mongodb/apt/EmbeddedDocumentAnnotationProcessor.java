/**
 * 
 */
package org.lambdamatic.mongodb.apt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

import org.apache.commons.lang3.ClassUtils;
import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.annotations.DocumentId;
import org.lambdamatic.mongodb.annotations.TransientField;
import org.lambdamatic.mongodb.metadata.ProjectionField;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.QueryField;
import org.lambdamatic.mongodb.metadata.QueryMetadata;
import org.stringtemplate.v4.ST;

/**
 * Processor for classes annotated with {@code EmbeddedDocument}. Generates their associated {@link QueryMetadata} and
 * {@link ProjectionMetadata} implementation classes in the target folder given in the constructor.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({ "org.lambdamatic.mongodb.annotations.EmbeddedDocument" })
public class EmbeddedDocumentAnnotationProcessor extends BaseAnnotationProcessor {

	/** constant to identify the list of {@link QueryField} in the template properties. */
	protected static final String QUERY_FIELDS = "queryFields";

	/** constant to identify the list of {@link ProjectionField} in the template properties. */
	protected static final String PROJECTION_FIELDS = "projectionFields";

	/**
	 * constant to identify the fully qualified name of the {@link QueryMetadata} implementation class in the template
	 * properties.
	 */
	protected static final String QUERY_METADATA_CLASS_NAME = "queryMetadataClassName";

	/**
	 * constant to identify the fully qualified name of the {@link ProjectionMetadata} implementation class in the
	 * template properties.
	 */
	protected static final String PROJECTION_METADATA_CLASS_NAME = "projectionMetadataClassName";

	/** constant to identify the package name in the template properties. */
	protected static final String PACKAGE_NAME = "packageName";

	/** constant to identify the extra import statements in the template properties. */
	protected static final String IMPORT_STATEMENTS = "imports";

	/** Name of the template file for {@link ProjectionMetadata} classes. */
	private static final String EMBEDDED_PROJECTION_METADATA_TEMPLATE = "embedded_projection_metadata_template.st";

	/** Name of the template file for {@link QueryMetadata} classes. */
	private static final String QUERY_METADATA_TEMPLATE = "query_metadata_template.st";

	/** StringTemplate for the {@link QueryMetadata} classes. */
	private ST queryMetadataTemplate;

	/** StringTemplate for the {@link ProjectionMetadata} embedded classes. */
	private ST embeddedProjectionMetadataTemplate;

	/**
	 * Constructor
	 * 
	 * @throws IOException
	 *             if templates could not be loaded.
	 */
	public EmbeddedDocumentAnnotationProcessor() throws IOException {
		super();
		this.queryMetadataTemplate = getStringTemplate(QUERY_METADATA_TEMPLATE);
		this.embeddedProjectionMetadataTemplate = getStringTemplate(EMBEDDED_PROJECTION_METADATA_TEMPLATE);
	}

	/**
	 * @return the {@link ST} template to generate the {@link QueryMetadata} class
	 */
	protected ST getQueryMetadataTemplate() {
		return queryMetadataTemplate;
	}

	/**
	 * @return the {@link ST} template to generate the {@link ProjectionMetadata} class
	 */
	protected ST getProjectionMetadataTemplate() {
		return embeddedProjectionMetadataTemplate;
	}

	@Override
	protected Map<String, Object> initializeTemplateContextProperties(final TypeElement domainElement) {
		final Map<String, Object> templateContextProperties = super.initializeTemplateContextProperties(domainElement);
		templateContextProperties.put(QUERY_FIELDS, getQueryFields(domainElement));
		templateContextProperties.put(PROJECTION_FIELDS, getProjectionFields(domainElement));
		templateContextProperties.put(QUERY_METADATA_CLASS_NAME, generateQueryMetadataSimpleClassName(domainElement));
		templateContextProperties.put(PROJECTION_METADATA_CLASS_NAME, generateProjectionSimpleClassName(domainElement));
		return templateContextProperties;
	}

	/**
	 * Generates the {@code QueryMetadata} and {@link ProjectionMetadata} implementation source code for the annotated
	 * class currently being processed.
	 * 
	 * @param allContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	@Override
	protected void doProcess(final Map<String, Object> templateContextProperties) throws IOException {
		generateQueryMetadataSourceCode(templateContextProperties);
		generateProjectionMetadataSourceCode(templateContextProperties);
	}

	/**
	 * Generates the {@code QueryMetadata} implementation source code for the annotated class currently being processed.
	 * 
	 * @param allContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	private void generateQueryMetadataSourceCode(final Map<String, Object> templateContextProperties)
			throws IOException {
		final String targetPackageName = (String) templateContextProperties.get(PACKAGE_NAME);
		@SuppressWarnings("unchecked")
		final List<QueryFieldMetadata> queryFields = (List<QueryFieldMetadata>) templateContextProperties
				.get(QUERY_FIELDS);
		templateContextProperties.put(IMPORT_STATEMENTS, findRequiredImportStatements(targetPackageName, queryFields));
		final String targetClassName = targetPackageName + "."
				+ templateContextProperties.get(QUERY_METADATA_CLASS_NAME);
		generateSourceCode(targetClassName, getQueryMetadataTemplate(), templateContextProperties);
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
	private void generateProjectionMetadataSourceCode(final Map<String, Object> templateContextProperties)
			throws IOException {
		final String targetClassName = templateContextProperties.get(PACKAGE_NAME) + "."
				+ templateContextProperties.get(PROJECTION_METADATA_CLASS_NAME);
		final String targetPackageName = (String) templateContextProperties.get(PACKAGE_NAME);
		@SuppressWarnings("unchecked")
		final List<ProjectionFieldMetadata> queryFields = (List<ProjectionFieldMetadata>) templateContextProperties
				.get(PROJECTION_FIELDS);
		final List<String> findRequiredImportStatements = findRequiredImportStatements(targetPackageName, queryFields);
		templateContextProperties.put(IMPORT_STATEMENTS, findRequiredImportStatements);
		generateSourceCode(targetClassName, getProjectionMetadataTemplate(), templateContextProperties);
	}

	/**
	 * Finds all required import statements that need to be included in the target template
	 * 
	 * @param targetPackageName
	 *            the target package for the class to be generated
	 * @param queryFields
	 *            the query fields whose classes may need to ne imported
	 * @return the {@link List} of class imports (excluding those in the same package)
	 */
	private List<String> findRequiredImportStatements(final String targetPackageName,
			final List<? extends BaseFieldMetadata> queryFields) {
		return queryFields.stream().map(f -> f.getRequiredJavaTypes()).flatMap(set -> set.stream()).distinct()
				.filter(t -> {
					final String packageCanonicalName = ClassUtils.getPackageCanonicalName(t);
					return !packageCanonicalName.equals(targetPackageName) && !packageCanonicalName.equals("java.lang");
				} ).collect(Collectors.toList());
	}

	/**
	 * Returns a {@link Map} of the type of the fields of the given classElement, indexed by their name.
	 * 
	 * @param classElement
	 *            the element to scan
	 * @return the map of fields
	 */
	protected List<QueryFieldMetadata> getQueryFields(final TypeElement classElement) {
		final List<QueryFieldMetadata> fields = new ArrayList<>();
		for (Element childElement : classElement.getEnclosedElements()) {
			if (childElement.getKind() == ElementKind.FIELD) {
				final TransientField transientFieldAnnotation = childElement.getAnnotation(TransientField.class);
				// skip field if it is annotated with @TransientField
				if (transientFieldAnnotation != null) {
					continue;
				}
				final VariableElement variableElement = (VariableElement) childElement;
				final DocumentId documentIdAnnotation = childElement.getAnnotation(DocumentId.class);
				try {
					if (documentIdAnnotation != null) {
						fields.add(new QueryFieldMetadata(variableElement, documentIdAnnotation));
					} else {
						final DocumentField documentFieldAnnotation = childElement.getAnnotation(DocumentField.class);
						fields.add(new QueryFieldMetadata(variableElement, documentFieldAnnotation));
					}
				} catch (MetadataGenerationException e) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
				}

			}
		}
		return fields;
	}

	/**
	 * Returns a {@link Map} of the type of the fields of the given classElement, indexed by their name.
	 * 
	 * @param classElement
	 *            the element to scan
	 * @return the map of fields
	 */
	protected List<ProjectionFieldMetadata> getProjectionFields(final TypeElement classElement) {
		final List<ProjectionFieldMetadata> fields = new ArrayList<>();
		for (Element childElement : classElement.getEnclosedElements()) {
			if (childElement.getKind() == ElementKind.FIELD) {
				final TransientField transientFieldAnnotation = childElement.getAnnotation(TransientField.class);
				// skip field if it is annotated with @TransientField
				if (transientFieldAnnotation != null) {
					continue;
				}
				final VariableElement variableElement = (VariableElement) childElement;
				final DocumentId documentIdAnnotation = childElement.getAnnotation(DocumentId.class);
				try {
					if (documentIdAnnotation != null) {
						fields.add(new ProjectionFieldMetadata(variableElement, documentIdAnnotation));
					} else {
						final DocumentField documentFieldAnnotation = childElement.getAnnotation(DocumentField.class);
						fields.add(new ProjectionFieldMetadata(variableElement, documentFieldAnnotation));
					}
				} catch (MetadataGenerationException e) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
				}
			}
		}
		return fields;
	}

}
