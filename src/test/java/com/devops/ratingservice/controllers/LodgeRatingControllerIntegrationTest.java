package com.devops.ratingservice.controllers;

import com.devops.ratingservice.model.LodgeRating;
import com.devops.ratingservice.repository.LodgeRatingRepository;
import com.devops.ratingservice.service.LodgeRatingService;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.*;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
public class LodgeRatingControllerIntegrationTest {
    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.6");  // Adjust to your desired version
    private MongoDatabase database;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private LodgeRatingRepository repository;
    @Autowired
    private LodgeRatingService service;

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
    public void givenValidLodgeId_whenRetrieveAverageRating_thenAverageReturned() {
        // given
        Integer lodgeId = 1;
        repository.save(LodgeRating.builder().rate(3).lodgeId(1).build());
        repository.save(LodgeRating.builder().rate(4).lodgeId(2).build());
        Double expectedAverage = service.getAverage(lodgeId);

        // when
        ResponseEntity<String> response = restTemplate.getForEntity("/api/lodge-ratings/average/" + lodgeId, String.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Double actualAverage = Double.valueOf(response.getBody());
        assertEquals(expectedAverage, actualAverage, 0.001);
    }

    @Test
    public void givenValidLodgeIdButNoRatings_whenRetrieveAverageRating_thenAverageReturned() {
        // given
        Integer lodgeId = 2;

        // when
        ResponseEntity<String> response = restTemplate.getForEntity("/api/lodge-ratings/average/" + lodgeId, String.class);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
     public void givenValidRequest_whenCreateNewRating_thenRatingIsCreated() {
         // given
         Integer guestId = 1;
         Integer lodgeId = 2;
         Integer rating = 3;

         // when
         ResponseEntity<LodgeRating> response = restTemplate
                 .getForEntity("/api/lodge-ratings/new-rating/"
                         + guestId + "/" + lodgeId + "/" + rating, LodgeRating.class);

         // then
         assertEquals(HttpStatus.OK, response.getStatusCode());
         assertNotNull(response.getBody());
         assertEquals(response.getBody().getGuestId(), guestId);
         assertEquals(response.getBody().getLodgeId(), lodgeId);
         assertEquals(response.getBody().getRate(), rating);
     }

     @Test
    public void givenValidRequestButExistingRating_whenCreateNewRating_thenForbidden() {
        // given
        Integer guestId = 1;
        Integer lodgeId = 2;
        Integer rating = 3;
        repository.save(LodgeRating.builder()
                .guestId(guestId)
                .lodgeId(lodgeId)
                .rate(rating)
                .build());

        // when
        ResponseEntity<LodgeRating> response = restTemplate
                .getForEntity("/api/lodge-ratings/new-rating/"
                        + guestId + "/" + lodgeId + "/" + rating, LodgeRating.class);

        // then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void givenInvalidRatingValue_whenCreateNewRating_thenBadRequest() throws Exception {
        // given
        Integer guestId = 1;
        Integer lodgeId = 2;
        Integer invalidRating = 6;

        // when
        ResponseEntity<LodgeRating> response = restTemplate
                .getForEntity("/api/lodge-ratings/new-rating/"
                        + guestId + "/" + lodgeId + "/" + invalidRating, LodgeRating.class);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

}
