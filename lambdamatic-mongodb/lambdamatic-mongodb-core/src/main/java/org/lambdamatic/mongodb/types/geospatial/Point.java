/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.types.geospatial;

/**
 * Geospatial Point location for Spatial Queries
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class Point {

	/** The longitude value.*/
	private double longitude;
	
	/** The latitude value.*/
	private double latitude;

	/**
	 * Default constructor
	 */
	public Point() {
		super();
	}
	
	/**
	 * Full constructor
	 * @param longitude
	 * @param latitude
	 */
	public Point(double longitude, double latitude) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

}

