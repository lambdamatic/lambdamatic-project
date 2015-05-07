package org.lambdamatic.mongodb.apt;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.apache.commons.lang3.ClassUtils;
import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.annotations.DocumentId;
import org.lambdamatic.mongodb.annotations.EmbeddedDocument;
import org.lambdamatic.mongodb.annotations.TransientField;
import org.lambdamatic.mongodb.metadata.QueryArray;
import org.lambdamatic.mongodb.metadata.QueryMetadata;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public abstract class BaseAnnotationProcessor extends AbstractProcessor {

	private final MustacheFactory mf = new DefaultMustacheFactory();
    
	/**
	 * Returns an instance of the {@link Mustache} identified by the given templateName
	 * @param templateName the fully qualified name of the {@link Mustache} to return
	 * @return the {@link Mustache}
	 * 
	 */
	Mustache getTemplate(final String templateName) {
		return mf.compile("templates" + File.separator + templateName);
	}
	
	/**
	 * Builds the simple name of the {@link QueryMetadata} class associated with the given {@code element}
	 * 
	 * @param element
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, prefixed by
	 *         {@link DocumentAnnotationProcessor#QUERY_METADATA_CLASSNAME_PREFIX}.
	 */
	public static String generateQueryMetadataSimpleClassName(final Element element) {
		return QueryFieldMetadata.QUERY_METADATA_CLASSNAME_PREFIX + element.getSimpleName().toString();
	}

	/**
	 * Builds the simple name of the {@link QueryArray} class associated with the given {@code element} if it is annotated with {@link EmbeddedDocument} only.
	 * 
	 * @param element
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, prefixed by
	 *         {@link DocumentAnnotationProcessor#QUERY_METADATA_CLASSNAME_PREFIX}, or <code>null</code> if the given element is not annotated with {@link EmbeddedDocument}.
	 */
	public static String generateQueryArrayMetadataSimpleClassName(final Element element) {
		if(element.getAnnotation(EmbeddedDocument.class) != null) {
			return QueryFieldMetadata.QUERY_METADATA_CLASSNAME_PREFIX + element.getSimpleName().toString() + QueryFieldMetadata.QUERY_ARRAY_METADATA_CLASSNAME_SUFFIX;
		}
		return null;
	}
	
	/**
	 * Builds the simple name of the {@link QueryMetadata} class associated with the given {@code element}
	 * 
	 * @param element
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, prefixed by
	 *         {@link DocumentAnnotationProcessor#QUERY_METADATA_CLASSNAME_PREFIX}.
	 */
	public static String generateProjectionSimpleClassName(final Element element) {
		return ProjectionFieldMetadata.PROJECTION_METADATA_CLASSNAME_PREFIX + element.getSimpleName().toString();
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
		for (TypeElement supportedAnnotation : annotations) {
			for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(supportedAnnotation)) {
				if (annotatedElement instanceof TypeElement) {
					try {
						final TypeElement domainElement = (TypeElement) annotatedElement;
						processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
								"Processing annotated class: " + domainElement.getQualifiedName(), domainElement);
						final Map<String, Object> templateContextProperties = initializeTemplateContextProperties(domainElement);
						doProcess(templateContextProperties);
					}
					// catch Throwable to avoid crashing the compiler.
					catch (final Throwable e) {
						final Writer writer = new StringWriter();
						e.printStackTrace(new PrintWriter(writer));
						processingEnv.getMessager().printMessage(
								Diagnostic.Kind.ERROR,
								"Failed to process annotated element '" + annotatedElement.getSimpleName() + "': "
										+ writer.toString());
					}
				}
			}
		}
		return false;
	}

	abstract void doProcess(final Map<String, Object> templateContextProperties) throws IOException;

	/**
	 * Generates the {@code QueryMetadata} implementation source code for the annotated class currently being processed.
	 * 
	 * @param allContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	void generateQueryMetadataSourceCode(final Mustache template, final Map<String, Object> templateContextProperties)
			throws IOException {
		final String targetPackageName = (String) templateContextProperties.get(Constants.PACKAGE_NAME);
		@SuppressWarnings("unchecked")
		final List<QueryFieldMetadata> queryFields = (List<QueryFieldMetadata>) templateContextProperties
				.get(Constants.QUERY_FIELDS);
		templateContextProperties.put(Constants.IMPORT_STATEMENTS, findRequiredImportStatements(targetPackageName, queryFields));
		final String targetClassName = targetPackageName + "."
				+ templateContextProperties.get(Constants.QUERY_METADATA_CLASS_NAME);
		generateSourceCode(targetClassName, template, templateContextProperties);
	}

	/**
	 * Generates the {@code QueryArray} implementation source code for the annotated class currently being processed.
	 * 
	 * @param allContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	void generateQueryArrayMetadataSourceCode(final Mustache template, final Map<String, Object> templateContextProperties)
			throws IOException {
		// skip this step if QueryArray metadata class is not needed.
		if(templateContextProperties.get(Constants.QUERY_ARRAY_METADATA_CLASS_NAME) == null) {
			return;
		}
		final String targetPackageName = (String) templateContextProperties.get(Constants.PACKAGE_NAME);
		final String targetClassName = targetPackageName + "."
				+ templateContextProperties.get(Constants.QUERY_ARRAY_METADATA_CLASS_NAME);
		generateSourceCode(targetClassName, template, templateContextProperties);
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
	void generateProjectionMetadataSourceCode(final Mustache template, final Map<String, Object> templateContextProperties)
			throws IOException {
		final String targetClassName = templateContextProperties.get(Constants.PACKAGE_NAME) + "."
				+ templateContextProperties.get(Constants.PROJECTION_METADATA_CLASS_NAME);
		final String targetPackageName = (String) templateContextProperties.get(Constants.PACKAGE_NAME);
		@SuppressWarnings("unchecked")
		final List<ProjectionFieldMetadata> queryFields = (List<ProjectionFieldMetadata>) templateContextProperties
				.get(Constants.PROJECTION_FIELDS);
		templateContextProperties.put(Constants.IMPORT_STATEMENTS, findRequiredImportStatements(targetPackageName, queryFields));
		generateSourceCode(targetClassName, template, templateContextProperties);
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
	private List<ImportStatement> findRequiredImportStatements(final String targetPackageName,
			final List<? extends BaseFieldMetadata> queryFields) {
		return queryFields.stream().map(f -> f.getRequiredJavaTypes()).flatMap(set -> set.stream()).distinct()
				.filter(t -> {
					final String packageCanonicalName = ClassUtils.getPackageCanonicalName(t);
					return !packageCanonicalName.equals(targetPackageName) && !packageCanonicalName.equals("java.lang");
				} ).map(i -> new ImportStatement(i)).collect(Collectors.toList());
	}
	
	Map<String, Object> initializeTemplateContextProperties(final TypeElement domainElement) {
		final Map<String, Object> properties = new HashMap<String, Object>();
		final PackageElement packageElement = (PackageElement) domainElement.getEnclosingElement();
		properties.put("processorClassName", DocumentAnnotationProcessor.class.getName());
		properties.put("packageName", packageElement.getQualifiedName().toString());
		properties.put("domainClassName", domainElement.getSimpleName().toString());
		return properties;
	}

	/**
	 * Generates the source code for the given {@code targetClassName} using the given {@link ST} template.
	 * 
	 * @param targetClassName
	 *            the fully qualified name of the class to generate
	 * @param template
	 *            the {@link ST} template to use
	 * @param properties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	void generateSourceCode(final String targetClassName, final Mustache template,
			final Map<String, Object> properties) throws IOException {
		if (template == null) {
			processingEnv.getMessager().printMessage(
					Diagnostic.Kind.WARNING,
					"Could not create the QueryMetadata class for the given '"
							+ properties.get("packageName") + "."
							+ properties.get("domainClassName")
							+ "' class: no queryMetadataTemplate available.");
			return;
		}
		final JavaFileObject jfo = processingEnv.getFiler().createSourceFile(targetClassName);
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "creating source file: " + jfo.toUri());
		final Writer writer = jfo.openWriter();
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "applying template: " + template.getName());
		template.execute(writer, properties).flush();
		writer.close();
	}

	/**
	 * Returns a {@link Map} of the type of the fields of the given classElement, indexed by their name.
	 * 
	 * @param classElement
	 *            the element to scan
	 * @return the map of fields
	 */
	List<ProjectionFieldMetadata> getProjectionFields(final TypeElement classElement) {
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

	/**
	 * Returns a {@link Map} of the type of the fields of the given classElement, indexed by their name.
	 * 
	 * @param classElement
	 *            the element to scan
	 * @return the map of fields
	 */
	List<QueryFieldMetadata> getQueryFields(final TypeElement classElement) {
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

}