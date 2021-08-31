package com.mealchak.mealchakserverapplication.dto.request;

import com.mealchak.mealchakserverapplication.model.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatMessageRequestDto {
    private ChatMessage.MessageType type;
    private String roomId;
    private Long senderId;
    private String message;
}
