package com.example.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;



@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class NettyControllerTest {

	@Autowired
	TestRestTemplate restTemplate;

		
	String requestMessage;
	String InvalidRequestMessage;
	
	@BeforeEach
	public void initRequestMessage() {
		requestMessage = "PASSBYE891A1E89084";
		InvalidRequestMessage = "PASSBYHello Netty";
	}
	
	@Test
	public void sendToNettyServerTest_Success() {


		ResponseEntity<String> responseEntity = restTemplate.postForEntity("/api/netty-client/send", requestMessage,
				String.class);
		
		assertNotNull(responseEntity.getBody());
		assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
		assertTrue(responseEntity.getBody().contains("END"));
	}
	@Test
	public void sendToNettyServerTest_InvaidInput() {
		
		ResponseEntity<String> responseEntity = restTemplate.postForEntity("/api/netty-client/send",InvalidRequestMessage,String.class);
		
		assertNotNull(responseEntity.getBody());
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
		assertFalse(responseEntity.getBody().contains("END"));
	}

}
