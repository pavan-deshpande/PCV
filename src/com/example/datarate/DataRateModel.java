package com.example.datarate;


public class DataRateModel {

	
	private int id;
	private int distance;
	private double time;
	private double rate;
	public long getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public double getTime() {
		return time;
	}
	public void setTime(double time) {
		this.time = time;
	}
	public double getRate() {
		return rate;
	}
	public void setRate(double time) {
		this.rate = time;
	}
	@Override
	public String toString() {
		return "DataRateModel [id=" + id + ", distance=" + distance + ", time="
				+ time + ", rate=" + rate + "]";
	}
	
	

}
