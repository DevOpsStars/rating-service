package com.devops.ratingservice.controller;

import com.devops.ratingservice.model.LodgeRating;
import com.devops.ratingservice.service.LodgeRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/lodge-ratings")
@CrossOrigin("*")
public class LodgeRatingController {


    private final LodgeRatingService lodgeRatingService;

    @Autowired
    public LodgeRatingController(LodgeRatingService lodgeRatingService) {
        this.lodgeRatingService = lodgeRatingService;
    }

    @GetMapping(value = "/new-rating/{guestId}/{lodgeId}/{rating}")
    public ResponseEntity<LodgeRating> create(@PathVariable Integer guestId, @PathVariable Integer lodgeId, @PathVariable Integer rating) {
        if (rating > 5 || rating < 1) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        LodgeRating result = this.lodgeRatingService.createNew(guestId, lodgeId, rating);
        if (result == null) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/all/{lodgeId}")
    public ResponseEntity<List<LodgeRating>> getAll(@PathVariable Integer lodgeId) {
        List<LodgeRating> res = lodgeRatingService.getAllByLodge(lodgeId);
        if (res.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping(value = "/all-by/{guestId}")
    public ResponseEntity<List<LodgeRating>> getAllByGuest(@PathVariable Integer guestId) {
        List<LodgeRating> result = lodgeRatingService.getAllByGuest(guestId);
        if (result.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/update/{id}/{rating}")
    public ResponseEntity<LodgeRating> update(@PathVariable String id, @PathVariable Integer rating) {
        if (rating > 5 || rating < 1) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        LodgeRating result = this.lodgeRatingService.update(id, rating);
        if (result == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        lodgeRatingService.delete(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @GetMapping(value = "/average/{lodgeId}")
    public ResponseEntity<Double> average(@PathVariable Integer lodgeId) {
        Double result = lodgeRatingService.getAverage(lodgeId);
        if (result == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
