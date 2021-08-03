package com.mealchak.mealchakserverapplication.dto.response;

import com.mealchak.mealchakserverapplication.model.User;
import lombok.Getter;

@Getter
public class UserInfoResponseDto {
    private final String username;
    private final String comment;
    private final String profileImg;

    public UserInfoResponseDto(User user) {
        this.username = user.getUsername();
        this.comment = user.getComment();
        this.profileImg = user.getProfileImg();
    }
}
