package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.request.ChatRoomCreateRequestDto;
import com.mealchak.mealchakserverapplication.dto.request.ChatRoomRequestDto;
import com.mealchak.mealchakserverapplication.dto.response.ChatRoomCreateResponseDto;
import com.mealchak.mealchakserverapplication.model.ChatRoom;
import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ChatRoomService {

    // HashPerations 레디스에서 쓰는 자료형
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo;

    private final ChatRoomRepository chatRoomRepository;

    public static final String ENTER_INFO = "ENTER_INFO";

    //채팅방생성
    public ChatRoomCreateResponseDto createChatRoom(ChatRoomCreateRequestDto requestDto, User user) {
        ChatRoom chatRoom = new ChatRoom(requestDto, user);
        chatRoomRepository.save(chatRoom);
        return new ChatRoomCreateResponseDto(chatRoom);
    }



    public List<ChatRoom> getOnesChatRoom(User user) {
        // 미완. 후에 데이터 관계 맵핑 or 불러오는 기획 설정 후 변경
        return chatRoomRepository.findAllByOwnUserIdOrderByCreatedAtDesc(user.getUserId());
    }


    public void setUserEnterInfo(String sessionId, String roomId) {
        hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId);    // redistemplate에 (입장type, ,) 누가 어떤방에 들어갔는지 정보를 리턴
    }

    public String getUserEnterRoomId(String sessionId) {
        return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
    }

    public void removeUserEnterInfo(String sessionId) {
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
    }


    //채팅방전부찾기
    public List<ChatRoom> getAll() {
        return chatRoomRepository.findAll();
    }


}
