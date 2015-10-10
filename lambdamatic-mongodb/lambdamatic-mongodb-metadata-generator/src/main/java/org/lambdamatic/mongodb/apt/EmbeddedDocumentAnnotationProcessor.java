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

import org.lambdamatic.mongodb.apt.template.MetadataTemplateContext;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.QueryMetadata;
import org.lambdamatic.mongodb.metadata.UpdateMetadata;

/**
 * Processor for classes annotated with {@code EmbeddedDocument}. Generates their associated
 * {@link QueryMetadata}, {@link ProjectionMetadata} and {@link UpdateMetadata} implementation
 * classes in the target folder given in the constructor.
 * 
 * @author Xavier Coulon
 *
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"org.lambdamatic.mongodb.annotations.EmbeddedDocument"})
// FIXME: the EmbeddedDocumentAnnotationProcessor should actually prevent having @DocumentId,
// instead of accepting it
public class EmbeddedDocumentAnnotationProcessor extends BaseAnnotationProcessor {

  /**
   * Generates the {@code QueryMetadata}, {@link ProjectionMetadata} and {@link UpdateMetadata}
   * implementation sources for the annotated class currently being processed.
   * 
   * @param domainType all properties to use when running the engine to generate the source code.
   * 
   * @throws IOException if one of the files could not be generated
   */
  @Override
  protected void doProcess(final TypeElement domainType) throws IOException {
    generateSourceCode(
        MetadataTemplateContext.createQueryMetadataTemplateContext(domainType, this));
    generateSourceCode(
        MetadataTemplateContext.createProjectionMetadataTemplateContext(domainType, this));
    generateSourceCode(
        MetadataTemplateContext.createUpdateMetadataTemplateContext(domainType, this));
  }

}
