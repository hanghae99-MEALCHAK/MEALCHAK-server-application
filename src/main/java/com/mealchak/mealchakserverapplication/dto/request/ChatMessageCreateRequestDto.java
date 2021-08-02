package com.mealchak.mealchakserverapplication.dto.request;

import com.mealchak.mealchakserverapplication.model.ChatMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class ChatMessageCreateRequestDto {
    private ChatMessage.MessageType type;
    private String username;
    private String message;
    private Long roomId;
}