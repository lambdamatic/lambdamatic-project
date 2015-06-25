package org.lamdamatic.jpa.apt;

import java.net.URISyntaxException;

import org.junit.Rule;
import org.junit.Test;
import org.lamdamatic.jpa.apt.testutils.CompilationAndAnnotationProcessingRule;
import org.lamdamatic.jpa.apt.testutils.WithDomainClass;

import com.example.domain.BlogEntry;

public class EntityMetadataGeneratorTest {
	
	@Rule
	public CompilationAndAnnotationProcessingRule generateMetdataClassesRule = new CompilationAndAnnotationProcessingRule();

	@Test
	@WithDomainClass(BlogEntry.class)
	public void shouldGenerateValidCode() throws URISyntaxException, ClassNotFoundException,
			NoSuchFieldException, SecurityException, InstantiationException, IllegalAccessException {
		Class.forName("com.example.domain.QBlogEntry");
	}

}
