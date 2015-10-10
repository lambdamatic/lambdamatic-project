/*******************************************************************************
 * Copyright (c) 2014, 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc. All rights
 * reserved. This program is made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.internal.codecs;

import java.util.Arrays;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.lambdamatic.SerializableConsumer;
import org.lambdamatic.mongodb.UpdateExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link CodecProvider} for the {@link UpdateExpressionCodec}.
 *
 */
public class UpdateExpressionCodecProvider
    implements CodecProvider {

  /** The usual Logger. */
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateExpressionCodecProvider.class);

  /**
   * Returns whether the given clazz implements the {@link SerializableConsumer} interface, in which
   * case it can return an instance of {@link UpdateExpressionCodec}.
   * 
   * @see org.bson.codecs.configuration.CodecProvider#get(java.lang.Class,
   *      org.bson.codecs.configuration.CodecRegistry)
   */
  @SuppressWarnings("unchecked")
  @Override
  public <PM> Codec<PM> get(final Class<PM> clazz, final CodecRegistry registry) {
    try {
      if (Arrays.stream(clazz.getInterfaces()).anyMatch(i -> i.equals(UpdateExpression.class))) {
        return (Codec<PM>) new UpdateExpressionCodec(registry);
      }
    } catch (SecurityException | IllegalArgumentException e) {
      LOGGER.error("Failed to check if class '{}' is an instance of ''", e, clazz.getName(),
          SerializableConsumer.class.getName());
    }
    return null;
  }

}
