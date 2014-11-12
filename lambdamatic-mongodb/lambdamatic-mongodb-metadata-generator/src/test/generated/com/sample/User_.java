package com.sample;

import javax.annotation.Generated;
import org.lambdamatic.mongodb.metadata.Metadata;

import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.annotations.FetchType;

/**
 * The {@link Metadata} class associated with the {@link User} domain class.
 *
 */
@Generated(value="org.lambdamatic.mongodb.apt.LambdamaticAnnotationsProcessor")
public class User_ implements Metadata<User> {

  @DocumentField(name="id", fetch=FetchType.EAGER)
  public org.lambdamatic.mongodb.metadata.ObjectIdField id;
  
  @DocumentField(name="", fetch=FetchType.EAGER)
  public org.lambdamatic.mongodb.metadata.StringField username;
  
  @DocumentField(name="", fetch=FetchType.EAGER)
  public org.lambdamatic.mongodb.metadata.StringField firstName;
  
  @DocumentField(name="", fetch=FetchType.EAGER)
  public org.lambdamatic.mongodb.metadata.StringField lastName;
  
}
