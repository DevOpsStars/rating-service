package com.devops.ratingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestRatingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(RatingServiceApplication::main).with(TestRatingServiceApplication.class).run(args);
	}

}
