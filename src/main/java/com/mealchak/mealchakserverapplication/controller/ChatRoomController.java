package com.mealchak.mealchakserverapplication.controller;

import com.mealchak.mealchakserverapplication.dto.request.ChatRoomCreateRequestDto;
import com.mealchak.mealchakserverapplication.dto.response.ChatRoomCreateResponseDto;
import com.mealchak.mealchakserverapplication.model.ChatRoom;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.ChatRoomRepository;
import com.mealchak.mealchakserverapplication.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 채팅 방 생성
    @PostMapping("/chat/rooms")
    public ChatRoomCreateResponseDto createChatRoom(@RequestBody ChatRoomCreateRequestDto requestDto,
                                                    @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.createChatRoom(requestDto, userDetails.getUser());
    }

    // 채팅방 목록 전체 조회
    @GetMapping("/chat/rooms")
    public List<ChatRoom> getChatRooms(){
        return chatRoomService.getChatRooms();
    }

    // 사용자별 채팅방 목록 조회
    @GetMapping("/chat/rooms/{userId}")
    public List<ChatRoom> getOnesChatRoom(@PathVariable Long userId) {
        return chatRoomService.getOnesChatRoom(userId);
    }


}
