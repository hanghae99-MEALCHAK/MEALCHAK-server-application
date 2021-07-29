package com.mealchak.mealchakserverapplication.dto.response;

import com.mealchak.mealchakserverapplication.model.ChatRoom;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomCreateResponseDto {
    private Long roomId;
    private String chatRoomName;
    private String username;

    public ChatRoomCreateResponseDto(ChatRoom chatRoom) {
        this.roomId = chatRoom.getRoomId();
        this.chatRoomName = chatRoom.getChatRoomName();
        this.username = chatRoom.getUsername();
    }
}
