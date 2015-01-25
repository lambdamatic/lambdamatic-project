package com.sample;

import javax.annotation.Generated;

import org.lambdamatic.mongodb.LambdamaticMongoCollection;
import org.lambdamatic.mongodb.crud.impl.LambdamaticMongoCollectionImpl;

import com.mongodb.MongoClient;

/**
 * The {@link LambdamaticMongoCollection} implementation associated with the {@link Foo} domain class and with support
 * for functional @{FilterExpression}. 
 *
 */
@Generated(value="org.lambdamatic.mongodb.apt.LambdamaticAnnotationsProcessor")
public class FooCollection extends LambdamaticMongoCollectionImpl<Foo, Foo_> {

	public FooCollection(final MongoClient mongoClient, final String databaseName, final String collectionName) {
		super(mongoClient, databaseName, collectionName, Foo.class);
	}

}
