/**
 * 
 */
package org.lambdamatic.mongodb.apt;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.lambdamatic.mongodb.annotations.Document;
import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.annotations.FetchType;
import org.lambdamatic.mongodb.annotations.TransientField;
import org.lambdamatic.mongodb.metadata.DateField;
import org.lambdamatic.mongodb.metadata.LocationField;
import org.lambdamatic.mongodb.metadata.Metadata;
import org.lambdamatic.mongodb.metadata.ObjectIdField;
import org.lambdamatic.mongodb.metadata.StringField;

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

	/** Name of the template file for metadata classes. */
	private static final String METADATA_TEMPLATE = "metadata_template.vm";

	/** Name of the template file for the datastore class. */
	private static final String DATASTORE_TEMPLATE = "datastore_template.vm";

	/** Name of the template file for the datastore producer class. */
	private static final String DATASTORE_PRODUCER_TEMPLATE = "datastore_producer_template.vm";

	/** Target package for the DataStore and its provider classes. */
	private static final String ORG_LAMBDAMATIC_MONGODB = "org.lambdamatic.mongodb";

	/** Name of the datastore class. */
	private static final String DATA_STORE = "DataStore";

	/** Name of the datastore provider class. */
	private static final String DATA_STORE_PRODUCER = "DataStoreProducer";

	/** Suffix to use for the generated metadata classes. */
	private static String METACLASS_SUFFIX = "_";

	/** The Velocity Engine. */
	private VelocityEngine velocityEngine;

	/** Velocity template for the metadata classes. */
	private Template metadataTemplate;

	/** Velocity template for the datastore class. */
	private Template datastoreTemplate;

	/** Velocity template for the datastore producer class. */
	private Template datastoreProducerTemplate;

	public LambdamaticAnnotationsProcessor() {
		loadVelocityContext();
	}

	private void loadVelocityContext() {
		System.err.println("Initializing " + getClass().getName());
		if (this.velocityEngine == null) {
			try {
				final Properties props = new Properties();
				final URL url = this.getClass().getClassLoader().getResource("velocity.properties");
				if (url != null) {
					props.load(url.openStream());
				}
				this.velocityEngine = new VelocityEngine(props);
				velocityEngine.init();
				this.metadataTemplate = velocityEngine.getTemplate(METADATA_TEMPLATE);
				this.datastoreTemplate = velocityEngine.getTemplate(DATASTORE_TEMPLATE);
				this.datastoreProducerTemplate = velocityEngine.getTemplate(DATASTORE_PRODUCER_TEMPLATE);
			} catch (IOException e) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
						"Failed to initialize Velocity Engine, Context or Template: " + e.getMessage());
			}
		}
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
		final Map<String, TypeElement> collections = new HashMap<String, TypeElement>();
		for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Document.class)) {
			if (annotatedElement instanceof TypeElement) {
				try {
					final TypeElement typeElement = (TypeElement) annotatedElement;
					final PackageElement packageElement = (PackageElement) typeElement.getEnclosingElement();
					processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "annotated class: " + typeElement.getQualifiedName(),
							typeElement);
					generateMetadataSourceCode(packageElement, typeElement);
					// keep the name of the collection defined in the @Document annotation
					final Document documentAnnotation = typeElement.getAnnotation(Document.class);
					collections.put(capitalize(documentAnnotation.collection()), typeElement);
				} catch (IOException e) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
							"Failed to process annotated element '" + annotatedElement.getSimpleName() + "': " + e.getMessage());
				}
			}
		}
		if (!collections.isEmpty()) {
			try {
				generateDatastoreSourceCode(ORG_LAMBDAMATIC_MONGODB, collections);
				generateDatastoreProviderSourceCode(ORG_LAMBDAMATIC_MONGODB);
			} catch (IOException e) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
						"Failed to generate DataStore or DataStoreProvider class: " + e.getMessage());
			}
			return true; // no further processing of this annotation type
		}
		return false;
	}

	/**
	 * Capitalizes the first character of the given word, only
	 * 
	 * @param word
	 *            the word to capitalize
	 * @return the capitalized word
	 */
	private static String capitalize(final String word) {
		return Character.toUpperCase(word.charAt(0)) + word.substring(1);
	}

	/**
	 * Generates the metadata source code for the given {@code classElement} in the given target {@code packageElement}.
	 * 
	 * @param packageElement
	 *            the package of the domain class element
	 * @param classElement
	 *            the domain class element
	 * 
	 * @throws IOException
	 */
	private void generateMetadataSourceCode(final PackageElement packageElement, final TypeElement classElement) throws IOException {
		if (this.metadataTemplate == null) {
			processingEnv.getMessager().printMessage(
					Diagnostic.Kind.WARNING,
					"Could not create the metadata class for the given '" + packageElement.getQualifiedName().toString() + "."
							+ classElement + "' class");
			return;
		}
		final VelocityContext velocityContext = new VelocityContext();
		velocityContext.put("processorClassName", LambdamaticAnnotationsProcessor.class.getName());
		velocityContext.put("packageName", packageElement.getQualifiedName().toString());
		velocityContext.put("metadataDomainClassName", generateMetadataClassName(classElement));
		velocityContext.put("domainClassName", classElement.getSimpleName().toString());
		velocityContext.put("fields", getFields(classElement));
		final JavaFileObject jfo = processingEnv.getFiler().createSourceFile(classElement.getQualifiedName().toString() + METACLASS_SUFFIX);
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "creating source file: " + jfo.toUri());
		final Writer writer = jfo.openWriter();
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "applying velocity template: " + metadataTemplate.getName());
		this.metadataTemplate.merge(velocityContext, writer);
		writer.close();
	}

	/**
	 * Generates the {@code DataStore} source code for the given MongoDB {@code collections}, in the target {@code packageElement}.
	 * 
	 * @param packageElement
	 *            the target package for the generated source code.
	 * @param collections
	 *            the names of the MongoDB collections that the generated DataStore will provide.
	 * 
	 * @throws IOException
	 */
	private void generateDatastoreSourceCode(final String packageName, final Map<String, TypeElement> collections) throws IOException {
		if (this.datastoreTemplate == null) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
					"Could not create the '" + packageName + "." + DATA_STORE + "' class.");
			return;
		}
		final VelocityContext velocityContext = new VelocityContext();
		velocityContext.put("processorClassName", LambdamaticAnnotationsProcessor.class.getName());
		velocityContext.put("packageName", packageName);
		velocityContext.put("dataStoreClassName", DATA_STORE);
		velocityContext.put("collections", collections);
		final JavaFileObject jfo = processingEnv.getFiler().createSourceFile(packageName + "." + DATA_STORE);
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "creating source file: " + jfo.toUri());
		final Writer writer = jfo.openWriter();
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "applying velocity template: " + datastoreTemplate.getName());
		this.datastoreTemplate.merge(velocityContext, writer);
		writer.close();
	}

	/**
	 * Generates the {@code DataStoreProvider} source code for the (already) generated {@code DataStore}, in the target
	 * {@code packageElement}.
	 * 
	 * @param packageElement
	 *            the target package for the generated source code.
	 * 
	 * @throws IOException
	 */
	private void generateDatastoreProviderSourceCode(final String packageName) throws IOException {
		if (this.datastoreProducerTemplate == null) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
					"Could not create the '" + packageName + "." + DATA_STORE_PRODUCER + "' class.");
			return;
		}
		final VelocityContext velocityContext = new VelocityContext();
		velocityContext.put("processorClassName", LambdamaticAnnotationsProcessor.class.getName());
		velocityContext.put("packageName", packageName);
		velocityContext.put("dataStoreProducerClassName", DATA_STORE_PRODUCER);
		velocityContext.put("dataStoreClassName", DATA_STORE);
		final JavaFileObject jfo = processingEnv.getFiler().createSourceFile(packageName + "." + DATA_STORE_PRODUCER);
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "creating source file: " + jfo.toUri());
		final Writer writer = jfo.openWriter();
		processingEnv.getMessager()
				.printMessage(Diagnostic.Kind.NOTE, "applying velocity template: " + datastoreProducerTemplate.getName());
		this.datastoreProducerTemplate.merge(velocityContext, writer);
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
	 * Builds the simple name of the Metadata class from associated with the given
	 * 
	 * @param typeElement
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, followed by {@link LambdamaticAnnotationsProcessor#METACLASS_SUFFIX}.
	 */
	public static String generateMetadataClassName(final TypeElement typeElement) {
		return typeElement.getSimpleName().toString() + METACLASS_SUFFIX;
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
					case "org.lambdamatic.mongodb.types.geospatial.Point":
						return LocationField.class.getName();
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

