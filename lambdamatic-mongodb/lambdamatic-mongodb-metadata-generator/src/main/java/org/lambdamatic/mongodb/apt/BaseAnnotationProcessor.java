package org.lambdamatic.mongodb.apt;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
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
import org.lambdamatic.mongodb.annotations.TransientField;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.QueryMetadata;
import org.lambdamatic.mongodb.metadata.UpdateMetadata;

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
						doProcess(domainElement, templateContextProperties);
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

	protected abstract void doProcess(TypeElement domainElement, Map<String, Object> templateContextProperties) throws IOException;

	protected Map<String, Object> initializeTemplateContextProperties(final TypeElement domainElement) {
		final Map<String, Object> templateContextProperties = new HashMap<String, Object>();
		final PackageElement packageElement = (PackageElement) domainElement.getEnclosingElement();
		templateContextProperties.put(Constants.PROCESSOR_CLASS_NAME , DocumentAnnotationProcessor.class.getName());
		templateContextProperties.put(Constants.PACKAGE_NAME, packageElement.getQualifiedName().toString());
		templateContextProperties.put(Constants.DOMAINE_CLASS_NAME, domainElement.getSimpleName().toString());
		final Map<Class<?>, List<? extends FieldMetadata>> allFields = getMetadataFields(domainElement);
		templateContextProperties.put(Constants.QUERY_FIELDS, allFields.get(QueryMetadata.class));
		templateContextProperties.put(Constants.PROJECTION_FIELDS, allFields.get(ProjectionMetadata.class));
		templateContextProperties.put(Constants.UPDATE_FIELDS, allFields.get(UpdateMetadata.class));
		return templateContextProperties;
	}

	/**
	 * Returns a {@link Map} of all the type of the fields of the given {@code classElement}, indexed by their usage,
	 * i.e, {@link QueryMetadata}, {@link ProjectionMetadata} or {@link UpdateMetadata}.
	 * 
	 * @param classElement
	 *            the element to scan
	 * @return the map of fields
	 */
	protected Map<Class<?>, List<? extends FieldMetadata>> getMetadataFields(final TypeElement classElement) {
		final Map<Class<?>, List<? extends FieldMetadata>> allFields = new HashMap<>();
		final List<FieldMetadata> queryFields = new ArrayList<>();
		allFields.put(QueryMetadata.class, queryFields);
		final List<FieldMetadata> projectionFields = new ArrayList<>();
		allFields.put(ProjectionMetadata.class, projectionFields);
		final List<FieldMetadata> updateFields = new ArrayList<>();
		allFields.put(UpdateMetadata.class, updateFields);
		
		for (Element childElement : classElement.getEnclosedElements()) {
			if (childElement.getKind() == ElementKind.FIELD) {
				final TransientField transientFieldAnnotation = childElement.getAnnotation(TransientField.class);
				// skip field if it is annotated with @TransientField
				if (transientFieldAnnotation != null) {
					continue;
				}
				try {
					final VariableElement variableElement = (VariableElement) childElement;
					final DocumentId documentIdAnnotation = childElement.getAnnotation(DocumentId.class);
					if (documentIdAnnotation != null) {
						queryFields.add(FieldMetadata.createQueryFieldMetadata(variableElement, documentIdAnnotation));
						projectionFields.add(FieldMetadata.createProjectionFieldMetadata(variableElement, documentIdAnnotation));
						updateFields.add(FieldMetadata.createUpdateFieldMetadata(variableElement, documentIdAnnotation));
					} else {
						final DocumentField documentFieldAnnotation = childElement.getAnnotation(DocumentField.class);
						queryFields.add(FieldMetadata.createQueryFieldMetadata(variableElement, documentFieldAnnotation));
						projectionFields.add(FieldMetadata.createProjectionFieldMetadata(variableElement, documentFieldAnnotation));
						updateFields.add(FieldMetadata.createUpdateFieldMetadata(variableElement, documentFieldAnnotation));
					}
				} catch (MetadataGenerationException e) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
				}
	
			}
		}
		return allFields;
	}

	/**
	 * Generates the source code for the given {@code targetClassName} using the given {@link ST} template.
	 * 
	 * @param simpleTargetClassName
	 *            the fully qualified name of the class to generate
	 * @param template
	 *            the {@link ST} template to use
	 * @param templateContext
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	protected void generateSourceCode(final Mustache template,
			final Map<String, Object> templateContext) throws IOException {
		if (template == null) {
			processingEnv.getMessager().printMessage(
					Diagnostic.Kind.WARNING,
					"Could not create the QueryMetadata class for the given '"
							+ templateContext.get("packageName") + "."
							+ templateContext.get("domainClassName")
							+ "' class: no queryMetadataTemplate available.");
			return;
		}
		final String simpleTargetClassName = (String) templateContext.get(Constants.GENERATED_SIMPLE_CLASS_NAME);
		final String targetPackageName = (String) templateContext.get(Constants.PACKAGE_NAME);
		final String targetClassName = targetPackageName + "." + simpleTargetClassName;
		@SuppressWarnings("unchecked")
		final List<FieldMetadata> templateFields = ((List<FieldMetadata>) templateContext.get(Constants.TEMPLATE_FIELDS_ALIAS));
		templateContext.put(Constants.IMPORT_STATEMENTS, findRequiredImportStatements(targetPackageName, templateFields));

		final JavaFileObject jfo = processingEnv.getFiler().createSourceFile(targetClassName);
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "creating source file: " + jfo.toUri());
		final Writer writer = jfo.openWriter();
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "applying template: " + template.getName());
		template.execute(writer, templateContext).flush();
		writer.close();
	}

	/**
	 * Finds all required import statements that need to be included in the target template
	 * 
	 * @param targetPackageName
	 *            the target package for the class to be generated
	 * @param fields
	 *            the query fields whose classes may need to ne imported
	 * @return the {@link List} of class imports (excluding those in the same package)
	 */
	private List<ImportStatement> findRequiredImportStatements(final String targetPackageName,
			final List<? extends FieldMetadata> fields) {
		if(fields == null) {
			return Collections.emptyList();
		}
		return fields.stream().map(f -> f.getRequiredJavaTypes()).flatMap(set -> set.stream()).distinct()
				.filter(t -> {
					final String packageCanonicalName = ClassUtils.getPackageCanonicalName(t);
					return !packageCanonicalName.equals(targetPackageName) && !packageCanonicalName.equals("java.lang");
				} ).map(i -> new ImportStatement(i)).collect(Collectors.toList());
	}

	/**
	 * Builds the simple name of the {@link QueryMetadata} class associated with the given {@code element}
	 * 
	 * @param element
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, prefixed by
	 *         {@link Constants#QUERY_METADATA_CLASSNAME_PREFIX}.
	 */
	protected static String createQueryMetadataSimpleClassName(final Element element) {
		return Constants.QUERY_METADATA_CLASSNAME_PREFIX + element.getSimpleName().toString();
	}

	/**
	 * Generates the {@code QueryMetadata} implementation source code for the annotated class currently being processed.
	 * 
	 * @param baseTemplateContext
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	protected void generateQueryMetadataSourceCode(final TypeElement domainElement, final Map<String, Object> baseTemplateContext)
			throws IOException {
		final Map<String, Object> templateContext = new HashMap<>(baseTemplateContext);
		templateContext.put(Constants.TEMPLATE_FIELDS_ALIAS, (List<FieldMetadata>) baseTemplateContext
				.get(Constants.QUERY_FIELDS));
		templateContext.put(Constants.GENERATED_SIMPLE_CLASS_NAME, createQueryMetadataSimpleClassName(domainElement));
		final Mustache template = getTemplate(Constants.QUERY_METADATA_TEMPLATE);
		generateSourceCode(template, templateContext);
	}

	/**
	 * Builds the simple name of the {@link ProjectionMetadata} class associated with the given {@code element}
	 * 
	 * @param element
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, prefixed by
	 *         {@link Constants#PROJECTION_METADATA_CLASSNAME_PREFIX}.
	 */
	protected static String createProjectionMetadataSimpleClassName(final Element element) {
		return Constants.PROJECTION_METADATA_CLASSNAME_PREFIX + element.getSimpleName().toString();
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
	@SuppressWarnings("unchecked")
	protected void generateProjectionMetadataSourceCode(final TypeElement domainElement, final Map<String, Object> baseTemplateContext)
			throws IOException {
		final Map<String, Object> templateContext = new HashMap<>(baseTemplateContext);
		templateContext.put(Constants.TEMPLATE_FIELDS_ALIAS, (List<FieldMetadata>) baseTemplateContext
				.get(Constants.PROJECTION_FIELDS));
		templateContext.put(Constants.GENERATED_SIMPLE_CLASS_NAME, createProjectionMetadataSimpleClassName(domainElement));
		final Mustache template = getTemplate(Constants.PROJECTION_METADATA_TEMPLATE);
		generateSourceCode(template, templateContext);
	}
	
	/**
	 * Builds the simple name of the {@link UpdateMetadata} class associated with the given {@code element}
	 * 
	 * @param element
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, prefixed by
	 *         {@link DocumentAnnotationProcessor#UPDATE_METADATA_CLASSNAME_PREFIX}.
	 */
	protected static String createUpdateMetadataSimpleClassName(final Element element) {
		return Constants.UPDATE_METADATA_CLASSNAME_PREFIX + element.getSimpleName().toString();
	}
	
	/**
	 * Generates the {@code UpdateMetadata} implementation source code for the annotated class currently being
	 * processed.
	 * 
	 * @param allContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	protected void generateUpdateMetadataSourceCode(final TypeElement domainElement, final Map<String, Object> baseTemplateContext)
			throws IOException {
		final Map<String, Object> templateContext = new HashMap<>(baseTemplateContext);
		templateContext.put(Constants.TEMPLATE_FIELDS_ALIAS, (List<FieldMetadata>) baseTemplateContext
				.get(Constants.UPDATE_FIELDS));
		templateContext.put(Constants.GENERATED_SIMPLE_CLASS_NAME, createUpdateMetadataSimpleClassName(domainElement));
		final Mustache template = getTemplate(Constants.UPDATE_METADATA_TEMPLATE);
		generateSourceCode(template, templateContext);

	}

}