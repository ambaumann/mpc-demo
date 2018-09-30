package com.example.mpcdemo.service;

import java.util.List;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.springframework.stereotype.Service;

import com.example.mpcdemo.domain.MPCAccount;
import com.example.mpcdemo.domain.RockTourSolution;
import com.example.mpcdemo.domain.dto.Solution;
import com.example.mpcdemo.domain.dto.SolutionState;
import com.example.mpcdemo.persistence.RockTourIO;


@Service
public class SolverService {
	
	public SolutionState state = SolutionState.NOTREADY;
	
	RockTourSolution solution;
	
	String solverConfig;
	
	public CacheService cacheService;

	public SolverService() {
		//solution = RockTourHardCodeAndDBIO.read(); //moved to solveSolution()
		SolutionInfo commonApp = new SolutionInfo();
		solverConfig = commonApp.getSolverConfig();
		cacheService = CacheService.getInstance();
	}
	
	public boolean isFinalSolutionReady() {
		return state.equals(SolutionState.COMPLETE);
	}

	
	public List<MPCAccount> getOrderedAccounts() {
		return getFinalSolution().accounts;
	}
	
	
	
	public Solution getFinalSolution() {
		if(!state.equals(SolutionState.COMPLETE)) {
			// maybe throw error
			return null;
		}
		return Solution.createFromSolvedRockTourSolution(solution);
	}

	public void solveSolution(){
		// TODO Auto-generated method stub
		if (state.equals(SolutionState.RUNNING))
			return;
		
		solution = RockTourIO.read();
		state = SolutionState.RUNNING;
		//TODO make the following async.
		SolverFactory<RockTourSolution> solverFactory = buildSolverFactory(EnvironmentMode.FAST_ASSERT);
		Solver<RockTourSolution> solver = solverFactory.buildSolver();
		RockTourSolution bestSolution = solver.solve(solution);
		solution = bestSolution;
		state = SolutionState.COMPLETE;
	}

	public void solverSolutionAsync() {
		
		new Thread(() -> solveSolution()).start();
	}
	
	public SolutionState getSolverStatus() {
		// TODO Auto-generated method stub
		return state;
		
	}
		
	/*public void addUserInput(String accountId) {
		if(!state.equals(SolutionState.INITIALIZED)) {
		//TODO log error.
		return;
		}
		
		cacheService.addUserInput(accountId);
		
		
		// TODO modify local solution.
		**
		 * Add $$ to existing revenue opportunity for each user input.
		 * still up in the air what will be in the user input. May be adding a new location of just updating an existing list.
		 
		
	}*/

	public void reset() {
		this.solution = RockTourIO.read();
		
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
