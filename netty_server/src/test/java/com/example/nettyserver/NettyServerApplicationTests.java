package com.example.nettyserver;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class NettyServerApplicationTests {
	
	@MockBean
	private NettyServer nettyServer;
	
	
	@Test
	void contextLoads() {
		// 這個方法不需要寫任何東西，只要它不拋出異常，就代表 Spring Boot 能正常啟動
	}

}
