package com.sample;

import com.mongodb.MongoClient;
import javax.annotation.Generated;
import javax.enterprise.inject.Produces;
import javax.enterprise.context.ApplicationScoped;

/**
 * CDI Producer for BikeStationCollection
 * 
 */
@Generated(value="org.lambdamatic.mongodb.apt.LambdamaticAnnotationsProcessor")
@ApplicationScoped
public class BikeStationCollectionProducer  {
	
    /**
     * Creates and returns a new instance of BikeStationCollection
     *
     * @param mongoClient the {@link MongoClient}.
     * @param databaseName the the name of the database to connect to.
     * @return a new instance of BikeStationCollection 
     */
    @Produces
    public BikeStationCollection getBikeStationCollection(final MongoClient mongoClient, final String databaseName) {
    	return new BikeStationCollection(mongoClient, databaseName);
    }

}
