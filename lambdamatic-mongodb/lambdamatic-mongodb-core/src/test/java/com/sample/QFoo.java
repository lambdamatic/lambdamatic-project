package com.sample;

import javax.annotation.Generated;
import org.lambdamatic.mongodb.metadata.QueryMetadata;

import org.lambdamatic.mongodb.annotations.DocumentField;

/**
 * The {@link QueryMetadata} class associated with the {@link Foo} domain class.
 *
 */
@Generated(value="org.lambdamatic.mongodb.apt.LambdamaticAnnotationsProcessor")
public class QFoo implements QueryMetadata<Foo> {

	@DocumentField(name="_id")
 	public org.lambdamatic.mongodb.metadata.StringField id;

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

	@DocumentField(name="enumFoo")
 	public com.sample.EnumFoo enumFoo;

	@DocumentField(name="location")
 	public org.lambdamatic.mongodb.metadata.LocationField location;


}
