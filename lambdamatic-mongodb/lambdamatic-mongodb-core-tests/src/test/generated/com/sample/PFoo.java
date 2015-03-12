package com.sample;

import javax.annotation.Generated;
import org.lambdamatic.mongodb.annotations.Document;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.metadata.ProjectionField;

/**
 * The {@link ProjectionMetadata} class associated with the {@link Foo} domain class annotated with {@link Document}.
 *
 */
@Generated(value="org.lambdamatic.mongodb.apt.DocumentAnnotationProcessor")
public class PFoo implements ProjectionMetadata<Foo> {

 	public ProjectionField id = new ProjectionField("_id");

 	public ProjectionField stringField = new ProjectionField("stringField");

 	public ProjectionField primitiveByteField = new ProjectionField("primitiveByteField");

 	public ProjectionField primitiveShortField = new ProjectionField("primitiveShortField");

 	public ProjectionField primitiveIntField = new ProjectionField("primitiveIntField");

 	public ProjectionField primitiveLongField = new ProjectionField("primitiveLongField");

 	public ProjectionField primitiveFloatField = new ProjectionField("primitiveFloatField");

 	public ProjectionField primitiveDoubleField = new ProjectionField("primitiveDoubleField");

 	public ProjectionField primitiveBooleanField = new ProjectionField("primitiveBooleanField");

 	public ProjectionField primitiveCharField = new ProjectionField("primitiveCharField");

 	public ProjectionField enumFoo = new ProjectionField("enumFoo");

 	public ProjectionField location = new ProjectionField("location");

 	public PBar bar = new PBar("bar");

 	public ProjectionField date = new ProjectionField("date");


}
