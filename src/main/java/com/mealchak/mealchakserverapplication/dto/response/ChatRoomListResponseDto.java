package com.mealchak.mealchakserverapplication.dto.response;

import com.mealchak.mealchakserverapplication.model.ChatRoom;
import com.mealchak.mealchakserverapplication.model.Post;
import lombok.Getter;

@Getter
public class ChatRoomListResponseDto {
    private final String title;
    private final String orderTime;
    private final Long ownUserId;
    private final Long roomId;
    private final Long postId;
    private final Long headCountChat;
    private final boolean chatValid;
    private final boolean newMessage;

    public ChatRoomListResponseDto(ChatRoom chatRoom, Post post, Long headCountChat,boolean newMessage) {
        this.title = post.getTitle();
        this.orderTime = post.getOrderTime();
        this.ownUserId = chatRoom.getOwnUserId();
        this.roomId = chatRoom.getId();
        this.postId = post.getId();
        this.headCountChat = headCountChat;
        this.chatValid = chatRoom.isChatValid();
        this.newMessage = newMessage;
    }
}
