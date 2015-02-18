/**
 * 
 */
package org.lambdamatic.mongodb.apt;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
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
import org.lambdamatic.mongodb.annotations.DocumentId;
import org.lambdamatic.mongodb.annotations.TransientField;
import org.lambdamatic.mongodb.codecs.DocumentCodec;
import org.lambdamatic.mongodb.metadata.DateField;
import org.lambdamatic.mongodb.metadata.LocationField;
import org.lambdamatic.mongodb.metadata.ObjectIdField;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.QueryMetadata;
import org.lambdamatic.mongodb.metadata.StringField;
import org.stringtemplate.v4.AutoIndentWriter;
import org.stringtemplate.v4.ST;


/**
 * Processor for classes annotated with {@code Document}. Generates their associated metadata Java classes in the target folder given in the
 * constructor.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.lambdamatic.mongodb.annotations.Document")
public class LambdamaticAnnotationsProcessor extends AbstractProcessor {

	/** Name of the template file for {@link QueryMetadata} classes. */
	private static final String QUERY_METADATA_TEMPLATE = "query_metadata_template.st";

	/** Suffix to use for the generated {@link QueryMetadata} classes. */
	private static String QUERY_METADATA_CLASSNAME_PREFIX = "Q";
	
	/** Name of the template file for {@link ProjectionMetadata} classes. */
	private static final String PROJECTION_METADATA_TEMPLATE = "projection_metadata_template.st";
	
	/** Suffix to use for the generated {@link ProjectionMetadata} classes. */
	private static String PREOJECTION_METADATA_CLASSNAME_PREFIX = "P";
	
	/** Name of the template file for the {@link LambdamaticMongoCollection} implementation classes. */
	private static final String MONGO_COLLECTION_TEMPLATE = "mongo_collection_template.st";

	/** Suffix to use for the generated {@link LambdamaticMongoCollection} implementation classes. */
	private static String MONGO_COLLECTION_CLASSNAME_SUFFIX = "Collection";
	
	/** Name of the template file for the {@link LambdamaticMongoCollection} implementation producer classes. */
	private static final String MONGO_COLLECTION_PRODUCER_TEMPLATE = "mongo_collection_producer_template.st";
	
	/** Suffix to use for the generated {@link LambdamaticMongoCollection} implementation producer classes. */
	private static String MONGO_COLLECTION_PRODUCER_CLASSNAME_SUFFIX = "CollectionProducer";
	
	/** StringTemplate for the {@link QueryMetadata} classes. */
	private ST queryMetadataTemplate;

	/** StringTemplate for the {@link ProjectionMetadata} classes. */
	private ST projectionMetadataTemplate;
	
	/** StringTemplate for the {@link LambdamaticMongoCollection} implementation classes. */
	private ST mongoCollectionTemplate;

	/** StringTemplate for {@link LambdamaticMongoCollection} implementation producer classes. */
	private ST mongoCollectionProducerTemplate;

	/**
	 * Constructor
	 * @throws IOException if templates could not be loaded.
	 */
	public LambdamaticAnnotationsProcessor() throws IOException {
		this.queryMetadataTemplate = getStringTemplate(QUERY_METADATA_TEMPLATE);
		this.projectionMetadataTemplate = getStringTemplate(PROJECTION_METADATA_TEMPLATE);
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
		// FIXME: the way the templateContent is retrieved causes problem when jar is reloaded in another IDE for a sample project. Current workaround that seems to work is closing/reopening the project. 
		final ClassLoader contextClassLoader = getClass().getClassLoader();
		final InputStream resourceAsStream = contextClassLoader.getResourceAsStream(templateFileName);
		final String templateContent = IOUtils.toString(resourceAsStream, "UTF-8");
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
					generateQueryMetadataSourceCode(templateContextProperties);
					generateProjectionMetadataSourceCode(templateContextProperties);
					generateMongoCollectionSourceCode(templateContextProperties);
					generateMongoCollectionProducerSourceCode(templateContextProperties);
				} 
				// catch Throwable to avoid crashing the compiler.
				catch (final Throwable e) {
					final Writer writer = new StringWriter();
					e.printStackTrace(new PrintWriter(writer));
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
							"Failed to process annotated element '" + annotatedElement.getSimpleName() + "': " + writer.toString());
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
		properties.put("queryFields", getQueryFields(domainElement));
		properties.put("queryMetadataClassName", generateQueryMetadataSimpleClassName(domainElement));
		properties.put("projectionMetadataClassName", generateProjectionMetadataSimpleClassName(domainElement));
		properties.put("mongoCollectionName", documentAnnotation.collection());
		properties.put("mongoCollectionClassName", generateMongoCollectionSimpleClassName(domainElement));
		properties.put("mongoCollectionProducerClassName", generateMongoCollectionProviderSimpleClassName(domainElement));
		return properties; 
	}

	/**
	 * Generates the {@link QueryMetadata} source code for the given {@code classElement} in the given target {@code packageElement}.
	 * 
	 * @param allContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	private void generateQueryMetadataSourceCode(final Map<String, Object> allContextProperties) throws IOException {
		if (this.queryMetadataTemplate == null) {
			processingEnv.getMessager().printMessage(
					Diagnostic.Kind.WARNING,
					"Could not create the QueryMetadata class for the given '" + allContextProperties.get("packageName") + "."
							+ allContextProperties.get("domainClassName") + "' class");
			return;
		}
		// fill the template with all the properties (even if we don't need them all)
		allContextProperties.keySet().stream().forEach(k -> this.queryMetadataTemplate.add(k, allContextProperties.get(k)));
		final JavaFileObject jfo = processingEnv.getFiler().createSourceFile(allContextProperties.get("packageName").toString() + "." + allContextProperties.get("queryMetadataClassName").toString());
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "creating source file: " + jfo.toUri());
		final Writer writer = jfo.openWriter();
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "applying template: " + queryMetadataTemplate.getName());
		this.queryMetadataTemplate.write(new AutoIndentWriter(writer));
		writer.close();
	}

	/**
	 * Generates the {@link ProjectionMetadata} source code for the given {@code classElement} in the given target {@code packageElement}.
	 * 
	 * @param allContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	private void generateProjectionMetadataSourceCode(final Map<String, Object> allContextProperties) throws IOException {
		if (this.projectionMetadataTemplate == null) {
			processingEnv.getMessager().printMessage(
					Diagnostic.Kind.WARNING,
					"Could not create the ProjectionMetadata class for the given '" + allContextProperties.get("packageName") + "."
							+ allContextProperties.get("domainClassName") + "' class");
			return;
		}
		// fill the template with all the properties (even if we don't need them all)
		allContextProperties.keySet().stream().forEach(k -> this.projectionMetadataTemplate.add(k, allContextProperties.get(k)));
		final JavaFileObject jfo = processingEnv.getFiler().createSourceFile(allContextProperties.get("packageName").toString() + "." + allContextProperties.get("projectionMetadataClassName").toString());
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "creating source file: " + jfo.toUri());
		final Writer writer = jfo.openWriter();
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "applying template: " + queryMetadataTemplate.getName());
		this.projectionMetadataTemplate.write(new AutoIndentWriter(writer));
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
	private List<QueryFieldMetadata> getQueryFields(final TypeElement classElement) {
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
				if(documentIdAnnotation != null) {
					fields.add(new QueryFieldMetadata(variableElement, documentIdAnnotation));
				} else {
					final DocumentField documentFieldAnnotation = childElement.getAnnotation(DocumentField.class);
					fields.add(new QueryFieldMetadata(variableElement, documentFieldAnnotation));
				}
			}
		}
		return fields;
	}

	/**
	 * Builds the simple name of the {@link QueryMetadata} class associated with the given {@code typeElement}
	 * 
	 * @param typeElement
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, prefixed by
	 *         {@link LambdamaticAnnotationsProcessor#QUERY_METADATA_CLASSNAME_PREFIX}.
	 */
	public static String generateQueryMetadataSimpleClassName(final TypeElement typeElement) {
		return QUERY_METADATA_CLASSNAME_PREFIX + typeElement.getSimpleName().toString();
	}

	/**
	 * Builds the simple name of the {@link ProjectionMetadata} class associated with the given {@code typeElement}
	 * 
	 * @param typeElement
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, prefixed by
	 *         {@link LambdamaticAnnotationsProcessor#PREOJECTION_METADATA_CLASSNAME_PREFIX}.
	 */
	public static String generateProjectionMetadataSimpleClassName(final TypeElement typeElement) {
		return PREOJECTION_METADATA_CLASSNAME_PREFIX + typeElement.getSimpleName().toString();
	}
	
	/**
	 * Builds the simple name of the {@link LambdamaticMongoCollection} class associated with the given  {@code typeElement}
	 * 
	 * @param typeElement
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, followed by {@link LambdamaticAnnotationsProcessor#MONGO_COLLECTION_CLASSNAME_SUFFIX}.
	 */
	public static String generateMongoCollectionSimpleClassName(final TypeElement typeElement) {
		return typeElement.getSimpleName().toString() + MONGO_COLLECTION_CLASSNAME_SUFFIX;
	}
	
	/**
	 * Builds the simple name of the {@link LambdamaticMongoCollection} provider class associated with the given  {@code typeElement}
	 * 
	 * @param typeElement
	 *            the type element from which the name will be generated
	 * @return the simple name of the given type element, followed by {@link LambdamaticAnnotationsProcessor#MONGO_COLLECTION_PRODUCER_CLASSNAME_SUFFIX}.
	 */
	public static String generateMongoCollectionProviderSimpleClassName(final TypeElement typeElement) {
		return typeElement.getSimpleName().toString() + MONGO_COLLECTION_PRODUCER_CLASSNAME_SUFFIX;
	}
	
	/**
	 * Information about a given field that should be generated in a {@link QueryMetadata} class.
	 * 
	 * @author Xavier Coulon <xcoulon@redhat.com>
	 *
	 */
	public class QueryFieldMetadata {

		/** The java field name. */
		private final String javaFieldName;
		/** The document field name. */
		private final String documentFieldName;
		/** The java field type. */
		private final String javaFieldType;

		/**
		 * Creates a {@link QueryFieldMetadata} from a field annotated with {@link DocumentId}.
		 * @param variableElement the field element 
		 * @param documentFieldAnnotation the {@link DocumentId} annotation
		 */
		public QueryFieldMetadata(final VariableElement variableElement, final DocumentId documentIdAnnotation) {
			this.javaFieldName = getVariableName(variableElement);
			this.javaFieldType = getVariableType(variableElement);
			this.documentFieldName = DocumentCodec.MONGOBD_DOCUMENT_ID;
		}
		
		/**
		 * Creates a {@link QueryFieldMetadata} from a field optionally annotated with {@link DocumentField}.
		 * @param variableElement the field element 
		 * @param documentFieldAnnotation the optional {@link DocumentField} annotation
		 */
		public QueryFieldMetadata(final VariableElement variableElement, final DocumentField documentFieldAnnotation) {
			this.javaFieldName = getVariableName(variableElement);
			this.javaFieldType = getVariableType(variableElement);
			this.documentFieldName = getDocumentFieldName(documentFieldAnnotation, javaFieldName);
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
			if (documentFieldAnnotation != null && !documentFieldAnnotation.name().isEmpty()) {
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

	}

	/**
	 * Information about a given field that should be generated in a {@link ProjectionMetadata} class.
	 * 
	 * @author Xavier Coulon <xcoulon@redhat.com>
	 *
	 */
	public class ProjectionFieldMetadata {
		
		/** The java field name. */
		private final String javaFieldName;
		/** The document field name. */
		private final String documentFieldName;
		/** The document field name. */
		private final int value;
		
		/**
		 * 
		 * @param variableElement
		 * @param documentFieldAnnotation
		 */
		public ProjectionFieldMetadata(final VariableElement variableElement, final DocumentField documentFieldAnnotation, final int value) {
			this.javaFieldName = getVariableName(variableElement);
			this.documentFieldName = getDocumentFieldName(documentFieldAnnotation, javaFieldName);
			this.value = value;
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
			if (documentFieldAnnotation != null && !documentFieldAnnotation.name().isEmpty()) {
				return documentFieldAnnotation.name();
			}
			return defaultDocumentFieldName;
		}

		/**
		 * @return the documentFieldName
		 */
		public String getDocumentFieldName() {
			return documentFieldName;
		}

		/**
		 * @return the javaFieldName
		 */
		public String getJavaFieldName() {
			return javaFieldName;
		}
		
		/**
		 * @return the value
		 */
		public int getValue() {
			return value;
		}
	}
}

