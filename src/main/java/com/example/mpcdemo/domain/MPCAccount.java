package com.example.mpcdemo.domain;

import java.time.LocalDate;

public class MPCAccount implements java.io.Serializable{

	private static final long serialVersionUID = -5302778948678910827L;
	private int accountId;
	private String venueName;
	private String city;
	private Double latitude;
	private Double longitude;
	private LocalDate availableDate;
	private int revenueOpportunity;
	private int requestCount = 0;
	
	public MPCAccount() {
		
	}
	
	public MPCAccount(int accountId, String venueName, String city, Double latitude, Double longitude, String availableDate) {
		super();
		this.accountId = accountId;
		this.venueName = venueName;
		this.city = city;
		this.latitude = latitude;
		this.longitude = longitude;
		this.availableDate = LocalDate.parse(availableDate);
	}
	
	public int getAccountId() {
		return accountId;
	}
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
	public String getVenueName() {
		return venueName;
	}
	public void setVenueName(String venueName) {
		this.venueName = venueName;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public LocalDate getAvailableDate() {
		return availableDate;
	}
	public void setAvailableDate(String availableDate) {
		//TODO: if (availableDate==null) availableDate = "2001-1-1";
		this.availableDate = LocalDate.parse(availableDate);
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getRevenueOpportunity() {
		return revenueOpportunity;
	}

	public void setRevenueOpportunity(int revenueOpportunity) {
		this.revenueOpportunity = revenueOpportunity;
	}

	public int getRequestCount() {
		return requestCount;
	}

	public void setRequestCount(int requestCount) {
		this.requestCount = requestCount;
		this.revenueOpportunity  += this.revenueOpportunity * requestCount;
	}
	
	

	
}
