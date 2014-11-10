package com.sample;

import javax.annotation.Generated;
import org.lambdamatic.mongodb.metadata.Metadata;

import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.annotations.FetchType;

/**
 * The {@link Metadata} class associated with the {@link BikeStation} domain class.
 *
 */
@Generated(value="org.lambdamatic.mongodb.apt.LambdamaticAnnotationsProcessor")
public class BikeStation_ implements Metadata<BikeStation> {

  @DocumentField(name="id", fetch=FetchType.EAGER)
  public org.lambdamatic.mongodb.metadata.ObjectIdField id;
  
  @DocumentField(name="stationName", fetch=FetchType.EAGER)
  public org.lambdamatic.mongodb.metadata.StringField stationName;
  
  @DocumentField(name="availableDocks", fetch=FetchType.EAGER)
  public int availableDocks;
  
  @DocumentField(name="totalDocks", fetch=FetchType.EAGER)
  public int totalDocks;
  
  @DocumentField(name="availableBikes", fetch=FetchType.EAGER)
  public int availableBikes;
  
  @DocumentField(name="location", fetch=FetchType.EAGER)
  public org.lambdamatic.mongodb.metadata.LocationField location;
  
  @DocumentField(name="status", fetch=FetchType.EAGER)
  public com.sample.BikeStationStatus status;
  
  @DocumentField(name="testStation", fetch=FetchType.EAGER)
  public boolean testStation;
  
  @DocumentField(name="executionTime", fetch=FetchType.EAGER)
  public org.lambdamatic.mongodb.metadata.DateField executionTime;
  
}
