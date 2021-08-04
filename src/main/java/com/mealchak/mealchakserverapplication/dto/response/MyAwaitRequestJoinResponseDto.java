package com.mealchak.mealchakserverapplication.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MyAwaitRequestJoinResponseDto {
    private String postTitle;

    @Builder
    public MyAwaitRequestJoinResponseDto(String postTitle){
     this.postTitle = postTitle;
    }
}
