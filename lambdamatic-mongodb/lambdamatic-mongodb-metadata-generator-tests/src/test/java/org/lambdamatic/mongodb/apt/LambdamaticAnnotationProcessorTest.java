/**
 * 
 */
package org.lambdamatic.mongodb.apt;

import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;
import java.net.URISyntaxException;

import org.bson.types.ObjectId;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.apt.testutil.ClassAssertion;
import org.lambdamatic.mongodb.apt.testutil.CompilationAndAnnotationProcessingRule;
import org.lambdamatic.mongodb.apt.testutil.FieldAssertion;
import org.lambdamatic.mongodb.apt.testutil.WithDomainClass;
import org.lambdamatic.mongodb.internal.LambdamaticMongoCollectionImpl;
import org.lambdamatic.mongodb.internal.codecs.DocumentCodec;
import org.lambdamatic.mongodb.internal.configuration.MongoClientConfiguration;
import org.lambdamatic.mongodb.metadata.LocationField;
import org.lambdamatic.mongodb.metadata.ProjectionField;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.QueryField;
import org.lambdamatic.mongodb.metadata.QueryMetadata;

import com.mongodb.MongoClient;
import com.sample.Bar;
import com.sample.EnumFoo;
import com.sample.Foo;

/**
 * Testing the {@link DocumentAnnotationProcessor}
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class LambdamaticAnnotationProcessorTest {

	@Rule
	public CompilationAndAnnotationProcessingRule generateMetdataClassesRule = new CompilationAndAnnotationProcessingRule();

	@Test
	@WithDomainClass(Foo.class)
	@WithDomainClass(Bar.class)
	public void shouldProcessFooClassAndGenerateQueryMetadataClass() throws URISyntaxException, ClassNotFoundException,
			NoSuchFieldException, SecurityException, InstantiationException, IllegalAccessException {
		// verification
		final Class<?> fooMetaClass = Class.forName("com.sample.QFoo");
		ClassAssertion.assertThat(fooMetaClass).isNotAbstract().isImplementing(QueryMetadata.class, Foo.class);
		FieldAssertion.assertThat(fooMetaClass, "id").isParameterizedType(QueryField.class, ObjectId.class)
				.isNotFinal().isNotStatic().hasAnnotation(DocumentField.class)
				.hasAttributeValue("name", DocumentCodec.MONGOBD_DOCUMENT_ID);
		FieldAssertion.assertThat(fooMetaClass, "stringField").isParameterizedType(QueryField.class, String.class)
				.isNotFinal().isNotStatic().hasAnnotation(DocumentField.class).hasAttributeValue("name", "stringField");
		FieldAssertion.assertThat(fooMetaClass, "primitiveByteField").isParameterizedType(QueryField.class, Byte.class)
				.isNotFinal().isNotStatic().hasAnnotation(DocumentField.class)
				.hasAttributeValue("name", "primitiveByteField");
		FieldAssertion.assertThat(fooMetaClass, "primitiveShortField")
				.isParameterizedType(QueryField.class, Short.class).isNotFinal().isNotStatic()
				.hasAnnotation(DocumentField.class).hasAttributeValue("name", "primitiveShortField");
		FieldAssertion.assertThat(fooMetaClass, "primitiveIntField")
				.isParameterizedType(QueryField.class, Integer.class).isNotFinal().isNotStatic()
				.hasAnnotation(DocumentField.class).hasAttributeValue("name", "primitiveIntField");
		FieldAssertion.assertThat(fooMetaClass, "primitiveLongField").isParameterizedType(QueryField.class, Long.class)
				.isNotFinal().isNotStatic().hasAnnotation(DocumentField.class)
				.hasAttributeValue("name", "primitiveLongField");
		FieldAssertion.assertThat(fooMetaClass, "primitiveFloatField")
				.isParameterizedType(QueryField.class, Float.class).isNotFinal().isNotStatic()
				.hasAnnotation(DocumentField.class).hasAttributeValue("name", "primitiveFloatField");
		FieldAssertion.assertThat(fooMetaClass, "primitiveDoubleField")
				.isParameterizedType(QueryField.class, Double.class).isNotFinal().isNotStatic()
				.hasAnnotation(DocumentField.class).hasAttributeValue("name", "primitiveDoubleField");
		FieldAssertion.assertThat(fooMetaClass, "primitiveBooleanField")
				.isParameterizedType(QueryField.class, Boolean.class).isNotFinal().isNotStatic()
				.hasAnnotation(DocumentField.class).hasAttributeValue("name", "primitiveBooleanField");
		FieldAssertion.assertThat(fooMetaClass, "primitiveCharField")
				.isParameterizedType(QueryField.class, Character.class).isNotFinal().isNotStatic()
				.hasAnnotation(DocumentField.class).hasAttributeValue("name", "primitiveCharField");
		FieldAssertion.assertThat(fooMetaClass, "location").isType(LocationField.class).isNotFinal().isNotStatic()
				.hasAnnotation(DocumentField.class).hasAttributeValue("name", "location");
		FieldAssertion.assertThat(fooMetaClass, "enumFoo").isParameterizedType(QueryField.class, EnumFoo.class)
				.isNotFinal().isNotStatic().hasAnnotation(DocumentField.class).hasAttributeValue("name", "enumFoo");
		FieldAssertion.assertThat(fooMetaClass, "bar").isType("com.sample.QBar").isNotFinal().isNotStatic()
				.hasAnnotation(DocumentField.class).hasAttributeValue("name", "bar");
		fail("Verify stringArray, stringFields and stringSets: QueryArrayField<String, QueryField<String>");
	}

	@Test
	@WithDomainClass(Foo.class)
	@WithDomainClass(Bar.class)
	public void shouldProcessFooClassAndGenerateProjectionMetadataClass() throws URISyntaxException,
			ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, InstantiationException {
		// verification
		final Class<?> fooMetaClass = Class.forName("com.sample.PFoo");
		ClassAssertion.assertThat(fooMetaClass).isNotAbstract().isImplementing(ProjectionMetadata.class, Foo.class)
				.isExtending("java.lang.Object");
		FieldAssertion.assertThat(fooMetaClass, "id").isType(ProjectionField.class).isNotFinal().isNotStatic()
				.hasNoAnnotation().hasDefaultValueEquals(new ProjectionField("_id"));
		FieldAssertion.assertThat(fooMetaClass, "stringField").isType(ProjectionField.class).isNotFinal().isNotStatic()
				.hasNoAnnotation().hasDefaultValueEquals(new ProjectionField("stringField"));
		FieldAssertion.assertThat(fooMetaClass, "primitiveByteField").isType(ProjectionField.class).isNotFinal()
				.isNotStatic().hasNoAnnotation().hasDefaultValueEquals(new ProjectionField("primitiveByteField"));
		FieldAssertion.assertThat(fooMetaClass, "primitiveShortField").isType(ProjectionField.class).isNotFinal()
				.isNotStatic().hasNoAnnotation().hasDefaultValueEquals(new ProjectionField("primitiveShortField"));
		FieldAssertion.assertThat(fooMetaClass, "primitiveIntField").isType(ProjectionField.class).isNotFinal()
				.isNotStatic().hasNoAnnotation().hasDefaultValueEquals(new ProjectionField("primitiveIntField"));
		FieldAssertion.assertThat(fooMetaClass, "primitiveLongField").isType(ProjectionField.class).isNotFinal()
				.isNotStatic().hasNoAnnotation().hasDefaultValueEquals(new ProjectionField("primitiveLongField"));
		FieldAssertion.assertThat(fooMetaClass, "primitiveFloatField").isType(ProjectionField.class).isNotFinal()
				.isNotStatic().hasNoAnnotation().hasDefaultValueEquals(new ProjectionField("primitiveFloatField"));
		FieldAssertion.assertThat(fooMetaClass, "primitiveDoubleField").isType(ProjectionField.class).isNotFinal()
				.isNotStatic().hasNoAnnotation().hasDefaultValueEquals(new ProjectionField("primitiveDoubleField"));
		FieldAssertion.assertThat(fooMetaClass, "primitiveBooleanField").isType(ProjectionField.class).isNotFinal()
				.isNotStatic().hasNoAnnotation().hasDefaultValueEquals(new ProjectionField("primitiveBooleanField"));
		FieldAssertion.assertThat(fooMetaClass, "primitiveCharField").isType(ProjectionField.class).isNotFinal()
				.isNotStatic().hasNoAnnotation().hasDefaultValueEquals(new ProjectionField("primitiveCharField"));
		FieldAssertion.assertThat(fooMetaClass, "location").isType(ProjectionField.class).isNotFinal().isNotStatic()
				.hasNoAnnotation().hasDefaultValueEquals(new ProjectionField("location"));
		FieldAssertion.assertThat(fooMetaClass, "enumFoo").isType(ProjectionField.class).isNotFinal().isNotStatic()
				.hasNoAnnotation().hasDefaultValueEquals(new ProjectionField("enumFoo"));
		FieldAssertion.assertThat(fooMetaClass, "bar").isType("com.sample.PBar").isNotFinal().isNotStatic()
				.hasNoAnnotation();
		// embedded PBar Projection class
		final Class<?> barMetaClass = Class.forName("com.sample.PBar");
		ClassAssertion.assertThat(barMetaClass).isNotAbstract().isImplementing(ProjectionMetadata.class, Bar.class)
				.isExtending(ProjectionField.class);

	}

	@Test
	@WithDomainClass(Foo.class)
	public void shouldProcessSingleDomainClassAndGenerateCollectionAndCollectionProducer() throws URISyntaxException,
			ClassNotFoundException, NoSuchFieldException, SecurityException, IOException {
		// verification
		final Class<?> queryFooClass = Class.forName("com.sample.QFoo");
		ClassAssertion.assertThat(queryFooClass).isNotNull();
		final Class<?> projectionFooClass = Class.forName("com.sample.PFoo");
		ClassAssertion.assertThat(projectionFooClass).isNotNull();
		final Class<?> fooCollectionClass = Class.forName("com.sample.FooCollection");
		ClassAssertion.assertThat(fooCollectionClass).isExtending(LambdamaticMongoCollectionImpl.class, Foo.class,
				queryFooClass, projectionFooClass);
		// should it rather provide a 'users' public field instead of a
		// getUsers() method ?
		final Class<?> userCollectionProducerClass = Class.forName("com.sample.FooCollectionProducer");
		ClassAssertion.assertThat(userCollectionProducerClass).hasMethod("getFooCollection", MongoClient.class,
				MongoClientConfiguration.class);

	}

	@Ignore
	@Test
	// @WithDomainClass(BikeStation.class)
	public void shouldAllowOnlyStringOrObjectIdForDocumentId() {
		fail("Not implemented yet");
	}

	@Ignore
	@Test
	// @WithDomainClass(BikeStation.class)
	public void shouldUseDocumentFieldNameInDomainClass() {
		fail("Not implemented yet - need to use custom mapping name and verify they are included in the generated metaclasses");
	}

}
