/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.codecs;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.lambdamatic.mongodb.annotations.Document;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class LambdamaticDocumentCodecProvider implements CodecProvider {

	/**
	 * {@inheritDoc}
	 * @see org.bson.codecs.configuration.CodecProvider#get(java.lang.Class, org.bson.codecs.configuration.CodecRegistry)
	 */
	@Override
	public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {
		if(clazz != null && clazz.getAnnotation(Document.class) != null) {
			//FIXME: the mappings should not be computed each time, so they need to be pulled out of the LambdamaticDocumentCodec class
			final LambdamaticDocumentCodec<T> lambdamaticDocumentCodec = new LambdamaticDocumentCodec<T>(clazz);
			return lambdamaticDocumentCodec;
		}
		return null;
	}

}
