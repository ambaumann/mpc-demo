package com.example.mpcdemo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.mpcdemo.domain.MPCAccount;
import com.example.mpcdemo.domain.dto.SolutionState;
import com.example.mpcdemo.service.CacheService;
import com.example.mpcdemo.service.SolverService;

@RestController
@RequestMapping("/api")
public class ProblemAPIController {

	@Autowired
	public SolverService solverService;
	
	@Autowired
	public CacheService cacheService;
	
	/**
	 * Endpoint to pull to know when solver is finished
	 * // TODO return type will probs change to enum or something more specific
	 * @return meh
	 */
	@GetMapping("/solver")
	public SolutionState getSolutionStatus() {
		return solverService.getSolverStatus();
	}
	
	/**
	 * Endpoint to execute solver after user input is finalized
	 */
	// will be a longer a sync method
	// not too restful of a rest endpoint but meh
	@PostMapping("/solver/action/solve")
	public void solveSolution() throws InterruptedException{
		solverService.solverSolutionAsync();
	}
	
	/**
	 * Endpoint to reset the state of the solver
	 * Not exactly the best design only being able to solve for one solution but meh.
	 * Based on having only one service.
	 */
	@PostMapping("/solver/action/reset")
	public void resetSolver() {
		solverService.reset();
	}
	
	
	/**
	 * Get the solved solution.
	 * Only call this when user input has been collected and solver has had some time to run.
	 * See the solver status if unsure.
	 * @return solved solution
	 */
	@GetMapping("solver/solution")
	public List<MPCAccount> getSolution() {
		
		if(solverService.isFinalSolutionReady()) {
			return solverService.getOrderedAccounts();
		} else {
			// TODO handle error.
			return null;
		}
	}
	
	@CrossOrigin(origins = "*", maxAge = 3600)
	@GetMapping("/solver/allaccounts")
	public MPCAccount[] getAlltAccounts(){
		return cacheService.loadAllAccountData();
	}

	@CrossOrigin(origins = "*", maxAge = 3600)
	@GetMapping("/solver/defaultaccounts")
	public MPCAccount[] getDefaultAccounts(){
		return cacheService.getDefaultAccounts();
	}

	@CrossOrigin(origins = "*", maxAge = 3600)
	@GetMapping("/solver/selectedaccounts")
	public MPCAccount[] getSelectedAccounts() throws InterruptedException {
		solveSolution(); //calling async solve solution preemptively
		return cacheService.getAccounts();
	}
	
	/**
	 * Add User Input to the solver before solving.
	 * Reset the solver service to delete previous user input
	 * @param userInput
	 */
	@CrossOrigin(origins = "*", maxAge = 3600)
	@PostMapping("/solver/userinput")
	public void addUserInput(@RequestParam("accountId") String accountId) {
		cacheService.addUserInput(accountId);
	}

	@CrossOrigin(origins = "*", maxAge = 3600)
	@GetMapping("/solver/printcache")
	public void printCache() {
		CacheService cacheService = CacheService.getInstance();
		cacheService.printCache();
	}
	
	//TODO: change to @PostMapping("/solver/action/reset")
	@CrossOrigin(origins = "*", maxAge = 3600)
	@GetMapping("/solver/initcache")
	public void initCache() {
		CacheService cacheService = CacheService.getInstance();
		cacheService.initializeCache();
	}
}
