package com.mealchak.mealchakserverapplication.dto.response;

import com.mealchak.mealchakserverapplication.model.ChatRoom;
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


    public ChatRoomListResponseDto(ChatRoom chatRoom, String title, Long headCountChat){
        this.createdAt = chatRoom.getCreatedAt();
        this.modifiedAt = chatRoom.getModifiedAt();
        this.title = title;
        this.ownUserId = chatRoom.getOwnUserId();
        this.roomId = chatRoom.getRoomId();
        this.postId = chatRoom.getPostId();
        this.uuid = chatRoom.getUuid();
        this.headCountChat = headCountChat;
    }

}