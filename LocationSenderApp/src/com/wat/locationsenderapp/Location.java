package com.wat.locationsenderapp;

public class Location {
	
	private String latitude;
	private String longitude;
	private String gForce;
	private String login;
	
	
	
	public String getLogin() {
		return login;
	}




	public void setLogin(String login) {
		this.login = login;
	}




	public String getgForce() {
		return gForce;
	}




	public void setgForce(String gForce) {
		this.gForce = gForce;
	}




	public Location() {
		super();
	}




	public Location(String latitude, String longitude, String gForce,String login) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.gForce = gForce;
		this.login = login;
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
				+" Login: "+ login + "]";
	}




	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}




	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	
	
	

}
