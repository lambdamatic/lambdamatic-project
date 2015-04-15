	package org.lambdamatic.mongodb.apt;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.apache.commons.lang3.ClassUtils;
import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.annotations.DocumentId;
import org.lambdamatic.mongodb.annotations.EmbeddedDocument;
import org.lambdamatic.mongodb.metadata.ProjectionArray;
import org.lambdamatic.mongodb.metadata.ProjectionField;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;

/**
 * Information about a given field that should be generated in a ProjectionMetadata class.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class ProjectionFieldMetadata extends BaseFieldMetadata {

	/** Suffix to use for the generated {@link ProjectionMetadata} classes. */
	public static String PROJECTION_METADATA_CLASSNAME_PREFIX = "P";

	/**
	 * Creates a {@link ProjectionFieldMetadata} from a field annotated with {@link DocumentId}.
	 * 
	 * @param variableElement
	 *            the field element
	 * @param documentIdAnnotation
	 *            the {@link DocumentId} annotation
	 * @throws MetadataGenerationException 
	 */
	public ProjectionFieldMetadata(final VariableElement variableElement, final DocumentId documentIdAnnotation) throws MetadataGenerationException {
		super(getMetadataFieldName(variableElement), getMetadataFieldType(variableElement.asType()), MONGOBD_DOCUMENT_ID);
	}

	/**
	 * Creates a {@link ProjectionFieldMetadata} from a field optionally annotated with {@link DocumentField}.
	 * 
	 * @param variableElement
	 *            the field element
	 * @param documentFieldAnnotation
	 *            the optional {@link DocumentField} annotation
	 * @throws MetadataGenerationException 
	 */
	public ProjectionFieldMetadata(final VariableElement variableElement, final DocumentField documentFieldAnnotation) throws MetadataGenerationException {
		super(getMetadataFieldName(variableElement), getMetadataFieldType(variableElement.asType()), getDocumentFieldName(documentFieldAnnotation));
	}
	
	/**
	 * Returns the fully qualified name of the type of the given {@link VariableElement}, or {@code null} if it was
	 * not a known or supported type.
	 * 
	 * @param variableType
	 *            the variable type to analyze
	 * @return
	 * @throws MetadataGenerationException 
	 */
	protected static FieldType getMetadataFieldType(final TypeMirror variableType) throws MetadataGenerationException {
		if (variableType instanceof PrimitiveType) {
			return new FieldType(ProjectionField.class);
		} else if (variableType instanceof DeclaredType) {
			final DeclaredType declaredType = (DeclaredType) variableType;
			final Element declaredElement = declaredType.asElement();
			// embedded documents
			if (declaredElement.getAnnotation(EmbeddedDocument.class) != null) {
				return new FieldType(getProjectionMetadataType(declaredElement));
			} 
			// collections (list/set)
			else if (isCollection(declaredElement)) {
				final TypeMirror typeArgument = declaredType.getTypeArguments().get(0);
				final FieldType queryFieldMetadata = QueryFieldMetadata.getMetadataFieldType(typeArgument);
				return new FieldType(ProjectionArray.class, queryFieldMetadata);
			} 
			// other types (primitives, Enum, String, Date, etc.)
			else {
				return new FieldType(ProjectionField.class);
			}
		} else if (variableType.getKind() == TypeKind.ARRAY) {
			final TypeMirror componentType = ((ArrayType) variableType).getComponentType();
			if (componentType.getAnnotation(EmbeddedDocument.class) != null) {
				throw new MetadataGenerationException("Unsupported EmbeddedDocument type: " + variableType);
				// return null; //generateQueryMetadataType(variableTypeElement);
			} else {
				return new FieldType(ProjectionArray.class, QueryFieldMetadata.getMetadataFieldType(componentType));
			}
		}
		throw new MetadataGenerationException("Unexpected variable type: " + variableType);
	}
	
	/**
	 * @return the fully qualified name of the {@link ProjectionMetadata} class corresponding to the given {@link Element}
	 * @param variableTypeElement the Element to use
	 */
	public static String getProjectionMetadataType(final Element variableTypeElement) {
		final String packageName = ClassUtils.getPackageCanonicalName(variableTypeElement.toString());
		final String shortClassName = PROJECTION_METADATA_CLASSNAME_PREFIX
				+ ClassUtils.getShortClassName(variableTypeElement.toString());
		return packageName + '.' + shortClassName;
	}

}