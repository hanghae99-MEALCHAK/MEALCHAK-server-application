package com.mealchak.mealchakserverapplication.model;

import com.mealchak.mealchakserverapplication.dto.request.ChatRoomCreateRequestDto;
import com.mealchak.mealchakserverapplication.dto.request.ChatRoomRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ChatRoom extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(nullable = false)
    private String chatRoomName;

    @Column(nullable = false)
    private Long ownUserId;

    public ChatRoom(ChatRoomCreateRequestDto requestDto, User user){
        this.chatRoomName = requestDto.getChatRoomName();
        this.ownUserId = user.getUserId();
    }
}
