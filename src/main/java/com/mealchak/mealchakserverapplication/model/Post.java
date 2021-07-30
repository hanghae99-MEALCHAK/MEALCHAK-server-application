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
    private String address;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private String restaurant;

    @Column(nullable = false)
    private String orderTime;

    @Column(nullable = false)
    private String contents;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "User_ID")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Menu_ID")
    private Menu menu;

    public Post(PostRequestDto requestDto,User user, Menu menu) {
        this.title = requestDto.getTitle();
        this.headCount = requestDto.getHeadCount();
        this.address = requestDto.getAddress();
        this.latitude = requestDto.getLatitude();
        this.longitude = requestDto.getLongitude();
        this.restaurant = requestDto.getRestaurant();
        this.orderTime = requestDto.getOrderTime();
        this.contents = requestDto.getContents();
        this.user = user;
        this.menu = menu;
    }

    public void update(PostRequestDto requestDto, Menu menu) {
        this.title = requestDto.getTitle();
        this.headCount = requestDto.getHeadCount();
        this.address = requestDto.getAddress();
        this.restaurant = requestDto.getRestaurant();
        this.orderTime = requestDto.getOrderTime();
        this.contents = requestDto.getContents();
        this.menu = menu;
    }
}
