package com.mealchak.mealchakserverapplication.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class User extends Timestamped {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private Long kakaoId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String thumbnailImg;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String profileImg;

    public User(Long kakaoId, String Username, String password, String email, String thumbnailImg, String profileImg) {
        this.kakaoId = kakaoId;
        this.username = Username;
        this.password = password;
        this.email = email;
        this.thumbnailImg = thumbnailImg;
        this.profileImg = profileImg;
    }

    public User(String Username, String password) {
        this.kakaoId = 123L;
        this.username = Username;
        this.password = password;
        this.email = Username;
        this.thumbnailImg = "test";
        this.profileImg = "test";
    }
}
