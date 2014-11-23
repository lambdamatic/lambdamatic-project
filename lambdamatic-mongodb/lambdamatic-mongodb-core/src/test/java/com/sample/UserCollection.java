package com.sample;

import javax.annotation.Generated;

import org.lambdamatic.mongodb.LambdamaticMongoCollection;
import org.lambdamatic.mongodb.LambdamaticMongoCollectionImpl;

import com.mongodb.MongoClient;

/**
 * The {@link LambdamaticMongoCollection} implementation associated with the {@link User} domain class and with support
 * for functional @{FilterExpression}. 
 *
 */
@Generated(value="org.lambdamatic.mongodb.apt.LambdamaticAnnotationsProcessor")
public class UserCollection extends LambdamaticMongoCollectionImpl<User, User_> {

	public UserCollection(final MongoClient mongoClient, final String databaseName) {
		super(mongoClient, databaseName, "users", User.class, User_.class);
	}

}
