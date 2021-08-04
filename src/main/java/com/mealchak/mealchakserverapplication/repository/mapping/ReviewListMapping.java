package com.mealchak.mealchakserverapplication.repository.mapping;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

public interface ReviewListMapping {
    String getReview();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime getCreatedAt();
    default String getUsername() {
        return getWriterUsername();
    }
    default String getProfileImg() {
        return getWriterProfileImg();
    }

    @JsonIgnore
    String getWriterUsername();
    @JsonIgnore
    String getWriterProfileImg();
}
