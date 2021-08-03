package com.mealchak.mealchakserverapplication.dto.response;

import com.mealchak.mealchakserverapplication.model.ChatRoom;
import com.mealchak.mealchakserverapplication.model.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatRoomListResponseDto {
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final String title;
    private final Long ownUserId;
    private final Long roomId;
    private final Long postId;
    private final Long headCountChat;

    public ChatRoomListResponseDto(ChatRoom chatRoom, Post post, Long headCountChat) {
        this.createdAt = chatRoom.getCreatedAt();
        this.modifiedAt = chatRoom.getModifiedAt();
        this.title = post.getTitle();
        this.ownUserId = chatRoom.getOwnUserId();
        this.roomId = chatRoom.getId();
        this.postId = post.getId();
        this.headCountChat = headCountChat;
    }
}
