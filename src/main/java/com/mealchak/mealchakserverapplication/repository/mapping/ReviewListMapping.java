package com.mealchak.mealchakserverapplication.repository.mapping;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mealchak.mealchakserverapplication.model.Review;

import java.time.LocalDateTime;

public interface ReviewListMapping {
    Long getId();
    String getReview();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime getCreatedAt();

    default String getUsername() {
        return getWriterUsername();
    }
    default Long getUserId() { return getWriterId(); }
    default String getProfileImg() {
        return getWriterProfileImg();
    }

    default String getManner() {
        Review.MannerType manner = getMannerType();
        if (Review.MannerType.BEST.equals(manner)) {
            return "최고에요!";
        } else if (Review.MannerType.GOOD.equals(manner)) {
            return "좋았어요";
        }
        return "별로에요";
    }

    @JsonIgnore
    String getWriterUsername();
    @JsonIgnore
    Long getWriterId();
    @JsonIgnore
    String getWriterProfileImg();
    @JsonIgnore
    Review.MannerType getMannerType();
}
