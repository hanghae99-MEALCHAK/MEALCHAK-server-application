package com.mealchak.mealchakserverapplication.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class ChatJoinRequestDto {
    private String username;
    private Long roomId;
    private String message;
}
