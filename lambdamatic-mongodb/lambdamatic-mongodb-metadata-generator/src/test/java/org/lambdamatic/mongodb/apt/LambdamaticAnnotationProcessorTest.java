/**
 * 
 */
package org.lambdamatic.mongodb.apt;

import static org.assertj.core.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.lambdamatic.mongodb.apt.testutil.ClassAssertion;
import org.lambdamatic.mongodb.apt.testutil.CompilationAndAnnotationProcessingRule;
import org.lambdamatic.mongodb.apt.testutil.FieldAssertion;
import org.lambdamatic.mongodb.apt.testutil.FileAssertion;
import org.lambdamatic.mongodb.apt.testutil.WithDomainClass;
import org.lambdamatic.mongodb.configuration.MongoClientConfiguration;
import org.lambdamatic.mongodb.crud.impl.LambdamaticMongoCollectionImpl;
import org.lambdamatic.mongodb.metadata.LocationField;
import org.lambdamatic.mongodb.metadata.Metadata;
import org.lambdamatic.mongodb.metadata.StringField;

import com.mongodb.MongoClient;
import com.sample.EnumFoo;
import com.sample.Foo;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class LambdamaticAnnotationProcessorTest {

	@Rule
	public CompilationAndAnnotationProcessingRule rule = new CompilationAndAnnotationProcessingRule();

	@Test
	@WithDomainClass(Foo.class)
	public void shouldProcessFooClassAndGenerateMetaClass() throws URISyntaxException, ClassNotFoundException, NoSuchFieldException,
			SecurityException {
		// verification
		final Class<?> fooMetaClass = Class.forName("com.sample.Foo_");
		ClassAssertion.assertThat(fooMetaClass).isImplementing(Metadata.class.getName(), Foo.class.getName());
		FieldAssertion.assertThat(fooMetaClass.getDeclaredField("stringField")).isType(StringField.class.getName()).isNotFinal()
				.isNotStatic();
		FieldAssertion.assertThat(fooMetaClass.getDeclaredField("primitiveByteField")).isType("byte").isNotFinal().isNotStatic();
		FieldAssertion.assertThat(fooMetaClass.getDeclaredField("primitiveShortField")).isType("short").isNotFinal().isNotStatic();
		FieldAssertion.assertThat(fooMetaClass.getDeclaredField("primitiveIntField")).isType("int").isNotFinal().isNotStatic();
		FieldAssertion.assertThat(fooMetaClass.getDeclaredField("primitiveLongField")).isType("long").isNotFinal().isNotStatic();
		FieldAssertion.assertThat(fooMetaClass.getDeclaredField("primitiveFloatField")).isType("float").isNotFinal().isNotStatic();
		FieldAssertion.assertThat(fooMetaClass.getDeclaredField("primitiveDoubleField")).isType("double").isNotFinal().isNotStatic();
		FieldAssertion.assertThat(fooMetaClass.getDeclaredField("primitiveBooleanField")).isType("boolean").isNotFinal().isNotStatic();
		FieldAssertion.assertThat(fooMetaClass.getDeclaredField("primitiveCharField")).isType("char").isNotFinal().isNotStatic();
		FieldAssertion.assertThat(fooMetaClass.getDeclaredField("location")).isType(LocationField.class.getName()).isNotFinal()
				.isNotStatic();
		FieldAssertion.assertThat(fooMetaClass.getDeclaredField("enumFoo")).isType(EnumFoo.class.getName()).isNotFinal().isNotStatic();
	}

	@Test
	@WithDomainClass(Foo.class)
	public void shouldProcessSingleDomainClassAndGenerateCollectionAndCollectionProducer() throws URISyntaxException,
			ClassNotFoundException, NoSuchFieldException, SecurityException, IOException {
		// verification
		final Class<?> metaFooClass = Class.forName("com.sample.Foo_");
		final Class<?> fooCollectionClass = Class.forName("com.sample.FooCollection");
		ClassAssertion.assertThat(fooCollectionClass).isExtending(LambdamaticMongoCollectionImpl.class, Foo.class, metaFooClass);
		FileAssertion.assertThat(System.getProperty("user.dir"), "src", "test", "generated",
				fooCollectionClass.getName().replace('.', File.separatorChar) + ".java").doesNotContain("$");
		// should it rather provide a 'users' public field instead of a getUsers() method ?
		final Class<?> userCollectionProducerClass = Class.forName("com.sample.FooCollectionProducer");
		ClassAssertion.assertThat(userCollectionProducerClass).hasMethod("getFooCollection", MongoClient.class, MongoClientConfiguration.class);

	}

	@Ignore
	@Test
	//@WithDomainClass(BikeStation.class)
	public void shouldAllowOnlyStringOrObjectIdForDocumentId() {
		fail("Not implemented yet");
	}

}
