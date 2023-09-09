package com.devops.ratingservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document
public class LodgeRating {
    @Id
    private String id;
    private Integer lodgeId;
    private Integer guestId;
    private Integer rate;
    private LocalDate created;
    private LocalDate lastUpdated;
}
