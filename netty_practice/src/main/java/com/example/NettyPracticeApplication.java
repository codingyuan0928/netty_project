package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NettyPracticeApplication {

	public static void main(String[] args) {
		SpringApplication.run(NettyPracticeApplication.class, args);
		System.out.println("Fruit Server started!");
	}

}
