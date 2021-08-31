package com.mealchak.mealchakserverapplication.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDto {
    private String username;
    private String password;
}