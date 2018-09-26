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

import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.mpcdemo.common.ProblemFileComparator;
import com.example.mpcdemo.common.TestSystemProperties;
import com.example.mpcdemo.domain.RockTourSolution;
import com.example.mpcdemo.service.SolutionInfo;
import com.example.mpcdemo.service.SolverService;

public class RockTourSolveAllTurtleTest{

	protected final transient Logger logger = LoggerFactory.getLogger(getClass());
	
	protected SolutionInfo commonApp;
	protected File dataFile;
	
	protected SolutionFileIO<RockTourSolution> solutionFileIO;
	
	private String solverConfig;
	
	@Before
	public void setup() {
		SolutionInfo solutionInfo = new SolutionInfo();
		solverConfig = solutionInfo.getSolverConfig();
        this.commonApp = solutionInfo;
        
        // Just hard coding getting a single file for now.
        this.dataFile = (File) getUnsolvedDirFilesAsParameters(solutionInfo).get(0)[0];
		solutionFileIO = commonApp.createSolutionFileIO();
	}
	
    //@Parameterized.Parameters(name = "{index}: {0}")
    //public static Collection<Object[]> getSolutionFilesAsParameters() {
    //    return getUnsolvedDirFilesAsParameters(new SolverService());
    //}

//    public RockTourSolveAllTurtleTest(File unsolvedDataFile) {
//        super(new SolverService(), unsolvedDataFile);
//    }
    
    protected static <Solution_> List<Object[]> getUnsolvedDirFilesAsParameters(SolutionInfo commonApp) {
        List<Object[]> filesAsParameters = new ArrayList<>();
        File dataDir = SolutionInfo.determineDataDir(SolutionInfo.DATA_DIR_NAME);
        File unsolvedDataDir = new File(dataDir, "unsolved");
        if (!unsolvedDataDir.exists()) {
            throw new IllegalStateException("The directory unsolvedDataDir (" + unsolvedDataDir.getAbsolutePath()
                    + ") does not exist.");
        } else {
            String inputFileExtension = commonApp.createSolutionFileIO().getInputFileExtension();
            List<File> fileList = new ArrayList<>(
                    FileUtils.listFiles(unsolvedDataDir, new String[]{inputFileExtension}, true));
            fileList.sort(new ProblemFileComparator());
            for (File file : fileList) {
                filesAsParameters.add(new Object[]{file});
            }
        }
        return filesAsParameters;
    }

    protected RockTourSolution readProblem() {
    	RockTourSolution problem = solutionFileIO.read(dataFile);
        logger.info("Opened: {}", dataFile);
        return problem;
    }
    
    //TODO removing this
    //private static final String MOVE_THREAD_COUNT_OVERRIDE = System.getProperty(TestSystemProperties.MOVE_THREAD_COUNT);


    /**
     * These tests are breaking.
     * https://issues.jboss.org/browse/PLANNER-1200
     */
    @Ignore
    @Test
    public void runFastAndFullAssert() {
        //checkRunTurtleTests();
        SolverFactory<RockTourSolution> solverFactory = buildSolverFactory();
        RockTourSolution problem = readProblem();
        // Specifically use NON_INTRUSIVE_FULL_ASSERT instead of FULL_ASSERT to flush out bugs hidden by intrusiveness
        // 1) NON_INTRUSIVE_FULL_ASSERT ASSERT to find CH bugs (but covers little ground)
        problem = buildAndSolve(solverFactory, EnvironmentMode.NON_INTRUSIVE_FULL_ASSERT, problem, 2L);
        // 2) FAST_ASSERT to run past CH into LS to find easy bugs (but covers much ground)
        problem = buildAndSolve(solverFactory, EnvironmentMode.FAST_ASSERT, problem, 5L);
        // 3) NON_INTRUSIVE_FULL_ASSERT ASSERT to find LS bugs (but covers little ground)
        problem = buildAndSolve(solverFactory, EnvironmentMode.NON_INTRUSIVE_FULL_ASSERT, problem, 3L);
    }

    protected RockTourSolution buildAndSolve(SolverFactory<RockTourSolution> solverFactory, EnvironmentMode environmentMode,
            RockTourSolution problem, long maximumMinutesSpent) {
        SolverConfig solverConfig = solverFactory.getSolverConfig();
        solverConfig.getTerminationConfig().setMinutesSpentLimit(maximumMinutesSpent);
        solverConfig.setEnvironmentMode(environmentMode);
        Class<? extends EasyScoreCalculator> easyScoreCalculatorClass = overwritingEasyScoreCalculatorClass();
        if (easyScoreCalculatorClass != null && environmentMode.isAsserted()) {
            ScoreDirectorFactoryConfig assertionScoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
            assertionScoreDirectorFactoryConfig.setEasyScoreCalculatorClass(easyScoreCalculatorClass);
            solverConfig.getScoreDirectorFactoryConfig().setAssertionScoreDirectorFactory(
                    assertionScoreDirectorFactoryConfig);
        }
        Solver<RockTourSolution> solver = solverFactory.buildSolver();
        RockTourSolution bestSolution = solver.solve(problem);
        return bestSolution;
    }

    protected Class<? extends EasyScoreCalculator> overwritingEasyScoreCalculatorClass()  {
        return null;
    }

    protected SolverFactory<RockTourSolution> buildSolverFactory() {
        SolverFactory<RockTourSolution> solverFactory = SolverFactory.createFromXmlResource(solverConfig);
        //buildAndSolve()// fills in minutesSpentLimit
        solverFactory.getSolverConfig().setTerminationConfig(new TerminationConfig());
        // TODO removing this for now. Believe this is now available with version 7.11
        //if (MOVE_THREAD_COUNT_OVERRIDE != null) {
        //    solverFactory.getSolverConfig().setMoveThreadCount(MOVE_THREAD_COUNT_OVERRIDE);
        //}
        return solverFactory;
    }
    
    protected static void checkRunTurtleTests() {
        assumeTrue("true".equals(System.getProperty(TestSystemProperties.RUN_TURTLE_TESTS)));
    }

}
