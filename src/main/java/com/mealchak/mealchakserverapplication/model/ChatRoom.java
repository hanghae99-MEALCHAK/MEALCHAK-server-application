package com.mealchak.mealchakserverapplication.model;

import com.mealchak.mealchakserverapplication.dto.request.ChatRoomCreateRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Optional;

@Getter
@NoArgsConstructor
@Entity
public class ChatRoom extends Timestamped{

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long roomId;

    @Column
    private String chatRoomName;

    @Column
    private Long userId;

    @Column
    private String username;

    public ChatRoom(ChatRoomCreateRequestDto requestDto, User user){
        this.chatRoomName = requestDto.getChatRoomName();
        this.username = user.getUsername();
        this.userId = user.getUserId();
    }

}


