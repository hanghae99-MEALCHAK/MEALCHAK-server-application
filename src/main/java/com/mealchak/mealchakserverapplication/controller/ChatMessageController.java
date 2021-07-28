package com.mealchak.mealchakserverapplication.controller;

import com.mealchak.mealchakserverapplication.dto.request.ChatMessageRequestDto;
import com.mealchak.mealchakserverapplication.model.ChatMessage;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ChatMessageController {

    private final ChatMessageService chatMessageService;


    @MessageMapping("/message")
    public void message(@AuthenticationPrincipal UserDetailsImpl userDetails, ChatMessageRequestDto messageRequestDto){
        messageRequestDto.setSender(userDetails.getUser().getUsername());
        ChatMessage chatMessage = new ChatMessage(messageRequestDto);
        chatMessageService.sendChatMessage(chatMessage);
        chatMessageService.save(chatMessage);
        chatMessageService.sendChatMessage(chatMessage);
    }
}
