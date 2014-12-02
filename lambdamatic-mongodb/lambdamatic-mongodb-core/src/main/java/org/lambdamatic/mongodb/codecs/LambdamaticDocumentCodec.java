/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.codecs;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;
import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.annotations.DocumentId;

import com.mongodb.DBObject;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class LambdamaticDocumentCodec<T> implements Codec<T> {

	/**
	 * Internal cache of bindings to convert domain class instances with
	 * incoming DBObjects.
	 */
	private static final Map<Class<?>, Map<String, Field>> globalBindings = new HashMap<>();

	/** Name of the field used to store the document id in MongoDB. */
	public static final String MONGOBD_DOCUMENT_ID = "_id";

	/**
	 * Name of the field used to handle the Java type associated with a document
	 * in MongoDB.
	 */
	public static final String TARGET_CLASS_FIELD = "_targetClass";

	/** The user-defined domain class associated with this Codec. */
	private final Class<T> targetClass;

	/**
	 * Constructor
	 * 
	 * @param targetClass
	 *            the domain class supported by this {@link Codec}.
	 */
	public LambdamaticDocumentCodec(final Class<T> targetClass) {
		this.targetClass = targetClass;
	}

	@Override
	public Class<T> getEncoderClass() {
		return targetClass;
	}
	
	
	/**
	 * Encodes the given {@code domainObject}, putting the {@code _id} attribute first, followed by {@code _targetClassName} to 
	 * be able to decode the BSON/JSON document later, followed by the other annotated Java attributes.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void encode(final BsonWriter writer, final T domainObject, final EncoderContext encoderContext) {
		try {
			writer.writeStartDocument();
			final Map<String, Field> bindings = getBindings(domainObject.getClass());
			// write the "_id" attribute first
			final Optional<Entry<String, Field>> idBinding = bindings.entrySet().stream().filter(e -> isIdBinding(e)).findFirst();
			if(idBinding.isPresent()) {
				final Object idValue = getBindingValue(domainObject, idBinding.get().getValue());
				if(idValue == null) {
					final ObjectId generatedIdValue = new ObjectId();
					idBinding.get().getValue().set(domainObject, generatedIdValue);
					writeValue(writer, MONGOBD_DOCUMENT_ID, generatedIdValue);	
				} else {
					writeValue(writer, MONGOBD_DOCUMENT_ID, idValue);	
				}
			}
			// write the technical/inner "_targetClassName" attribute
			writer.writeString(TARGET_CLASS_FIELD, domainObject.getClass().getName());
			// write other attributes
			bindings.entrySet().stream().filter(e -> isIdBinding(e)).forEach(
					binding -> {
						final Object bindingValue = getBindingValue(domainObject, binding.getValue());
						if(bindingValue != null) {
							writeValue(writer, MONGOBD_DOCUMENT_ID, bindingValue);
						}
					});
			writer.writeEndDocument();
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new ConversionException("Failed to convert following domain object to BSON document: " + domainObject, e);
		} finally {
			writer.flush();
		}
	}

	/**
	 * @param writer
	 */
	private void writeValue(final BsonWriter writer, final String attributeName, final Object attributeValue) {
		if (attributeValue.getClass().isEnum()) {
			writer.writeString(attributeName, ((Enum<?>) attributeValue).name());
		} else if (attributeValue instanceof String) {
			writer.writeString(attributeName, (String) attributeValue);
		} else if (attributeValue instanceof ObjectId) {
			writer.writeString(attributeName, attributeValue.toString());
		}
	}

	/**
	 * Checks if the given binding is a {@link DocumentId} binding.
	 * @param binding
	 * @return {@code true} if the given field is annotated with {@link DocumentId}, {@code false} otherwise. 
	 */
	private static boolean isIdBinding(final Entry<String, Field> binding) {
		return binding.getValue().getAnnotation(DocumentId.class) != null;
	}

	/**
	 * Retrieves the value of the given {@link Field} in the given {@code domainObject}
	 * @param domainObject the object to analyze
	 * @param field the target {@link Field}
	 * @return the field value or {@code null} if it was not set
	 * @throws ConversionException 
	 */
	private Object getBindingValue(final T domainObject, final Field domainInstanceField) {
		domainInstanceField.setAccessible(true);
		try {
			return domainInstanceField.get(domainObject);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new ConversionException("Failed to retrieve value for field '" + domainInstanceField.getName()
					+ "' in domain object '" + domainObject + "'", e);
		}
	}
	
	@Override
	public T decode(final BsonReader reader, final DecoderContext decoderContext) {
		return null;
	}

	/**
	 * Analyzes and puts in a memory map the bindings to use when converting an
	 * incomping {@link DBObject} into an instance of {@link targetClass}. The
	 * bindings is a {@link Map} of {@link DocumentField} in the
	 * {@link targetClass} indexed by the name of the {@link key} )in the
	 * incoming {@link DBObject}.
	 * 
	 * @param targetClass
	 *            the {@link Class} for which the {@link Map} bindings should be
	 *            returned
	 * @return the {@link Map} of bindings for the given {@link targetClass}
	 */
	private static Map<String, Field> getBindings(final Class<?> targetClass) {
		if (!globalBindings.containsKey(targetClass)) {
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
				} else {
					classBindings.put(field.getName(), field);
				}
			}
			globalBindings.put(targetClass, classBindings);
		}
		return Collections.unmodifiableMap(globalBindings.get(targetClass));
	}

}
