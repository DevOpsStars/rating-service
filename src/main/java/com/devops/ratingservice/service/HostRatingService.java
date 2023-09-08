package com.devops.ratingservice.service;

import com.devops.ratingservice.model.HostRating;
import com.devops.ratingservice.repository.HostRatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class HostRatingService {
    private final HostRatingRepository hostRatingRepository;

    @Autowired
    public HostRatingService(HostRatingRepository hostRatingRepository) {
        this.hostRatingRepository = hostRatingRepository;
    }

    public HostRating createNew(Integer guestId, Integer hostId, Integer rating) {
        // check if guest already has rated same host
        List<HostRating> previousRatings = hostRatingRepository.findAllByGuestIdAndHostId(guestId, hostId);
        if (!previousRatings.isEmpty()) return null;

        HostRating newRating = HostRating.builder()
                .id(null)
                .rate(rating)
                .guestId(guestId)
                .hostId(hostId)
                .created(LocalDate.now())
                .lastUpdated(LocalDate.now())
                .build();
        return hostRatingRepository.save(newRating);
    }

    public HostRating update(String id, Integer newRating) {
        // check if rating exists
        HostRating rating = hostRatingRepository.findById(id).orElse(null);
        if (rating == null) {
            return null;
        }

        rating.setRate(newRating);
        rating.setLastUpdated(LocalDate.now());
        return hostRatingRepository.save(rating);
    }

    public void delete(String id) {
        hostRatingRepository.deleteById(id);
    }

    public Double getAverage(Integer hostId) {
        List<HostRating> ratings = hostRatingRepository.findAllByHostId(hostId);
        Double count = Double.valueOf(ratings.size());
        if (count == 0) return null;
        Double average = ratings.stream()
                .mapToDouble(HostRating::getRate)
                .average().orElse(0.0);
        return average;
    }

    public List<HostRating> getAllByHost(Integer hostId) {
        return hostRatingRepository.findAllByHostId(hostId);
    }

    public List<HostRating> getAllByGuest(Integer guestId) {
        return hostRatingRepository.findAllByGuestId(guestId);
    }
}
