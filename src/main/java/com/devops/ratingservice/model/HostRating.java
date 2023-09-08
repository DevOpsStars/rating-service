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
public class HostRating {
    @Id
    private String id;
    private Integer hostId;
    private Integer guestId;
    private Integer rate;
    private LocalDate created;
    private LocalDate lastUpdated;
}
