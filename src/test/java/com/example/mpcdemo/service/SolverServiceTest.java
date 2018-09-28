package com.example.mpcdemo.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.mpcdemo.domain.RockTourSolution;
import com.example.mpcdemo.domain.dto.Solution;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SolverServiceTest {

	@Autowired
	SolverService solver;
	
	@Autowired
	ObjectMapper mapper;
	
	@Test
	public void solverNoUserInputSolveTest() throws JsonProcessingException {
		solver.solveSolution();
		solver.getSolverStatus();
		RockTourSolution rtSolution = solver.solution;
		System.out.println("Score: " +rtSolution.getScore());
		Solution solution = solver.getFinalSolution();
		printSolution(solution);
	}
	
	@Test
	public void userInputTest() throws JsonProcessingException {
		solver.addUserInput("103");
		solver.addUserInput("104");
		solver.addUserInput("103");
		CacheService cacheService = CacheService.getInstance();
		cacheService.printCache();
	}
	
	private void printSolution(Solution solution) throws JsonProcessingException {
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		String json = mapper.writeValueAsString(solution);
		System.out.println(json);
	}
}
