package com.mealchak.mealchakserverapplication.dto.request;

import lombok.Getter;

import java.util.Date;

@Getter
public class PostRequestDto {
    private String title;
    private int headCount;
    private String address;
    private double latitude;
    private double longitude;
    private String restaurant;
    private Date orderTime;
    private String contents;
    private String category;
}
