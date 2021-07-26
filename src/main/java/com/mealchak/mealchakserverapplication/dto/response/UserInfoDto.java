package com.mealchak.mealchakserverapplication.dto.response;

import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import lombok.Getter;
import lombok.Setter;

//@Getter
@Setter
public class UserInfoDto {
    private Long user_id;
    private String user_nickname;

//    public UserInfoDto(User user) {
//        this.user_id = user.getUserId();
//        this.user_nickname = user.getUsername();
//    }
}