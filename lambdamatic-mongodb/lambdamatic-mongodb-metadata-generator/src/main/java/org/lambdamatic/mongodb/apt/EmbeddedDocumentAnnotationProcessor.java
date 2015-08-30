/**
 * 
 */
package org.lambdamatic.mongodb.apt;

import java.io.IOException;
import java.util.Map;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.QueryMetadata;
import org.lambdamatic.mongodb.metadata.UpdateMetadata;

/**
 * Processor for classes annotated with {@code EmbeddedDocument}. Generates their associated {@link QueryMetadata} and
 * {@link ProjectionMetadata} implementation classes in the target folder given in the constructor.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({ "org.lambdamatic.mongodb.annotations.EmbeddedDocument" })
//FIXME: the EmbeddedDocumentAnnotationProcessor should actually prevent having @DocumentId, instead of accepting it 
public class EmbeddedDocumentAnnotationProcessor extends BaseAnnotationProcessor {

	/**
	 * Constructor
	 * 
	 * @throws IOException
	 *             if templates could not be loaded.
	 */
	public EmbeddedDocumentAnnotationProcessor() throws IOException {
		super();
	}

	/**
	 * Generates the {@code QueryMetadata}, {@link ProjectionMetadata} and {@link UpdateMetadata} implementation sources for the annotated
	 * class currently being processed.
	 * @param allContextProperties
	 *            all properties to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	@Override
	protected void doProcess(final TypeElement domainElement, final Map<String, Object> templateContextProperties) throws IOException {
		generateQueryMetadataSourceCode(domainElement, templateContextProperties);
		generateProjectionMetadataSourceCode(domainElement, templateContextProperties);
		generateUpdateMetadataSourceCode(domainElement, templateContextProperties);
	}

}
