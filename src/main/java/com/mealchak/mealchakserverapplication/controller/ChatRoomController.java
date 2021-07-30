package com.mealchak.mealchakserverapplication.controller;

import com.mealchak.mealchakserverapplication.dto.request.ChatRoomCreateRequestDto;
import com.mealchak.mealchakserverapplication.dto.request.ChatRoomRequestDto;
import com.mealchak.mealchakserverapplication.dto.response.ChatRoomCreateResponseDto;
import com.mealchak.mealchakserverapplication.model.ChatMessage;
import com.mealchak.mealchakserverapplication.model.ChatRoom;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.service.ChatMessageService;
import com.mealchak.mealchakserverapplication.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;


    // 채팅 방 생성
    @PostMapping("/chat/rooms")
    public ChatRoomCreateResponseDto createChatRoom(@RequestBody ChatRoomCreateRequestDto requestDto,
                                                    @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.createChatRoom(requestDto, userDetails.getUser());
    }

    // 사용자별 채팅방 목록 조회
    @GetMapping("/chat/rooms/mine")
    public List<ChatRoom> getOnesChatRoom(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 미완
        return chatRoomService.getOnesChatRoom(userDetails.getUser());
    }

    // 해당 채팅방의 메세지 조회
    @GetMapping("/chat/{roomId}/messages")
    public Page<ChatMessage> getRoomMessage(@PathVariable String roomId, @PageableDefault Pageable pageable){
        return chatMessageService.getChatMessageByRoomId(roomId, pageable);
    }



//    //필요없을것같지만 테스트를위해
//    @PostMapping("/chat/getall")
//    @ResponseBody
//    public List<ChatRoom> room(){
//        return chatRoomService.getAll();
//    }
}
