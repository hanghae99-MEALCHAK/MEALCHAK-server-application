package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.request.ChatMessageCreateRequestDto;
import com.mealchak.mealchakserverapplication.dto.response.ChatMessageCreateResponseDto;
import com.mealchak.mealchakserverapplication.dto.response.ChatRoomCreateResponseDto;
import com.mealchak.mealchakserverapplication.model.ChatMessage;
import com.mealchak.mealchakserverapplication.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
//    private final SimpMessagingTemplate template;
    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;


    @Transactional
    public void createChatMessage(ChatMessageCreateRequestDto requestDto) {
        if (requestDto.getType().equals(ChatMessage.MessageType.ENTER)){
            requestDto.setMessage("님이 입장하셨습니다.");
        } else if (requestDto.getType().equals(ChatMessage.MessageType.QUIT)){
            requestDto.setMessage("님이 퇴장하셨습니다.");
        }
        ChatMessage chatMessage = new ChatMessage(requestDto);
        chatMessageRepository.save(chatMessage);
//        template.convertAndSend("/sub/chat/" + requestDto.getRoomId(), requestDto );
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }

//    @Transactional
//    public ChatMessageCreateResponseDto createChatMessage(ChatMessageCreateRequestDto requestDto, Long roomId) {
//        List<ChatMessage> chatMessageList = chatMessageRepository.findAllByRoomId(roomId);
//        ChatMessage chatMessage = new ChatMessage(requestDto);
//        chatMessageRepository.save(chatMessage);
//        return new ChatMessageCreateResponseDto(chatMessage, chatMessageList);
//
//    }


}
