package com.mealchak.mealchakserverapplication.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mealchak.mealchakserverapplication.dto.request.ReviewRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor // 기본생성자를 만듭니다.
@Getter
@Entity // 테이블과 연계됨을 스프링에게 알려줍니다.
public class Review  extends Timestamped{
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    @Column(nullable = false)
    private String review;

    @ManyToOne
    @JsonIgnore
    private User user;

    @ManyToOne
    @JsonIgnore
    private User writer;

    public Review(ReviewRequestDto requestDto, User user, User writer) {
        this.review = requestDto.getReview();
        this.user = user;
        this.writer = writer;
    }

    public void updateReview(ReviewRequestDto requestDto){
        this.review = requestDto.getReview();
    }
}
