package com.mealchak.mealchakserverapplication.controller;

import com.mealchak.mealchakserverapplication.dto.response.ChatRoomListResponseDto;
import com.mealchak.mealchakserverapplication.model.ChatMessage;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.service.ChatMessageService;
import com.mealchak.mealchakserverapplication.service.ChatRoomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Api(tags = {"5. 채팅방"}) // Swagger
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    // 사용자별 채팅방 목록 조회
    @ApiOperation(value = "사용자별 채팅방 목록 조회", notes = "사용자별 채팅방 목록 조회")
    @GetMapping("/chat/rooms/mine")
    public List<ChatRoomListResponseDto> getOnesChatRoom(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.getOnesChatRoom(userDetails.getUser());
    }

    // 해당 채팅방의 메세지 조회
    @ApiOperation(value = "해당 채팅방의 메세지 조회", notes = "해당 채팅방의 메세지 조회")
    @GetMapping("/chat/{roomId}/messages")
    public Page<ChatMessage> getRoomMessage(@PathVariable String roomId, @PageableDefault Pageable pageable) {
        return chatMessageService.getChatMessageByRoomId(roomId, pageable);
   }

    @ApiOperation(value = "해당 채팅방 나가기", notes = "해당 채팅방 나가기")
    @DeleteMapping("/chat/quit/{postId}")
    public void quitChat(@PathVariable Long postId,@AuthenticationPrincipal UserDetailsImpl userDetails){
        chatRoomService.quitChat(postId,userDetails);
    }
}
