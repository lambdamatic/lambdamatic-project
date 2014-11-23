/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.converters;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class LambdamaticFilterExpressionCodecProvider implements CodecProvider {

	/**
	 * {@inheritDoc}
	 * @see org.bson.codecs.configuration.CodecProvider#get(java.lang.Class, org.bson.codecs.configuration.CodecRegistry)
	 */
	@Override
	public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {
		// TODO Auto-generated method stub
		return null;
	}

}
