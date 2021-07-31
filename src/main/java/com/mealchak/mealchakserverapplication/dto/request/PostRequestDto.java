package com.mealchak.mealchakserverapplication.dto.request;

import lombok.Getter;

@Getter
public class PostRequestDto {
    private String title;
    private int headCount;
    private String category;
    private String address;
    private double latitude;
    private double longitude;
    private String restaurant;
    private String orderTime;
    private String contents;
}
