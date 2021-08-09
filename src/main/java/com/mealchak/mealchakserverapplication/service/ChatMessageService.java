package com.mealchak.mealchakserverapplication.service;

import com.google.gson.JsonObject;
import com.mealchak.mealchakserverapplication.model.ChatMessage;
import com.mealchak.mealchakserverapplication.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final ChatMessageRepository chatMessageRepository;

    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1) {
            return destination.substring(lastIndex + 1);
        } else {
            throw new IllegalArgumentException("lastIndex 오류입니다.");
        }
    }

    public void sendChatMessage(ChatMessage chatMessageRequestDto) {
        if (ChatMessage.MessageType.ENTER.equals(chatMessageRequestDto.getType())) {
            chatMessageRequestDto.setMessage(chatMessageRequestDto.getSender().getUsername() + "님이 참여중입니다.");
            chatMessageRequestDto.setSender(chatMessageRequestDto.getSender());
        } else if (ChatMessage.MessageType.QUIT.equals(chatMessageRequestDto.getType())) {
            chatMessageRequestDto.setMessage(chatMessageRequestDto.getSender() + "님이 퇴장했습니다.");
            chatMessageRequestDto.setSender(chatMessageRequestDto.getSender());
        } else if (ChatMessage.MessageType.TALK.equals(chatMessageRequestDto.getType())) {
            String content = chatMessageRequestDto.getMessage();
            boolean isSlang = isSlang(content);
            if (isSlang) {
                chatMessageRequestDto.setMessage("바르고 고운말을 사용합시다.");
            }
        }
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessageRequestDto);
    }

    public Page<ChatMessage> getChatMessageByRoomId(String roomId, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        pageable = PageRequest.of(page, 150, sort);
        return chatMessageRepository.findByRoomId(roomId, pageable);
    }

    public boolean isSlang(String content) {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        System.out.println();

        StringBuilder sb = new StringBuilder();
        sb.append("{\"text\":\""+content+"\"}");
        String body = sb.toString();

        HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);
        ResponseEntity<String> responseEntity = rest.exchange("http://104.154.113.3/chk", HttpMethod.POST, requestEntity, String.class);
        String response = responseEntity.getBody();
        String isSlang = "욕";
        if (response != null && response.equals(isSlang)){
            return true;
        }
        return false;
    }
}
