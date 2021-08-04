package com.mealchak.mealchakserverapplication.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor // 기본생성자를 만듭니다.
@Getter
@Entity // 테이블과 연계됨을 스프링에게 알려줍니다.
public class Menu {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    @Id
    private Long id;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String imgUrl;

    @Column(nullable = false)
    @JsonIgnore
    private int count;

    public Menu(String category, int count) {
        this.category = category;
        this.count = count;
        this.imgUrl = "http://115.85.182.57:8080/image/menu_japanese.jpg";
    }

    public void updateMenuCount(int count) {
        this.count = this.count + count;
    }

    public void resetMenuCount(){
        this.count = 0;
    }
}