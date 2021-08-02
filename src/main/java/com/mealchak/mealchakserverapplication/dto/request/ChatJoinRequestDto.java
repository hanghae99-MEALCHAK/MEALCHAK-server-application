package com.mealchak.mealchakserverapplication.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatJoinRequestDto {
    private String username;
    private Long roomId;
    private String message;
}
