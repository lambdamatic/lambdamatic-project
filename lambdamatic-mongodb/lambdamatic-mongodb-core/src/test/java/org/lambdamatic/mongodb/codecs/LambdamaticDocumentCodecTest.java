/**
 * 
 */
package org.lambdamatic.mongodb.codecs;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;
import org.bson.json.JsonWriter;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.junit.Ignore;
import org.junit.Test;
import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.codecs.ConversionException;
import org.lambdamatic.mongodb.codecs.DBObjectConverter;
import org.lambdamatic.mongodb.codecs.LambdamaticDocumentCodec;
import org.lambdamatic.mongodb.codecs.LambdamaticFilterExpressionCodec;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sample.BikeStation;
import com.sample.BikeStationStatus;
import com.sample.User;

/**
 * Testing the {@link LambdamaticFilterExpressionCodec}
 * @author xcoulon
 *
 */
public class LambdamaticDocumentCodecTest {
	
	/** The usual Logger.*/
	private static final Logger LOGGER = LoggerFactory.getLogger(LambdamaticDocumentCodecTest.class);
	
	@Test
	public void shouldEncodeUserDocumentWithId() throws IOException, JSONException {
		// given
		final User user = new User(new ObjectId("5459fed60986a72813eb2d59"), "jdoe", "John", "Doe");
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final BsonWriter bsonWriter = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8")); 
		final EncoderContext context = EncoderContext.builder().isEncodingCollectibleDocument(true).build();
		// when
		new LambdamaticDocumentCodec<User>(User.class).encode(bsonWriter, user, context); 
		// then
		final String actual = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
		LOGGER.debug("Output JSON: {}", actual);
		final String expected = "{_id: '5459fed60986a72813eb2d59', _targetClass:'com.sample.User', userName:'jdoe', firstName:'John', lastName:'Doe'}";
		JSONAssert.assertEquals(expected, actual, true);
	}

	@Test
	public void shouldGenerateDocumentIdWhileEncoding() throws IOException, JSONException {
		// given
		final User user = new User("jdoe", "John", "Doe");
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final BsonWriter bsonWriter = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8")); 
		final EncoderContext context = EncoderContext.builder().isEncodingCollectibleDocument(true).build();
		// when
		new LambdamaticDocumentCodec<User>(User.class).encode(bsonWriter, user, context); 
		// then
		final String actual = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
		LOGGER.debug("Output JSON: {}", actual);
		assertThat(user.getId()).isNotNull();
		final String expected = "{_id: '" + user.getId() + "', _targetClass:'com.sample.User', userName:'jdoe', firstName:'John', lastName:'Doe'}";
		JSONAssert.assertEquals(expected, actual, true);
	}
	
	final static Date now = new Date();
	
	private BasicDBObject generateDBObjectWithPrimitiveTypes() {
		return new BasicDBObject().append("booleanField", true).append("shortField", (short) 1).append("intField", 2)
				.append("longField", 3l).append("doubleField", 4.4d).append("floatField", 5.5f)
				.append("stringField", "stringValue").append("dateField", now);
	}

	private BasicDBObject generateDBObjectWithObjectTypes() {
		return new BasicDBObject().append("booleanField", Boolean.TRUE).append("shortField", new Short((short) 1))
				.append("intField", new Integer(2)).append("longField", 3l).append("doubleField", 4.4d)
				.append("floatField", 5.5f).append("stringField", "stringValue").append("dateField", now);
	}

	@Test
	public void shouldConvertObjectWithPrimitiveTypesWithoutAnnotationToDBObject() throws ConversionException {
		// given
		final DBObject dbObject = generateDBObjectWithPrimitiveTypes();
		// when
		final SampleClassWithPrimitiveTypesWithoutAnnotation result = DBObjectConverter.convert(dbObject,
				SampleClassWithPrimitiveTypesWithoutAnnotation.class);
		// then
		assertThat(result.booleanField).isEqualTo(true);
		assertThat(result.shortField).isEqualTo(Short.valueOf("1"));
		assertThat(result.intField).isEqualTo(2);
		assertThat(result.longField).isEqualTo(3l);
		assertThat(result.doubleField).isEqualTo(4.4d);
		assertThat(result.floatField).isEqualTo(5.5f);
		assertThat(result.stringField).isEqualTo("stringValue");
		assertThat(result.dateField).isEqualTo(now);
	}

	@Test
	public void shouldConvertObjectWithPrimitiveTypesWithAnnotationsToDBObject() throws ConversionException {
		// given
		final DBObject dbObject = generateDBObjectWithPrimitiveTypes();
		// when
		final SampleClassWithPrimitiveTypesWithAnnotations result = DBObjectConverter.convert(dbObject,
				SampleClassWithPrimitiveTypesWithAnnotations.class);
		// then
		assertThat(result._booleanField).isEqualTo(true);
		assertThat(result._shortField).isEqualTo(Short.valueOf("1"));
		assertThat(result._intField).isEqualTo(2);
		assertThat(result._longField).isEqualTo(3l);
		assertThat(result._doubleField).isEqualTo(4.4d);
		assertThat(result._floatField).isEqualTo(5.5f);
		assertThat(result._stringField).isEqualTo("stringValue");
		assertThat(result._dateField).isEqualTo(now);
	}

