/**
 * 
 */
package org.lambdamatic.apt;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import org.lambdamatic.annotations.Document;

/**
 * @author xcoulon
 *
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.lambdamatic.annotations.Document")
public class LambdamaticAnnotationsProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (Element elem : roundEnv.getElementsAnnotatedWith(Document.class)) {
			Document documentAnnotation = elem.getAnnotation(Document.class);
	        String message = "annotation found in " + elem.getSimpleName();
	        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
	    }
	    return true; // no further processing of this annotation type
	}

}
