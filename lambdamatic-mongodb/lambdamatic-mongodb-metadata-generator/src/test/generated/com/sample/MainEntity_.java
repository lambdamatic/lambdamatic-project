package com.sample;

import javax.annotation.Generated;
import org.lambdamatic.mongodb.metadata.Metadata;

import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.annotations.FetchType;

/**
 * The {@link Metadata} class associated with the {@link MainEntity} domain class.
 *
 */
@Generated(value="org.lambdamatic.mongodb.apt.LambdamaticAnnotationsProcessor")
public class MainEntity_ implements Metadata<MainEntity> {

  @DocumentField(name="id", fetch=FetchType.EAGER)
  public org.lambdamatic.mongodb.metadata.StringField id;
  
  @DocumentField(name="", fetch=FetchType.EAGER)
  public org.lambdamatic.mongodb.metadata.StringField stringField;
  
  @DocumentField(name="", fetch=FetchType.EAGER)
  public byte primitiveByteField;
  
  @DocumentField(name="", fetch=FetchType.EAGER)
  public short primitiveShortField;
  
  @DocumentField(name="", fetch=FetchType.EAGER)
  public int primitiveIntField;
  
  @DocumentField(name="", fetch=FetchType.EAGER)
  public long primitiveLongField;
  
  @DocumentField(name="", fetch=FetchType.EAGER)
  public float primitiveFloatField;
  
  @DocumentField(name="", fetch=FetchType.EAGER)
  public double primitiveDoubleField;
  
  @DocumentField(name="", fetch=FetchType.EAGER)
  public boolean primitiveBooleanField;
  
  @DocumentField(name="", fetch=FetchType.EAGER)
  public char primitiveCharField;
  
}
