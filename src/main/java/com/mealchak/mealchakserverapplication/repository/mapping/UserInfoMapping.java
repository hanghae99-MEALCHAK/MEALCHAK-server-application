package com.mealchak.mealchakserverapplication.repository.mapping;

import com.mealchak.mealchakserverapplication.model.Location;

public interface UserInfoMapping {
    Long getId();
    String getUsername();
    String getProfileImg();
    String getAge();
    String getGender();
    String getComment();
    float getMannerScore();
    Location getLocation();
}