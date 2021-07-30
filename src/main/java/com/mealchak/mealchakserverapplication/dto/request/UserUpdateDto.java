package com.mealchak.mealchakserverapplication.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UserUpdateDto {

    private double latitude;
    private double longitude;
    private String address;

}
