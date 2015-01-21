/**
 * 
 */
package org.lambdamatic.mongodb.apt;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.apache.commons.io.IOUtils;
import org.lambdamatic.mongodb.LambdamaticMongoCollection;
import org.lambdamatic.mongodb.annotations.Document;
import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.annotations.FetchType;
import org.lambdamatic.mongodb.annotations.TransientField;
import org.lambdamatic.mongodb.metadata.DateField;
import org.lambdamatic.mongodb.metadata.LocationField;
import org.lambdamatic.mongodb.metadata.Metadata;
import org.lambdamatic.mongodb.metadata.ObjectIdField;
import org.lambdamatic.mongodb.metadata.StringField;
import org.stringtemplate.v4.AutoIndentWriter;
import org.stringtemplate.v4.ST;


/**
 * Processor for classes annotated with {@code Document}. Generates their associated metadata Java classes in the target folder given in the
 * constructor.
 * 
 * @author Xavier Coulon
 *
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.lambdamatic.mongodb.annotations.Document")
public class LambdamaticAnnotationsProcessor extends AbstractProcessor {

	/** Name of the template file for {@link Metadata} classes. */
	private static final String METACLASS_TEMPLATE = "metadata_template.st";

	/** Suffix to use for the generated metadata classes. */
	private static String METACLASS_NAME_SUFFIX = "_";
	
	/** Name of the template file for the {@link LambdamaticMongoCollection} implementation classes. */
	private static final String MONGO_COLLECTION_TEMPLATE = "mongo_collection_template.st";

	/** Suffix to use for the generated {@link LambdamaticMongoCollection} implementation classes. */
	private static String MONGO_COLLECTION_NAME_SUFFIX = "Collection";
	
	/** Name of the template file for the {@link LambdamaticMongoCollection} implementation producer classes. */
	private static final String MONGO_COLLECTION_PRODUCER_TEMPLATE = "mongo_collection_producer_template.st";
	
	/** Suffix to use for the generated {@link LambdamaticMongoCollection} implementation producer classes. */
	private static String MONGO_COLLECTION_PRODUCER_NAME_SUFFIX = "CollectionProducer";
	
	/** StringTemplate for the metadata classes. */
	private ST metadataTemplate;

	/** StringTemplate for the {@link LambdamaticMongoCollection} implementation classes. */
	private ST mongoCollectionTemplate;

	/** StringTemplate for {@link LambdamaticMongoCollection} implementation producer classes. */
	private ST mongoCollectionProducerTemplate;

	/**
	 * Constructor
	 * @throws IOException if templates could not be loaded.
	 */
	public LambdamaticAnnotationsProcessor() throws IOException {
		this.metadataTemplate = getStringTemplate(METACLASS_TEMPLATE);
		this.mongoCollectionTemplate = getStringTemplate(MONGO_COLLECTION_TEMPLATE);
		this.mongoCollectionProducerTemplate = getStringTemplate(MONGO_COLLECTION_PRODUCER_TEMPLATE);
	}

	/**
	 * Initializes a StringTemplate ({@link ST}) from the given {@code templateFileName}
	 * @param templateFileName the name of the template file to load
	 * @return the corresponding StringTemplate
	 * @throws IOException
	 */
	private ST getStringTemplate(final String templateFileName) throws IOException {
		final String templateContent = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream(templateFileName), "UTF-8");
		return new ST(templateContent, '$', '$');
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
		for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Document.class)) {
			if (annotatedElement instanceof TypeElement) {
				try {
					final TypeElement domainElement = (TypeElement) annotatedElement;
					processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "annotated class: " + domainElement.getQualifiedName(),
							domainElement);
					final Map<String, Object> templateContextProperties = initializeTemplateContextProperties(domainElement);
					generateMetadataSourceCode(templateContextProperties);
					generateMongoCollectionSourceCode(templateContextProperties);
					generateMongoCollectionProducerSourceCode(templateContextProperties);
				} catch (IOException e) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
							"Failed to process annotated element '" + annotatedElement.getSimpleName() + "': " + e.getMessage());
				}
			}
		}
		return false;
	}
	
	private Map<String, Object> initializeTemplateContextProperties(final TypeElement domainElement) {
		final Map<String, Object> properties = new HashMap<String, Object>();
		final PackageElement packageElement = (PackageElement) domainElement.getEnclosingElement();
		final Document documentAnnotation = domainElement.getAnnotation(Document.class);
		properties.put("processorClassName", LambdamaticAnnotationsProcessor.class.getName());
		properties.put("packageName", packageElement.getQualifiedName().toString());
		properties.put("domainClassName", domainElement.getSimpleName().toString());
		properties.put("fields", getFields(domainElement));
		properties.put("metadataDomainClassName", generateMetadataSimpleClassName(domainElement));
		properties.put("mongoCollectionName", documentAnnotation.collection());
		properties.put("mongoCollectionClassName", generateMongoCollectionSimpleClassName(domainElement));
		properties.put("mongoCollectionProducerClassName", generateMongoCollectionProviderSimpleClassName(domainElement));
		return properties; 
	}

	/**
	 * Generates the metadata source code for the given {@code classElement} in the given target {@code packageElement}.
	 * 
	 * @param allContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	private void generateMetadataSourceCode(final Map<String, Object> allContextProperties) throws IOException {
		if (this.metadataTemplate == null) {
			processingEnv.getMessager().printMessage(
					Diagnostic.Kind.WARNING,
					"Could not create the metadata class for the given '" + allContextProperties.get("packageName") + "."
							+ allContextProperties.get("domainClassName") + "' class");
			return;
		}
		// fill the template with all the properties (even if we don't need them all)
		allContextProperties.keySet().stream().forEach(k -> this.metadataTemplate.add(k, allContextProperties.get(k)));
		final JavaFileObject jfo = processingEnv.getFiler().createSourceFile(allContextProperties.get("packageName").toString() + "." + allContextProperties.get("metadataDomainClassName").toString());
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "creating source file: " + jfo.toUri());
		final Writer writer = jfo.openWriter();
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "applying template: " + metadataTemplate.getName());
		this.metadataTemplate.write(new AutoIndentWriter(writer));
		writer.close();
	}

	/**
	 * Generates the {@code LambdamaticMongoCollection} implementation source code for the underlying MongoDB collection.
	 * 
	 * @param allContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	private void generateMongoCollectionSourceCode(final Map<String, Object> allContextProperties) throws IOException {
		if (this.mongoCollectionTemplate == null) {
			processingEnv.getMessager().printMessage(
					Diagnostic.Kind.WARNING,
					"Could not create the LambdamaticMongoCollection implementation class for the given '" + allContextProperties.get("packageName") + "."
							+ allContextProperties.get("domainClassName") + "' class");
			return;
		}
		// fill the template with all the properties (even if we don't need them all)
		allContextProperties.keySet().stream().forEach(k -> mongoCollectionTemplate.add(k, allContextProperties.get(k)));
		final JavaFileObject jfo = processingEnv.getFiler().createSourceFile(allContextProperties.get("packageName").toString() + "." + allContextProperties.get("mongoCollectionClassName").toString());
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "creating source file: " + jfo.toUri());
		final Writer writer = jfo.openWriter();
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "applying template: " + this.mongoCollectionTemplate.getName());
		this.mongoCollectionTemplate.write(new AutoIndentWriter(writer));
		writer.close();
	}

	/**
	 * Generates the CDI Producer for the {@code LambdamaticMongoCollection} implementation.
	 * 
	 * @param allContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	private void generateMongoCollectionProducerSourceCode(final Map<String, Object> allContextProperties) throws IOException {
		if (this.mongoCollectionProducerTemplate == null) {
			processingEnv.getMessager().printMessage(
					Diagnostic.Kind.WARNING,
					"Could not create the LambdamaticMongoCollection implementation class for the given '" + allContextProperties.get("packageName") + "."
							+ allContextProperties.get("domainClassName") + "' class");
			return;
		}
		// fill the template context with all the properties (even if we don't need them all)
		allContextProperties.keySet().stream().forEach(k -> mongoCollectionProducerTemplate.add(k, allContextProperties.get(k)));
		final JavaFileObject jfo = processingEnv.getFiler().createSourceFile(allContextProperties.get("packageName").toString() + "." + allContextProperties.get("mongoCollectionProducerClassName").toString());
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "creating source file: " + jfo.toUri());
		final Writer writer = jfo.openWriter();
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "applying template: " + this.mongoCollectionProducerTemplate.getName());
		this.mongoCollectionProducerTemplate.write(new AutoIndentWriter(writer));
		writer.close();
	}
	
	/**
	 * Returns a {@link Map} of the type of the fields of the given classElement, indexed by their name.
	 * 
	 * @param classElement
	 *            the element to scan
	 * @return the map of fields
	 */
	private List<FieldMetadata> getFields(final TypeElement classElement) {
		final List<FieldMetadata> fields = new ArrayList<>();
		for (Element childElement : classElement.getEnclosedElements()) {
			if (childElement.getKind() == ElementKind.FIELD) {
				final TransientField transientFieldAnnotation = childElement.getAnnotation(TransientField.class);
				// skip field if it is annotated with @TransientField
				if (transientFieldAnnotation != null) {
					continue;
				}
				final DocumentField documentFieldAnnotation = childElement.getAnnotation(DocumentField.class);
				final VariableElement variableElement = (VariableElement) childElement;
				fields.add(new FieldMetadata(variableElement, documentFieldAnnotation));
			}
		}
		return fields;
	}

	/**
	 * Builds the simple name of the Metadata class associated with the given {@code typeElement}
	 * 
	 * @param typeElement
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, followed by {@link LambdamaticAnnotationsProcessor#METACLASS_SUFFIX}.
	 */
	public static String generateMetadataSimpleClassName(final TypeElement typeElement) {
		return typeElement.getSimpleName().toString() + METACLASS_NAME_SUFFIX;
	}

	/**
	 * Builds the simple name of the {@link LambdamaticMongoCollection} class associated with the given  {@code typeElement}
	 * 
	 * @param typeElement
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, followed by {@link LambdamaticAnnotationsProcessor#MONGO_COLLECTION_NAME_SUFFIX}.
	 */
	public static String generateMongoCollectionSimpleClassName(final TypeElement typeElement) {
		return typeElement.getSimpleName().toString() + MONGO_COLLECTION_NAME_SUFFIX;
	}
	
	/**
	 * Builds the simple name of the {@link LambdamaticMongoCollection} provider class associated with the given  {@code typeElement}
	 * 
	 * @param typeElement
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, followed by {@link LambdamaticAnnotationsProcessor#MONGO_COLLECTION_PRODUCER_NAME_SUFFIX}.
	 */
	public static String generateMongoCollectionProviderSimpleClassName(final TypeElement typeElement) {
		return typeElement.getSimpleName().toString() + MONGO_COLLECTION_PRODUCER_NAME_SUFFIX;
	}
	
	/**
	 * Information about a given field that should be generated in a {@link Metadata} class.
	 * 
	 * @author Xavier Coulon <xcoulon@redhat.com>
	 *
	 */
	public class FieldMetadata {

		/** The java field name. */
		private final String javaFieldName;
		/** The document field name. */
		private final String documentFieldName;
		/** The java field type. */
		private final String javaFieldType;
		/** the document field fetch strategy. */
		private final FetchType documentFieldFetchType;

		/**
		 * 
		 * @param variableElement
		 * @param documentFieldAnnotation
		 */
		public FieldMetadata(final VariableElement variableElement, final DocumentField documentFieldAnnotation) {
			this.javaFieldName = getVariableName(variableElement);
			this.javaFieldType = getVariableType(variableElement);
			this.documentFieldName = getDocumentFieldName(documentFieldAnnotation, javaFieldName);
			this.documentFieldFetchType = getDocumentFieldFetchType(documentFieldAnnotation);
		}

		/**
		 * Returns the {@link DocumentField#fetch()} value if the given {@code documentFieldAnnotation} is not null, otherwise, returns the
		 * default value configured in the {@link DocumentField} annotation.
		 * 
		 * @param documentFieldAnnotation
		 *            the annotation to analyze
		 * @return the {@link FetchType} strategy
		 */
		private FetchType getDocumentFieldFetchType(final DocumentField documentFieldAnnotation) {
			if (documentFieldAnnotation != null) {
				return documentFieldAnnotation.fetch();
			}
			// MUST match the default value in DocumentField
			return FetchType.EAGER;
		}

		/**
		 * Returns the {@link DocumentField#name()} value if the given {@code documentFieldAnnotation} is not null, otherwise it returns the
		 * given {@code defaultDocumentFieldName}.
		 * 
		 * @param documentFieldAnnotation
		 *            the annotation to analyze
		 * @param defaultDocumentFieldName
		 *            the default value if the given annotation was {@code null}
		 * @return the name of the field in the document
		 */
		private String getDocumentFieldName(final DocumentField documentFieldAnnotation, final String defaultDocumentFieldName) {
			if (documentFieldAnnotation != null) {
				return documentFieldAnnotation.name();
			}
			return defaultDocumentFieldName;
		}

		/**
		 * Returns the fully qualified name of the type of the given {@link VariableElement}, or {@code null} if it was not a known or
		 * supported type.
		 * 
		 * @param variableElement
		 *            the variable to analyze
		 * @return
		 */
		private String getVariableType(final VariableElement variableElement) {
			//try {
				final TypeMirror variableType = variableElement.asType();
				if (variableType instanceof PrimitiveType) {
					return variableType.getKind().name().toLowerCase();
				} else if (variableType instanceof DeclaredType) {
					final Element variableTypeElement = ((DeclaredType) variableType).asElement();
					if(variableTypeElement.getKind() == ElementKind.ENUM) {
						return variableTypeElement.toString();
					}
					switch (variableType.toString()) {
					case "java.lang.String":
						return StringField.class.getName();
					case "java.util.Date":
						return DateField.class.getName();
					case "org.bson.types.ObjectId":
						return ObjectIdField.class.getName();
					case "org.lambdamatic.mongodb.types.geospatial.Location":
						return LocationField.class.getName();
					default:
						throw new RuntimeException("Unsupported field '" + variableElement.getSimpleName() + "'  of type " + variableType.toString());
					}
					
				}
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
						"Unexpected variable type for '" + variableElement.getSimpleName() + "' : " + variableType);
				/*} catch (ClassNotFoundException e) {
				processingEnv.getMessager().printMessage(
						Diagnostic.Kind.ERROR,
						"Error occurred while processing '" + variableElement.getSimpleName() + "' : " + e.getClass() + " "
								+ e.getMessage());

			}*/
			return null;
		}

		/**
		 * Returns the simple name of the given {@link VariableElement}
		 * 
		 * @param variableElement
		 *            the variable to analyze
		 * @return the java field name to use in the metadata class
		 */
		private String getVariableName(final VariableElement variableElement) {
			return variableElement.getSimpleName().toString();
		}

		/**
		 * @return the javaFieldName
		 */
		public String getJavaFieldName() {
			return javaFieldName;
		}

		/**
		 * @return the documentFieldName
		 */
		public String getDocumentFieldName() {
			return documentFieldName;
		}

		/**
		 * @return the javaFieldType
		 */
		public String getJavaFieldType() {
			return javaFieldType;
		}

		/**
		 * @return the documentFieldFetchType
		 */
		public FetchType getDocumentFieldFetchType() {
			return documentFieldFetchType;
		}

	}
}

