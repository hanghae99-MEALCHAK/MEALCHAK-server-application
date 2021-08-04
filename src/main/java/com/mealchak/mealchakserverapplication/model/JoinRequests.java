package com.mealchak.mealchakserverapplication.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class JoinRequests {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private Long ownUserId;

    public JoinRequests(Long userId,Long postId,Long ownUserId) {
        this.userId = userId;
        this.postId = postId;
        this.ownUserId = ownUserId;
    }
}
