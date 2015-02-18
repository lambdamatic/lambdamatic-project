package com.sample;

import javax.annotation.Generated;

import org.lambdamatic.mongodb.metadata.ProjectionField;
import org.lambdamatic.mongodb.metadata.ProjectionMetadata;

/**
 * The {@link ProjectionMetadata} class associated with the {@link Foo} domain class.
 *
 */
@Generated(value="org.lambdamatic.mongodb.apt.LambdamaticAnnotationsProcessor")
public class PFoo implements ProjectionMetadata<Foo> {

 	public final ProjectionField id = new ProjectionField("_id");

 	public final ProjectionField stringField = new ProjectionField("stringField");

 	public final ProjectionField location = new ProjectionField("location");

	// more to come here


}
