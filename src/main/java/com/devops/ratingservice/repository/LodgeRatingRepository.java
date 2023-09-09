package com.devops.ratingservice.repository;

import com.devops.ratingservice.model.LodgeRating;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LodgeRatingRepository extends MongoRepository<LodgeRating, String> {
    List<LodgeRating> findAllByGuestIdAndLodgeId(Integer guestId, Integer lodgeId);
    List<LodgeRating> findAllByLodgeId(Integer lodgeId);
    List<LodgeRating> findAllByGuestId(Integer guestId);
}
