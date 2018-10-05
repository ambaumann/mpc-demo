package com.example.mpcdemo.common;

import java.util.ArrayList;

import com.example.mpcdemo.domain.dto.Solution;
import com.example.mpcdemo.domain.dto.SolutionEntry;

public class TestDataHelper {
	
	public static Solution getTestReturnSolution() {
		Solution solution = new Solution();
		solution.solutionName = "Demo Test Solution";
		
		SolutionEntry se1 = new SolutionEntry();
		se1.venueName = "A cool place";
		se1.cityName = "Blue";
		se1.latitude = 12.1929378;
		se1.longitude = 56.192;
		se1.revenueOpportunity = 1000000;
		
		SolutionEntry se2 = new SolutionEntry();
		se2.venueName = "A cool place";
		se2.cityName = "Blue";
		se2.latitude = 12.1929378;
		se2.longitude = 56.192;
		se2.revenueOpportunity = 1000000;
		
		ArrayList<SolutionEntry> entries = new ArrayList<SolutionEntry>();
		entries.add(se1);
		entries.add(se2);
		
		//TODO: need to replace solutionEntries with accounts<MPCAccount>
		//solution.solutionEntries = entries;
		return null;
	}
}
