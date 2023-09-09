package com.devops.ratingservice.controllers;
import com.devops.ratingservice.controller.HostRatingController;
import com.devops.ratingservice.model.HostRating;
import com.devops.ratingservice.service.HostRatingService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ActiveProfiles("test")
public class HostRatingControllerUnitTest {

    @InjectMocks
    private HostRatingController hostRatingController;

    @Mock
    private HostRatingService hostRatingService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(hostRatingController).build();
    }

    @Test
    public void givenValidRequest_whenCreateNewRating_thenRatingIsCreated() throws Exception {
        // given
        when(hostRatingService.createNew(anyInt(), anyInt(), anyInt())).thenReturn(new HostRating());

        // when
        mockMvc.perform(get("/api/host-ratings/new-rating/1/2/5"))

                // then
                .andExpect(status().isOk());
        verify(hostRatingService).createNew(1, 2, 5);
    }

    @Test
    public void givenValidRequestButExistingRating_whenCreateNewRating_thenForbidden() throws Exception {
        // given
        when(hostRatingService.createNew(anyInt(), anyInt(), anyInt())).thenReturn(null);

        // when
        mockMvc.perform(get("/api/host-ratings/new-rating/1/2/5"))

                // then
                .andExpect(status().isForbidden());
        verify(hostRatingService).createNew(1, 2, 5);
    }

    @Test
    public void givenInvalidRatingValue_whenCreateNewRating_thenBadRequest() throws Exception {
        // given
        int invalidRating = 6;

        // when
        mockMvc.perform(get("/api/host-ratings/new-rating/1/2/" + invalidRating))

                // then
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenInvalidRatingId_whenUpdate_thenBadRequest() throws Exception {
        // given
        int invalidRating = -2;

        // when
        mockMvc.perform(get("/api/host-ratings/update/abcdefg/" + invalidRating))

                // then
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenValidHostId_whenRetrieveRatingsForHost_thenAllRatingsReturned() throws Exception {
        // given
        when(hostRatingService.getAllByHost(anyInt())).thenReturn(List.of(new HostRating()));

        // when
        mockMvc.perform(get("/api/host-ratings/all/1"))

                // then
                .andExpect(status().isOk());
        verify(hostRatingService).getAllByHost(1);
    }

    @Test
    public void givenValidHostIdButNoRatings_whenRetrieveRatingsForHost_thenAllRatingsReturned() throws Exception {
        // given
        when(hostRatingService.getAllByHost(anyInt())).thenReturn(new ArrayList<HostRating>());

        // when
        mockMvc.perform(get("/api/host-ratings/all/1"))

                // then
                .andExpect(status().isNotFound());
        verify(hostRatingService).getAllByHost(1);
    }

    @Test
    public void givenValidHostId_whenRetrieveAverageRating_thenAverageReturned() throws Exception {
        // given
        when(hostRatingService.getAverage(anyInt())).thenReturn(3.5);

        // when
        mockMvc.perform(get("/api/host-ratings/average/1"))

                // then
                .andExpect(status().isOk())
                .andExpect(content().string("3.5"));
        verify(hostRatingService).getAverage(1);
    }

    @Test
    public void givenValidHostIdButNoRatings_whenRetrieveAverageRating_thenNoContentReturned() throws Exception {
        // given
        when(hostRatingService.getAverage(anyInt())).thenReturn(null);

        // when
        mockMvc.perform(get("/api/host-ratings/average/1"))

                // then
                .andExpect(status().isNotFound());
        verify(hostRatingService).getAverage(1);
    }
}
