/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.codecs;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.types.ObjectId;
import org.lambdamatic.FilterExpression;
import org.lambdamatic.LambdaExpression;
import org.lambdamatic.analyzer.LambdaExpressionAnalyzer;
import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.annotations.DocumentId;
import org.lambdamatic.mongodb.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Utility class to convert instances of {@link DBObject} to domain objects and vice-versa, as well as
 * {@link FilterExpression} to {@link DBObject} when queries need to be executed.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
@Deprecated
public class DBObjectConverter {

	/** The logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(DBObjectConverter.class);

	/** Internal cache of bindings to convert domain class instances with incoming DBObjects. */
	private static final Map<Class<?>, Map<String, Field>> globalBindings = new HashMap<>();

	/** Name of the field used to store the document id in MongoDB. */
	public static final String MONGOBD_DOCUMENT_ID = "_id";

	/** Name of the field used to handle the Java type associated with a document in MongoDB. */
	public static final String TARGET_CLASS_FIELD = "_targetClass";

	/**
	 * Convert the given {@link FilterExpression} associated with some {@link Metadata}
	 * 
	 * @param filterExpression
	 *            the lambda expression to convert into a {@link DBObject}
	 * @param metadataClass
	 *            the {@link Class} associated with the {@link FilterExpression}
	 * @return a {@link DBObject} document to submit to MongoDB
	 * @throws ConversionException
	 */
	public static <T, M extends Metadata<T>> DBObject convert(final FilterExpression<M> filterExpression, final Class<M> metadataClass) throws ConversionException {
		final LambdaExpressionAnalyzer analyzer = new LambdaExpressionAnalyzer();
		final LambdaExpression astRoot = analyzer.analyzeLambdaExpression(filterExpression);
		//final ExpressionConverter expressionConverter = new ExpressionConverter(metadataClass);
		//astRoot.accept(expressionConverter);
		//return expressionConverter.getResult();
		return null;
	}

	/**
	 * Converts the given {@code domainInstance} into a {@link DBObject} that can be stored in MongoDB.
	 * 
	 * @param domainInstance
	 *            the input domain object.
	 * 
	 * @return the corresponding {@link DBObject}
	 */
	public static <T> DBObject convert(final T domainInstance) {
		try {
			final DBObject result = new BasicDBObject();
			result.put(TARGET_CLASS_FIELD, domainInstance.getClass().getName());
			final Map<String, Field> bindings = getBindings(domainInstance.getClass());
			for (Entry<String, Field> binding : bindings.entrySet()) {
				final String documentAttribute = binding.getKey();
				final Field domainInstanceField = binding.getValue();
				domainInstanceField.setAccessible(true);
				final Object domainInstanceFieldValue = domainInstanceField.get(domainInstance);
				if (domainInstanceFieldValue != null) {
					if(domainInstanceFieldValue.getClass().isEnum()) {
						result.put(documentAttribute, ((Enum<?>)domainInstanceFieldValue).name());
					} else {
						result.put(documentAttribute, domainInstanceFieldValue);
					}
				}
			}
			// also include the fully qualified name of the given domain instance
			return result;
		} catch (IllegalAccessException e) {
			throw new ConversionException("Failed to convert '" + domainInstance.toString() + "' to DbObject", e);
		}
	}

	/**
	 * Sets the given {@code objectId} as the {@code document id} of the given {@code domainInstance}, using, in order
	 * of preferences:
	 * <ul>
	 * <li>the field annotated with {@link DocumentId} (the field MUST be of type {@link ObjectId} or {@link String})
	 * <li>
	 * <li>the field of type {@link ObjectId} even if it has no {@link DocumentId} annotation.
	 * </ul>
	 * 
	 * @param domainInstance
	 *            the domain instance whose {@code id} will be set
	 * @param objectId
	 *            the id value
	 */
	public static <T> void setDocumentId(final T domainInstance, final ObjectId objectId) {
		try {
			final Map<String, Field> bindings = getBindings(domainInstance.getClass());
			final Field documentIdField = bindings.get(MONGOBD_DOCUMENT_ID);
			documentIdField.setAccessible(true);
			documentIdField.set(domainInstance, convert(objectId, documentIdField.getClass()));
		} catch (IllegalArgumentException | IllegalAccessException | ParseException e) {
			throw new ConversionException("Failed to set document id with value '" + objectId
					+ "' in domain instance of type " + domainInstance.getClass().getName(), e);
		}

	}

	/**
	 * Converts the given {@link DBObject} into a instance of the given {@code targetClass}.
	 * 
	 * 
	 *
	 * //FIXME: feature described below must be implemented to support inheritance.
	 * <p>
	 * <strong>Note:</strong> If the given {@link DBObject} contains a field named {@code TARGET_CLASS_FIELD}, then its
	 * value will be used as the type of the object to instantiate and populate, provided that it is a subclass of the
	 * given {@code targetClass}.
	 * </p>
	 * 
	 * @param dbObject
	 * @param targetClass
	 * @return
	 * @throws ConversionException
	 */
	public static <T> T convert(final DBObject dbObject, final Class<T> targetClass) throws ConversionException {
		try {
			final Map<String, Field> classBindings = getBindings(targetClass);
			final T targetInstance = (T) targetClass.newInstance();
			// TODO: verify that inherited private fields are also supported
			for (Iterator<String> iterator = dbObject.keySet().iterator(); iterator.hasNext();) {
				final String key = iterator.next();
				final Field field = classBindings.get(key);
				if (field == null) {
					LOGGER.debug("Field '{}' does not exist in class '{}'", key, targetClass.getName());
					continue;
				}
				field.setAccessible(true);
				field.set(targetInstance, convert(dbObject.get(key), field.getType()));
			}
			return targetInstance;
		} catch (InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException
				| ParseException e) {
			throw new ConversionException("Failed to convert dbObject " + dbObject + " to " + targetClass.getName(), e);
		}
	}

	/**
	 * Converts the given {@code value} into an instance of the given {@code type}
	 * 
	 * @param value
	 *            the input value
	 * @param type
	 *            the output type
	 * @return the output value
	 * @throws ParseException
	 */
	private static Object convert(final Object value, Class<?> type) throws ParseException {
		if(type.isEnum() && value instanceof String) {
			for(Object e : type.getEnumConstants()) {
				if(e.toString().equals((String)value)) {
					return e;
				}
			}
		}
		switch (type.getName()) {
		case "short":
			return Short.parseShort(value.toString());
		case "java.lang.String":
			return value.toString();
		}
		return value;
	}

	/**
	 * Analyzes and puts in a memory map the bindings to use when converting an incomping {@link DBObject} into an
	 * instance of {@link targetClass}. The bindings is a {@link Map} of {@link DocumentField} in the
	 * {@link targetClass} indexed by the name of the {@link key} )in the incoming {@link DBObject}.
	 * 
	 * @param targetClass
	 *            the {@link Class} for which the {@link Map} bindings should be returned
	 * @return the {@link Map} of bindings for the given {@link targetClass}
	 */
	private static Map<String, Field> getBindings(final Class<?> targetClass) {
		if (!globalBindings.containsKey(targetClass)) {
			final Map<String, Field> classBindings = new HashMap<>();
			// let's analyze the class' declared fields
			// TODO: verify that inherited private fields are also supported
			for (Field field : targetClass.getDeclaredFields()) {
				final DocumentId documentIdAnnotation = field.getAnnotation(DocumentId.class);
				if (documentIdAnnotation != null) {
					classBindings.put("_id", field);
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
		return globalBindings.get(targetClass);
	}
}

