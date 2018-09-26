package com.example.mpcdemo.service;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.springframework.stereotype.Service;

import com.example.mpcdemo.domain.RockTourSolution;
import com.example.mpcdemo.domain.dto.Solution;
import com.example.mpcdemo.domain.dto.SolutionState;
import com.example.mpcdemo.domain.dto.UserInput;
import com.example.mpcdemo.persistence.RockTourHardCodeAndDBIO;


@Service
public class SolverService {
	
	public SolutionState state;
	
	RockTourSolution solution;
	
	String solverConfig;

	public SolverService() {
		solution = RockTourHardCodeAndDBIO.read();
		SolutionInfo commonApp = new SolutionInfo();
		solverConfig = commonApp.getSolverConfig();
	}
	
	public boolean isFinalSolutionReady() {
		return state.equals(SolutionState.COMPLETE);
	}

	public Solution getFinalSolution() {
		if(!state.equals(SolutionState.COMPLETE)) {
			// maybe throw error
			return null;
		}
		return Solution.createFromSolvedRockTourSolution(solution);
	}

	public void solveSolution() {
		// TODO Auto-generated method stub
		
		state = SolutionState.RUNNING;
		//TODO make the following async.
		SolverFactory<RockTourSolution> solverFactory = buildSolverFactory(EnvironmentMode.FAST_ASSERT);
		Solver<RockTourSolution> solver = solverFactory.buildSolver();
		RockTourSolution bestSolution = solver.solve(solution);
		solution = bestSolution;
		state = SolutionState.COMPLETE;
	}

	public SolutionState getSolverStatus() {
		// TODO Auto-generated method stub
		return state;
		
	}
	
	public void addUserInput(UserInput userInput) {
		if(!state.equals(SolutionState.INITIALIZED)) {
			//TODO log error.
			return;
		}
		
		// TODO modify local solution.
		/**
		 * Add $$ to existing revenue opportunity for each user input.
		 * still up in the air what will be in the user input. May be adding a new location of just updating an existing list.
		 */
	}

	public void reset() {
		this.solution = RockTourHardCodeAndDBIO.read();
		
	}
	
	protected SolverFactory<RockTourSolution> buildSolverFactory(EnvironmentMode environmentMode) {
		SolverFactory<RockTourSolution> solverFactory = SolverFactory.createFromXmlResource(solverConfig);
		solverFactory.getSolverConfig().setEnvironmentMode(environmentMode);
		solverFactory.getSolverConfig()
				.setTerminationConfig(new TerminationConfig().withMinutesSpentLimit(1L));
				// TODO figure out best termination config
				//.setTerminationConfig(new TerminationConfig().withBestScoreLimit(bestScoreLimitString));
		// TODO this is currently not avalable
		// solverFactory.getSolverConfig().setMoveThreadCount(moveThreadCount);

		return solverFactory;
	}
}
