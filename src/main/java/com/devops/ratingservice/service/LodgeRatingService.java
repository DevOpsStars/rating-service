package com.devops.ratingservice.service;

import com.devops.ratingservice.model.LodgeRating;
import com.devops.ratingservice.repository.LodgeRatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LodgeRatingService {
    private final LodgeRatingRepository lodgeRatingRepository;

    @Autowired
    public LodgeRatingService(LodgeRatingRepository lodgeRatingRepository) {
        this.lodgeRatingRepository = lodgeRatingRepository;
    }

    public LodgeRating createNew(Integer guestId, Integer lodgeId, Integer rating) {
        // check if guest already has rated same lodge
        List<LodgeRating> previousRatings = lodgeRatingRepository.findAllByGuestIdAndLodgeId(guestId, lodgeId);
        if (!previousRatings.isEmpty()) return null;

        LodgeRating newRating = LodgeRating.builder()
                .id(null)
                .rate(rating)
                .guestId(guestId)
                .lodgeId(lodgeId)
                .created(LocalDate.now())
                .lastUpdated(LocalDate.now())
                .build();
        return lodgeRatingRepository.save(newRating);
    }

    public LodgeRating update(String id, Integer newRating) {
        // check if rating exists
        LodgeRating rating = lodgeRatingRepository.findById(id).orElse(null);
        if (rating == null) {
            return null;
        }

        rating.setRate(newRating);
        rating.setLastUpdated(LocalDate.now());
        return lodgeRatingRepository.save(rating);
    }

    public void delete(String id) {
        lodgeRatingRepository.deleteById(id);
    }

    public Double getAverage(Integer lodgeId) {
        List<LodgeRating> ratings = lodgeRatingRepository.findAllByLodgeId(lodgeId);
        int count = ratings.size();
        if (count == 0) return null;
        return ratings.stream()
                .mapToDouble(LodgeRating::getRate)
                .average().orElse(0.0);
    }

    public List<LodgeRating> getAllByLodge(Integer lodgeId) {
        return lodgeRatingRepository.findAllByLodgeId(lodgeId);
    }

    public List<LodgeRating> getAllByGuest(Integer guestId) {
        return lodgeRatingRepository.findAllByGuestId(guestId);
    }
}
