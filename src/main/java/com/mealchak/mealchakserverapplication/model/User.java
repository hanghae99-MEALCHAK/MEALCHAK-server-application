package com.mealchak.mealchakserverapplication.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User extends Timestamped {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    @JsonIgnore
    private Long kakaoId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(nullable = false)
    @JsonIgnore
    private String email;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String profileImg;

    @Column
    @JsonIgnore
    private String comment;

    @Column
    private float mannerScore;

    @Embedded
    @JsonIgnore
    private Location location;

    public User(String username, String password) {
        this.kakaoId = 123L;
        this.username = username;
        this.password = password;
        this.email = username;
        this.profileImg = "http://115.85.182.57:8080/image/profileDefaultImg.jpg";
        this.location = new Location("강남구 항해리99", 37.49791, 127.027678);
    }

    public User(Long kakaoId, String Username, String password, String email, String profileImg,
                Location location) {
        this.kakaoId = kakaoId;
        this.username = Username;
        this.password = password;
        this.email = email;
        this.profileImg = profileImg;
        this.location = location;
    }

    public void updateUserInfo(String username, String comment, String profileImg) {
        this.username = username;
        this.comment = comment;
        this.profileImg = profileImg;
    }

    public void updateUserDisc(Location location) {
        this.location = location;
    }
    public void updateMannerScore(float mannerScore) {
        this.mannerScore += mannerScore;
    }
}
