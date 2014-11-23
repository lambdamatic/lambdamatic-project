/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.converters;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.lambdamatic.FilterExpression;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class LambdamaticFilterExpressionCodec implements Codec<FilterExpression<?>> {

	@Override
	public Class<FilterExpression<?>> getEncoderClass() {
		return null;
	}
	
	@Override
	public void encode(BsonWriter writer, FilterExpression<?> expression, EncoderContext encoderContext) {
		// TODO Auto-generated method stub

	}

	@Override
	public FilterExpression<?> decode(BsonReader reader, DecoderContext decoderContext) {
		// TODO Auto-generated method stub
		return null;
	}

}
