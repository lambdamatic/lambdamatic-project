/*******************************************************************************
 * Copyright (c) 2015 Red Hat.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.internal;

import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.lambdamatic.mongodb.internal.codecs.BindingService;
import org.lambdamatic.mongodb.internal.codecs.DocumentCodecProvider;
import org.lambdamatic.mongodb.internal.codecs.FilterExpressionCodecProvider;
import org.lambdamatic.mongodb.internal.codecs.IdFilterCodecProvider;
import org.lambdamatic.mongodb.internal.codecs.ProjectionExpressionCodecProvider;
import org.lambdamatic.mongodb.internal.codecs.UpdateExpressionCodecProvider;

/**
 * @author xcoulon
 *
 */
public class BsonUtils {
	
	/**
	 * Internal cache of bindings to convert domain class instances from/to {@link BsonDocument}.
	 */
	private static final BindingService bindingService = new BindingService();

	/** The registry of the custom {@link Codec}. */
	static final CodecRegistry codecRegistry = CodecRegistries.fromProviders(new DocumentCodecProvider(bindingService),
			new FilterExpressionCodecProvider(), new ProjectionExpressionCodecProvider(),
			new UpdateExpressionCodecProvider(bindingService), new IdFilterCodecProvider(bindingService),
			new BsonValueCodecProvider());;

	/**
	 * Private constructor of this utility class
	 */
	private BsonUtils() {
	}
	
	/**
	 * Converts the given {@link Object} into a {@link BsonDocument} using the registered {@link Codec} in the {@link CodecRegistry}.
	 * @param expression the object to convert
	 * @return the corresponding {@link BsonDocument} or <code>null</code>
	 */
	public static BsonDocument asBsonDocument(final Object expression) {
		return BsonDocumentWrapper.asBsonDocument(expression, codecRegistry);
	}

}
