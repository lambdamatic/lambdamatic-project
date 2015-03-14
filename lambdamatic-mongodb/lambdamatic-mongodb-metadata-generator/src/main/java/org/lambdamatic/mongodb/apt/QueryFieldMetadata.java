	package org.lambdamatic.mongodb.apt;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;

import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.annotations.DocumentId;
import org.lambdamatic.mongodb.annotations.EmbeddedDocument;
import org.lambdamatic.mongodb.metadata.LocationField;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.QueryField;

/**
 * Information about a given field that should be generated in a {@link ProjectionMetadata} class.
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
	 * @param documentIdAnnotation
	 *            the {@link DocumentId} annotation
	 * @throws MetadataGenerationException 
	 */
	public QueryFieldMetadata(final VariableElement variableElement, final DocumentId documentIdAnnotation) throws MetadataGenerationException {
		super(variableElement, documentIdAnnotation);
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
	public QueryFieldMetadata(final VariableElement variableElement, final DocumentField documentFieldAnnotation) throws MetadataGenerationException {
		super(variableElement, documentFieldAnnotation);
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
	// FIXME: should either use fully qualified names or add import declarations, especially in case of
	// domain classes in other packages
	@Override
	protected String getVariableType(final VariableElement variableElement) throws MetadataGenerationException {
		// try {
		final TypeMirror variableType = variableElement.asType();
		if (variableType instanceof PrimitiveType) {
			return variableType.getKind().name().toLowerCase();
		} else if (variableType instanceof DeclaredType) {
			final Element variableTypeElement = ((DeclaredType) variableType).asElement();
			if (variableTypeElement.getKind() == ElementKind.ENUM) {
				return variableTypeElement.toString();
			} else if (variableTypeElement.getAnnotation(EmbeddedDocument.class) != null) {
				return EmbeddedDocumentAnnotationProcessor.generateQueryMetadataSimpleClassName(variableTypeElement);
			}
			switch (variableType.toString()) {
			case "org.lambdamatic.mongodb.types.geospatial.Location":
				return LocationField.class.getName();
			default:
				return QueryField.class.getName() + '<' + variableType.toString() + '>' ;
			}
		}
		throw new MetadataGenerationException(
				"Unexpected variable type for '" + variableElement.getSimpleName() + "' : " + variableType);
	}


}