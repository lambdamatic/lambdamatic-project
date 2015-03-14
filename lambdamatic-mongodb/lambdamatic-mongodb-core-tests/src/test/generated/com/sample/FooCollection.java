package com.sample;

import javax.annotation.Generated;

import org.lambdamatic.mongodb.internal.LambdamaticMongoCollection;
import org.lambdamatic.mongodb.internal.crud.impl.LambdamaticMongoCollectionImpl;

import com.mongodb.MongoClient;

/**
 * The {@link LambdamaticMongoCollection} implementation associated with the {@link Foo} 
 * domain class and with support for functional @{FilterExpression}. 
 *
 */
@Generated(value="org.lambdamatic.mongodb.internal.apt.DocumentAnnotationProcessor")
public class FooCollection extends LambdamaticMongoCollectionImpl<Foo, QFoo, PFoo> {

	public FooCollection(final MongoClient mongoClient, final String databaseName, final String collectionName) {
		super(mongoClient, databaseName, collectionName, Foo.class);
	}

}