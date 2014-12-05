package com.sample;

import com.mongodb.MongoClient;
import javax.annotation.Generated;
import javax.enterprise.inject.Produces;
import javax.enterprise.context.ApplicationScoped;

/**
 * CDI Producer for FooCollection
 * 
 */
@Generated(value="org.lambdamatic.mongodb.apt.LambdamaticAnnotationsProcessor")
@ApplicationScoped
public class FooCollectionProducer  {
	
    /**
     * Creates and returns a new instance of FooCollection
     *
     * @param mongoClient the {@link MongoClient}.
     * @param databaseName the the name of the database to connect to.
     * @return a new instance of FooCollection 
     */
    @Produces
    public FooCollection getFooCollection(final MongoClient mongoClient, final String databaseName) {
    	return new FooCollection(mongoClient, databaseName);
    }

}
