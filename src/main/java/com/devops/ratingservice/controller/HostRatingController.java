package com.devops.ratingservice.controller;

import com.devops.ratingservice.model.HostRating;
import com.devops.ratingservice.service.HostRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/host-ratings")
@CrossOrigin("*")
public class HostRatingController {


    private final HostRatingService hostRatingService;

    @Autowired
    public HostRatingController(HostRatingService hostRatingService) {
        this.hostRatingService = hostRatingService;
    }

    @GetMapping(value = "/new-rating/{guestId}/{hostId}/{rating}")
    public ResponseEntity<HostRating> create(@PathVariable Integer guestId, @PathVariable Integer hostId, @PathVariable Integer rating) {
        HostRating result = this.hostRatingService.createNew(guestId, hostId, rating);
        if (result == null) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/all/{hostId}")
    public ResponseEntity<List<HostRating>> getAll(@PathVariable Integer hostId) {
        List<HostRating> res = hostRatingService.getAllByHost(hostId);
        if (res.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping(value = "/all-by/{guestId}")
    public ResponseEntity<List<HostRating>> getAllByGuest(@PathVariable Integer guestId) {
        List<HostRating> result = hostRatingService.getAllByGuest(guestId);
        if (result.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/update/{id}/{rating}")
    public ResponseEntity<HostRating> update(@PathVariable String id, @PathVariable Integer rating) {
        HostRating result = this.hostRatingService.update(id, rating);
        if (result == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        hostRatingService.delete(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @GetMapping(value = "/average/{hostId}")
    public ResponseEntity<Double> average(@PathVariable Integer hostId) {
        Double result = hostRatingService.getAverage(hostId);
        if (result == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        System.out.println("result from service " + result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
