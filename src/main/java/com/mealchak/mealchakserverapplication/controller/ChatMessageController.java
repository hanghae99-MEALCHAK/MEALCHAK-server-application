package com.mealchak.mealchakserverapplication.controller;

import com.mealchak.mealchakserverapplication.dto.request.ChatMessageRequestDto;
import com.mealchak.mealchakserverapplication.model.ChatMessage;
import com.mealchak.mealchakserverapplication.service.ChatMessageService;
import com.mealchak.mealchakserverapplication.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Api(tags = {"4. 채팅 메시지"}) // Swagger
public class ChatMessageController {
    private final ChatMessageService chatMessageService;
    private final UserService userService;

    @ApiOperation(value = "채팅 메시지 송수신", notes = "채팅 메시지 송수신")
    @MessageMapping("/message")
    public void message(@RequestBody ChatMessageRequestDto messageRequestDto) {
        ChatMessage chatMessage = new ChatMessage(messageRequestDto, userService);
        chatMessageService.sendChatMessage(chatMessage);
    }
}
