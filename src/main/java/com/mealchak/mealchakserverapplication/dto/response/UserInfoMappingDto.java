package com.mealchak.mealchakserverapplication.dto.response;

import com.mealchak.mealchakserverapplication.repository.mapping.UserInfoMapping;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoMappingDto {
    private String address;
    private Long id;
    private String comment;
    private String username;
    private String profileImg;
    private String age;
    private String gender;
    private float mannerScore;
    private double longitude;
    private double latitude;

    private boolean newMessage;
    private boolean newJoinRequest;

    public UserInfoMappingDto(UserInfoMapping userInfoMapping,boolean newMessage,boolean newJoinRequest){
        this.address = userInfoMapping.getLocation().getAddress();
        this.id = userInfoMapping.getId();
        this.comment = userInfoMapping.getComment();
        this.username = userInfoMapping.getUsername();
        this.profileImg = userInfoMapping.getProfileImg();
        this.age = userInfoMapping.getAge();
        this.gender = userInfoMapping.getGender();
        this.mannerScore = userInfoMapping.getMannerScore();
        this.newJoinRequest = newJoinRequest;
        this.newMessage = newMessage;
        this.longitude = userInfoMapping.getLocation().getLongitude();
        this.latitude = userInfoMapping.getLocation().getLatitude();
    }
}
