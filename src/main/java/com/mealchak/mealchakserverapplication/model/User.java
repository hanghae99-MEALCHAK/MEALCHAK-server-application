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

    @OneToOne
    @JoinColumn(name = "server_id")
    private CreatedServers createdServers;

    public User(Long kakaoId, String Username, String password, String email) {
        this.kakaoId = kakaoId;
        this.username = Username;
        this.password = password;
        this.email= email;
    }

}
