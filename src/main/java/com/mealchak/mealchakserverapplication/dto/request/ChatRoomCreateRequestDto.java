package com.mealchak.mealchakserverapplication.dto.request;

import com.mealchak.mealchakserverapplication.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomCreateRequestDto {
    private String chatRoomName;
}
