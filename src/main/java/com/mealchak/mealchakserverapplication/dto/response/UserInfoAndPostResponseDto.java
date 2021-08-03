package com.mealchak.mealchakserverapplication.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserInfoAndPostResponseDto {
    private Long userId;
    private String username;
    private String profileImg;
    private String postTitle;
}
