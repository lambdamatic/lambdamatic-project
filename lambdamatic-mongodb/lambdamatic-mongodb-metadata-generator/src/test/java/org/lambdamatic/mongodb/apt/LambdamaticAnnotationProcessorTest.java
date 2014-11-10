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
import org.lambdamatic.mongodb.metadata.LocationField;
import org.lambdamatic.mongodb.metadata.Metadata;
import org.lambdamatic.mongodb.metadata.StringField;

import com.mongodb.MongoClient;
import com.sample.BikeStation;
import com.sample.BikeStationStatus;
import com.sample.MainEntity;
import com.sample.User;

/**
 * @author Xavier Coulon
 *
 */
public class LambdamaticAnnotationProcessorTest {

	@Rule
	public CompilationAndAnnotationProcessingRule rule = new CompilationAndAnnotationProcessingRule();

	@Test
	@WithDomainClass(MainEntity.class)
	public void shouldProcessMainEntityMetaClass() throws URISyntaxException, ClassNotFoundException,
			NoSuchFieldException, SecurityException {
		// verification
		final Class<?> mainEntityMetaClass = Class.forName("com.sample.MainEntity_");
		ClassAssertion.assertThat(mainEntityMetaClass).isImplementing(Metadata.class.getName(),
				MainEntity.class.getName());
		FieldAssertion.assertThat(mainEntityMetaClass.getDeclaredField("stringField"))
				.isType(StringField.class.getName()).isNotFinal().isNotStatic();
		FieldAssertion.assertThat(mainEntityMetaClass.getDeclaredField("primitiveByteField")).isType("byte")
				.isNotFinal().isNotStatic();
		FieldAssertion.assertThat(mainEntityMetaClass.getDeclaredField("primitiveShortField")).isType("short")
				.isNotFinal().isNotStatic();
		FieldAssertion.assertThat(mainEntityMetaClass.getDeclaredField("primitiveIntField")).isType("int").isNotFinal()
				.isNotStatic();
		FieldAssertion.assertThat(mainEntityMetaClass.getDeclaredField("primitiveLongField")).isType("long")
				.isNotFinal().isNotStatic();
		FieldAssertion.assertThat(mainEntityMetaClass.getDeclaredField("primitiveFloatField")).isType("float")
				.isNotFinal().isNotStatic();
		FieldAssertion.assertThat(mainEntityMetaClass.getDeclaredField("primitiveDoubleField")).isType("double")
				.isNotFinal().isNotStatic();
		FieldAssertion.assertThat(mainEntityMetaClass.getDeclaredField("primitiveBooleanField")).isType("boolean")
				.isNotFinal().isNotStatic();
		FieldAssertion.assertThat(mainEntityMetaClass.getDeclaredField("primitiveCharField")).isType("char")
				.isNotFinal().isNotStatic();

	}

	@Test
	@WithDomainClass(User.class)
	public void shouldProcessUserMetaClass() throws URISyntaxException, ClassNotFoundException, NoSuchFieldException,
			SecurityException {
		// verification
		final Class<?> userMetaClass = Class.forName("com.sample.User_");
		ClassAssertion.assertThat(userMetaClass).isImplementing(Metadata.class.getName(), User.class.getName());
		FieldAssertion.assertThat(userMetaClass.getDeclaredField("username")).isType(StringField.class.getName())
				.isNotFinal().isNotStatic();
	}

	@Test
	@WithDomainClass(User.class)
	public void shouldProcessSingleDomainClassAndGenerateDataStoreAndProducer() throws URISyntaxException,
			ClassNotFoundException, NoSuchFieldException, SecurityException, IOException {
		// verification
		final Class<?> dataStoreClass = Class.forName("org.lambdamatic.mongodb.DataStore");
		ClassAssertion.assertThat(dataStoreClass).hasMethod("getUsers");
		FileAssertion.assertThat(System.getProperty("user.dir"), "src", "test", "generated",
				dataStoreClass.getName().replace('.', File.separatorChar) + ".java").doesNotContain("$");
		// should it rather provide a 'users' public field instead of a getUsers() method ?
		final Class<?> dataStoreProviderClass = Class.forName("org.lambdamatic.mongodb.DataStoreProducer");
		ClassAssertion.assertThat(dataStoreProviderClass).hasMethod("getDataStore", MongoClient.class);

	}

	@Test
	@WithDomainClass(MainEntity.class)
	@WithDomainClass(User.class)
	public void shouldProcessAllDomainClassesAndGenerateDataStoreAndProducer() throws URISyntaxException,
			ClassNotFoundException, NoSuchFieldException, SecurityException {
		// verification
		final Class<?> dataStoreClass = Class.forName("org.lambdamatic.mongodb.DataStore");
		ClassAssertion.assertThat(dataStoreClass).hasMethod("getMainCollection");
		ClassAssertion.assertThat(dataStoreClass).hasMethod("getUsers");

		final Class<?> dataStoreProviderClass = Class.forName("org.lambdamatic.mongodb.DataStoreProducer");
		ClassAssertion.assertThat(dataStoreProviderClass).hasMethod("getDataStore", MongoClient.class);
	}

	@Test
	@WithDomainClass(BikeStation.class)
	public void shouldProcessDomainClassWithEnumAndPointLocation() throws URISyntaxException, ClassNotFoundException,
			NoSuchFieldException, SecurityException {
		// verification
		final Class<?> bikeStationMetaClass = Class.forName("com.sample.BikeStation_");
		ClassAssertion.assertThat(bikeStationMetaClass).isImplementing(Metadata.class.getName(),
				BikeStation.class.getName());
		FieldAssertion.assertThat(bikeStationMetaClass.getDeclaredField("location"))
				.isType(LocationField.class.getName()).isNotFinal().isNotStatic();
		FieldAssertion.assertThat(bikeStationMetaClass.getDeclaredField("status"))
		.isType(BikeStationStatus.class.getName()).isNotFinal().isNotStatic();
	}

	@Ignore
	@Test
	@WithDomainClass(BikeStation.class)
	public void shouldAllowOnlyStringOrObjectIdForDocumentId() {
		fail("Not implemented yet");
	}
}
