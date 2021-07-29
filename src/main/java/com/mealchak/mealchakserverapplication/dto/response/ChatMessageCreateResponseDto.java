package com.mealchak.mealchakserverapplication.dto.response;

import com.mealchak.mealchakserverapplication.model.ChatMessage;
import com.mealchak.mealchakserverapplication.model.ChatRoom;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ChatMessageCreateResponseDto {

    private Long roomId;
    private String username;
    private String message;
    private List<ChatMessage> chatMessageList;

    public ChatMessageCreateResponseDto(ChatMessage chatMessage, List<ChatMessage> chatMessageList) {
        this.roomId = chatMessage.getRoomId();
        this.username = chatMessage.getUsername();
        this.message = chatMessage.getMessage();
        this.chatMessageList = chatMessageList;

    }
}
