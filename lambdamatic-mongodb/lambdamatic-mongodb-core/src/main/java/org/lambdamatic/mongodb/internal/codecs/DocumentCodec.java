/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
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
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class DocumentCodec<T> implements Codec<T> {

	/** The Logger name to use when logging conversion results. */
	static final String LOGGER_NAME = DocumentCodec.class.getName();

	/** The usual Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(LOGGER_NAME);

	/** The user-defined domain class associated with this Codec. */
	private final Class<T> targetClass;

	/** the codec registry, to decode elements of an incoming BSON document. */
	private final CodecRegistry codecRegistry;

	/** The binding service. */
	private final BindingService bindingService;

	/**
	 * Constructor
	 * 
	 * @param targetClass
	 *            the domain class supported by this {@link Codec}.
	 * @param codecRegistry
	 *            the associated {@link CodecRegistry}
	 * @param bindingService
	 *            the Binding Service.
	 */
	public DocumentCodec(final Class<T> targetClass, final CodecRegistry codecRegistry,
			final BindingService bindingService) {
		this.targetClass = targetClass;
		this.codecRegistry = codecRegistry;
		this.bindingService = bindingService;
	}

	/**
	 * @return the {@link BindingService} of this {@link DocumentCodec}.
	 */
	public BindingService getBindingService() {
		return bindingService;
	}

	public CodecRegistry getCodecRegistry() {
		return codecRegistry;
	}

	@Override
	public Class<T> getEncoderClass() {
		return targetClass;
	}

	/**
	 * Encodes the given {@code domainObject}, putting the {@code _id} attribute first, followed by
	 * {@code _targetClassName} to be able to decode the BSON/JSON document later, followed by the other annotated Java
	 * attributes.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void encode(final BsonWriter writer, final T domainObject, final EncoderContext encoderContext) {
		final DocumentEncoder encoder = new DocumentEncoder(this.targetClass, this.bindingService, this.codecRegistry);
		if (LOGGER.isDebugEnabled()) {
			try {
				// use an intermediate JsonWriter whose Outputstream can be
				// retrieved
				final ByteArrayOutputStream jsonOutputStream = new ByteArrayOutputStream();
				final BsonWriter debugWriter = new JsonWriter(new OutputStreamWriter(jsonOutputStream, "UTF-8"));
				encoder.encodeDomainObject(debugWriter, domainObject, encoderContext);
				final String jsonContent = IOUtils.toString(jsonOutputStream.toByteArray(), "UTF-8");
				LOGGER.debug("Encoded document: {}", jsonContent);
				// now, write the document in the target writer
				final JsonReader jsonContentReader = new JsonReader(jsonContent);
				writer.pipe(jsonContentReader);
				writer.flush();
			} catch (IOException e) {
				throw new ConversionException("Failed to convert '" + domainObject.toString() + "' to a BSON document",
						e);
			}
		} else {
			encoder.encodeDomainObject(writer, domainObject, encoderContext);
		}
	}

	/**
	 * Converts the BSON Documentation provided by the given {@link BsonReader} into an instance of user-domain class.
	 * The user-domain specific class is found in the {@link DocumentCodec#TARGET_CLASS_FIELD} field, otherwise, the
	 * code assumes it should return an instance of type {@code T}, where {@code T} is the parameter type of this
	 * {@link Codec}).
	 * <p>
	 * Note: target class {@code T} should have a default constructor (this may be improved in further version)
	 * </p>
	 */
	@Override
	public T decode(final BsonReader reader, final DecoderContext decoderContext) {
		final DocumentEncoder encoder = new DocumentEncoder(this.targetClass, this.bindingService, this.codecRegistry);
		// code adapted from "org.bson.codecs.BsonDocumentCodec"
		return encoder.decodeDocument(reader, decoderContext);
	}

}
