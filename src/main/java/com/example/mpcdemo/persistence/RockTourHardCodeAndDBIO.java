package com.example.mpcdemo.persistence;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import com.example.mpcdemo.domain.RockBus;
import com.example.mpcdemo.domain.RockLocation;
import com.example.mpcdemo.domain.RockShow;
import com.example.mpcdemo.domain.RockTourParametrization;
import com.example.mpcdemo.domain.RockTourSolution;

/**
 * Class that handles reading hard coded initial solution **as if the solution
 * had come from elsewhere
 *
 */
public class RockTourHardCodeAndDBIO {

	public static RockTourSolution read() {
		RockTourSolution solution = new RockTourSolution();
		readConfiguration(solution);
		readBus(solution);
		readShowList(solution);
		readDrivingTime(solution);
		return solution;
	}

	private static final Pattern VALID_TAG_PATTERN = Pattern
			.compile("(?U)^[\\w&\\-\\.\\/\\(\\)\\'][\\w&\\-\\.\\/\\(\\)\\' ]*[\\w&\\-\\.\\/\\(\\)\\']?$");
	private static final Pattern VALID_NAME_PATTERN = VALID_TAG_PATTERN;

	private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("E yyyy-MM-dd", Locale.ENGLISH);

	private static void readConfiguration(RockTourSolution solution) {
		solution.setTourName("MPC Demo");

		if (!VALID_NAME_PATTERN.matcher(solution.getTourName()).matches()) {
			throw new IllegalStateException("The tour name (" + solution.getTourName()
					+ ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
		}
		RockTourParametrization parametrization = new RockTourParametrization();
		// "Maximum driving time in seconds between 2 shows on the same day."
		parametrization.setEarlyLateBreakDrivingSecondsBudget(3600);
		// "Maximum driving time in seconds per night between 2 shows."
		parametrization.setNightDrivingSecondsBudget(25200);
		// "Maximum driving time in seconds since last weekend rest."
		parametrization.setHosWeekDrivingSecondsBudget(180000);
		// "Maximum driving days since last weekend rest."
		parametrization.setHosWeekConsecutiveDrivingDaysBudget(7);
		// "Minimum weekend rest in days (actually in full night sleeps: 2 days
		// guarantees only 32 hours)."
		parametrization.setHosWeekRestDays(2);
		// "Set this to 1 to prioritize visiting all shows (over the other
		// constraints)."
		parametrization.setMissedShowPenalty(0);
		// "Reward per revenue opportunity."
		parametrization.setRevenueOpportunity(1);
		// "Driving time cost per second."
		parametrization.setDrivingTimeCostPerSecond(1);
		// "Cost per day for each day that a visit is later in the schedule."
		parametrization.setDelayCostPerDay(30);
		parametrization.setId(1L);
		solution.setParametrization(parametrization);
	}

	private static void readBus(RockTourSolution solution) {
		RockBus bus = new RockBus();
		String startCityName = "Montgomery, Alabama";
		double startLatitude = 32.377716;
		double startLongitude = -86.300568;
		bus.setStartLocation(new RockLocation(startCityName, startLatitude, startLongitude));
		bus.setStartDate(LocalDate.parse("Thu 2018-02-01", DAY_FORMATTER));

		String endCityName = "Montgomery, Alabama";
		double endLatitude = 32.377716;
		double endLongitude = -86.300568;
		bus.setEndLocation(new RockLocation(endCityName, endLatitude, endLongitude));
		bus.setEndDate(LocalDate.parse("Sat 2018-12-01", DAY_FORMATTER));
		bus.setId(1L);
		solution.setBus(bus);

	}

	private static void readShowList(RockTourSolution solution) {
		LocalDate startDate = solution.getBus().getStartDate();
		LocalDate endDate = solution.getBus().getEndDate();
		// TODO maybe compare hard set dates are in between start and end?
		List<RockShow> showList = new ArrayList<>();
		showList.add(one());
		showList.add(two());
		showList.add(three());
		showList.add(four());
		showList.add(five());
		showList.add(six());
		showList.add(seven());
		showList.add(eight());
		showList.add(nine());
		showList.add(ten());
		solution.setShowList(showList);

	}

	private static RockShow one() {
		NavigableSet<LocalDate> availableDateSet = new TreeSet<>();
		availableDateSet.add(LocalDate.of(2018, 10, 15));
		availableDateSet.add(LocalDate.of(2018, 11, 29));
		return createRockShow(1L, "A1", "Phoenix, California", 33.4484, -112.074, 1.0, 100000, true, availableDateSet);
	}

	private static RockShow two() {
		NavigableSet<LocalDate> availableDateSet = new TreeSet<>();
		availableDateSet.add(LocalDate.of(2018, 10, 18));
		return createRockShow(2L, "V2", "Foster City, California", 37.5585, -122.2711, 1.0, 13000000, true,
				availableDateSet);
	}

	private static RockShow three() {
		NavigableSet<LocalDate> availableDateSet = new TreeSet<>();
		availableDateSet.add(LocalDate.of(2018, 10, 17));
		return createRockShow(3L, "E3", "Dallas, Texas", 32.7767, -96.797, 0.5, 2400000, true, availableDateSet);
	}

	private static RockShow four() {
		NavigableSet<LocalDate> availableDateSet = new TreeSet<>();
		availableDateSet.add(LocalDate.of(2018, 10, 22));
		return createRockShow(4L, "P4", "Mexico City, Mexico", 19.4326, -99.1332, 1.0, 2000000, true, availableDateSet);
	}

	private static RockShow five() {
		NavigableSet<LocalDate> availableDateSet = new TreeSet<>();
		availableDateSet.add(LocalDate.of(2018, 10, 24));
		return createRockShow(5L, "D5", "Chicago, Illinois", 41.8781, -87.6298, 0.5, 1900000, true, availableDateSet);
	}

	private static RockShow six() {
		NavigableSet<LocalDate> availableDateSet = new TreeSet<>();
		availableDateSet.add(LocalDate.of(2018, 10, 23));
		return createRockShow(6L, "M6", "Rochester, New York", 39.1031, -84.512, 1.0, 100000, true, availableDateSet);
	}

	private static RockShow seven() {
		NavigableSet<LocalDate> availableDateSet = new TreeSet<>();
		availableDateSet.add(LocalDate.of(2018, 10, 25));
		return createRockShow(7L, "R7", "Toronto, Canada", 43.6532, -79.3832, 1.0, 2400000, true, availableDateSet);
	}

	private static RockShow eight() {
		NavigableSet<LocalDate> availableDateSet = new TreeSet<>();
		availableDateSet.add(LocalDate.of(2018, 10, 16));
		return createRockShow(8L, "C8", "New York, New York", 40.7128, -74.006, 1.0, 2200000, true, availableDateSet);
	}

	private static RockShow nine() {
		NavigableSet<LocalDate> availableDateSet = new TreeSet<>();
		availableDateSet.add(LocalDate.of(2018, 10, 19));
		return createRockShow(9L, "9D", "Atlanta, Georgia", 33.749027, -84.388229, 1.0, 10000000, true,
				availableDateSet);
	}

	private static RockShow ten() {
		NavigableSet<LocalDate> availableDateSet = new TreeSet<>();
		availableDateSet.add(LocalDate.of(2018, 10, 29));
		return createRockShow(10L, "T10", "Seattle, Washington", 47.6062, -122.3321, 1.0, 1500000, true,
				availableDateSet);
	}

	private static RockShow createRockShow(Long id, String venueName, String cityName, Double latitude,
			Double longitude, Double duration, int revenueOpportunity, boolean required,
			NavigableSet<LocalDate> availableDateSet) {
		RockShow rs = new RockShow();
		rs.setId(id);
		rs.setVenueName(venueName);

		rs.setLocation(new RockLocation(cityName, latitude, longitude));
		int durationInHalfDay = (int) (duration * 2.0);
		if (((double) durationInHalfDay) != duration * 2.0) {
			throw new IllegalStateException("The duration (" + duration + ") should be a multiple of 0.5.");
		}
		if (durationInHalfDay < 1) {
			throw new IllegalStateException("The duration (" + duration + ") should be at least 0.5.");
		}
		rs.setDurationInHalfDay(durationInHalfDay);
		rs.setRevenueOpportunity(revenueOpportunity);
		rs.setRequired(required);
		// NavigableSet<LocalDate> availableDateSet = new TreeSet<>();
		if (availableDateSet.isEmpty()) {
			throw new IllegalStateException(
					"The show (" + rs.getVenueName() + ")'s has no available date: all dates are unavailable.");
		}
		rs.setAvailableDateSet(availableDateSet);
		return rs;
	}

	private static void readDrivingTime(RockTourSolution solution) {
		Map<Pair<Double, Double>, List<RockLocation>> latLongToLocationMap = Stream
				.concat(Stream.of(solution.getBus().getStartLocation(), solution.getBus().getEndLocation()),
						solution.getShowList().stream().map(RockShow::getLocation))
				.distinct()
				.sorted(Comparator.comparing(RockLocation::getLatitude).thenComparing(RockLocation::getLongitude)
						.thenComparing(RockLocation::getCityName))
				.collect(groupingBy(location -> Pair.of(location.getLatitude(), location.getLongitude()),
						LinkedHashMap::new, toList()));
		latLongToLocationMap.forEach((fromLatLong, fromLocationList) -> {
			for (RockLocation fromLocation : fromLocationList) {
				fromLocation.setDrivingSecondsMap(new LinkedHashMap<>(fromLocationList.size()));
			}
			latLongToLocationMap.forEach((toLatLong, toLocationList) -> {
				long drivingTime = 0L;
				for (RockLocation fromLocation : fromLocationList) {
					for (RockLocation toLocation : toLocationList) {
						// TODO use haversine air distance and convert to average seconds for truck
						drivingTime = fromLocation.getAirDistanceTo(toLocation);
						fromLocation.getDrivingSecondsMap().put(toLocation, drivingTime);
					}
				}
			});
		});
		return;
	}

}
