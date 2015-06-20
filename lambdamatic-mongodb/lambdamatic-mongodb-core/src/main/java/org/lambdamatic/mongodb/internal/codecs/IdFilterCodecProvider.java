/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.internal.codecs;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.lambdamatic.mongodb.internal.IdFilter;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class IdFilterCodecProvider implements CodecProvider {

	/** The binding service. */
	private final BindingService bindingService;

	/**
	 * Constructor
	 * 
	 * @param bindingService
	 *            the {@link BindingService}.
	 */
	public IdFilterCodecProvider(final BindingService bindingService) {
		this.bindingService = bindingService;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.bson.codecs.configuration.CodecProvider#get(java.lang.Class,
	 *      org.bson.codecs.configuration.CodecRegistry)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {
		if (clazz != null && clazz.equals(IdFilter.class)) {
			return (Codec<T>) new IdFilterCodec(bindingService);
		}
		return null;
	}

}
