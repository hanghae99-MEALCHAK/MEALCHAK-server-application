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
    private String restaurant;

    @Column(nullable = false)
    private String orderTime;

    @Column(nullable = false)
    private String contents;

    @OneToOne
    @JoinColumn(name="room_id")
    private ChatRoom chatRoom;

    @Column(nullable = false)
    private boolean checkValid;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "User_ID")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Menu_ID")
    private Menu menu;

    @Embedded
    private Location location;

    @Transient
    private double distance;

    @Transient
    private Long nowHeadCount;

    public void updateDistance(double distance) {
    this.distance = distance;
    }

    public void updateNowHeadCount(Long nowHeadCount) { this.nowHeadCount = nowHeadCount; }

    public Post(PostRequestDto requestDto, User user, Menu menu, Location location, ChatRoom chatRoom) {
        this.title = requestDto.getTitle();
        this.headCount = requestDto.getHeadCount();
        this.restaurant = requestDto.getRestaurant();
        this.orderTime = requestDto.getOrderTime();
        this.contents = requestDto.getContents();
        this.user = user;
        this.menu = menu;
        this.location = location;
        this.checkValid = true;
        this.chatRoom = chatRoom;
    }

    public void update(PostRequestDto requestDto, Menu menu, Location location) {
        this.title = requestDto.getTitle();
        this.headCount = requestDto.getHeadCount();
        this.restaurant = requestDto.getRestaurant();
        this.orderTime = requestDto.getOrderTime();
        this.contents = requestDto.getContents();
        this.menu = menu;
        this.location = location;
    }

    public void expired(boolean checkValid){
        this.checkValid = checkValid;
    }
}
