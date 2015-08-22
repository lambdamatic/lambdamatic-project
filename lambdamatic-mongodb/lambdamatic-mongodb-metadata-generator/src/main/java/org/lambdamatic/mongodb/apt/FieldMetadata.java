package org.lambdamatic.mongodb.apt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
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
import org.lambdamatic.mongodb.exceptions.ConversionException;
import org.lambdamatic.mongodb.metadata.LocationField;
import org.lambdamatic.mongodb.metadata.ProjectionArray;
import org.lambdamatic.mongodb.metadata.ProjectionField;
import org.lambdamatic.mongodb.metadata.ProjectionMap;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.QueryArray;
import org.lambdamatic.mongodb.metadata.QueryField;
import org.lambdamatic.mongodb.metadata.QueryMap;
import org.lambdamatic.mongodb.metadata.QueryMetadata;
import org.lambdamatic.mongodb.metadata.UpdateArray;
import org.lambdamatic.mongodb.metadata.UpdateMap;
import org.lambdamatic.mongodb.metadata.UpdateMetadata;
import org.lambdamatic.mongodb.metadata.ext.QStringArray;

public class FieldMetadata {

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
	 * @param variableElement
	 *            the field element
	 * @param documentIdAnnotation
	 *            the {@link DocumentId} annotation
	 * @throws MetadataGenerationException
	 */
	public static FieldMetadata createQueryFieldMetadata(final VariableElement variableElement, final DocumentId documentIdAnnotation)
			throws MetadataGenerationException {
		return new FieldMetadata(getMetadataFieldName(variableElement), getQueryMetadataFieldType(variableElement.asType()), MONGOBD_DOCUMENT_ID);
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
	public static FieldMetadata createQueryFieldMetadata(final VariableElement variableElement, final DocumentField documentFieldAnnotation)
			throws MetadataGenerationException {
		return new FieldMetadata(getMetadataFieldName(variableElement), getQueryMetadataFieldType(variableElement.asType()), getDocumentFieldName(documentFieldAnnotation));
	}
	
	/**
	 * Creates a {@link ProjectionFieldMetadata} from a field annotated with {@link DocumentId}.
	 * 
	 * @param variableElement
	 *            the field element
	 * @param documentIdAnnotation
	 *            the {@link DocumentId} annotation
	 * @throws MetadataGenerationException
	 */
	public static FieldMetadata createProjectionFieldMetadata(final VariableElement variableElement, final DocumentId documentIdAnnotation)
			throws MetadataGenerationException {
		return new FieldMetadata(getMetadataFieldName(variableElement), getProjectionMetadataFieldType(variableElement.asType()),
				MONGOBD_DOCUMENT_ID);
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
	public static FieldMetadata createProjectionFieldMetadata(final VariableElement variableElement, final DocumentField documentFieldAnnotation)
			throws MetadataGenerationException {
		return new FieldMetadata(getMetadataFieldName(variableElement), getProjectionMetadataFieldType(variableElement.asType()),
				getDocumentFieldName(documentFieldAnnotation));
	}

	/**
	 * Creates a {@link UpdateFieldMetadata} from a field annotated with {@link DocumentId}.
	 * 
	 * @param variableElement
	 *            the field element
	 * @param documentIdAnnotation
	 *            the {@link DocumentId} annotation
	 * @throws MetadataGenerationException
	 */
	public static FieldMetadata createUpdateFieldMetadata(final VariableElement variableElement, final DocumentId documentIdAnnotation)
			throws MetadataGenerationException {
		return new FieldMetadata(getMetadataFieldName(variableElement), getUpdateMetadataFieldType(variableElement.asType()), MONGOBD_DOCUMENT_ID);
	}

	/**
	 * Creates a {@link UpdateFieldMetadata} from a field optionally annotated with {@link DocumentField}.
	 * 
	 * @param variableElement
	 *            the field element
	 * @param documentFieldAnnotation
	 *            the optional {@link DocumentField} annotation
	 * @throws MetadataGenerationException
	 */
	public static FieldMetadata createUpdateFieldMetadata(final VariableElement variableElement, final DocumentField documentFieldAnnotation)
			throws MetadataGenerationException {
		return new FieldMetadata(getMetadataFieldName(variableElement), getUpdateMetadataFieldType(variableElement.asType()), getDocumentFieldName(documentFieldAnnotation));
	}
	
	/**
	 * Creates a {@link FieldMetadata}
	 * 
	 * @param javaFieldName
	 *            The java field name
	 * @param javaFieldType
	 *            The java field type
	 * @param documentFieldName
	 *            The document field name
	 */
	private FieldMetadata(final String javaFieldName, final FieldType javaFieldType, final String documentFieldName)
			throws MetadataGenerationException {
		this.javaFieldName = javaFieldName;
		this.javaFieldType = javaFieldType;
		this.documentFieldName = (documentFieldName != null) ? documentFieldName:javaFieldName;
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

	// *****************************************************************************************
	// utility class
	// *****************************************************************************************
	/**
	 * Returns the simple name of the given {@link VariableElement}
	 * 
	 * @param variableElement
	 *            the variable to analyze
	 * @return the java field name to use in the metadata class
	 */
	private static String getMetadataFieldName(final VariableElement variableElement) {
		return variableElement.getSimpleName().toString();
	}
	
	/**
	 * Returns the {@link DocumentField#name()} value if the given {@code documentFieldAnnotation} or <code>null</code> if no such annotation was found.
	 * 
	 * @param documentFieldAnnotation
	 *            the annotation to analyze
	 * @return the name of the field in the document
	 */
	private static String getDocumentFieldName(final DocumentField documentFieldAnnotation) {
		if (documentFieldAnnotation != null && !documentFieldAnnotation.name().isEmpty()) {
			return documentFieldAnnotation.name();
		}
		return null;
	}

	/**
	 * Returns the fully qualified name of the type of the given {@link VariableElement}, or throws a {@link MetadataGenerationException} if it was not
	 * a known or supported type.
	 * 
	 * @param variableElement
	 *            the variable to analyze
	 * @return
	 * @throws MetadataGenerationException if the given variable type is not supported
	 */
	private static FieldType getQueryMetadataFieldType(final TypeMirror variableType) throws MetadataGenerationException {
		if (variableType instanceof PrimitiveType) {
			return new FieldType(QueryField.class, ClassUtils.primitiveToWrapper(getVariableType(variableType)).getName());
		} else if (variableType instanceof DeclaredType) {
			final DeclaredType declaredType = (DeclaredType) variableType;
			final Element declaredElement = declaredType.asElement();
			// embedded documents
			if (declaredElement.getAnnotation(EmbeddedDocument.class) != null) {
				return new FieldType(getQueryMetadataType(declaredElement.toString()));
			} 
			// collections (list/set)
			else if (isCollection(declaredElement)) {
				final TypeMirror typeArgument = declaredType.getTypeArguments().get(0);
				return new FieldType(QueryArray.class, getQueryMetadataFieldType(typeArgument));
			} 
			// map
			else if (isMap(declaredElement)) {
					final TypeMirror keyTypeArgument = declaredType.getTypeArguments().get(0);
					final TypeMirror valueTypeArgument = declaredType.getTypeArguments().get(1);
					return new FieldType(QueryMap.class, keyTypeArgument.toString(), getQueryMetadataFieldType(valueTypeArgument));
			} else {
				switch (variableType.toString()) {
				case "org.lambdamatic.mongodb.types.geospatial.Location":
					return new FieldType(LocationField.class);
				default:
					return new FieldType(QueryField.class, variableType.toString());
				}
			}
		} else if (variableType.getKind() == TypeKind.ARRAY) {
			final TypeMirror componentType = ((ArrayType) variableType).getComponentType();
			final Element variableTypeElement = ((DeclaredType) componentType).asElement();
			if (variableTypeElement.getKind() == ElementKind.ENUM) {
				return new FieldType(QueryArray.class, componentType.toString());
			} else if (componentType.getAnnotation(EmbeddedDocument.class) != null) {
				throw new MetadataGenerationException("Unsupported EmbeddedDocument type: " + variableType);
				// return null; //generateQueryMetadataType(variableTypeElement);
			} else {
				return new FieldType(QueryArray.class, componentType.toString());
			}
		}
		throw new MetadataGenerationException("Unexpected variable type: " + variableType);
	}
	/**
	 * 
	 * @param element the element to analyze
	 * @return <code>true</code> if the given element is a {@link TypeElement} that implements {@link Collection}, <code>false</code> otherwise.
	 */
	//FIXME: refactor with Stream and pass the Collection.class in parameter
	private static boolean isCollection(final Element element) {
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
	 * 
	 * @param element the element to analyze
	 * @return <code>true</code> if the given element is a {@link TypeElement} that implements {@link Collection}, <code>false</code> otherwise.
	 */
	//FIXME: remove this method and use the one above with argument 'Map.class'
	private static boolean isMap(final Element element) { //, final ProcessingEnvironment processingEnvironment
		if (element instanceof TypeElement) {
			final TypeElement typeElement = (TypeElement)element;
			if(typeElement.getQualifiedName().contentEquals(Map.class.getName())) {
				return true;
			}
			final List<? extends TypeMirror> interfaceMirrors = typeElement.getInterfaces();
			for (TypeMirror interfaceMirror : interfaceMirrors) {
				if (interfaceMirror.getKind() == TypeKind.DECLARED) {
					final DeclaredType declaredInterface = (DeclaredType) interfaceMirror;
					if (declaredInterface.asElement().toString().equals(Map.class.getName())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Attempts to load the Java {@link Class} associated with the given {@link TypeMirror}. This may not be possible if the {@link TypeMirror} corresponds to a user class that has not been compiled yet, in which case the method returns <code>null</code>. 
	 * @param variableType the {@link TypeMirror} to analyze
	 * @return the Java {@link Class} or <code>null</code> if it could not be loaded.
	 */
	private static Class<?> getVariableType(final TypeMirror variableType) {
		try {
			return ClassUtils.getClass(variableType.toString());
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * @return the fully qualified name of the {@link QueryMetadata} class corresponding to the given {@link Element}
	 * @param elementTypeName the fully qualified name of the Element to use
	 */
	private static String getQueryMetadataType(final String elementTypeName) {
		final String packageName = ClassUtils.getPackageCanonicalName(elementTypeName.toString());
		final String shortClassName = Constants.QUERY_METADATA_CLASSNAME_PREFIX
				+ ClassUtils.getShortClassName(elementTypeName.toString());
		return packageName + '.' + shortClassName;
	}

	/**
	 * @return the fully qualified name of the {@link QueryMetadata} class corresponding to the given {@link Element}
	 * @param elementTypeName the fully qualified name of the Element to use
	 */
	private static String getQueryArrayMetadataType(final String elementTypeName) {
		switch (elementTypeName) {
		//FIXME: implements other base types
		case "java.lang.String":
			return QStringArray.class.getName();
		default:
			final String packageName = ClassUtils.getPackageCanonicalName(elementTypeName.toString());
			final String shortClassName = Constants.QUERY_METADATA_CLASSNAME_PREFIX
					+ ClassUtils.getShortClassName(elementTypeName.toString()) + Constants.QUERY_ARRAY_METADATA_CLASSNAME_SUFFIX;
			return packageName + '.' + shortClassName;
		}
	}

	/**
	 * @return the fully qualified name of the {@link QueryMetadata} class corresponding to the given {@link Element}
	 * @param keyTypeName the fully qualified name of the Key to use
	 * @param valueTypeName the fully qualified name of the Value to use
	 */
	private static String getQueryMapMetadataType(final String keyTypeName, final String valueTypeName) {
			final String packageName = ClassUtils.getPackageCanonicalName(keyTypeName.toString());
			final String shortClassName = Constants.QUERY_METADATA_CLASSNAME_PREFIX
					+ ClassUtils.getShortClassName(keyTypeName.toString()) + Constants.QUERY_ARRAY_METADATA_CLASSNAME_SUFFIX;
			return packageName + '.' + shortClassName;
	}
	
	/**
	 * Returns the fully qualified name of the type of the given {@link VariableElement}, or {@code null} if it was not
	 * a known or supported type.
	 * 
	 * @param variableType
	 *            the variable type to analyze
	 * @return
	 * @throws MetadataGenerationException
	 */
	private static FieldType getProjectionMetadataFieldType(final TypeMirror variableType) throws MetadataGenerationException {
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
				// using Query metadata field type to allow for nested lambda expressions
				final FieldType queryFieldMetadata = getQueryMetadataFieldType(typeArgument);
				return new FieldType(ProjectionArray.class, queryFieldMetadata);
			}
			// map
			else if (isMap(declaredElement)) {
				final TypeMirror keyArgument = declaredType.getTypeArguments().get(0);
				final TypeMirror valueArgument = declaredType.getTypeArguments().get(1);
				// using Query metadata field type to allow for nested lambda expressions
				final FieldType projectionFieldMetadata = getQueryMetadataFieldType(valueArgument);
				return new FieldType(ProjectionMap.class, keyArgument.toString(), projectionFieldMetadata);
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
				return new FieldType(ProjectionArray.class, getQueryMetadataFieldType(componentType));
			}
		}
		throw new MetadataGenerationException("Unexpected variable type: " + variableType);
	}

	/**
	 * @return the fully qualified name of the {@link ProjectionMetadata} class corresponding to the given
	 *         {@link Element}.
	 * @param variableTypeElement
	 *            the Element to use
	 */
	private static String getProjectionMetadataType(final Element variableTypeElement) {
		final String packageName = ClassUtils.getPackageCanonicalName(variableTypeElement.toString());
		final String shortClassName = Constants.PROJECTION_METADATA_CLASSNAME_PREFIX
				+ ClassUtils.getShortClassName(variableTypeElement.toString());
		return packageName + '.' + shortClassName;
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
	private static FieldType getUpdateMetadataFieldType(final TypeMirror variableType) throws MetadataGenerationException {
		if (variableType instanceof PrimitiveType) {
			return new FieldType(getSimilarDeclaredType((PrimitiveType) variableType));
		} else if (variableType instanceof DeclaredType) {
			final DeclaredType declaredType = (DeclaredType) variableType;
			final Element declaredElement = declaredType.asElement();
				// embedded documents
				if (declaredElement.getAnnotation(EmbeddedDocument.class) != null) {
					return new FieldType(getUpdateMetadataType(declaredElement.toString()));
				} 
				// collections (list/set)
				else if (isCollection(declaredElement)) {
					final TypeMirror typeArgument = declaredType.getTypeArguments().get(0);
					return new FieldType(UpdateArray.class, typeArgument.toString());
				} 
				// map
				else if (isMap(declaredElement)) {
						final TypeMirror keyArgument = declaredType.getTypeArguments().get(0);
						final TypeMirror valueArgument = declaredType.getTypeArguments().get(1);
						return new FieldType(UpdateMap.class, keyArgument.toString(), valueArgument.toString());
				} else {
					switch (variableType.toString()) {
					case "org.lambdamatic.mongodb.types.geospatial.Location":
						return new FieldType(LocationField.class);
					default:
						return new FieldType(variableType.toString());
					}
				}
			
		} else if (variableType.getKind() == TypeKind.ARRAY) {
			final TypeMirror componentType = ((ArrayType) variableType).getComponentType();
			final Element variableTypeElement = ((DeclaredType) componentType).asElement();
			if (variableTypeElement.getKind() == ElementKind.ENUM) {
				return new FieldType(componentType.toString());
			} else if (componentType.getAnnotation(EmbeddedDocument.class) != null) {
				throw new MetadataGenerationException("Unsupported EmbeddedDocument type: " + variableType);
				// return null; //generateUpdateMetadataType(variableTypeElement);
			} else {
				return new FieldType(componentType.toString());
			}
		}
		throw new MetadataGenerationException("Unexpected variable type: " + variableType);
	}

	/**
	 * @return the fully qualified name of the {@link UpdateMetadata} class corresponding to the given {@link Element}
	 * @param elementTypeName the fully qualified name of the Element to use
	 */
	private static String getUpdateMetadataType(final String elementTypeName) {
		final String packageName = ClassUtils.getPackageCanonicalName(elementTypeName.toString());
		final String shortClassName = Constants.UPDATE_METADATA_CLASSNAME_PREFIX
				+ ClassUtils.getShortClassName(elementTypeName.toString());
		return packageName + '.' + shortClassName;
	}

	/**
	 * @return the fully qualified name of the {@link UpdateMetadata} class corresponding to the given {@link Element}
	 * @param elementTypeName the fully qualified name of the Element to use
	 */
	private static String getUpdateArrayMetadataType(final String elementTypeName) {
		switch (elementTypeName) {
		//FIXME: implements other base types, or use generics ?
		case "java.lang.String":
			return QStringArray.class.getName();
		default:
			final String packageName = ClassUtils.getPackageCanonicalName(elementTypeName.toString());
			final String shortClassName = Constants.UPDATE_METADATA_CLASSNAME_PREFIX
					+ ClassUtils.getShortClassName(elementTypeName.toString()) + Constants.UPDATE_ARRAY_METADATA_CLASSNAME_SUFFIX;
			return packageName + '.' + shortClassName;
		}
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
	
	@Override
	public String toString() {
		return "@documentField(\"" + this.documentFieldName + "\") " + this.javaFieldType + " " + this.javaFieldName;
	}
}