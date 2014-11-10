package org.lambdamatic.mongodb;

import javax.annotation.Generated;

import org.lambdamatic.mongodb.Collection;
import org.lambdamatic.mongodb.CollectionImpl;

import com.mongodb.MongoClient;
import com.sample.BikeStation;
import com.sample.BikeStation_;
import com.sample.User;
import com.sample.User_;

/**
 * The generated DataStore class, that provides users with access to the declared MongoDB {@link Collection}s.
 *
 * 
 */
@Generated(value="org.lambdamatic.apt.LambdamaticAnnotationsProcessor")
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
   * @return the Users Collection
   */
  public final Collection<User, User_>  getUsers() { 
    return new CollectionImpl<User, User_>(mongoClient, "lambdamatic-tests", "users", User.class, User_.class);
  }

  /**
   * @return the BikeStations Collection
   */
  public final Collection<BikeStation, BikeStation_>  getBikeStations() { 
	  return new CollectionImpl<BikeStation, BikeStation_>(mongoClient, "lambdamatic-tests", "bikeStations", BikeStation.class, BikeStation_.class);
  }
  
}
