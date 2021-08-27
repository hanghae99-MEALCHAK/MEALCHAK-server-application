package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.model.ChatMessage;
import com.mealchak.mealchakserverapplication.repository.ChatMessageQueryRepository;
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

    private final ChatMessageQueryRepository chatMessageQueryRepository;
    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final BanUserListService banUserListService;
    private final ChatRoomService chatRoomService;


    // 메세지의 헤더에서 추출한 정보로 roomId 를 확인하고 리턴함
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1) {
            return destination.substring(lastIndex + 1);
        } else {
            throw new IllegalArgumentException("lastIndex 오류입니다.");
        }
    }

    // 메세지의 type 을 확인하고 그에따라 작업을 분기시킴
    public void sendChatMessage(ChatMessage chatMessageRequestDto) {
        // 채팅방 입장시
        if (ChatMessage.MessageType.ENTER.equals(chatMessageRequestDto.getType())) {
            chatMessageRequestDto.setMessage(chatMessageRequestDto.getSender().getUsername() + "님이 들어왔어요.");
            chatMessageRequestDto.setSender(chatMessageRequestDto.getSender());
        } else if (ChatMessage.MessageType.QUIT.equals(chatMessageRequestDto.getType())) {
            chatMessageRequestDto.setMessage(chatMessageRequestDto.getSender().getUsername() + "님이 자리를 비웠어요.");
            chatMessageRequestDto.setSender(chatMessageRequestDto.getSender());
            // 채팅방 퇴장시
        } else if (ChatMessage.MessageType.BAN.equals(chatMessageRequestDto.getType())){
            Long userId = Long.parseLong(chatMessageRequestDto.getMessage());
            Long roomId = Long.parseLong(chatMessageRequestDto.getRoomId());
            banUserListService.banUser(userId,roomId);
            // 채팅방 강퇴시
        }else if (ChatMessage.MessageType.BREAK.equals(chatMessageRequestDto.getType())) {
            Long roomId = Long.parseLong(chatMessageRequestDto.getRoomId());
            chatRoomService.updateChatValid(roomId);
            // 채팅방 폭파시
        }
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessageRequestDto);
    }

    // 채팅방의 마지막 150개 메세지를 페이징하여 리턴함
    public Page<ChatMessage> getChatMessageByRoomId(String roomId, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt" );
        pageable = PageRequest.of(page, 150, sort );
        return chatMessageQueryRepository.findByRoomIdOrderByIdDesc(roomId, pageable);
    }
}
