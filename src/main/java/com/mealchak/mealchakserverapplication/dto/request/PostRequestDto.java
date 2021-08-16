package com.mealchak.mealchakserverapplication.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PostRequestDto {
    private String title;
    private int headCount;
    private String address;
    private double latitude;
    private double longitude;
    private String restaurant;
    private String orderTime;
    private String contents;
    private String category;
}
