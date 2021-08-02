package com.mealchak.mealchakserverapplication.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mealchak.mealchakserverapplication.model.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponseDto {
    private Long postId;
    private String title;
    private String contents;
    private int headCount;
    private String category;
    private String restaurant;
    private String orderTime;
    private String address;
    private Long userId;
    private String username;
    private String profileImg;
    private double distance;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public PostResponseDto(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.headCount = post.getHeadCount();
        this.restaurant = post.getRestaurant();
        this.orderTime = post.getOrderTime();
        this.contents = post.getContents();
        this.category = post.getMenu().getCategory();
        this.address = post.getLocation().getAddress();
        this.distance = post.getDistance();
        this.userId = post.getUser().getId();
        this.username = post.getUser().getUsername();
        this.profileImg = post.getUser().getProfileImg();
        this.createdAt = post.getCreatedAt();
    }
}
