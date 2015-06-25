package org.lambdamatic.jpa.apt;

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
import javax.persistence.Basic;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.apache.commons.lang3.ClassUtils;
import org.lambdamatic.jpa.QueryMetadata;

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
	 *         {@link Constants#QUERY_METADATA_CLASSNAME_PREFIX}.
	 */
	static String generateQueryMetadataSimpleClassName(final Element element) {
		return Constants.QUERY_METADATA_CLASSNAME_PREFIX + element.getSimpleName().toString();
	}

	/**
	 * Builds the simple name of the {@link ProjectionMetadata} class associated with the given {@code element}
	 * 
	 * @param element
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, prefixed by
	 *         {@link Constants#PROJECTION_METADATA_CLASSNAME_PREFIX}.
	 */
	static String generateProjectionSimpleClassName(final Element element) {
		return Constants.PROJECTION_METADATA_CLASSNAME_PREFIX + element.getSimpleName().toString();
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
	 * Finds all required import statements that need to be included in the target template
	 * 
	 * @param targetPackageName
	 *            the target package for the class to be generated
	 * @param queryFields
	 *            the query fields whose classes may need to ne imported
	 * @return the {@link List} of class imports (excluding those in the same package)
	 */
	List<ImportStatement> findRequiredImportStatements(final String targetPackageName,
			final List<? extends BaseFieldMetadata> queryFields) {
		return queryFields.stream().map(f -> f.getRequiredJavaTypes()).flatMap(set -> set.stream()).distinct()
				.filter(t -> {
					final String packageCanonicalName = ClassUtils.getPackageCanonicalName(t);
					return !packageCanonicalName.equals(targetPackageName) && !packageCanonicalName.equals("java.lang");
				} ).map(i -> new ImportStatement(i)).collect(Collectors.toList());
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
	 * Generates the {@code UpdateArray} implementation source code for the annotated class currently being processed.
	 * 
	 * @param allContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	void generateUpdateArrayMetadataSourceCode(final Mustache template, final Map<String, Object> templateContextProperties)
			throws IOException {
		// skip this step if UpdateArray metadata class is not needed.
		if(templateContextProperties.get(Constants.UPDATE_ARRAY_METADATA_CLASS_NAME) == null) {
			return;
		}
		final String targetPackageName = (String) templateContextProperties.get(Constants.PACKAGE_NAME);
		final String targetClassName = targetPackageName + "."
				+ templateContextProperties.get(Constants.UPDATE_ARRAY_METADATA_CLASS_NAME);
		generateSourceCode(targetClassName, template, templateContextProperties);
	}

	
	
	Map<String, Object> initializeTemplateContextProperties(final TypeElement domainElement) {
		final Map<String, Object> properties = new HashMap<String, Object>();
		final PackageElement packageElement = (PackageElement) domainElement.getEnclosingElement();
		properties.put("processorClassName", EntityMetadataGenerator.class.getName());
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
	 * Returns a {@link Map} of all the type of the fields of the given {@code classElement}, indexed by their usage,
	 * i.e, {@link QueryMetadata}, {@link ProjectionMetadata} or {@link UpdateMetadata}.
	 * 
	 * @param classElement
	 *            the element to scan
	 * @return the map of fields
	 */
	Map<Class<?>, List<? extends BaseFieldMetadata>> getMetadataFields(final TypeElement classElement) {
		final Map<Class<?>, List<? extends BaseFieldMetadata>> allFields = new HashMap<>();
		final List<QueryFieldMetadata> queryFields = new ArrayList<>();
		allFields.put(QueryMetadata.class, queryFields);
		
		for (Element childElement : classElement.getEnclosedElements()) {
			if (childElement.getKind() == ElementKind.FIELD) {
				final VariableElement variableElement = (VariableElement) childElement;
				try {
						final Basic basicAnnotation = childElement.getAnnotation(Basic.class);
						queryFields.add(new QueryFieldMetadata(variableElement, basicAnnotation));
				} catch (MetadataGenerationException e) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
				}
	
			}
		}
		return allFields;
	}

}