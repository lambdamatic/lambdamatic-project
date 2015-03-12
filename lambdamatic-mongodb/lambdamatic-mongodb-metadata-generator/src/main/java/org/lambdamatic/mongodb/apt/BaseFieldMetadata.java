package org.lambdamatic.mongodb.apt;

import javax.lang.model.element.VariableElement;

import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.annotations.DocumentId;

public abstract class BaseFieldMetadata {

	public static final String MONGOBD_DOCUMENT_ID = "_id";
	
	/** The java field name. */
	protected final String javaFieldName;
	/** The document field name. */
	protected final String documentFieldName;
	/** The java field type. */
	protected final String javaFieldType;

	/**
	 * Creates a {@link QueryFieldMetadata} from a field annotated with {@link DocumentId}.
	 * 
	 * @param variableElement
	 *            the field element
	 * @param documentIdAnnotation
	 *            the {@link DocumentId} annotation
	 * @throws MetadataGenerationException 
	 */
	public BaseFieldMetadata(final VariableElement variableElement, final DocumentId documentIdAnnotation) throws MetadataGenerationException {
		this.javaFieldName = getVariableName(variableElement);
		this.javaFieldType = getVariableType(variableElement);
		this.documentFieldName = MONGOBD_DOCUMENT_ID;
	}

	/**
	 * Creates a {@link QueryFieldMetadata} from a field optionally annotated with {@link DocumentField}.
	 * 
	 * @param variableElement
	 *            the field element
	 * @param documentFieldAnnotation
	 *            the optional {@link DocumentField} annotation
	 * @throws MetadataGenerationException 
	 */
	public BaseFieldMetadata(final VariableElement variableElement, final DocumentField documentFieldAnnotation) throws MetadataGenerationException {
		this.javaFieldName = getVariableName(variableElement);
		this.javaFieldType = getVariableType(variableElement);
		this.documentFieldName = getDocumentFieldName(documentFieldAnnotation, javaFieldName);
	}

	/**
	 * Returns the {@link DocumentField#name()} value if the given {@code documentFieldAnnotation} is not null,
	 * otherwise it returns the given {@code defaultDocumentFieldName}.
	 * 
	 * @param documentFieldAnnotation
	 *            the annotation to analyze
	 * @param defaultDocumentFieldName
	 *            the default value if the given annotation was {@code null}
	 * @return the name of the field in the document
	 */
	protected String getDocumentFieldName(final DocumentField documentFieldAnnotation, final String defaultDocumentFieldName) {
		if (documentFieldAnnotation != null && !documentFieldAnnotation.name().isEmpty()) {
			return documentFieldAnnotation.name();
		}
		return defaultDocumentFieldName;
	}

	/**
	 * Returns the fully qualified name of the type of the given {@link VariableElement}, or {@code null} if it was
	 * not a known or supported type.
	 * 
	 * @param variableElement
	 *            the variable to analyze
	 * @return
	 * @throws MetadataGenerationException 
	 */
	protected abstract String getVariableType(final VariableElement variableElement) throws MetadataGenerationException;

	/**
	 * Returns the simple name of the given {@link VariableElement}
	 * 
	 * @param variableElement
	 *            the variable to analyze
	 * @return the java field name to use in the metadata class
	 */
	protected String getVariableName(final VariableElement variableElement) {
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