package com.example.mpcdemo.domain.dto;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.example.mpcdemo.domain.RockShow;
import com.example.mpcdemo.domain.RockTourSolution;

public class Solution {
	public static Solution createFromSolvedRockTourSolution(RockTourSolution rtSolution) {
		Solution solution = new Solution();
		solution.solutionEntries = rtSolution.getShowList().stream()
				// Sort
				.sorted(new Comparator<RockShow>() {
					@Override
					public int compare(RockShow arg0, RockShow arg1) {
						if (arg0.getDate() == null || arg1.getDate() == null)
							return 0;
						return arg0.getDate().compareTo(arg1.getDate());
					}
				})
				// Convert
				.map(rockShow -> SolutionEntry.createFromRockShow(rockShow)).collect(Collectors.toList());
		return solution;
	}

	public String solutionName;
	public List<SolutionEntry> solutionEntries;
}
