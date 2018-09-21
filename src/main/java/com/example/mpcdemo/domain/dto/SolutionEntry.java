package com.example.mpcdemo.domain.dto;

import com.example.mpcdemo.domain.RockShow;

public class SolutionEntry {
	
	public static SolutionEntry createFromRockShow(RockShow rockShow) {
		SolutionEntry solutionEntry = new SolutionEntry();
		solutionEntry.venueName = rockShow.getVenueName();
		//NOT null checks/ guards
		solutionEntry.cityName = rockShow.getLocation().getCityName();
		solutionEntry.latitude = rockShow.getLocation().getLatitude();
		solutionEntry.longitude = rockShow.getLocation().getLongitude();
		solutionEntry.revenueOpportunity = rockShow.getRevenueOpportunity();
		return solutionEntry;
	}
	public String venueName;
	//flatten previous rock location
    //private RockLocation location;
    public String cityName;
    public double latitude;
    public double longitude;
    public int revenueOpportunity;
    // TODO maybe add distance to next location??
	
}
