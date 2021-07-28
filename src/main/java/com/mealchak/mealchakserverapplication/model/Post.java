package com.mealchak.mealchakserverapplication.model;

import com.mealchak.mealchakserverapplication.dto.request.PostRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor // 기본생성자를 만듭니다.
@Getter
@Entity // 테이블과 연계됨을 스프링에게 알려줍니다.
public class Post extends Timestamped {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int headCount;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String restaurant;

    @Column(nullable = false)
    private String orderTime;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Long userId;

    public Post(String title, int headCount, String category, String address, String restaurant, String orderTime, String contents, String username, Long userId) {
        this.title = title;
        this.headCount = headCount;
        this.category = category;
        this.address = address;
        this.restaurant = restaurant;
        this.orderTime = orderTime;
        this.contents = contents;
        this.username = username;
        this.userId = userId;
    }

    public Post(String username, Long userId, PostRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.headCount = requestDto.getHeadCount();
        this.category = requestDto.getCategory();
        this.address = requestDto.getAddress();
        this.restaurant = requestDto.getRestaurant();
        this.orderTime = requestDto.getOrderTime();
        this.contents = requestDto.getContents();
        this.username = username;
        this.userId = userId;
    }

    public void update(PostRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.headCount = requestDto.getHeadCount();
        this.category = requestDto.getCategory();
        this.address = requestDto.getAddress();
        this.restaurant = requestDto.getRestaurant();
        this.orderTime = requestDto.getOrderTime();
        this.contents = requestDto.getContents();
    }
}
