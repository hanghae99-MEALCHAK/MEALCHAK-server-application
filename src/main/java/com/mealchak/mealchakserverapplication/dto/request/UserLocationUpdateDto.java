package com.mealchak.mealchakserverapplication.dto.request;

import lombok.Getter;

@Getter
public class UserLocationUpdateDto {
    private double latitude;
    private double longitude;
    private String address;
}
