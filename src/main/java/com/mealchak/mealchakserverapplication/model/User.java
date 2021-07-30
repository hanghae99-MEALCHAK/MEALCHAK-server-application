package com.mealchak.mealchakserverapplication.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
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

    @Embedded
    private Location location;

    public void updateUsername(String newUsername) {
        this.username = newUsername;
    }

    public void updateUserDisc(Location location) {
        this.location = location;
    }

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

    public User(Long kakaoId, String Username, String password, String email, String thumbnailImg, String profileImg, Location location) {
        this.kakaoId = kakaoId;
        this.username = Username;
        this.password = password;
        this.email = email;
        this.thumbnailImg = thumbnailImg;
        this.profileImg = profileImg;
        this.location = location;
    }
}
