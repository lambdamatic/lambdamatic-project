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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.json.JsonReader;
import org.bson.json.JsonWriter;
import org.lambdamatic.LambdaExpression;
import org.lambdamatic.SerializableFunction;
import org.lambdamatic.analyzer.LambdaExpressionAnalyzer;
import org.lambdamatic.mongodb.exceptions.ConversionException;
import org.lambdamatic.mongodb.metadata.Projection;
import org.lambdamatic.mongodb.metadata.Projection.ProjectionType;
import org.lambdamatic.mongodb.metadata.ProjectionField;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.query.context.FindContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Standalone {@link Codec} for Lambda {@link SerializableFunction}s.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class ProjectionExpressionCodec implements Codec<SerializableFunction<ProjectionMetadata<?>,Projection>> {

	/** The Logger name to use when logging conversion results.*/
	static final String LOGGER_NAME = ProjectionExpressionCodec.class.getName();
	
	/** The usual Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(LOGGER_NAME);

	@Override
	public Class<SerializableFunction<ProjectionMetadata<?>,Projection>> getEncoderClass() {
		return null;
	}

	@Override
	public void encode(final BsonWriter writer, final SerializableFunction<ProjectionMetadata<?>,Projection> projectionExpression,
			final EncoderContext encoderContext) {
		if (LOGGER.isInfoEnabled()) {
			try {
				// use an intermediate JsonWriter whose Outputstream can be
				// retrieved
				final ByteArrayOutputStream jsonOutputStream = new ByteArrayOutputStream();
				final BsonWriter debugWriter = new JsonWriter(new OutputStreamWriter(jsonOutputStream, "UTF-8"));
				doEncodeProjection(debugWriter, projectionExpression, encoderContext);
				final String jsonContent = IOUtils.toString(jsonOutputStream.toByteArray(), "UTF-8");
				LOGGER.info("Projection expression: {}", jsonContent);
				// now, write the document in the target writer
				final JsonReader jsonContentReader = new JsonReader(jsonContent);
				writer.pipe(jsonContentReader);
				writer.flush();
			} catch (IOException e) {
				throw new ConversionException("Failed to pipe content into a te;porary JSON document while encoding the specified projection", e);
			}
		} else {
			doEncodeProjection(writer, projectionExpression, encoderContext);
		}
	}

	/**
	 * Encodes the given {@link Projection} into the given {@link BsonWriter}, using the {@link EncoderContext} if necessary.
	 * @param writer the output writer
	 * @param projectionExpressiom the {@link Projection} to encode
	 * @param encoderContext then encoder context
	 */
	protected void doEncodeProjection(final BsonWriter writer, final SerializableFunction<ProjectionMetadata<?>, Projection> projectionExpression, final EncoderContext encoderContext) {
		final Projection projection = getProjection(projectionExpression);
		final Map<Projection.ProjectionType, List<String>> projections = findProjections(projection);
		writer.writeStartDocument();
		projections.get(ProjectionType.INCLUDE).stream().forEach(fieldName -> {
			writer.writeInt32(fieldName, 1);
		});
		projections.get(ProjectionType.EXCLUDE).stream().forEach(fieldName -> {
			writer.writeInt32(fieldName, 0);
		});
		writer.writeEndDocument();
		writer.flush();
	}

	/**
	 * Evaluates the given {@link SerializableFunction} and returns the {@link Projection} result. The argument passed
	 * to the expression during the call is a {@link Class#newInstance()} of the expected type (a subclass of
	 * {@link ProjectionMetadata}.
	 * 
	 * @param projectionExpression
	 *            the lambda expression to call
	 * @return the expression result.
	 * @throws ConversionException if something went wrong while instantiating the argument.
	 * @see SerializableFunction#apply(Object)
	 */
	private Projection getProjection(final SerializableFunction<ProjectionMetadata<?>, Projection> projectionExpression) {
		try {
			@SuppressWarnings("unchecked")
			final Class<ProjectionMetadata<?>> argumentType = (Class<ProjectionMetadata<?>>) LambdaExpressionAnalyzer.getInstance().getArgumentType(projectionExpression);
			final Projection projection = projectionExpression.apply(argumentType.newInstance());
			return projection;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new ConversionException("Failed to retrieve the result of the Projection lambda expression", e);
		}
	}

	/**
	 * Look ups the {@link ProjectionMetadata} {@link ProjectionField} used in the given {@link LambdaExpression} to
	 * determine which fields should be included or excluded as part of the Projection BSON document.
	 * 
	 * <p>
	 * <strong>note:</strong> if <code>_id</code> field is not explicitly specified in the
	 * {@link ProjectionType#INCLUDE} list of fields, it is excluded (because the current API does not allow for mixing
	 * {@link ProjectionType#INCLUDE} and {@link ProjectionType#EXCLUDE}.
	 * </p>
	 * 
	 * @param projection
	 *            the {@link Projection} object resulting of the lambda expression passed in the
	 *            {@link FindContext#projection(SerializableFunction)} method.
	 * 
	 * @return a map of included/excluded (BSON) field names, indexed by {@link ProjectionType}
	 */
	private Map<Projection.ProjectionType, List<String>> findProjections(final Projection projection) {
		if(projection == null) {
			throw new ConversionException("Projection cannot be null");
		}
		try {
			final Map<Projection.ProjectionType, List<String>> result = new HashMap<>();
			result.put(ProjectionType.INCLUDE, new ArrayList<>());
			result.put(ProjectionType.EXCLUDE, new ArrayList<>());
			projection.getFields().stream().forEach(f -> {
				result.get(projection.getType()).add(f.getFieldName());
			});
			// specific case of '_id' field: add to exclusions if not specified in inclusions
			if(!result.get(ProjectionType.INCLUDE).contains(DocumentCodec.MONGOBD_DOCUMENT_ID)) {
				result.get(ProjectionType.EXCLUDE).add(DocumentCodec.MONGOBD_DOCUMENT_ID);
			}
			return result;
		} catch(SecurityException e) {
			throw new ConversionException("Failed to generate projection Bson document while reading metadata class fields", e);
		}
	}

	@Override
	public SerializableFunction<ProjectionMetadata<?>, Projection> decode(final BsonReader reader, final DecoderContext decoderContext) {
		// the filter expression is used in the queries, so it can only be
		// encoded, never decoded
		return null;
	}

}
