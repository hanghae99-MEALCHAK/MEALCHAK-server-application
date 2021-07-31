package com.mealchak.mealchakserverapplication.repository.mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface UserInfoMapping {
    Long getId();
    String getUsername();
    String getEmail();
    String getProfileImg();

    default String getAddress() {
        return getLocationAddress();
    }
    @JsonIgnore
    String getLocationAddress();
}