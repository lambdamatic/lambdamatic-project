package org.lambdamatic.mongodb.apt;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.apache.commons.lang3.ClassUtils;
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
	 * @param variableElement
	 *            the field element
	 * @param documentIdAnnotation
	 *            the {@link DocumentId} annotation
	 * @throws MetadataGenerationException
	 */
	public BaseFieldMetadata(final VariableElement variableElement, final DocumentId documentIdAnnotation)
			throws MetadataGenerationException {
		this.javaFieldName = getMetadataFieldName(variableElement);
		this.javaFieldType = getMetadataFieldType(variableElement.asType());
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
	public BaseFieldMetadata(final VariableElement variableElement, final DocumentField documentFieldAnnotation)
			throws MetadataGenerationException {
		this.javaFieldName = getMetadataFieldName(variableElement);
		this.documentFieldName = getDocumentFieldName(documentFieldAnnotation, javaFieldName);
		this.javaFieldType = getMetadataFieldType(variableElement.asType());
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
	protected String getDocumentFieldName(final DocumentField documentFieldAnnotation,
			final String defaultDocumentFieldName) {
		if (documentFieldAnnotation != null && !documentFieldAnnotation.name().isEmpty()) {
			return documentFieldAnnotation.name();
		}
		return defaultDocumentFieldName;
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
	protected abstract FieldType getMetadataFieldType(final TypeMirror variableType) throws MetadataGenerationException;

	/**
	 * Returns the simple name of the given {@link VariableElement}
	 * 
	 * @param variableElement
	 *            the variable to analyze
	 * @return the java field name to use in the metadata class
	 */
	protected String getMetadataFieldName(final VariableElement variableElement) {
		// FIXME: should check for @DocumentField annotation value here ?
		return variableElement.getSimpleName().toString();
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

	/**
	 * A field type to be used in the class templates. Provides the simple Java type name along with all types to
	 * declare as import statements.
	 * <p>
	 * Eg: <code>List&lt;Foo&gt;</code> with <code>java.util.List, com.sample.Foo</code>
	 * </p>
	 * 
	 * @author Xavier Coulon <xcoulon@redhat.com>
	 *
	 */
	public static class FieldType {

		/** the simple name of the Java field type. */
		private final String simpleName;

		/** all Java types to declare in the imports. */
		private final Set<String> requiredTypes = new HashSet<>();

		/**
		 * Constructor for a simple type
		 * 
		 * @param javaType
		 *            the Java field type.
		 */
		public FieldType(final Class<?> javaType) {
			this(javaType, Collections.emptyList());
		}

		/**
		 * Constructor for a parameterized type
		 * 
		 * @param javaType
		 *            the Java field type.
		 * @param parameterTypes
		 *            the Java parameter types.
		 */
		public FieldType(final Class<?> javaType, final Object... parameterTypes) {
			this(javaType, Arrays.asList(parameterTypes));
		}

		/**
		 * Constructor for a parameterized type
		 * 
		 * @param javaType
		 *            the Java field type.
		 * @param parameterTypes
		 *            the Java parameter types.
		 */
		public FieldType(final Class<?> javaType, final Collection<Object> parameterTypes) {
			final StringBuilder simpleNameBuilder = new StringBuilder();
			simpleNameBuilder.append(javaType.getSimpleName());
			this.requiredTypes.add(javaType.getName());
			if (!parameterTypes.isEmpty()) {
				simpleNameBuilder.append('<');
				parameterTypes.stream().forEach(p -> {
					if (p instanceof String) {
						final String fullyQualifiedName = (String) p;
						simpleNameBuilder.append(ClassUtils.getShortClassName(fullyQualifiedName)).append(", ");
						this.requiredTypes.add(fullyQualifiedName);
					} else if (p instanceof FieldType) {
						final FieldType fieldType = (FieldType) p;
						simpleNameBuilder.append(fieldType.getSimpleName()).append(", ");
						this.requiredTypes.addAll(fieldType.getRequiredTypes());
					}
				} );
				// little hack: there's an extra ", " sequence that needs to be remove from the simpleNameBuilder
				simpleNameBuilder.delete(simpleNameBuilder.length() - 2, simpleNameBuilder.length());
				simpleNameBuilder.append('>');
			}
			this.simpleName = simpleNameBuilder.toString();
		}

		/**
		 * Constructor for a parameterized type
		 * 
		 * @param javaTypeName
		 *            the fully qualified name of the Java field type.
		 */
		public FieldType(final String javaTypeName) {
			this.simpleName = ClassUtils.getShortCanonicalName(javaTypeName);
			this.requiredTypes.add(javaTypeName);
		}

		/**
		 * @return the simple name of the Java field type.
		 */
		public String getSimpleName() {
			return simpleName;
		}

		/**
		 * @return all the Java types to declare in the imports
		 */
		public Set<String> getRequiredTypes() {
			return requiredTypes;
		}
	}

}