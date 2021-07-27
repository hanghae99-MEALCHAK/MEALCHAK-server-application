package com.mealchak.mealchakserverapplication.dto.request;

import lombok.Getter;

@Getter
public class PostRequestDto {
    private String title;
    private int headCount;
    private String category;
    private String address;
    private String orderTime;
    private String contents;
}
