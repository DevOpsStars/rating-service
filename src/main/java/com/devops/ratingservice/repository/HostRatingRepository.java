package com.devops.ratingservice.repository;

import com.devops.ratingservice.model.HostRating;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HostRatingRepository extends MongoRepository<HostRating, String> {
    List<HostRating> findAllByGuestIdAndHostId(Integer guestId, Integer hostId);
    List<HostRating> findAllByHostId(Integer hostId);
    List<HostRating> findAllByGuestId(Integer guestId);
}
