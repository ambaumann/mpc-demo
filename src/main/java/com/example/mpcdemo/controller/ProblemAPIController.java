package com.example.mpcdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mpcdemo.domain.dto.Solution;
import com.example.mpcdemo.domain.dto.UserInput;
import com.example.mpcdemo.service.SolverService;

@RestController
@RequestMapping("/api")
public class ProblemAPIController {

	@Autowired
	public SolverService solverService;
	
	/**
	 * Endpoint to pull to know when solver is finished
	 * // TODO return type will probs change to enum or something more specific
	 * @return meh
	 */
	@GetMapping("/solver")
	public boolean getSolutionStatus() {
		return solverService.getSolverStatus();
	}
	
	/**
	 * Endpoint to execute solver after user input is finalized
	 */
	// will be a longer a sync method
	// not too restful of a rest endpoint but meh
	@PostMapping("/solver/action/solve")
	public void solveSolution() {
		solverService.solveSolution();
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
	public Solution getSolution() {
		
		if(solverService.isFinalSolutionReady()) {
			return solverService.getFinalSolution();
		} else {
			// TODO handle error.
			return null;
		}
	}
	
	/**
	 * Add User Input to the solver before solving.
	 * Reset the solver service to delete previous user input
	 * @param userInput
	 */
	@PostMapping("/solver/userinput")
	public void addUserInput(@RequestBody UserInput userInput) {
		solverService.addUserInput(userInput);
	}
}
