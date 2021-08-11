package com.mealchak.mealchakserverapplication.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MyAwaitRequestJoinResponseDto {
    private final Long joinRequestId;
    private final String postTitle;

    @Builder
    public MyAwaitRequestJoinResponseDto(Long joinRequestId, String postTitle) {
        this.joinRequestId = joinRequestId;
        this.postTitle = postTitle;
    }
}
