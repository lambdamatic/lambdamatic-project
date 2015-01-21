package $packageName$;

import com.mongodb.MongoClient;
import javax.annotation.Generated;
import javax.enterprise.inject.Produces;
import javax.enterprise.context.ApplicationScoped;

/**
 * CDI Producer for $mongoCollectionClassName$
 * 
 */
@Generated(value="$processorClassName$")
@ApplicationScoped
public class $mongoCollectionProducerClassName$  {
	
    /**
     * Creates and returns a new instance of $mongoCollectionClassName$
     *
     * @param mongoClient the {@link MongoClient}.
     * @param databaseName the the name of the database to connect to.
     * @return a new instance of $mongoCollectionClassName$ 
     */
    @Produces
    public $mongoCollectionClassName$ get$mongoCollectionClassName$(final MongoClient mongoClient, final String databaseName) {
    	return new $mongoCollectionClassName$(mongoClient, databaseName);
    }

}
