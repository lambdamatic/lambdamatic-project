package org.lambdamatic.mongodb.apt.template;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.apache.commons.lang3.ClassUtils;
import org.lambdamatic.mongodb.exceptions.ConversionException;

public class ElementUtils {

	private ElementUtils() {
	}

	/**
	 * Returns the value of the given annotation using a Function.
	 * 
	 * @param annotation
	 *            the annotation to analyze
	 * @param valueRetriever
	 *            the retrieval {@link Function} to apply if the given {@link Annotation} is not null.
	 * @param defaultValue
	 *            the default value to return if the annotation is null or if the given Function did not return any
	 *            value.
	 * @return the annotation value or <code>null</code> if it is empty or null.
	 */
	public static <T extends Annotation, R> R getAnnotationValue(final T annotation,
			final Function<T, R> valueRetriever, final R defaultValue) {
		if (annotation != null) {
			final R value = valueRetriever.apply(annotation);
			// do not allow empty String (if applicable)
			if (defaultValue.getClass().equals(String.class) && !String.class.cast(value).isEmpty()) {
				return value;
			} else if (!defaultValue.getClass().equals(String.class) && value != null) {
				return value;
			}
		}
		return defaultValue;
	}

	/**
	 * Checks if the given {@link Element} corresponds to a {@link List} type.
	 * 
	 * @param element
	 *            the element to analyze
	 * @return <code>true</code> if the given {@link Element} corresponds to a type that implements {@link List},
	 *         <code>false</code> otherwise.
	 */
	// FIXME: refactor with Stream and pass the Collection.class in parameter
	public static boolean isList(final TypeElement element) {
		if (element.toString().equals(List.class.getName())) {
			return true;
		}
		final List<? extends TypeMirror> interfaceMirrors = ((TypeElement) element).getInterfaces();
		for (TypeMirror interfaceMirror : interfaceMirrors) {
			if (interfaceMirror.getKind() == TypeKind.DECLARED) {
				final DeclaredType declaredInterface = (DeclaredType) interfaceMirror;
				if (declaredInterface.asElement().toString().equals(List.class.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if the given {@link Element} corresponds to a {@link Collection} type.
	 * 
	 * @param element
	 *            the element to analyze
	 * @return <code>true</code> if the given {@link Element} corresponds to a type that implements {@link Collection},
	 *         <code>false</code> otherwise.
	 */
	// FIXME: refactor with Stream and pass the Collection.class in parameter
	public static boolean isCollection(final TypeElement element) {
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
	 * @param element
	 *            the element to analyze
	 * @return <code>true</code> if the given element is a {@link TypeElement} that implements {@link Collection},
	 *         <code>false</code> otherwise.
	 */
	// FIXME: remove this method and use the one above with argument 'Map.class'
	public static boolean isMap(final TypeElement element) { // , final ProcessingEnvironment processingEnvironment
		if (element instanceof TypeElement) {
			final TypeElement typeElement = (TypeElement) element;
			if (typeElement.getQualifiedName().contentEquals(Map.class.getName())) {
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
	 * Attempts to load the Java {@link Class} associated with the given {@link TypeMirror}. This may not be possible if
	 * the {@link TypeMirror} corresponds to a user class that has not been compiled yet, in which case the method
	 * returns <code>null</code>.
	 * 
	 * @param variableType
	 *            the {@link TypeMirror} to analyze
	 * @return the Java {@link Class} or <code>null</code> if it could not be loaded.
	 */
	public static Class<?> getVariableType(final TypeMirror variableType) {
		try {
			return ClassUtils.getClass(variableType.toString());
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * Attempts to load the Java {@link Class} associated with the given {@code className}. This may not be possible if
	 * the {@link TypeMirror} corresponds to a user class that has not been compiled yet, in which case the method
	 * returns <code>null</code>.
	 * 
	 * @param variableType
	 *            the {@link TypeMirror} to analyze
	 * @return the Java {@link Class} or <code>null</code> if it could not be loaded.
	 */
	public static Class<?> getVariableType(final String className) {
		try {
			return ClassUtils.getClass(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static Class<?> getSimilarDeclaredType(final PrimitiveType variableType) {
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
