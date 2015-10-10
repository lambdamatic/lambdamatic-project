/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.apt;

import java.io.IOException;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import org.lambdamatic.mongodb.annotations.BaseDocument;
import org.lambdamatic.mongodb.apt.template.MetadataTemplateContext;
import org.lambdamatic.mongodb.apt.template.MongoCollectionProducerTemplateContext;
import org.lambdamatic.mongodb.apt.template.MongoCollectionTemplateContext;

/**
 * Processor for classes annotated with {@code Document} or {@link BaseDocument}. Generates their
 * associated metadata Java classes in the target folder given in the constructor.
 * 
 * @author Xavier Coulon
 *
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"org.lambdamatic.mongodb.annotations.Document"})
public class DocumentAnnotationProcessor extends BaseAnnotationProcessor {

  @Override
  protected void doProcess(final TypeElement domainType) throws IOException {
    generateSourceCode(
        MetadataTemplateContext.createQueryMetadataTemplateContext(domainType, this));
    generateSourceCode(
        MetadataTemplateContext.createProjectionMetadataTemplateContext(domainType, this));
    generateSourceCode(
        MetadataTemplateContext.createUpdateMetadataTemplateContext(domainType, this));
    generateSourceCode(new MongoCollectionTemplateContext(domainType, this));
    generateSourceCode(new MongoCollectionProducerTemplateContext(domainType, this));
  }

}
