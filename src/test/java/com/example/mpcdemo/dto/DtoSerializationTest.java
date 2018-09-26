package com.example.mpcdemo.dto;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.mpcdemo.common.TestDataHelper;
import com.example.mpcdemo.domain.dto.Solution;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DtoSerializationTest {

	@Autowired
	ObjectMapper objectMapper;
	
	@Test
	public void solutionToJsonAndBackTest() throws IOException {
		Solution solution = TestDataHelper.getTestReturnSolution();
		testToJsonAndBack(solution);
	}
	
	public <T> void testToJsonAndBack(T object) throws IOException {
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		String json = objectMapper.writeValueAsString(object);
		assertNotNull(json);
		System.out.println(json);
		@SuppressWarnings("unchecked")
		T back = (T) objectMapper.readValue(json, object.getClass());
		
		// TODO add lombok equals method generation
		//assertEquals(object, back);
	}
}
