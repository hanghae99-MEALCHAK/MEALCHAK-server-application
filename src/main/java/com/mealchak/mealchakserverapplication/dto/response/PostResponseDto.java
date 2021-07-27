package com.mealchak.mealchakserverapplication.dto.response;

import lombok.Getter;

@Getter
public class PostResponseDto {
    private String title;
    private int headCount;
    private String address;
    private String orderTime;
    private String contents;
}
