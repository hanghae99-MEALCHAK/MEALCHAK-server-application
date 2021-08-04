package com.mealchak.mealchakserverapplication.dto.request;

import com.mealchak.mealchakserverapplication.model.ChatMessage;
import lombok.Getter;

@Getter
public class ChatMessageRequestDto {
    private ChatMessage.MessageType type;
    private String roomId;
    private String senderId;
    private String sender;
    private String senderImg;
    private String message;
}
