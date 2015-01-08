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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;

import org.bson.BsonElement;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.annotations.DocumentId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DBObject;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class LambdamaticDocumentCodec<T> implements Codec<T> {

	/** the usual logger.*/
	private final static Logger LOGGER = LoggerFactory.getLogger(LambdamaticDocumentCodec.class);
	
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
	
	/** the codec registry, to decode elements of an incoming BSON document. */
	private final CodecRegistry codecRegistry;

	/**
	 * Constructor
	 * 
	 * @param targetClass
	 *            the domain class supported by this {@link Codec}.
	 */
	public LambdamaticDocumentCodec(final Class<T> targetClass, final CodecRegistry codecRegistry) {
        if (codecRegistry == null) {
            throw new IllegalArgumentException("Codec registry can not be null");
        }
        this.codecRegistry = codecRegistry;
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
		// FIXME: use a debugWriter to log the generated document 
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
			bindings.entrySet().stream().filter(e -> !isIdBinding(e)).forEach(
					binding -> {
						final Object bindingValue = getBindingValue(domainObject, binding.getValue());
						if(bindingValue != null) {
							writeValue(writer, binding.getKey(), bindingValue);
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

	/**
	 * Converts the BSON Documentation provided by the given {@link BsonReader}
	 * into an instance of user-domain class. The user-domain specific class is
	 * found in the {@link LambdamaticDocumentCodec#TARGET_CLASS_FIELD} field,
	 * otherwise, the code assumes it should return an instance of type
	 * {@code T}, where {@code T} is the parameter type of this {@link Codec}).
	 * <p>Note: target class {@code T} should have a default constructor (this may be improved in further version)</p>
	 */
	@Override
	public T decode(final BsonReader reader, final DecoderContext decoderContext) {
		// code duplicated and adapted from "org.bson.codecs.BsonDocumentCodec"
		final Map<String, BsonElement> keyValuePairs = new HashMap<>();
        reader.readStartDocument();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String fieldName = reader.readName();
            keyValuePairs.put(fieldName, new BsonElement(fieldName, readValue(reader, decoderContext)));
        }
        reader.readEndDocument();
        // now, convert the map key-pairs into an instance of the target document 
		final T domainDocument = getTargetClass(keyValuePairs);
		final Map<String, Field> bindings = getBindings(domainDocument.getClass());
		for (Iterator<String> iterator = keyValuePairs.keySet().iterator(); iterator.hasNext();) {
			final String key = iterator.next();
			final Field field = bindings.get(key);
			if (field == null) {
				LOGGER.debug("Field '{}' does not exist in class '{}'", key, targetClass.getName());
				continue;
			}
			final Object fieldValue = getValue(keyValuePairs.get(key), field.getType());
			try {
				field.setAccessible(true);
				field.set(domainDocument, fieldValue);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new ConversionException("Unable to set value '" + fieldValue + "' to field '" + domainDocument.getClass().getName() + "." + field.getName() + "'", e);
			}
		}
        return domainDocument;
	}
	
	/**
	 * Returns the value of the given {@code bsonElement} converted to the given {@code fieldType}.
	 * @param bsonElement the {@link BsonElement} to inspect
	 * @param expectedType the {@link Class} of the value to return
	 * @return the wrapped value
	 */
	private Object getValue(final BsonElement bsonElement, final Class<?> expectedType) {
		final Object bsonValue = getValue(bsonElement);
		return convert(bsonValue, expectedType);
	}

	/**
	 * Converts the given {@code value} to the given {@code type}
	 * @param targetType the target type
	 * @param value the value to convert
	 * @return the converted value
	 */
	private Object convert(final Object value, final Class<?> targetType) {
		if(targetType.isEnum() && value instanceof String) {
			for(Object e : targetType.getEnumConstants()) {
				if(e.toString().equals((String)value)) {
					return e;
				}
			}
		}
		switch (targetType.getName()) {
		case "short":
			return Short.parseShort(value.toString());
		case "java.lang.String":
			return value.toString();
		case "org.bson.types.ObjectId":
			return new ObjectId(value.toString());
		}
		return value;
	}
	
	/**
	 * Returns the value of the given {@link BsonElement}.
	 * @param bsonElement the element to inspect
	 * @return the wrapped value
	 */
	private Object getValue(final BsonElement bsonElement) {
		if(bsonElement != null) {
		BsonValue bsonValue = bsonElement.getValue();
		switch(bsonValue.getBsonType()) {
			case ARRAY:
				return bsonValue.asArray().getValues();
			case BINARY:
				return bsonValue.asBinary().getData();
			case BOOLEAN:
				return bsonValue.asBoolean().getValue();
			case DATE_TIME:
				return bsonValue.asDateTime().getValue();
			case DB_POINTER:
				return bsonValue.asDBPointer().getId();
			case DOUBLE:
				return bsonValue.asDouble().getValue();
			case INT32:
				return bsonValue.asInt32().getValue();
			case INT64:
				return bsonValue.asInt64().getValue();
			case JAVASCRIPT:
				return bsonValue.asJavaScript().getCode();
			case JAVASCRIPT_WITH_SCOPE:
				return bsonValue.asJavaScriptWithScope().getCode();
			case NULL:
				return null;
			case OBJECT_ID:
				return bsonValue.asObjectId().getValue();
			case REGULAR_EXPRESSION:
				return bsonValue.asRegularExpression().getPattern();
			case STRING:
				return bsonValue.asString().getValue();
			case SYMBOL:
				return bsonValue.asSymbol().getSymbol();
			case TIMESTAMP:
				return bsonValue.asTimestamp().getTime();
			case DOCUMENT:
				// FIXME: this should be supported for embedded documents
			case END_OF_DOCUMENT:
			case UNDEFINED:
			case MAX_KEY:
			case MIN_KEY:
			default:
				throw new ConversionException("Unexpected BSON Element value of type '" + bsonElement.getValue().getBsonType() + "'");
			}
		}
		return null;
	}

	/**
	 * Instantiate the target user-domain Document if the given
	 * {@code keyPairValues} contains a field named
	 * {@link LambdamaticDocumentCodec#TARGET_CLASS_FIELD}, otherwise use the
	 * default {@code targetClass} provided in the constructor of this codec.
	 * 
	 * @param keyValuePairs the key-value pairs to use
	 * @return the instance of the target user-domain Document
	 */
	@SuppressWarnings("unchecked")
	private T getTargetClass(final Map<String, BsonElement> keyValuePairs) {
		final String targetClassName = getTargetClassName(keyValuePairs);
		try {
			return (T) Class.forName(targetClassName).newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new ConversionException("Failed to create a new instance of '" + targetClassName + "'", e);
		}
	}

	/**
	 * Retrieves the fully qualified name of the target Document if the given
	 * {@code keyPairValues} contains a field named
	 * {@link LambdamaticDocumentCodec#TARGET_CLASS_FIELD}, otherwise use the
	 * default {@code targetClass} provided in the constructor of this codec.
	 * 
	 * @param keyValuePairs the key-value pairs to use
	 * @return the fully qualified name of the class to instantiate
	 */
	private String getTargetClassName(final Map<String, BsonElement> keyValuePairs) {
		if (keyValuePairs.containsKey(TARGET_CLASS_FIELD)) {
			final BsonElement targetClassElement = keyValuePairs.get(TARGET_CLASS_FIELD);
			if (targetClassElement.getValue().isString()) {
				return targetClassElement.getValue().asString().getValue();
			}
		}
		return this.targetClass.getName();
	}

	/**
     * Returns the value of the current reader position 
     *
     * @param reader         the read to read the value from
     * @param decoderContext the context
     * @return the non-null value read from the reader
     */
    protected BsonValue readValue(final BsonReader reader, final DecoderContext decoderContext) {
        return codecRegistry.get(BsonValueCodecProvider.getClassForBsonType(reader.getCurrentBsonType())).decode(reader, decoderContext);
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
