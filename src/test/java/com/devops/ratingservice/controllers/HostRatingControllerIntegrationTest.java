package com.devops.ratingservice.controllers;

import com.devops.ratingservice.model.HostRating;
import com.devops.ratingservice.repository.HostRatingRepository;
import com.devops.ratingservice.service.HostRatingService;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
public class HostRatingControllerIntegrationTest {
    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.6");  // Adjust to your desired version
    private MongoDatabase database;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private HostRatingRepository repository;
    @Autowired
    private HostRatingService service;

    @BeforeAll
    static void beforeAll() {
        mongoDBContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mongoDBContainer.stop();
    }

    @BeforeEach
    public void setup() {
        mongoDBContainer.start();
        MongoClient mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl());
        database = mongoClient.getDatabase("test");
    }

    @AfterEach
    public void cleanup() {
        for (String collectionName : database.listCollectionNames()) {
            database.getCollection(collectionName).deleteMany(new Document());
        }
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }


    @Test
    public void givenValidHostId_whenRetrieveAverageRating_thenAverageReturned() {
        // given
        Integer hostId = 1;
        repository.save(HostRating.builder().rate(3).hostId(1).build());
        repository.save(HostRating.builder().rate(4).hostId(2).build());
        Double expectedAverage = service.getAverage(hostId);

        // when
        ResponseEntity<String> response = restTemplate.getForEntity("/api/host-ratings/average/" + hostId, String.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Double actualAverage = Double.valueOf(response.getBody());
        assertEquals(expectedAverage, actualAverage, 0.001);
    }

    @Test
    public void givenValidHostIdButNoRatings_whenRetrieveAverageRating_thenAverageReturned() {
        // given
        Integer hostId = 2;

        // when
        ResponseEntity<String> response = restTemplate.getForEntity("/api/host-ratings/average/" + hostId, String.class);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
     public void givenValidRequest_whenCreateNewRating_thenRatingIsCreated() {
         // given
         Integer guestId = 1;
         Integer hostId = 2;
         Integer rating = 3;

         // when
         ResponseEntity<HostRating> response = restTemplate
                 .getForEntity("/api/host-ratings/new-rating/"
                         + guestId + "/" + hostId + "/" + rating, HostRating.class);

         // then
         assertEquals(HttpStatus.OK, response.getStatusCode());
         assertNotNull(response.getBody());
         assertEquals(response.getBody().getGuestId(), guestId);
         assertEquals(response.getBody().getHostId(), hostId);
         assertEquals(response.getBody().getRate(), rating);
     }

     @Test
    public void givenValidRequestButExistingRating_whenCreateNewRating_thenForbidden() {
        // given
        Integer guestId = 1;
        Integer hostId = 2;
        Integer rating = 3;
        repository.save(HostRating.builder()
                .guestId(guestId)
                .hostId(hostId)
                .rate(rating)
                .build());

        // when
        ResponseEntity<HostRating> response = restTemplate
                .getForEntity("/api/host-ratings/new-rating/"
                        + guestId + "/" + hostId + "/" + rating, HostRating.class);

        // then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void givenInvalidRatingValue_whenCreateNewRating_thenBadRequest() throws Exception {
        // given
        Integer guestId = 1;
        Integer hostId = 2;
        Integer invalidRating = 6;

        // when
        ResponseEntity<HostRating> response = restTemplate
                .getForEntity("/api/host-ratings/new-rating/"
                        + guestId + "/" + hostId + "/" + invalidRating, HostRating.class);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

}
