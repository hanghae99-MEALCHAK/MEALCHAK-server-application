package com.mealchak.mealchakserverapplication.dto.request;

import com.mealchak.mealchakserverapplication.model.ChatMessage;
import lombok.Getter;

@Getter
public class ChatMessageRequestDto {
    private ChatMessage.MessageType type;
    private String roomId;
    private String sender;
    private String message;
}
