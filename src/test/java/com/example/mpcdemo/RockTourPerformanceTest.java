/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mpcdemo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.mpcdemo.domain.RockTourSolution;
import com.example.mpcdemo.service.SolutionInfo;

public class RockTourPerformanceTest {

	// ************************************************************************
	// Tests
	// ************************************************************************

	protected final transient Logger logger = LoggerFactory.getLogger(getClass());

	protected SolutionFileIO<RockTourSolution> solutionFileIO;
	protected String solverConfig;

	@Before
	public void setup() {
		SolutionInfo commonApp = new SolutionInfo();
		solutionFileIO = commonApp.createSolutionFileIO();
		solverConfig = commonApp.getSolverConfig();
	}

	@Test(timeout = 600000)
	public void solveModel() {
		File unsolvedDataFile = new File("data/rocktour/unsolved/47shows.xlsx");
		runSpeedTest(unsolvedDataFile, "0hard/72725670medium/-6208480soft");
	}

	@Test(timeout = 600000)
	public void solveModelFastAssert() {
		File unsolvedDataFile = new File("data/rocktour/unsolved/47shows.xlsx");
		runSpeedTest(unsolvedDataFile, "0hard/72725039medium/-5186309soft", EnvironmentMode.FAST_ASSERT);
	}

	protected void runSpeedTest(File unsolvedDataFile, String bestScoreLimitString) {
		runSpeedTest(unsolvedDataFile, bestScoreLimitString, EnvironmentMode.REPRODUCIBLE);
	}

	protected void runSpeedTest(File unsolvedDataFile, String bestScoreLimitString, EnvironmentMode environmentMode) {
		SolverFactory<RockTourSolution> solverFactory = buildSolverFactory(bestScoreLimitString, environmentMode);
		RockTourSolution problem = solutionFileIO.read(unsolvedDataFile);
		logger.info("Opened: {}", unsolvedDataFile);
		Solver<RockTourSolution> solver = solverFactory.buildSolver();
		RockTourSolution bestSolution = solver.solve(problem);
		assertScoreAndConstraintMatches(solver, bestSolution, bestScoreLimitString);
	}

	protected SolverFactory<RockTourSolution> buildSolverFactory(String bestScoreLimitString,
			EnvironmentMode environmentMode) {
		SolverFactory<RockTourSolution> solverFactory = SolverFactory.createFromXmlResource(solverConfig);
		solverFactory.getSolverConfig().setEnvironmentMode(environmentMode);
		solverFactory.getSolverConfig()
				.setTerminationConfig(new TerminationConfig().withBestScoreLimit(bestScoreLimitString));
		// TODO this is currently not avalable
		// solverFactory.getSolverConfig().setMoveThreadCount(moveThreadCount);

		return solverFactory;
	}

	private void assertScoreAndConstraintMatches(Solver<RockTourSolution> solver, RockTourSolution bestSolution,
			String bestScoreLimitString) {
		assertNotNull(bestSolution);
		InnerScoreDirectorFactory<RockTourSolution> scoreDirectorFactory = (InnerScoreDirectorFactory<RockTourSolution>) solver
				.getScoreDirectorFactory();
		Score bestScore = scoreDirectorFactory.getSolutionDescriptor().getScore(bestSolution);
		ScoreDefinition scoreDefinition = scoreDirectorFactory.getScoreDefinition();
		Score bestScoreLimit = scoreDefinition.parseScore(bestScoreLimitString);
		assertTrue("The bestScore (" + bestScore + ") must be at least the bestScoreLimit (" + bestScoreLimit + ").",
				bestScore.compareTo(bestScoreLimit) >= 0);

		try (ScoreDirector<RockTourSolution> scoreDirector = scoreDirectorFactory.buildScoreDirector()) {
			scoreDirector.setWorkingSolution(bestSolution);
			Score score = scoreDirector.calculateScore();
			assertEquals(score, bestScore);
			if (scoreDirector.isConstraintMatchEnabled()) {
				Collection<ConstraintMatchTotal> constraintMatchTotals = scoreDirector.getConstraintMatchTotals();
				assertNotNull(constraintMatchTotals);
				assertEquals(score, constraintMatchTotals.stream()
						// TODO change here as well due to version of optaplanner
						.map(ConstraintMatchTotal::getScore).reduce(Score::add)
						.orElse(scoreDefinition.getZeroScore()));
				assertNotNull(scoreDirector.getIndictmentMap());
			}
		}
	}

}
