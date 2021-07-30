package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.request.ChatRoomCreateRequestDto;
import com.mealchak.mealchakserverapplication.dto.response.ChatRoomCreateResponseDto;
import com.mealchak.mealchakserverapplication.model.ChatRoom;
import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.repository.ChatRoomRepository;
import com.mealchak.mealchakserverapplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    // 채팅방 생성
    @Transactional
    public ChatRoomCreateResponseDto createChatRoom(ChatRoomCreateRequestDto requestDto, User user) {
        ChatRoom chatRoom = new ChatRoom(requestDto, user);
        chatRoomRepository.save(chatRoom);
        return new ChatRoomCreateResponseDto(chatRoom);
    }

    @Transactional
    public List<ChatRoom> getChatRooms() {
        return chatRoomRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public List<ChatRoom> getOnesChatRoom(Long userId){
        return chatRoomRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }


}
