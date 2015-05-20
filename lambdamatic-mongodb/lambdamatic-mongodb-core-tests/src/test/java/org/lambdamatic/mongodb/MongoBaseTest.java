package org.lambdamatic.mongodb;

import static com.lordofthejars.nosqlunit.mongodb.MongoDbConfigurationBuilder.mongoDb;

import org.junit.Rule;
import org.lambdamatic.mongodb.testutils.DropMongoCollectionsRule;

import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import com.mongodb.MongoClient;
import com.sample.Foo;

public abstract class MongoBaseTest {

	public static final String DATABASE_NAME = "tests";
	public static final String COLLECTION_NAME = ((org.lambdamatic.mongodb.annotations.Document) Foo.class
				.getAnnotation(org.lambdamatic.mongodb.annotations.Document.class)).collection();
	
	protected MongoClient mongoClient = new MongoClient();
	
	@Rule
	public DropMongoCollectionsRule collectionCleaning = new DropMongoCollectionsRule(mongoClient, DATABASE_NAME,
				COLLECTION_NAME);
	@Rule
	public MongoDbRule remoteMongoDbRule = new MongoDbRule(mongoDb().databaseName(DATABASE_NAME).build());

	
}