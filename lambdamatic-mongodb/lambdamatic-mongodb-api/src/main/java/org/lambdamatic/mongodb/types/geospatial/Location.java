/*******************************************************************************
 * Copyright (c) 2014, 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc. All rights
 * reserved. This program is made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.types.geospatial;

import org.lambdamatic.mongodb.exceptions.ConversionException;

/**
 * Geospatial Location for Spatial Queries.
 * 
 * @author Xavier Coulon
 *
 */
public class Location {

  /** The latitude value. */
  private double latitude;

  /** The longitude value. */
  private double longitude;

  /**
   * Default constructor.
   */
  public Location() {
    super();
  }

  /**
   * Full constructor.
   * 
   * @param latitude the latitude
   * @param longitude the longitude
   */
  public Location(double latitude, double longitude) {
    super();
    this.latitude = latitude;
    this.longitude = longitude;
  }

  /**
   * Converts the given {@link String} value to a {@link Location}, assuming the format is:
   * {@code latitude,longitude} , where {@code latitude} and {@code longitude} are (casted to)
   * double values.
   * <p>
   * Eg: {@code "40.782865,-73.965355"} (Central Park, NYC)
   * </p>
   * <p>
   * This method can be used by JAX-RS 2.0 implementations to unmarshall incoming HTTP reauest
   * parameters.
   * </p>
   * 
   * @param value the value to parse
   * @return a location
   */
  public static Location fromString(final String value) {
    final String[] coordinates = value.split(",");
    try {
      if (coordinates.length == 2) {
        final double latitude = Double.parseDouble(coordinates[0]);
        final double longitude = Double.parseDouble(coordinates[1]);
        return new Location(latitude, longitude);
      }
    } catch (NullPointerException | NumberFormatException e) {
      throw new ConversionException("Failed to convert value '" + value
          + "' to a valid location. Format must be '<latitude>,<logitude>'.", e);
    }
    throw new ConversionException("Failed to convert value '" + value
        + "' to a valid location. Format must be '<latitude>,<logitude>'.");
  }

  /**
   * @return the latitude.
   */
  public double getLatitude() {
    return this.latitude;
  }

  /**
   * Sets the latitude.
   * 
   * @param latitude the latitude to set
   */
  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  /**
   * @return the longitude.
   */
  public double getLongitude() {
    return this.longitude;
  }

  /**
   * @param longitude the longitude to set.
   */
  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  @Override
  public String toString() {
    return "[" + this.latitude + ", " + this.longitude + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(this.latitude);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(this.longitude);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Location other = (Location) obj;
    if (Double.doubleToLongBits(this.latitude) != Double.doubleToLongBits(other.latitude)) {
      return false;
    }
    if (Double.doubleToLongBits(this.longitude) != Double.doubleToLongBits(other.longitude)) {
      return false;
    }
    return true;
  }

}
