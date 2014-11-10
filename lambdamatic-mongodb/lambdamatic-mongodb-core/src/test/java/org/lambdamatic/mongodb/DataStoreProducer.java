package org.lambdamatic.mongodb;

import javax.annotation.Generated;
import javax.enterprise.inject.Produces;

import com.mongodb.MongoClient;

/**
 * //TODO: add some javadoc
 * 
 */
@Generated(value="org.lambdamatic.apt.LambdamaticAnnotationsProcessor")
public class DataStoreProducer  {
	
    /**
     * //TODO: add some javadoc
     *
     */
    @Produces
    public DataStore getDataStore(final MongoClient mongoClient) {
    	return new DataStore(mongoClient);
    }

}
