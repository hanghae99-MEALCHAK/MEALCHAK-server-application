package com.mealchak.mealchakserverapplication.dto.response;

import com.mealchak.mealchakserverapplication.model.ChatRoom;
import com.mealchak.mealchakserverapplication.model.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatRoomListResponseDto {
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String title;
    private Long ownUserId;
    private Long roomId;
    private Long postId;
    private String uuid;
    private Long headCountChat;

    public ChatRoomListResponseDto(ChatRoom chatRoom, Post post, Long headCountChat) {
        this.createdAt = chatRoom.getCreatedAt();
        this.modifiedAt = chatRoom.getModifiedAt();
        this.title = post.getTitle();
        this.ownUserId = chatRoom.getOwnUserId();
        this.roomId = chatRoom.getId();
        this.postId = post.getId();
        this.uuid = chatRoom.getUuid();
        this.headCountChat = headCountChat;
    }
}
