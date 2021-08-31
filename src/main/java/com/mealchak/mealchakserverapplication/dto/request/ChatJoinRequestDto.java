package com.mealchak.mealchakserverapplication.dto.request;

import lombok.Getter;

@Getter
public class ChatJoinRequestDto {
    private String username;
    private Long roomId;
    private String message;
}
