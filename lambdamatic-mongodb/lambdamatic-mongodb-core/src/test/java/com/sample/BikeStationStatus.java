/**
 * 
 */
package com.sample;

/**
 * @author Xavier Coulon
 *
 */
public enum BikeStationStatus {

	IN_SERVICE(1), PLANNED(2), NOT_IN_SERVICE(3), UNKNOWN(4);

	public final int key;

	private BikeStationStatus(final int key) {
		this.key = key;
	}

	public static BikeStationStatus valueOf(final int key) {
		switch (key) {
		case 1:
			return IN_SERVICE;
		case 2:
			return PLANNED;
		case 3:
			return NOT_IN_SERVICE;
		default:
			return UNKNOWN;
		}
	}

}

