package com.sample;

import javax.annotation.Generated;

import org.lambdamatic.mongodb.LambdamaticMongoCollection;
import org.lambdamatic.mongodb.LambdamaticMongoCollectionImpl;

import com.mongodb.MongoClient;

@Generated(value="org.lambdamatic.mongodb.apt.LambdamaticAnnotationsProcessor")
public class BikeStationCollection extends LambdamaticMongoCollectionImpl<BikeStation, BikeStation_> {

	public BikeStationCollection(final MongoClient mongoClient, final String databaseName) {
		super(mongoClient, databaseName, "bikeStations", BikeStation.class, BikeStation_.class);
	}

}
