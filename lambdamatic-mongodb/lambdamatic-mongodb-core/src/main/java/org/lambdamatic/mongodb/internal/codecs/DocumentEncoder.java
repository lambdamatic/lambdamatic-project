/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.internal.codecs;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonElement;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.lambdamatic.mongodb.annotations.Document;
import org.lambdamatic.mongodb.annotations.EmbeddedDocument;
import org.lambdamatic.mongodb.exceptions.ConversionException;
import org.lambdamatic.mongodb.types.geospatial.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes a given Document class (ie, annotated with {@link Document} or {@link EmbeddedDocument})
 * into a MongoDB {@link BsonWriter}.
 * 
 * @author Xavier Coulon
 */
public class DocumentEncoder {

  private static final Logger LOGGER = LoggerFactory.getLogger(DocumentEncoder.class);

  private final CodecRegistry codecRegistry;

  private final Class<?> targetClass;

  /**
   * Constructor.
   * 
   * @param targetClass the actual target class to encode
   * @param codecRegistry the associated CodecRegistry that will provide the appropriate codec
   */
  public DocumentEncoder(final Class<?> targetClass, final CodecRegistry codecRegistry) {
    this.targetClass = targetClass;
    this.codecRegistry = codecRegistry;
  }

  /**
   * Decodes the document and returns a correspond instance of the associated Domain type.
   * 
   * @param reader the reader to read the BSON document from
   * @param decoderContext the {@link DecoderContext}
   * @param <DomainType> the expected domain type
   * @return an instance of a domain class
   */
  public <DomainType> DomainType decodeDocument(final BsonReader reader,
      final DecoderContext decoderContext) {
    final Map<String, BsonElement> keyValuePairs = new HashMap<>();
    reader.readStartDocument();
    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
      String fieldName = reader.readName();
      keyValuePairs.put(fieldName,
          new BsonElement(fieldName, readBsonValue(reader, decoderContext)));
    }
    reader.readEndDocument();
    // now, convert the map of key-value pairs into an instance of the target document
    final DomainType domainDocument = getTargetClass(keyValuePairs);
    final Map<String, Field> bindings =
        BindingService.getInstance().getBindings(domainDocument.getClass());
    for (Iterator<String> iterator = keyValuePairs.keySet().iterator(); iterator.hasNext();) {
      final String key = iterator.next();
      final Field field = bindings.get(key);
      if (field == null) {
        LOGGER.debug("Field '{}' does not exist in class '{}'", key, domainDocument.getClass());
        continue;
      }
      final Object fieldValue = getValue(keyValuePairs.get(key), field.getType());
      try {
        field.setAccessible(true);
        field.set(domainDocument, fieldValue);
      } catch (IllegalArgumentException | IllegalAccessException e) {
        throw new ConversionException("Unable to set value '" + fieldValue + "' to field '"
            + domainDocument.getClass().getName() + "." + field.getName() + "'", e);
      }
    }
    return domainDocument;
  }

  /**
   * Instantiate the target user-domain Document if the given {@code keyPairValues} contains a field
   * named {@link DocumentCodec#TARGET_CLASS_FIELD}, otherwise use the default {@code targetClass}
   * provided in the constructor of this codec.
   * 
   * @param keyValuePairs the key-value pairs to use
   * @return the instance of the target user-domain Document
   */
  @SuppressWarnings("unchecked")
  private <T> T getTargetClass(final Map<String, BsonElement> keyValuePairs) {
    final String targetClassName = getTargetClassName(keyValuePairs);
    try {
      return (T) Class.forName(targetClassName).newInstance();
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      throw new ConversionException("Failed to create a new instance of '" + targetClassName + "'",
          e);
    }
  }

  /**
   * Retrieves the fully qualified name of the target Document if the given {@code keyPairValues}
   * contains a field named {@link DocumentCodec#TARGET_CLASS_FIELD}, otherwise use the default
   * {@code targetClass} provided in the constructor of this codec.
   * 
   * @param keyValuePairs the key-value pairs to use
   * @return the fully qualified name of the class to instantiate
   */
  private String getTargetClassName(final Map<String, BsonElement> keyValuePairs) {
    if (keyValuePairs.containsKey(EncoderUtils.TARGET_CLASS_FIELD)) {
      final BsonElement targetClassElement = keyValuePairs.get(EncoderUtils.TARGET_CLASS_FIELD);
      return targetClassElement.getValue().asString().getValue();
    }
    return this.targetClass.getName();
  }

  /**
   * Converts the given {@code value} to the given {@code type}.
   * 
   * @param targetType the target type
   * @param value the value to convert
   * @return the converted value
   */
  // TODO do we really need this method ?
  // @Deprecated
  private Object convert(final Object value, final Class<?> targetType) {
    if (targetType.isEnum() && value instanceof String) {
      // enum field
      for (Object e : targetType.getEnumConstants()) {
        if (e.toString().equals((String) value)) {
          return e;
        }
      }
    } else if (value instanceof BsonDocument
        // embedded documents
        && targetType.getAnnotation(EmbeddedDocument.class) != null) {
      try (final BsonDocumentReader reader = new BsonDocumentReader((BsonDocument) value)) {
        return decodeDocument(reader, DecoderContext.builder().build());
      }
    } else if (List.class.isAssignableFrom(targetType)) {
      // List of embedded values/documents
      return ((List<?>) value);
    } else if (Set.class.isAssignableFrom(targetType)) {
      // Set of embedded values/documents
      return (Set<?>) value;
    } else if (Map.class.isAssignableFrom(targetType)) {
      // Set of embedded values/documents
      return (Map<?, ?>) value;
    } else if (targetType.isArray()) {
      // array of embedded values/documents
      return (Object[]) value;
    } else {
      // other types of fields.
      // FIXME: missing switches. Can we write it differently ?
      switch (targetType.getName()) {
        case "boolean":
        case "java.lang.Boolean":
          return Boolean.parseBoolean(value.toString());
        case "byte":
        case "java.lang.Byte":
          return Byte.parseByte(value.toString());
        case "short":
        case "java.lang.Short":
          return Short.parseShort(value.toString());
        case "char":
        case "java.lang.Character":
          return new Character((char) value);
        case "int":
        case "java.lang.Integer":
          return Integer.parseInt(value.toString());
        case "long":
        case "java.lang.Long":
          return Long.parseLong(value.toString());
        case "float":
        case "java.lang.Float":
          return Float.parseFloat(value.toString());
        case "double":
        case "java.lang.Double":
          return Double.parseDouble(value.toString());
        case "java.lang.String":
          return value.toString();
        case "org.bson.types.ObjectId":
          return new ObjectId(value.toString());
        case "java.util.Date":
          return new Date((long) value);
        case "org.lambdamatic.mongodb.types.geospatial.Location":
          if (value instanceof Location) {
            return value;
          } else {
            try (final BsonDocumentReader reader = new BsonDocumentReader((BsonDocument) value)) {
              return new LocationCodec(this.codecRegistry).decode(reader,
                  DecoderContext.builder().build());
            }
          }
        default:
          // will throw ConversionException
          break;
      }
    }
    throw new ConversionException(
        "Unable to convert value '" + value + "' to type " + targetType.getName());
  }

  /**
   * Returns the value of the given {@code bsonElement} converted to the given {@code fieldType}.
   * 
   * @param bsonElement the {@link BsonElement} to inspect
   * @param expectedType the {@link Class} of the value to return
   * @return the wrapped value or {@code null} if the given {@code bsonElement} is {@code null}
   */
  protected Object getValue(final BsonElement bsonElement, final Class<?> expectedType) {
    if (bsonElement != null) {
      final Object bsonValue = decodeValue(bsonElement.getValue(), expectedType);
      return convert(bsonValue, expectedType);
    }
    return null;
  }

  /**
   * Returns the value of the given {@link BsonElement}.
   * 
   * @param bsonValue the element to inspect
   * @param expectedType the expected type of the {@code bsonValue}
   * @return the wrapped value
   */
  public Object decodeValue(final BsonValue bsonValue, final Class<?> expectedType) {
    if (bsonValue != null) {
      switch (bsonValue.getBsonType()) {
        case ARRAY:
          if (List.class.isAssignableFrom(expectedType)) {
            return bsonValue.asArray().getValues().stream().map(v -> decodeValue(v, null))
                .collect(Collectors.toList());
          } else if (Set.class.isAssignableFrom(expectedType)) {
            return bsonValue.asArray().getValues().stream().map(v -> decodeValue(v, null))
                .collect(Collectors.toSet());
          } else if (Map.class.isAssignableFrom(expectedType)) {
            return bsonValue.asArray().getValues().stream()
                .collect(Collectors.<BsonValue, Object, Object, TreeMap<Object, Object>>toMap(
                    // retrieve name of the first element in the document
                    k -> readFirstName(k), // the document
                    // retrieve value of the first element in the document
                    v -> readFirstValue(v),
                    // binary operator (see Collectors#throwingMerger()
                    (k1, k2) -> {
                      throw new IllegalStateException(String.format("Duplicate key %s", k1));
                    } ,
                    // map supplier
                    TreeMap::new));
          } else if (expectedType.isArray()) {
            return bsonValue.asArray().getValues().stream()
                .map(v -> decodeValue(v, expectedType.getComponentType())).toArray(
                    size -> (Object[]) Array.newInstance(expectedType.getComponentType(), size));
          }
          break;
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
          final BsonDocument bsonDocument = (BsonDocument) bsonValue;
          if (expectedType == Location.class) {
            return new LocationCodec(this.codecRegistry)
                .decode(new BsonDocumentReader(bsonDocument), DecoderContext.builder().build());
          } else {
            return new DocumentCodec<>(expectedType, this.codecRegistry)
                .decode(new BsonDocumentReader(bsonDocument), DecoderContext.builder().build());
          }
        default:
          throw new ConversionException(
              "Unexpected BSON Element value of type '" + bsonValue.getBsonType() + "'");
      }
    }
    return null;
  }

  /**
   * Returns the value of the current reader position.
   *
   * @param reader the read to read the value from
   * @param decoderContext the context
   * @return the non-null value read from the reader
   */
  public BsonValue readBsonValue(final BsonReader reader, final DecoderContext decoderContext) {
    final Class<? extends BsonValue> classForBsonType =
        BsonValueCodecProvider.getClassForBsonType(reader.getCurrentBsonType());
    final Codec<? extends BsonValue> codec = this.codecRegistry.get(classForBsonType);
    return codec.decode(reader, decoderContext);
  }

  /**
   * Reads the first name in the given {@link BsonValue}, assuming that it is a
   * {@link BsonDocument}.
   * 
   * @param bsonValue the BsonDocument to read
   * @return the first name
   */
  private static Object readFirstName(final BsonValue bsonValue) {
    if (bsonValue == null) {
      throw new ConversionException("Expected a BsonDocument but the given value was a null");

    } else if (!bsonValue.isDocument()) {
      throw new ConversionException(
          "Expected a BsonDocument but the given value was a " + bsonValue.getBsonType().name());
    }
    try (final BsonDocumentReader reader = new BsonDocumentReader((BsonDocument) bsonValue)) {
      reader.readStartDocument();
      final String name = reader.readName();
      return name;
    }
  }

  /**
   * Reads the first value in the given {@link BsonValue}, assuming that it is a
   * {@link BsonDocument}.
   * 
   * @param bsonValue the BsonDocument to read
   * @return the first value
   */
  private Object readFirstValue(final BsonValue bsonValue) {
    if (bsonValue == null) {
      throw new ConversionException("Expected a BsonDocument but the given value was a null");

    } else if (!bsonValue.isDocument()) {
      throw new ConversionException(
          "Expected a BsonDocument but the given value was a " + bsonValue.getBsonType().name());
    }
    try (final BsonDocumentReader reader = new BsonDocumentReader((BsonDocument) bsonValue)) {
      reader.readStartDocument();
      reader.readName();
      final Object value =
          decodeValue(readBsonValue(reader, DecoderContext.builder().build()), null);
      return value;
    }
  }
}

