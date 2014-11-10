/**
 * 
 */
package com.sample;

import java.util.Date;

import org.bson.types.ObjectId;
import org.lambdamatic.mongodb.annotations.Document;
import org.lambdamatic.mongodb.types.geospatial.Point;

/**
 * A Bike Station document
 * 
 * @author Xavier Coulon
 *
 */
@Document(collection="bikestations")
public class BikeStation {
	
	private ObjectId id;
	
	private String stationName;
	
	private int availableDocks;

	private int totalDocks;

	private int availableBikes;
	
	private Point location;
	
	private BikeStationStatus status;

	private boolean testStation;

	private Date executionTime;
	
	/**
	 * Empty Constructor
	 */
	public BikeStation() {
		
	}
	
	/**
	 * Full constructor
	 * @param id
	 * @param stationName
	 * @param availableDocks
	 * @param totalDocks
	 * @param availableBikes
	 * @param latitude
	 * @param longitude
	 * @param status
	 * @param testStation
	 * @param executionTime
	 */
	public BikeStation(final ObjectId id, final String stationName, final int availableDocks, final int totalDocks, final int availableBikes,
			final Double latitude, final Double longitude, final BikeStationStatus status, final boolean testStation, final Date executionTime) {
		super();
		this.id = id;
		this.stationName = stationName;
		this.availableDocks = availableDocks;
		this.totalDocks = totalDocks;
		this.availableBikes = availableBikes;
		this.location = new Point(longitude, latitude);
		this.status = status;
		this.testStation = testStation;
		this.executionTime = executionTime;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(final ObjectId id) {
		this.id = id;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(final String stationName) {
		this.stationName = stationName;
	}

	public int getAvailableDocks() {
		return availableDocks;
	}

	public void setAvailableDocks(final int availableDocks) {
		this.availableDocks = availableDocks;
	}

	public int getTotalDocks() {
		return totalDocks;
	}

	public void setTotalDocks(final int totalDocks) {
		this.totalDocks = totalDocks;
	}

	public int getAvailableBikes() {
		return availableBikes;
	}

	public void setAvailableBikes(final int availableBikes) {
		this.availableBikes = availableBikes;
	}

	public Point getLocation() {
		return location;
	}
	
	public void setLocation(final Point location) {
		this.location = location;
	}

	public BikeStationStatus getStatus() {
		return status;
	}

	public void setStatus(final BikeStationStatus status) {
		this.status = status;
	}

	public boolean isTestStation() {
		return testStation;
	}
	
	public void setTestStation(final boolean testStation) {
		this.testStation = testStation;
	}

	public Date getExecutionTime() {
		return executionTime;
	}
	
	public void setExecutionTime(final Date executionTime) {
		this.executionTime = executionTime;
	}
	
	@Override
	public String toString() {
		return "Bike station " + stationName + " available docks:" + availableDocks + " / available bikes:" + availableBikes;
	}
	
	
}

