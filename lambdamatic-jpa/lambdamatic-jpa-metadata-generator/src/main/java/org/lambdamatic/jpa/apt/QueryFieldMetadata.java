package org.lambdamatic.jpa.apt;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.persistence.Basic;
import javax.persistence.Id;

import org.apache.commons.lang3.ClassUtils;
import org.lambdamatic.jpa.ConversionException;
import org.lambdamatic.jpa.QueryField;
import org.lambdamatic.jpa.QueryMetadata;

/**
 * Information about a given field that should be generated in a {@link QueryMetadata} class.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class QueryFieldMetadata extends BaseFieldMetadata {

	/**
	 * Creates a {@link QueryFieldMetadata} from a field annotated with {@link DocumentId}.
	 * 
	 * @param variableElement
	 *            the field element
	 * @param idAnnotation
	 *            the {@link DocumentId} annotation
	 * @throws MetadataGenerationException
	 */
	public QueryFieldMetadata(final VariableElement variableElement, final Id idAnnotation)
			throws MetadataGenerationException {
		super(getMetadataFieldName(variableElement), getMetadataFieldType(variableElement.asType()), MONGOBD_DOCUMENT_ID);
	}

	/**
	 * Creates a {@link QueryFieldMetadata} from a field optionally annotated with {@link DocumentField}.
	 * 
	 * @param variableElement
	 *            the field element
	 * @param basicAnnotation
	 *            the optional {@link DocumentField} annotation
	 * @throws MetadataGenerationException
	 */
	public QueryFieldMetadata(final VariableElement variableElement, final Basic basicAnnotation)
			throws MetadataGenerationException {
		super(getMetadataFieldName(variableElement), getMetadataFieldType(variableElement.asType()), getDocumentFieldName(basicAnnotation));
	}

	/**
	 * Returns the fully qualified name of the type of the given {@link VariableElement}, or {@code null} if it was not
	 * a known or supported type.
	 * 
	 * @param variableElement
	 *            the variable to analyze
	 * @return
	 * @throws MetadataGenerationException
	 */
	protected static FieldType getMetadataFieldType(final TypeMirror variableType) throws MetadataGenerationException {
		if (variableType instanceof PrimitiveType) {
			return new FieldType(QueryField.class, getSimilarDeclaredType((PrimitiveType) variableType).getName());
		} else if (variableType instanceof DeclaredType) {
			final DeclaredType declaredType = (DeclaredType) variableType;
			return new FieldType(QueryField.class, variableType.toString());
		}
		throw new MetadataGenerationException("Unexpected variable type: " + variableType);
	}

	/**
	 * @return the fully qualified name of the {@link QueryMetadata} class corresponding to the given {@link Element}
	 * @param elementTypeName the fully qualified name of the Element to use
	 */
	public static String getQueryMetadataType(final String elementTypeName) {
		final String packageName = ClassUtils.getPackageCanonicalName(elementTypeName.toString());
		final String shortClassName = Constants.QUERY_METADATA_CLASSNAME_PREFIX
				+ ClassUtils.getShortClassName(elementTypeName.toString());
		return packageName + '.' + shortClassName;
	}

	private static Class<?> getSimilarDeclaredType(final PrimitiveType variableType) {
		final TypeKind variableTypeKind = variableType.getKind();
		switch (variableTypeKind) {
		case BOOLEAN:
			return Boolean.class;
		case BYTE:
			return Byte.class;
		case SHORT:
			return Short.class;
		case INT:
			return Integer.class;
		case LONG:
			return Long.class;
		case FLOAT:
			return Float.class;
		case DOUBLE:
			return Double.class;
		case CHAR:
			return Character.class;
		default:
			throw new ConversionException(
					"Failed to provide a declared type equivalent to '" + variableTypeKind.name().toLowerCase() + "'");
		}
	}

}