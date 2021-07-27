package com.mealchak.mealchakserverapplication.model;

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
    private Long id;

    @Column(nullable = false)
    private String chatRoomName;

    public ChatRoom(ChatRoomRequestDto requestDto){
        this.chatRoomName = requestDto.getChatRoomName();
    }
}
