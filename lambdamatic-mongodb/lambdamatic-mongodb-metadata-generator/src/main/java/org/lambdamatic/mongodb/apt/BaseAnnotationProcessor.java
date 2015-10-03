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
package org.lambdamatic.mongodb.apt;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.lambdamatic.mongodb.apt.template.BaseTemplateContext;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public abstract class BaseAnnotationProcessor extends AbstractProcessor {

	private final MustacheFactory mf = new DefaultMustacheFactory();
    
	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
		for (TypeElement supportedAnnotation : annotations) {
			for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(supportedAnnotation)) {
				if (annotatedElement instanceof TypeElement) {
					try {
						final TypeElement domainElement = (TypeElement) annotatedElement;
						processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
								"Processing annotated class: " + domainElement.getQualifiedName(), domainElement);
						doProcess(domainElement);
					}
					// catch Throwable to avoid crashing the compiler.
					catch (final Throwable e) {
						final Writer writer = new StringWriter();
						e.printStackTrace(new PrintWriter(writer));
						processingEnv.getMessager().printMessage(
								Diagnostic.Kind.ERROR,
								"Failed to process annotated element '" + annotatedElement.getSimpleName() + "': "
										+ writer.toString());
					}
				}
			}
		}
		return false;
	}

	protected abstract void doProcess(final TypeElement domainElement) throws IOException;

	/**
	 * Generates the source code for the given {@code targetClassName} using the given {@link ST} template.
	 * 
	 * @param templateContext
	 *            the template context to use when running the engine to generate the source code.
	 * 
	 * @throws IOException
	 */
	protected void generateSourceCode(final BaseTemplateContext templateContext) throws IOException {
		final Mustache template = getTemplate(templateContext.getTemplateFileName());
		if (template == null) {
			processingEnv.getMessager().printMessage(
					Diagnostic.Kind.WARNING,
					"Could not create the QueryMetadata class for the given '"
							+ templateContext.getFullyQualifiedClassName() 
							+ "' class: no queryMetadataTemplate available.");
			return;
		}
		final JavaFileObject jfo = processingEnv.getFiler().createSourceFile(templateContext.getFullyQualifiedClassName());
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "creating source file: " + jfo.toUri());
		final Writer writer = jfo.openWriter();
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "applying template: " + template.getName());
		template.execute(writer, templateContext).flush();
		writer.close();
	}
	
	/**
	 * Returns an instance of the {@link Mustache} identified by the given templateName
	 * @param templateName the fully qualified name of the {@link Mustache} to return
	 * @return the {@link Mustache}
	 * 
	 */
	private Mustache getTemplate(final String templateName) {
		return mf.compile("templates" + File.separator + templateName);
	}
	
}