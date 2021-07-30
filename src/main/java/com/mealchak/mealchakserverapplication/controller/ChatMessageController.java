package com.mealchak.mealchakserverapplication.controller;

import com.mealchak.mealchakserverapplication.dto.request.ChatJoinRequestDto;
import com.mealchak.mealchakserverapplication.dto.request.ChatMessageCreateRequestDto;
import com.mealchak.mealchakserverapplication.dto.response.ChatMessageCreateResponseDto;
import com.mealchak.mealchakserverapplication.model.ChatMessage;
import com.mealchak.mealchakserverapplication.pubsub.RedisPublisher;
import com.mealchak.mealchakserverapplication.pubsub.RedisSubscriber;
import com.mealchak.mealchakserverapplication.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@RequiredArgsConstructor
@Controller
public class ChatMessageController {
    private final RedisMessageListenerContainer redisMessageListener;

    private final ChatMessageService chatMessageService;
    private final RedisPublisher redisPublisher;
    private final RedisSubscriber redisSubscriber;



//    @MessageMapping("/chat")
//    public void chatMessage(ChatMessageCreateRequestDto requestDto) {
//        chatMessageService.createChatMessage(requestDto);
//    }

//    @MessageMapping("/chats")
//    @SendTo("/sub/chats")
//    public ChatMessageCreateResponseDto chatMessage(ChatMessageCreateRequestDto requestDto) throws Exception {
//        Thread.sleep(1000); // simulated delay
//        return new ChatMessageCreateResponseDto(HtmlUtils.htmlEscape(requestDto.getUsername())
//                +"님의 메세지 > "+ HtmlUtils.htmlEscape(requestDto.getMessage()));
//    }


//    @MessageMapping("/{roomId}")
//    @SendTo("/sub/{roomId}")
//    public ChatMessage chatMessage(@DestinationVariable Long roomId, ChatMessageCreateRequestDto requestDto) throws Exception {
//        Thread.sleep(1000); // simulated delay
//        return chatMessageService.createChatMessage(requestDto);
//    }
}