	@Test
	public void shouldConvertObjectWithObjectTypesWithoutAnnotationsToDBObject() {
		// given
		final DBObject dbObject = generateDBObjectWithObjectTypes();
		// when
		final SampleClassWithObjectTypesWithoutAnnotation result = DBObjectConverter.convert(dbObject,
				SampleClassWithObjectTypesWithoutAnnotation.class);
		// then
		assertThat(result.booleanField).isEqualTo(true);
		assertThat(result.shortField).isEqualTo(Short.valueOf("1"));
		assertThat(result.intField).isEqualTo(2);
		assertThat(result.longField).isEqualTo(3l);
		assertThat(result.doubleField).isEqualTo(4.4d);
		assertThat(result.floatField).isEqualTo(5.5f);
		assertThat(result.stringField).isEqualTo("stringValue");
		assertThat(result.dateField).isEqualTo(now);
	}

	@Test
	public void shouldConvertObjectWithObjectTypesWithAnnotationsToDBObject() {
		// given
		final DBObject dbObject = generateDBObjectWithObjectTypes();
		// when
		final SampleClassWithObjectTypeWithsAnnotations result = DBObjectConverter.convert(dbObject,
				SampleClassWithObjectTypeWithsAnnotations.class);
		// then
		assertThat(result._booleanField).isEqualTo(true);
		assertThat(result._shortField).isEqualTo(Short.valueOf("1"));
		assertThat(result._intField).isEqualTo(2);
		assertThat(result._longField).isEqualTo(3l);
		assertThat(result._doubleField).isEqualTo(4.4d);
		assertThat(result._floatField).isEqualTo(5.5f);
		assertThat(result._stringField).isEqualTo("stringValue");
		assertThat(result._dateField).isEqualTo(now);
	}

	@Test
	@Ignore
	public void shouldConvertObjectWithArraysAndListsOfObjectTypesToDBObject() {
		// given

		// when

		// then
	}

	@Test
	@Ignore
	public void shouldConvertObjectWithNestedElementsToDBObject() {
		// given

		// when

		// then
	}

	@Test
	public void shouldConvertObjectWithEnumElementsToDBObject() {
		// given
		final BikeStation domainInstance = new BikeStation();
		domainInstance.setStatus(BikeStationStatus.IN_SERVICE);
		// when
		final DBObject result = DBObjectConverter.convert(domainInstance);
		// then
		assertThat(result.get("status")).isEqualTo("IN_SERVICE");
	}

	@Test
	public void shouldConvertDBObjectToSampleClassWithPrimitiveTypesWithoutAnnotation() {
		// given
		final SampleClassWithPrimitiveTypesWithoutAnnotation domainInstance = new SampleClassWithPrimitiveTypesWithoutAnnotation();
		domainInstance.booleanField = true;
		domainInstance.dateField = now;
		domainInstance.doubleField = 4.4d;
		domainInstance.floatField = 5.5f;
		domainInstance.intField = 2;
		domainInstance.longField=3l;
		domainInstance.shortField=(short)1;
		domainInstance.stringField="stringValue";
		// when
		final DBObject result = DBObjectConverter.convert(domainInstance);
		// then
		assertThat(result.get("booleanField")).isEqualTo(true);
		assertThat(result.get("shortField")).isEqualTo(Short.valueOf("1"));
		assertThat(result.get("intField")).isEqualTo(2);
		assertThat(result.get("longField")).isEqualTo(3l);
		assertThat(result.get("doubleField")).isEqualTo(4.4d);
		assertThat(result.get("floatField")).isEqualTo(5.5f);
		assertThat(result.get("stringField")).isEqualTo("stringValue");
		assertThat(result.get("dateField")).isEqualTo(now);
		assertThat(result.get("dateField")).isEqualTo(now);
		assertThat(result.get(DBObjectConverter.TARGET_CLASS_FIELD)).isEqualTo(SampleClassWithPrimitiveTypesWithoutAnnotation.class.getName());
	}

	static class SampleClassWithPrimitiveTypesWithoutAnnotation {
		private boolean booleanField;
		private short shortField;
		private int intField;
		private long longField;
		private float floatField;
		private double doubleField;
		private String stringField;
		private Date dateField;
	}

	static class SampleClassWithPrimitiveTypesWithAnnotations {
		@DocumentField(name = "booleanField")
		private boolean _booleanField;
		@DocumentField(name = "shortField")
		private short _shortField;
		@DocumentField(name = "intField")
		private int _intField;
		@DocumentField(name = "longField")
		private long _longField;
		@DocumentField(name = "floatField")
		private float _floatField;
		@DocumentField(name = "doubleField")
		private double _doubleField;
		@DocumentField(name = "stringField")
		private String _stringField;
		@DocumentField(name = "dateField")
		private Date _dateField;
	}

	static class SampleClassWithObjectTypesWithoutAnnotation {
		private Boolean booleanField;
		private Short shortField;
		private Integer intField;
		private Long longField;
		private Float floatField;
		private Double doubleField;
		private String stringField;
		private Date dateField;
	}

	static class SampleClassWithObjectTypeWithsAnnotations {
		@DocumentField(name = "booleanField")
		private Boolean _booleanField;
		@DocumentField(name = "shortField")
		private Short _shortField;
		@DocumentField(name = "intField")
		private Integer _intField;
		@DocumentField(name = "longField")
		private Long _longField;
		@DocumentField(name = "floatField")
		private Float _floatField;
		@DocumentField(name = "doubleField")
		private Double _doubleField;
		@DocumentField(name = "stringField")
		private String _stringField;
		@DocumentField(name = "dateField")
		private Date _dateField;
	}
	
}
