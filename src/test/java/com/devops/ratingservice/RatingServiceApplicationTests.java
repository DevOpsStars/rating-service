package com.devops.ratingservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = TestRatingServiceApplication.class)
@ActiveProfiles("test")
class RatingServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
