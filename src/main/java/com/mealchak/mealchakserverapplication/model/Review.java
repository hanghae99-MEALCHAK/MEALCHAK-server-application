//package com.mealchak.mealchakserverapplication.model;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.mealchak.mealchakserverapplication.repository.ReviewRepository;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//
//@NoArgsConstructor // 기본생성자를 만듭니다.
//@Getter
//@Entity // 테이블과 연계됨을 스프링에게 알려줍니다.
//public class Review  extends Timestamped{
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    @Id
//    private Long id;
//
//    @Column(nullable = false)
//    private String review;
//
//    @ManyToOne
//    @JsonIgnore
//    private User user;
//
//    public void createReview(ReviewRepository repository, User user){
//        this.review = repository.getReview();
//        this.user = user;
//    }
//
//}
