package com.devops.ratingservice.controllers;

import com.devops.ratingservice.controller.LodgeRatingController;
import com.devops.ratingservice.model.LodgeRating;
import com.devops.ratingservice.service.LodgeRatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
public class LodgeRatingControllerUnitTest {

    @InjectMocks
    private LodgeRatingController lodgeRatingController;

    @Mock
    private LodgeRatingService lodgeRatingService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(lodgeRatingController).build();
    }

    @Test
    public void givenValidRequest_whenCreateNewRating_thenRatingIsCreated() throws Exception {
        // given
        when(lodgeRatingService.createNew(anyInt(), anyInt(), anyInt())).thenReturn(new LodgeRating());

        // when
        mockMvc.perform(get("/api/lodge-ratings/new-rating/1/2/5"))

                // then
                .andExpect(status().isOk());
        verify(lodgeRatingService).createNew(1, 2, 5);
    }

    @Test
    public void givenValidRequestButExistingRating_whenCreateNewRating_thenForbidden() throws Exception {
        // given
        when(lodgeRatingService.createNew(anyInt(), anyInt(), anyInt())).thenReturn(null);

        // when
        mockMvc.perform(get("/api/lodge-ratings/new-rating/1/2/5"))

                // then
                .andExpect(status().isForbidden());
        verify(lodgeRatingService).createNew(1, 2, 5);
    }

    @Test
    public void givenInvalidRatingValue_whenCreateNewRating_thenBadRequest() throws Exception {
        // given
        int invalidRating = 6;

        // when
        mockMvc.perform(get("/api/lodge-ratings/new-rating/1/2/" + invalidRating))

                // then
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenInvalidRatingId_whenUpdate_thenBadRequest() throws Exception {
        // given
        int invalidRating = -2;

        // when
        mockMvc.perform(get("/api/lodge-ratings/update/abcdefg/" + invalidRating))

                // then
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenValidLodgeId_whenRetrieveRatingsForLodge_thenAllRatingsReturned() throws Exception {
        // given
        when(lodgeRatingService.getAllByLodge(anyInt())).thenReturn(List.of(new LodgeRating()));

        // when
        mockMvc.perform(get("/api/lodge-ratings/all/1"))

                // then
                .andExpect(status().isOk());
        verify(lodgeRatingService).getAllByLodge(1);
    }

    @Test
    public void givenValidLodgeIdButNoRatings_whenRetrieveRatingsForLodge_thenAllRatingsReturned() throws Exception {
        // given
        when(lodgeRatingService.getAllByLodge(anyInt())).thenReturn(new ArrayList<LodgeRating>());

        // when
        mockMvc.perform(get("/api/lodge-ratings/all/1"))

                // then
                .andExpect(status().isNotFound());
        verify(lodgeRatingService).getAllByLodge(1);
    }

    @Test
    public void givenValidLodgeId_whenRetrieveAverageRating_thenAverageReturned() throws Exception {
        // given
        when(lodgeRatingService.getAverage(anyInt())).thenReturn(3.5);

        // when
        mockMvc.perform(get("/api/lodge-ratings/average/1"))

                // then
                .andExpect(status().isOk())
                .andExpect(content().string("3.5"));
        verify(lodgeRatingService).getAverage(1);
    }

    @Test
    public void givenValidLodgeIdButNoRatings_whenRetrieveAverageRating_thenNoContentReturned() throws Exception {
        // given
        when(lodgeRatingService.getAverage(anyInt())).thenReturn(null);

        // when
        mockMvc.perform(get("/api/lodge-ratings/average/1"))

                // then
                .andExpect(status().isNotFound());
        verify(lodgeRatingService).getAverage(1);
    }
}
