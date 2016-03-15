package com.wat.locationsenderapp;

public class Location {
	
	private String latitude;
	private String longitude;
	
	
	
	
	public Location() {
		super();
	}




	public Location(String latitude, String longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}




	public String getLatitude() {
		return latitude;
	}




	public String getLongitude() {
		return longitude;
	}




	@Override
	public String toString() {
		return "Location [latitude=" + latitude + ", longitude=" + longitude
				+ "]";
	}




	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}




	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	
	
	

}
