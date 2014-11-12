package com.sample;

import javax.annotation.Generated;

import org.lambdamatic.mongodb.DBCollection;
import org.lambdamatic.mongodb.DBCollectionImpl;

import com.mongodb.MongoClient;

/**
 * The {@link DBCollection} implementation associated with the {@link User} domain class.
 *
 */
@Generated(value="org.lambdamatic.mongodb.apt.LambdamaticAnnotationsProcessor")
public class UsersCollection extends DBCollectionImpl<User, User_> {

	public UsersCollection(final MongoClient mongoClient, final String databaseName) {
		super(mongoClient, ${dbCollectionName}, User.class, User_.class);
	}

}
