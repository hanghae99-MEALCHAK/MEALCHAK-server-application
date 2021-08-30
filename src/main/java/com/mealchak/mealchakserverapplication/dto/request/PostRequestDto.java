package com.mealchak.mealchakserverapplication.dto.request;

import com.mealchak.mealchakserverapplication.model.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostRequestDto {
    private String title;
    private int headCount;
    private String address;
    private double latitude;
    private double longitude;
    private String restaurant;
    private String orderTime;
    private String contents;
    private String category;
    private Post.meetingType meetingType;
    private String placeUrl;
}
