package org.lambdamatic.mongodb;

import com.mongodb.MongoClient;
import javax.annotation.Generated;
import org.lambdamatic.mongodb.Collection;
import org.lambdamatic.mongodb.CollectionImpl;


import com.sample.BikeStation;
import com.sample.BikeStation_;

/**
 * The generated {@link DataStore} class that provides users with access to the declared MongoDB {@link Collection}s.
 *
 * 
 */
@Generated(value="org.lambdamatic.mongodb.apt.LambdamaticAnnotationsProcessor")
public class DataStore  {

  /** The underlying MongoDB Client. */
  private final MongoClient mongoClient;
  
  /**
   * Constructor.
   * @param mongoClient the underlying MongoDB Client
   */
  public DataStore(final MongoClient mongoClient) {
    this.mongoClient = mongoClient;
  }
  
  /**
   * @return the Bikestations Collection
   */
  public final Collection<BikeStation, BikeStation_> getBikestations() { 
    return new CollectionImpl<BikeStation, BikeStation_>(mongoClient, "$databaseName", "Bikestations", BikeStation.class, BikeStation_.class);
  }

}
