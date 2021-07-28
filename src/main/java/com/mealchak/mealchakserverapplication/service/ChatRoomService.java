package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.request.ChatRoomRequestDto;
import com.mealchak.mealchakserverapplication.model.ChatRoom;
import com.mealchak.mealchakserverapplication.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
@RequiredArgsConstructor
public class ChatRoomService {


    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo;

    private final ChatRoomRepository chatRoomRepository;

    public static final String ENTER_INFO = "ENTER_INFO";

    //채팅방생성
    public ChatRoom createChatRoom(ChatRoomRequestDto requestDto, Long userId) {
        ChatRoom chatRoom = new ChatRoom(requestDto, userId);
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }

    //채팅방찾기
    public ChatRoom getChatRoom(Long id) {
        ChatRoom chatRoom = chatRoomRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("잘못된 접근이거나 이미 종료된 채팅방입니다.")
        );
        return chatRoom;
    }

    public void setUserEnterInfo(String sessionId, String roomId) {
        hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId);
    }

    public String getUserEnterRoomId(String sessionId) {
        return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
    }

    public void removeUserEnterInfo(String sessionId) {
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
    }
}
