package com.mealchak.mealchakserverapplication.dto.request;

import com.mealchak.mealchakserverapplication.model.Review;
import lombok.Getter;

@Getter
public class ReviewRequestDto {
    private String review;
    private Review.MannerType mannerType;
}
