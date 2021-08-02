package com.mealchak.mealchakserverapplication.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ChatRoom extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(nullable = false)
    private String uuid;

    @Column(nullable = false)
    private Long ownUserId;

//    @Column(nullable = false)
//    private Long postId;

    @OneToOne
    @JoinColumn(name="post_id")
    private Post post;

    public ChatRoom(Post post, String uuid, User user) {
        this.post = post;
        this.uuid = uuid;
        this.ownUserId = user.getId();
    }
}
