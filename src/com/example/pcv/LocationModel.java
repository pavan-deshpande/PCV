package com.example.pcv;

import java.io.Serializable;

public class LocationModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double latitude;
	private double longitude;
	private double velocity;
	
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getVelocity() {
		return velocity;
	}
	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}
	@Override
	public String toString() {
		return "LocationModel [latitude=" + latitude + ", longitude="
				+ longitude + ", velocity=" + velocity + "]";
	}
	
	
}
