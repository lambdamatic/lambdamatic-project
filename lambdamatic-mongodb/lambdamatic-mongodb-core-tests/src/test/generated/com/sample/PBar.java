package com.sample;

import javax.annotation.Generated;
import org.lambdamatic.mongodb.internal.annotations.EmbeddedDocument;
import org.lambdamatic.mongodb.internal.metadata.ProjectionMetadata;
import org.lambdamatic.mongodb.internal.metadata.ProjectionField;

/**
 * The {@link ProjectionMetadata} class associated with the {@link Bar} domain class annotated with {@link EmbeddedDocument}.
 *
 */
@Generated(value="org.lambdamatic.mongodb.internal.apt.DocumentAnnotationProcessor")
public class PBar extends ProjectionField implements ProjectionMetadata<Bar> {

    /**
	 * Constructor
	 * @param fieldName the name of the field in MongoDB.
	 */
	public PBar(final String fieldName) {
		super(fieldName);
	}

 	public ProjectionField stringField = new ProjectionField("stringField");

 	public ProjectionField primitiveByteField = new ProjectionField("primitiveByteField");

 	public ProjectionField primitiveShortField = new ProjectionField("primitiveShortField");

 	public ProjectionField primitiveIntField = new ProjectionField("primitiveIntField");

 	public ProjectionField primitiveLongField = new ProjectionField("primitiveLongField");

 	public ProjectionField primitiveFloatField = new ProjectionField("primitiveFloatField");

 	public ProjectionField primitiveDoubleField = new ProjectionField("primitiveDoubleField");

 	public ProjectionField primitiveBooleanField = new ProjectionField("primitiveBooleanField");

 	public ProjectionField primitiveCharField = new ProjectionField("primitiveCharField");

 	public ProjectionField enumBar = new ProjectionField("enumBar");

 	public ProjectionField location = new ProjectionField("location");

 	public ProjectionField date = new ProjectionField("date");


}
