/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.codecs;

import java.util.Arrays;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.lambdamatic.SerializableFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class ProjectionExpressionCodecProvider implements CodecProvider {

	/** The usual Logger.*/
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectionExpressionCodecProvider.class);
	
	/**
	 * Returns whether the given clazz implements the {@link SerializableFunction} interface, in which case it can return an
	 * instance of {@link ProjectionExpressionCodec}. 
	 * 
	 * @see org.bson.codecs.configuration.CodecProvider#get(java.lang.Class,
	 *      org.bson.codecs.configuration.CodecRegistry)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <PM> Codec<PM> get(final Class<PM> clazz, final CodecRegistry registry) {
		try {
			if(Arrays.stream(clazz.getInterfaces()).anyMatch(i -> i.equals(SerializableFunction.class))) {
				return (Codec<PM>) new ProjectionExpressionCodec();
			}
		} catch (SecurityException | IllegalArgumentException e) {
			LOGGER.error("Failed to check if class '{}' is an instance of ''", e, clazz.getName(), SerializableFunction.class.getName());
		}
		return null;
	}

}
