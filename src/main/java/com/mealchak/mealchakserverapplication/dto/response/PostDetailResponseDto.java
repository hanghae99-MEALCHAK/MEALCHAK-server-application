package com.mealchak.mealchakserverapplication.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mealchak.mealchakserverapplication.model.Post;
import com.mealchak.mealchakserverapplication.model.User;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostDetailResponseDto {
    private final Long postId;
    private final String title;
    private final String contents;
    private final int headCount;
    private final String category;
    private final String restaurant;
    private final String orderTime;
    private final String address;
    private final double latitude;
    private final double longitude;
    private final Long userId;
    private final String username;
    private final String profileImg;
    private final double distance;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;
    private final Long roomId;
    private final Long nowHeadCount;
    private final List<User> userList;
    private final Post.meetingType meetingType;

    public PostDetailResponseDto(Post post, List<User> userList) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.headCount = post.getHeadCount();
        this.restaurant = post.getRestaurant();
        this.orderTime = post.getOrderTime();
        this.contents = post.getContents();
        this.category = post.getMenu().getCategory();
        this.address = post.getLocation().getAddress();
        this.latitude = post.getLocation().getLatitude();
        this.longitude = post.getLocation().getLongitude();
        this.distance = post.getDistance();
        this.userId = post.getUser().getId();
        this.username = post.getUser().getUsername();
        this.profileImg = post.getUser().getProfileImg();
        this.createdAt = post.getCreatedAt();
        this.roomId = post.getChatRoom().getId();
        this.nowHeadCount = post.getNowHeadCount();
        this.userList = userList;
        this.meetingType = post.getMeetingType();
    }
}
