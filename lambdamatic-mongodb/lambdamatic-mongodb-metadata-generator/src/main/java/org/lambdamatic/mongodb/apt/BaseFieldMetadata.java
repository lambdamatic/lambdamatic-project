package org.lambdamatic.mongodb.apt;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.annotations.DocumentId;

public abstract class BaseFieldMetadata {

	public static final String MONGOBD_DOCUMENT_ID = "_id";

	/** The java field name. */
	protected final String javaFieldName;
	/** The document field name. */
	protected final String documentFieldName;
	/** The java field type. */
	protected final FieldType javaFieldType;

	/**
	 * Creates a {@link QueryFieldMetadata} from a field annotated with {@link DocumentId}.
	 * 
	 * @param javaFieldName
	 *            The java field name
	 * @param javaFieldType
	 *            The java field type
	 * @param documentFieldName
	 *            The document field name
	 */
	public BaseFieldMetadata(final String javaFieldName, final FieldType javaFieldType, final String documentFieldName)
			throws MetadataGenerationException {
		this.javaFieldName = javaFieldName;
		this.javaFieldType = javaFieldType;
		this.documentFieldName = (documentFieldName != null) ? documentFieldName:javaFieldName;
	}

	/**
	 * Returns the {@link DocumentField#name()} value if the given {@code documentFieldAnnotation} or <code>null</code> if no such annotation was found.
	 * 
	 * @param documentFieldAnnotation
	 *            the annotation to analyze
	 * @return the name of the field in the document
	 */
	protected static String getDocumentFieldName(final DocumentField documentFieldAnnotation) {
		if (documentFieldAnnotation != null && !documentFieldAnnotation.name().isEmpty()) {
			return documentFieldAnnotation.name();
		}
		return null;
	}

	/**
	 * Returns the simple name of the given {@link VariableElement}
	 * 
	 * @param variableElement
	 *            the variable to analyze
	 * @return the java field name to use in the metadata class
	 */
	protected static String getMetadataFieldName(final VariableElement variableElement) {
		return variableElement.getSimpleName().toString();
	}
	
	/**
	 * 
	 * @param element the element to analyze
	 * @return <code>true</code> if the given element is a {@link TypeElement} that implements {@link Collection}, <code>false</code> otherwise.
	 */
	protected static boolean isCollection(final Element element) { //, final ProcessingEnvironment processingEnvironment
		if (element instanceof TypeElement) {
			final List<? extends TypeMirror> interfaceMirrors = ((TypeElement) element).getInterfaces();
			for (TypeMirror interfaceMirror : interfaceMirrors) {
				if (interfaceMirror.getKind() == TypeKind.DECLARED) {
					final DeclaredType declaredInterface = (DeclaredType) interfaceMirror;
					if (declaredInterface.asElement().toString().equals(Collection.class.getName())) {
						return true;
					}
				}
			}
		}
		return false;
	}



	/**
	 * @return the document field name.
	 */
	public String getDocumentFieldName() {
		return documentFieldName;
	}

	/**
	 * @return the Java field name.
	 */
	public String getJavaFieldName() {
		return javaFieldName;
	}

	/**
	 * @return the Java field type.
	 */
	public FieldType getJavaFieldType() {
		return javaFieldType;
	}

	/**
	 * @return the required Java types (to include in the import statements)
	 */
	public Set<String> getRequiredJavaTypes() {
		return this.javaFieldType.getRequiredTypes();
	}

}