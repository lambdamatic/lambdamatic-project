package com.sample;

import javax.annotation.Generated;
import org.lambdamatic.mongodb.metadata.QueryMetadata;

import org.lambdamatic.mongodb.annotations.DocumentField;

/**
 * The {@link QueryMetadata} class associated with the {@link Bar} domain class.
 *
 */
@Generated(value="org.lambdamatic.mongodb.apt.DocumentAnnotationProcessor")
public class QBar implements QueryMetadata<Bar> {

	@DocumentField(name="stringField")
 	public org.lambdamatic.mongodb.metadata.StringField stringField;

	@DocumentField(name="primitiveByteField")
 	public byte primitiveByteField;

	@DocumentField(name="primitiveShortField")
 	public short primitiveShortField;

	@DocumentField(name="primitiveIntField")
 	public int primitiveIntField;

	@DocumentField(name="primitiveLongField")
 	public long primitiveLongField;

	@DocumentField(name="primitiveFloatField")
 	public float primitiveFloatField;

	@DocumentField(name="primitiveDoubleField")
 	public double primitiveDoubleField;

	@DocumentField(name="primitiveBooleanField")
 	public boolean primitiveBooleanField;

	@DocumentField(name="primitiveCharField")
 	public char primitiveCharField;

	@DocumentField(name="enumBar")
 	public com.sample.EnumBar enumBar;

	@DocumentField(name="location")
 	public org.lambdamatic.mongodb.metadata.LocationField location;

	@DocumentField(name="date")
 	public org.lambdamatic.mongodb.metadata.DateField date;


}
