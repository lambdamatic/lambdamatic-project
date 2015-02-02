package com.sample;

import javax.annotation.Generated;

import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.metadata.Metadata;

/**
 * The {@link Metadata} class associated with the {@link Foo} domain class.
 *
 */
@Generated(value="org.lambdamatic.mongodb.apt.LambdamaticAnnotationsProcessor")
public class Foo_ implements Metadata<Foo> {

  @DocumentField
  public org.lambdamatic.mongodb.metadata.StringField id;
  
  @DocumentField
  public org.lambdamatic.mongodb.metadata.StringField stringField;
  
  @DocumentField
  public byte primitiveByteField;
  
  @DocumentField
  public short primitiveShortField;
  
  @DocumentField
  public int primitiveIntField;
  
  @DocumentField
  public long primitiveLongField;
  
  @DocumentField
  public float primitiveFloatField;
  
  @DocumentField
  public double primitiveDoubleField;
  
  @DocumentField
  public boolean primitiveBooleanField;
  
  @DocumentField
  public char primitiveCharField;
  
  @DocumentField
  public com.sample.EnumFoo enumFoo;
  
  @DocumentField
  public org.lambdamatic.mongodb.metadata.LocationField location;
  
}
