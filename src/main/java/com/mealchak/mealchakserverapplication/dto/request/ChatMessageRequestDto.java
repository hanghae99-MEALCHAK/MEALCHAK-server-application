package com.mealchak.mealchakserverapplication.dto.request;

import com.mealchak.mealchakserverapplication.model.ChatMessage;
import lombok.Getter;

@Getter
public class ChatMessageRequestDto {
    private ChatMessage.MessageType type;
    private String roomId;
    private Long senderId;
    private String message;
}
