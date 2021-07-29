package com.mealchak.mealchakserverapplication.oauth2.provider;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class KakaoUserInfo {
    Long id;
    String email;
    String nickname;
    String thumbnailImg;
    String profileImg;
}