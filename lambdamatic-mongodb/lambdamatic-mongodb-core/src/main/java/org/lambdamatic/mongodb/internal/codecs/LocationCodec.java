/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.internal.codecs;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonElement;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.exceptions.ConversionException;
import org.lambdamatic.mongodb.types.geospatial.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom {@link Codec} to encode and decode {@link Location} to/from {@link BsonDocument} with the
 * following format:
 * 
 * <pre>
 * {
 *  "type" : "Point",
 *  "coordinates" : [40.72, -73.92]
 * }
 * </pre>
 * 
 * @author Xavier Coulon
 *
 */
public class LocationCodec
    extends DocumentCodec<Location> {

  private static final Logger LOGGER = LoggerFactory.getLogger(LocationCodec.class);

  /**
   * Constructor
   * 
   * @param codecRegistry the {@link CodecRegistry} to use.
   */
  public LocationCodec(final CodecRegistry codecRegistry) {
    super(Location.class, codecRegistry);
  }

  /**
   * Writes a custom {@link BsonDocument} in the given {@link BsonWriter} from the given
   * {@link Location} using the following format:
   * 
   * <pre>
   * {
   *  "type" : "Point",
   *  "coordinates" : [&lt;latitude&gt;, &lt;longitude&gt;]
   * }
   * </pre>
   * 
   * where &lt;latitude&gt; and &lt;longitude&gt; are provided by {@link Location#getLatitude()} and
   * {@link Location#getLongitude()} respectively.
   * <p>
   * This {@link Codec#encode(BsonWriter, Object, EncoderContext)} method assumes that the
   * {@link BsonWriter#writeStartDocument(String)} method was called before.
   * 
   * @see org.bson.codecs.Encoder#encode(org.bson.BsonWriter, java.lang.Object,
   *      org.bson.codecs.EncoderContext)
   */
  @Override
  public void encode(final BsonWriter writer, final Location location,
      final EncoderContext encoderContext) {
    writer.writeString("type", "Point");
    writer.writeStartArray("coordinates");
    writer.writeDouble(location.getLatitude());
    writer.writeDouble(location.getLongitude());
    writer.writeEndArray();
  }

  /**
   * @see org.bson.codecs.Encoder#getEncoderClass()
   */
  @Override
  public Class<Location> getEncoderClass() {
    return Location.class;
  }

  /**
   * @see org.bson.codecs.Decoder#decode(org.bson.BsonReader, org.bson.codecs.DecoderContext)
   */
  @Override
  public Location decode(final BsonReader reader, final DecoderContext decoderContext) {
    // code duplicated and adapted from "org.bson.codecs.BsonDocumentCodec"
    final DocumentEncoder documentEncoder = new DocumentEncoder(Location.class, getCodecRegistry());
    final Map<String, BsonElement> keyValuePairs = new HashMap<>();
    reader.readStartDocument();
    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
      String fieldName = reader.readName();
      keyValuePairs.put(fieldName,
          new BsonElement(fieldName, documentEncoder.readBsonValue(reader, decoderContext)));
    }
    reader.readEndDocument();
    // now, convert the map key-pairs into an instance of the target
    // document
    final LocationDocument locationDocument = new LocationDocument();
    final Map<String, Field> bindings =
        BindingService.getInstance().getBindings(LocationDocument.class);
    for (Iterator<String> iterator = keyValuePairs.keySet().iterator(); iterator.hasNext();) {
      final String key = iterator.next();
      final Field field = bindings.get(key);
      if (field == null) {
        LOGGER.debug("Field '{}' does not exist in class '{}'", key, LocationDocument.class);
        continue;
      }
      final Object fieldValue = documentEncoder.getValue(keyValuePairs.get(key), field.getType());
      try {
        field.setAccessible(true);
        field.set(locationDocument, fieldValue);
      } catch (IllegalArgumentException | IllegalAccessException e) {
        throw new ConversionException("Unable to set value '" + fieldValue + "' to field '"
            + LocationDocument.class.getName() + "." + field.getName() + "'", e);
      }
    }
    return locationDocument.toLocation();
  }

  static class LocationDocument {

    @DocumentField
    private String type;

    @DocumentField
    private Double[] coordinates;

    public Location toLocation() {
      return new Location(this.coordinates[0], this.coordinates[1]);
    }
  }

}
