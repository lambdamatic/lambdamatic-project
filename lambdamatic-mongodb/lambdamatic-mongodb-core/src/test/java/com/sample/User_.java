package com.sample;

import javax.annotation.Generated;

import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.metadata.Metadata;

/**
 * //TODO: add some javadoc
 *
 * 
 */
@Generated(value="org.lambdamatic.apt.LambdamaticAnnotationsProcessor")
public class User_ implements Metadata<User> {

  @DocumentField(name="firstName")
  public org.lambdamatic.mongodb.metadata.StringField firstName;
  
  @DocumentField(name="lastName")
  public org.lambdamatic.mongodb.metadata.StringField lastName;
  
  @DocumentField(name="userName")
  public org.lambdamatic.mongodb.metadata.StringField userName;
  
}
