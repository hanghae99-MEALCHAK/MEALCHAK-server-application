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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "room_id")
    private Long id;

    @Column(nullable = false)
    private String uuid;

    @Column(nullable = false)
    private Long ownUserId;

    @OneToOne(mappedBy = "chatRoom")
    private Post post;

    public ChatRoom(String uuid, User user) {
        this.uuid = uuid;
        this.ownUserId = user.getId();
    }
}
