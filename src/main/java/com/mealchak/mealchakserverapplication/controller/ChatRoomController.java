package com.mealchak.mealchakserverapplication.controller;

import com.mealchak.mealchakserverapplication.dto.request.ChatRoomRequestDto;
import com.mealchak.mealchakserverapplication.jwt.JwtTokenProvider;
import com.mealchak.mealchakserverapplication.model.ChatRoom;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/chat/create")
    //어떤 유저가 만들었는지 알아야하므로 userdetails를 함께 받음
    public ChatRoom createChatRoom(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody ChatRoomRequestDto requestDto){
        //생각해보니 중복된걸로받으면 안되니까 이부분은 프론트에서 받을때 uuid를 합성해서받거나 dto에서 uuid 합쳐줘야할듯
        requestDto.getChatRoomName();
        //유저id를 받아옴
        Long userId = userDetails.getUser().getUserId();
        ChatRoom chatRoom = chatRoomService.createChatRoom(requestDto,userId);
        //안보내줘도 상관없지만 uuid처리 할경우 프론트도 uuid를 알아야할테니 일단 보내주는걸로
        return chatRoom;
    }

    //방장외의 사람이 입장하는경우
    @GetMapping("/chat/{id}")
    public ChatRoom getChatRoom(@PathVariable Long id){
        return chatRoomService.getChatRoom(id);
    }
}
