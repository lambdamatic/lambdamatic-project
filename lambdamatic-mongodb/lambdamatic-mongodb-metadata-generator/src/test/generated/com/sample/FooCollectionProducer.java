package com.sample;

import javax.annotation.Generated;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.lambdamatic.mongodb.configuration.MongoClientConfiguration;

import com.mongodb.MongoClient;


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
     * @param MongoClientConfiguration the client configuration, including the name of the database to connect to.
     * @return a new instance of FooCollection 
     */
    @Produces
    public FooCollection getFooCollection(final MongoClient mongoClient, final MongoClientConfiguration mongoClientConfiguration) {
    	return new FooCollection(mongoClient, mongoClientConfiguration.getDatabaseName(), "foos");
    }

}
