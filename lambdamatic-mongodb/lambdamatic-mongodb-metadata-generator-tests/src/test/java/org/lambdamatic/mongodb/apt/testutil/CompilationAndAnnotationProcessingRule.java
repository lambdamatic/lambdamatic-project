/**
 * 
 */
package org.lambdamatic.mongodb.apt.testutil;

import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.tools.StandardLocation.SOURCE_OUTPUT;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.lambdamatic.mongodb.apt.DocumentAnnotationProcessor;
import org.lambdamatic.mongodb.apt.EmbeddedDocumentAnnotationProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Xavier Coulon
 *
 */
public class CompilationAndAnnotationProcessingRule implements MethodRule {

	/** The logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CompilationAndAnnotationProcessingRule.class);
	
	/** The "src/test/java" folder in which all test sources should be found. */
	private final static File SRC_TEST_JAVA_FOLDER = toFolder("src/test/java");
	/** The "target/generated-test-sources/test-annotations" folder in which all test sources should be found. */
	private final static File TARGET_GENERATED_TEST_SOURCES_FOLDER = toFolder("target/generated-test-sources/test-annotations");
	/** The "target/test-classes/" folder in which all test sources should be found. */
	private final static File TARGET_TEST_CLASSES_FOLDER = toFolder("target/test-classes");

	/**
	 * Converts the given {@code pathFragments} into a file relative to {@code System.getProperty("user.dir")}.
	 * 
	 * @param relativePath
	 *            the path fragments
	 * @return the file (or directory)
	 */
	private static File toFolder(final String relativePath) {
		final String[] pathFragments = relativePath.split("/");
		final File folder = Paths.get(System.getProperty("user.dir"), pathFragments).toFile();
		assertThat(folder).isDirectory();
		return folder;
	}

	@Override
	public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
		final List<Class<?>> domainClasses = new ArrayList<>();
		final WithDomainClass[] withDomainClassAnnotations = method.getMethod().getAnnotationsByType(
				WithDomainClass.class);
		for (WithDomainClass withDomainClassAnnotation : withDomainClassAnnotations) {
			domainClasses.add(withDomainClassAnnotation.value());
		}
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				cleanGeneratedSources();
				processAndCompile(domainClasses);
				base.evaluate();
			}

		};
	}

	/**
	 * Clean the {@code TARGET_GENERATED_TEST_SOURCES_FOLDER folder} before running the test.
	 * 
	 * @throws IOException
	 */
	private void cleanGeneratedSources() throws IOException {
		if (TARGET_GENERATED_TEST_SOURCES_FOLDER.exists()) {
			if (TARGET_GENERATED_TEST_SOURCES_FOLDER.isFile()) {
				TARGET_GENERATED_TEST_SOURCES_FOLDER.delete();
				Files.createDirectory(TARGET_GENERATED_TEST_SOURCES_FOLDER.toPath());
			} else {
				Files.walk(TARGET_GENERATED_TEST_SOURCES_FOLDER.toPath(), Integer.MAX_VALUE,
						FileVisitOption.FOLLOW_LINKS).filter(p -> !p.toFile().equals(TARGET_GENERATED_TEST_SOURCES_FOLDER)).forEach(p -> p.toFile().delete());
			}
		} else {
			Files.createDirectory(TARGET_GENERATED_TEST_SOURCES_FOLDER.toPath());
		}
	}

	/**
	 * Generated the meta classes before the given {@code domainClass} is compiled.
	 * 
	 * @param domainClass
	 *            the domain class with relevant annotations
	 * @throws IOException
	 */
	private void processAndCompile(final List<Class<?>> domainClasses) throws IOException {
		// configuration
		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		final CompilationDiagnosticListener diagnosticListener = new CompilationDiagnosticListener();
		final StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticListener, null, null);
		fileManager.setLocation(SOURCE_OUTPUT, Arrays.asList(TARGET_GENERATED_TEST_SOURCES_FOLDER));
		fileManager.setLocation(CLASS_OUTPUT, Arrays.asList(TARGET_TEST_CLASSES_FOLDER));
		final List<File> domainSourceFiles = new ArrayList<>();
		for (Class<?> domainClass : domainClasses) {
			final String domainSourceFileName = domainClass.getName().replace(".", File.separator) + ".java";
			final File domainSourceFile = new File(SRC_TEST_JAVA_FOLDER, domainSourceFileName);
			assertThat(domainSourceFile).exists();
			domainSourceFiles.add(domainSourceFile);
		}
		final Iterable<? extends JavaFileObject> filesToCompile = fileManager
				.getJavaFileObjectsFromFiles(domainSourceFiles);
		final CompilationTask basicCompilationTask = compiler.getTask(null, fileManager, diagnosticListener, null,
				null, filesToCompile);
		basicCompilationTask.call();
		// final List<String> options = Arrays.asList("-proc:only");
		final CompilationTask aptCompilationTask = compiler.getTask(null, fileManager, diagnosticListener, null, null,
				filesToCompile);
		aptCompilationTask.setProcessors(Arrays.asList(new DocumentAnnotationProcessor(), new EmbeddedDocumentAnnotationProcessor()));
		// operation
		aptCompilationTask.call();
	}
	
	/**
	 * Listens to annotation processing and compilation events and logs the relevant bits
	 * 
	 * @author Xavier Coulon <xcoulon@redhat.com>
	 *
	 */
	public class CompilationDiagnosticListener implements DiagnosticListener<JavaFileObject>{

		@Override
		public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
			switch(diagnostic.getKind()) {
			case NOTE:
				LOGGER.debug("{}: {} at line {}", diagnostic.getKind(), diagnostic.getMessage(null), diagnostic.getLineNumber());
				break;
			case MANDATORY_WARNING:
			case WARNING:
				LOGGER.warn("{}: {} at line {}", diagnostic.getKind(), diagnostic.getMessage(null), diagnostic.getLineNumber());
				break;
			case ERROR:
				LOGGER.error("{}: {} at {}:{}", diagnostic.getKind(), diagnostic.getMessage(null), (diagnostic.getSource() != null ? diagnostic.getSource().getName() : "unknown source"), diagnostic.getLineNumber());
				break;
			default:
				LOGGER.info("{}: {} at line {}", diagnostic.getKind(), diagnostic.getMessage(null), diagnostic.getLineNumber());
				break;
			}
		}
		
	}

}

