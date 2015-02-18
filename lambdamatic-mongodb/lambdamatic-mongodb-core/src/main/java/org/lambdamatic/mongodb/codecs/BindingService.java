/**
 * 
 */
package org.lambdamatic.mongodb.codecs;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.bson.BsonDocument;
import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.annotations.DocumentId;

import com.mongodb.DBObject;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class BindingService {

	/**
	 * Cache of bindings to convert domain class instances to/from {@link BsonDocument}s.
	 */
	private final Map<Class<?>, Map<String, Field>> bindings = new HashMap<Class<?>, Map<String,Field>>();

	/**
	 * Checks if the given binding is a {@link DocumentId} binding.
	 * @param binding
	 * @return {@code true} if the given field is annotated with {@link DocumentId}, {@code false} otherwise. 
	 */
	public boolean isIdBinding(final Entry<String, Field> binding) {
		return binding.getValue().getAnnotation(DocumentId.class) != null;
	}
	
	/**
	 * Retrieves the value of the given {@link Field} in the given {@code domainObject}
	 * @param domainObject the object to analyze
	 * @param field the target {@link Field}
	 * @return the field value or {@code null} if it was not set
	 * @throws ConversionException 
	 */
	public Object getFieldValue(final Object domainObject, final Field domainField) {
		domainField.setAccessible(true);
		try {
			return domainField.get(domainObject);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new ConversionException("Failed to retrieve value for field '" + domainField.getName()
					+ "' in domain object '" + domainObject + "'", e);
		}
	}

	/**
	 * Analyzes and puts in a memory map the bindings to use when converting an
	 * incoming {@link DBObject} into an instance of {@link targetClass}. The
	 * bindings is a {@link Map} of {@link DocumentField} in the
	 * {@link targetClass} indexed by the name of the {@link key} )in the
	 * incoming {@link DBObject}.
	 * 
	 * @param targetClass
	 *            the {@link Class} for which the {@link Map} bindings should be
	 *            returned
	 * @return the {@link Map} of bindings for the given {@link targetClass}
	 */
	 public Map<String, Field> getBindings(final Class<?> targetClass) {
		if (!bindings.containsKey(targetClass)) {
			final Map<String, Field> classBindings = new TreeMap<>();
			// let's analyze the class' declared fields
			// TODO: verify that inherited private fields are also supported
			for (Field field : targetClass.getDeclaredFields()) {
				final DocumentId documentIdAnnotation = field.getAnnotation(DocumentId.class);
				if (documentIdAnnotation != null) {
					classBindings.put("_id", field);
					continue;
				}
				final DocumentField documentFieldAnnotation = field.getAnnotation(DocumentField.class);
				if (documentFieldAnnotation != null && documentFieldAnnotation.name().isEmpty()) {
					classBindings.put(field.getName(), field);
				} else if (documentFieldAnnotation != null && !documentFieldAnnotation.name().isEmpty()) {
					classBindings.put(documentFieldAnnotation.name(), field);
				} 
				// custom requirement: ignore Jacoco/EclEmma fields introduced at for code coverage 
				else if(!field.getName().startsWith("$jacoco")) {
					classBindings.put(field.getName(), field);
				}
			}
			bindings.put(targetClass, classBindings);
		}
		return Collections.unmodifiableMap(bindings.get(targetClass));
	}

	/**
	 * @param targetClass
	 * @return the {@link Field} annotated with {@link DocumentField} with {@code name} attribute equals {@link DocumentCodec#MONGOBD_DOCUMENT_ID}.
	 * @throws ConversionException if none was found.
	 */
	public Field getIdBinding(final Class<?> targetClass) {
		final Map<String, Field> targetClassBindings = getBindings(targetClass);
		final Optional<Field> idBinding = targetClassBindings.values().stream()
				.filter(f -> f.getAnnotation(DocumentField.class).name().equals(DocumentCodec.MONGOBD_DOCUMENT_ID))
				.findFirst();
		if (idBinding.isPresent()) {
			return idBinding.get();
		}
		throw new ConversionException("Could not find binding for field annotated with '"
				+ DocumentCodec.MONGOBD_DOCUMENT_ID + "' for class " + targetClass.getName());
	}
}
