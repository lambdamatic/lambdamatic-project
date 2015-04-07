package org.lambdamatic.mongodb.apt;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.apache.commons.io.IOUtils;
import org.lambdamatic.mongodb.metadata.QueryMetadata;
import org.stringtemplate.v4.AutoIndentWriter;
import org.stringtemplate.v4.ST;

public abstract class BaseAnnotationProcessor extends AbstractProcessor {

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
	 * Initializes a StringTemplate ({@link ST}) from the given {@code templateFileName}
	 * 
	 * @param templateFileName
	 *            the name of the template file to load
	 * @return the corresponding StringTemplate
	 * @throws IOException
	 */
	ST getStringTemplate(final String templateFileName) throws IOException {
		// FIXME: the way the templateContent is retrieved causes problem when jar is reloaded in another IDE for a
		// sample project. Current workaround that seems to work is closing/reopening the project.
		final ClassLoader contextClassLoader = getClass().getClassLoader();
		final InputStream resourceAsStream = contextClassLoader.getResourceAsStream(templateFileName);
		final String templateContent = IOUtils.toString(resourceAsStream, "UTF-8");
		return new ST(templateContent, '$', '$');
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
	 * @param allContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	void generateSourceCode(final String targetClassName, final ST template,
			final Map<String, Object> allContextProperties) throws IOException {
		if (template == null) {
			processingEnv.getMessager().printMessage(
					Diagnostic.Kind.WARNING,
					"Could not create the QueryMetadata class for the given '"
							+ allContextProperties.get("packageName") + "."
							+ allContextProperties.get("domainClassName")
							+ "' class: no queryMetadataTemplate available.");
			return;
		}
		// fill the template with all the properties (even if we don't need them all)
		allContextProperties.keySet().stream().forEach(k -> template.add(k, allContextProperties.get(k)));
		final JavaFileObject jfo = processingEnv.getFiler().createSourceFile(targetClassName);
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "creating source file: " + jfo.toUri());
		final Writer writer = jfo.openWriter();
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "applying template: " + template.getName());
		template.write(new AutoIndentWriter(writer));
		writer.close();
	}

}