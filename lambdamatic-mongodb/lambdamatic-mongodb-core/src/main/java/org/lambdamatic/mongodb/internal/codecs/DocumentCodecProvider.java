/*******************************************************************************
 * Copyright (c) 2014, 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc. All rights
 * reserved. This program is made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.internal.codecs;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.lambdamatic.mongodb.annotations.Document;

/**
 * {@link CodecProvider} implementation for the {@link DocumentCodec}.
 *
 */
public class DocumentCodecProvider
    implements CodecProvider {

  @Override
  public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {
    if (clazz != null && clazz.getAnnotation(Document.class) != null) {
      final DocumentCodec<T> lambdamaticDocumentCodec = new DocumentCodec<>(clazz, registry);
      return lambdamaticDocumentCodec;
    }
    return null;
  }

}
