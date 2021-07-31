package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.Location;

public interface UserInfoMapping {
    Long getId();
    String getUsername();
    String getEmail();
    Location getLocation();
    String getThumbnailImg();
    String getProfileImg();
}
