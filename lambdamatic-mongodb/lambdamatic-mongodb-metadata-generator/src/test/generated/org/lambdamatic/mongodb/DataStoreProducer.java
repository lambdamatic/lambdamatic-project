package org.lambdamatic.mongodb;

import com.mongodb.MongoClient;
import javax.annotation.Generated;
import javax.enterprise.inject.Produces;
import javax.enterprise.context.ApplicationScoped;

/**
 * CDI Producer for DataStore
 * 
 */
@Generated(value="org.lambdamatic.mongodb.apt.LambdamaticAnnotationsProcessor")
@ApplicationScoped
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
