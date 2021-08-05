package com.mealchak.mealchakserverapplication.dto.response;

import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.repository.mapping.ReviewListMapping;
import lombok.Getter;

import java.util.List;

@Getter
public class OtherUserInfoResponseDto {
    private final Long userId;
    private final String username;
    private final String comment;
    private final String profileImg;
    private final float mannerScore;
    private final List<ReviewListMapping> reviews;

    public OtherUserInfoResponseDto(User user, List<ReviewListMapping> reviews) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.comment = user.getComment();
        this.profileImg = user.getProfileImg();
        this.mannerScore = user.getMannerScore();
        this.reviews = reviews;
    }
}
