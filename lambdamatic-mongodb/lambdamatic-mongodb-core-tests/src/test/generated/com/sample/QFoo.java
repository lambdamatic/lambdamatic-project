package com.sample;

import javax.annotation.Generated;
import org.lambdamatic.mongodb.internal.metadata.QueryMetadata;

import org.lambdamatic.mongodb.internal.annotations.DocumentField;

/**
 * The {@link QueryMetadata} class associated with the {@link Foo} domain class.
 *
 */
@Generated(value="org.lambdamatic.mongodb.internal.apt.DocumentAnnotationProcessor")
public class QFoo implements QueryMetadata<Foo> {

	@DocumentField(name="_id")
 	public org.lambdamatic.mongodb.internal.metadata.ObjectIdField id;

	@DocumentField(name="stringField")
 	public org.lambdamatic.mongodb.internal.metadata.StringField stringField;

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
 	public org.lambdamatic.mongodb.internal.metadata.LocationField location;

	@DocumentField(name="bar")
 	public QBar bar;

	@DocumentField(name="date")
 	public org.lambdamatic.mongodb.internal.metadata.DateField date;


}
