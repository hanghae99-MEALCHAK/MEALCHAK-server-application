package com.mealchak.mealchakserverapplication.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserInfoAndPostResponseDto {
    private Long joinRequestId;
    private Long userId;
    private String username;
    private String profileImg;
    private String postTitle;

    @Builder
    public UserInfoAndPostResponseDto(Long joinRequestId,Long userId,String username,String profileImg, String postTitle){
        this.joinRequestId = joinRequestId;
        this.userId = userId;
        this.username = username;
        this.profileImg = profileImg;
        this.postTitle = postTitle;
    }

}
