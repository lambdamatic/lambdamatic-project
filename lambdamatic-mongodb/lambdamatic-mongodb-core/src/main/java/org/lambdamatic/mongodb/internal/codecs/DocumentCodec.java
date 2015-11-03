/*******************************************************************************
 * Copyright (c) 2014, 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc. All rights
 * reserved. This program is made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.internal.codecs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.commons.io.IOUtils;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.json.JsonReader;
import org.bson.json.JsonWriter;
import org.lambdamatic.mongodb.exceptions.ConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encode/decodes a given Domain object into a BSON document.
 * 
 * @param <DomainType> the actual domain type to encode and decode
 *
 */
public class DocumentCodec<DomainType>
    implements Codec<DomainType> {

  /** The Logger name to use when logging conversion results. */
  static final String LOGGER_NAME = DocumentCodec.class.getName();

  /** The usual Logger. */
  private static final Logger LOGGER = LoggerFactory.getLogger(LOGGER_NAME);

  /** The user-defined domain class associated with this Codec. */
  private final Class<DomainType> targetClass;

  /** the codec registry, to decode elements of an incoming BSON document. */
  private final CodecRegistry codecRegistry;

  /**
   * Constructor
   * 
   * @param targetClass the domain class supported by this {@link Codec}.
   * @param codecRegistry the associated {@link CodecRegistry}
   */
  public DocumentCodec(final Class<DomainType> targetClass, final CodecRegistry codecRegistry) {
    this.targetClass = targetClass;
    this.codecRegistry = codecRegistry;
  }

  /**
   * @return the {@link CodecRegistry} associated with this codec.
   */
  public CodecRegistry getCodecRegistry() {
    return this.codecRegistry;
  }

  @Override
  public Class<DomainType> getEncoderClass() {
    return this.targetClass;
  }

  /**
   * Encodes the given {@code domainObject}, putting the {@code _id} attribute first, followed by
   * {@code _targetClassName} to be able to decode the BSON/JSON document later, followed by the
   * other annotated Java attributes.
   * 
   * {@inheritDoc}
   */
  @Override
  public void encode(final BsonWriter writer, final DomainType domainObject,
      final EncoderContext encoderContext) {
    if (LOGGER.isDebugEnabled()) {
      final ByteArrayOutputStream jsonOutputStream = new ByteArrayOutputStream();
      try (final JsonWriter debugWriter =
          new JsonWriter(new OutputStreamWriter(jsonOutputStream, "UTF-8"))) {
        // use an intermediate JsonWriter whose Outputstream can be
        // retrieved
        EncoderUtils.encodeDomainObject(debugWriter, domainObject, encoderContext,
            this.codecRegistry);
        final String jsonContent = IOUtils.toString(jsonOutputStream.toByteArray(), "UTF-8");
        LOGGER.debug("Encoded document: {}", jsonContent);
        // now, write the document in the target writer
        try (final JsonReader jsonContentReader = new JsonReader(jsonContent)) {
          writer.pipe(jsonContentReader);
          writer.flush();
        }
      } catch (IOException e) {
        throw new ConversionException(
            "Failed to convert '" + domainObject.toString() + "' to a BSON document", e);
      }
    } else {
      EncoderUtils.encodeDomainObject(writer, domainObject, encoderContext, this.codecRegistry);
    }
  }

  /**
   * Converts the BSON Documentation provided by the given {@link BsonReader} into an instance of
   * user-domain class.
   */
  @Override
  public DomainType decode(final BsonReader reader, final DecoderContext decoderContext) {
    final DocumentDecoder decoder = new DocumentDecoder(this.targetClass, this.codecRegistry);
    // code adapted from "org.bson.codecs.BsonDocumentCodec"
    return decoder.decodeDocument(reader, decoderContext);
  }

}
