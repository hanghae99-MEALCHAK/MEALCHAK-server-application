package com.mealchak.mealchakserverapplication.controller;

import com.mealchak.mealchakserverapplication.dto.request.ChatRoomRequestDto;
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

    @PostMapping("/chat/create")
    //어떤 유저가 만들었는지 알아야하므로 userdetails를 함께 받음
    public ChatRoom createChatRoom(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam String name){
        //유저id를 받아옴
        Long userId = userDetails.getUser().getUserId();
        return chatRoomService.createChatRoom(name,userId);
    }

    //방장외의 사람이 입장하는경우
    @GetMapping("/chat/{id}")
    public ChatRoom getChatRoom(@PathVariable Long id){
        return chatRoomService.getChatRoom(id);
    }

    //여기방채팅보여줘
    @GetMapping("/chat/{id}/messages")
    public Page<ChatMessage> getRoomMessage(@PathVariable String id, @PageableDefault Pageable pageable){
        return chatMessageService.getChatMessageByRoomId(id, pageable);
    }

    //필요없을것같지만 테스트를위해
    @PostMapping("/chat/getall")
    @ResponseBody
    public List<ChatRoom> room(){
        return chatRoomService.getAll();
    }
}
