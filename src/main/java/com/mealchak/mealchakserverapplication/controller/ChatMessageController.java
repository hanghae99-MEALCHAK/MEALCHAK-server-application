package com.mealchak.mealchakserverapplication.controller;

import com.mealchak.mealchakserverapplication.dto.request.ChatMessageRequestDto;
import com.mealchak.mealchakserverapplication.model.ChatMessage;
import com.mealchak.mealchakserverapplication.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ChatMessageController {

    private final ChatMessageService chatMessageService;


    @MessageMapping("/message")
//    public void message(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody ChatMessageRequestDto messageRequestDto){
    public void message(@RequestBody ChatMessageRequestDto messageRequestDto) {
        //임시로 주석처리
//        messageRequestDto.setSender(userDetails.getUser().getUsername());
        ChatMessage chatMessage = new ChatMessage(messageRequestDto);
        chatMessageService.sendChatMessage(chatMessage);
//        chatMessageService.save(chatMessage);
    }
}
