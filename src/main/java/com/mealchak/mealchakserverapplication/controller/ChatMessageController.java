package com.mealchak.mealchakserverapplication.controller;

import com.mealchak.mealchakserverapplication.dto.request.ChatMessageRequestDto;
import com.mealchak.mealchakserverapplication.model.ChatMessage;
import com.mealchak.mealchakserverapplication.service.ChatMessageService;
import com.mealchak.mealchakserverapplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ChatMessageController {
    private final ChatMessageService chatMessageService;
    private final UserService userService;

    @MessageMapping("/message")
    public void message(@RequestBody ChatMessageRequestDto messageRequestDto) {
        ChatMessage chatMessage = new ChatMessage(messageRequestDto, userService);
        chatMessageService.sendChatMessage(chatMessage);
    }
}
