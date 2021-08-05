package com.mealchak.mealchakserverapplication.dto.response;

import lombok.Getter;

@Getter
public class MyAwaitRequestJoinResponseDto {
    private final String postTitle;

    public MyAwaitRequestJoinResponseDto(String postTitle){
     this.postTitle = postTitle;
    }
}
