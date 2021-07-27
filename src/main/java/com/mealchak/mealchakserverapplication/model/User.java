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
    private Long userId;

    @Column(nullable = false)
    private Long kakaoId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinColumn(name="USER_ID",nullable = false)
//    private List<Post> post = new ArrayList<>();

    public User(Long kakaoId, String Username, String password, String email) {
        this.kakaoId = kakaoId;
        this.username = Username;
        this.password = password;
        this.email = email;
    }
    public User(String Username, String password) {
        this.kakaoId = 123L;
        this.username = Username;
        this.password = password;
        this.email = "test";
    }
}
