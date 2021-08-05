package com.mealchak.mealchakserverapplication.repository.mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface UserInfoMapping {
    Long getId();
    String getUsername();
    String getProfileImg();
    String getComment();
    float getMannerScore();

    default String getAddress() {
        return getLocationAddress();
    }
    @JsonIgnore
    String getLocationAddress();
}