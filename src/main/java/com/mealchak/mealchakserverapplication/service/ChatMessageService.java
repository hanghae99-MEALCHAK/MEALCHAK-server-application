package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.model.ChatMessage;
import com.mealchak.mealchakserverapplication.repository.ChatMessageRepository;
import com.mealchak.mealchakserverapplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final BanUserListService banUserListService;
    private final UserRepository userRepository;

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
        } else if (ChatMessage.MessageType.BAN.equals(chatMessageRequestDto.getType())){
            Long userId = Long.parseLong(chatMessageRequestDto.getMessage());
            Long roomId = Long.parseLong(chatMessageRequestDto.getRoomId());
            banUserListService.banUser(userId,roomId);
        }

        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessageRequestDto);
    }

    public Page<ChatMessage> getChatMessageByRoomId(String roomId, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt" );
        pageable = PageRequest.of(page, 150, sort );
        return chatMessageRepository.findByRoomId(roomId, pageable);
    }
}
