package com.mealchak.mealchakserverapplication.model;

import com.mealchak.mealchakserverapplication.dto.request.PostRequestDto;
import com.mealchak.mealchakserverapplication.dto.request.UserUpdateDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor
@Embeddable
public class Location {

    private String address;

    private double latitude;

    private double longitude;

    public Location(UserUpdateDto updateDto) {
        this.latitude = updateDto.getLatitude();
        this.longitude = updateDto.getLongitude();
        this.address = updateDto.getAddress();
    }

    public Location(PostRequestDto requestDto) {
        this.latitude = requestDto.getLatitude();
        this.longitude = requestDto.getLongitude();
        this.address = requestDto.getAddress();
    }

    public Location(String address, double latitude, double longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}