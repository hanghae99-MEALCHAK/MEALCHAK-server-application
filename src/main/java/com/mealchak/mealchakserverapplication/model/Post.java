package com.mealchak.mealchakserverapplication.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mealchak.mealchakserverapplication.dto.request.PostRequestDto;
import jdk.nashorn.internal.ir.annotations.Ignore;
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
    private String orderTime;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private String username;

    public Post(String title, int headCount, String category, String address, String orderTime, String contents, String username) {
        this.title = title;
        this.headCount = headCount;
        this.category = category;
        this.address = address;
        this.orderTime = orderTime;
        this.contents = contents;
        this.username = username;
    }

    public Post(String username, PostRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.headCount = requestDto.getHeadCount();
        this.category = requestDto.getCategory();
        this.address = requestDto.getAddress();
        this.orderTime = requestDto.getOrderTime();
        this.contents = requestDto.getContents();
        this.username = username;
    }

    public void update(PostRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.headCount = requestDto.getHeadCount();
        this.category = requestDto.getCategory();
        this.address = requestDto.getAddress();
        this.orderTime = requestDto.getOrderTime();
        this.contents = requestDto.getContents();
    }
}
