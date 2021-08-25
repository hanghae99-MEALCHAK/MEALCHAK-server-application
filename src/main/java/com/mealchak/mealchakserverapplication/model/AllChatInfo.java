package com.mealchak.mealchakserverapplication.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AllChatInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToOne
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    @Column(nullable = false)
    private Long lastMessageId;

    public AllChatInfo(User user, ChatRoom chatRoom) {
        this.user = user;
        this.chatRoom = chatRoom;
        this.lastMessageId = 0L;
    }

    public void updateLastMessageId(Long lastMessageId){
        this.lastMessageId = lastMessageId;
    }
}
