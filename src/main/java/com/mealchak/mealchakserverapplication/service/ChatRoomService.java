package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.response.ChatRoomListResponseDto;
import com.mealchak.mealchakserverapplication.model.AllChatInfo;
import com.mealchak.mealchakserverapplication.model.ChatRoom;
import com.mealchak.mealchakserverapplication.model.Post;
import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.ChatRoomRepository;
import com.mealchak.mealchakserverapplication.repository.PostRepository;
import com.mealchak.mealchakserverapplication.repository.AllChatInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final PostRepository postRepository;

    // HashPerations 레디스에서 쓰는 자료형
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo;
    private final ChatRoomRepository chatRoomRepository;
    private final AllChatInfoRepository allChatInfoRepository;

    public static final String ENTER_INFO = "ENTER_INFO";

    //채팅방생성
    @Transactional
    public ChatRoom createChatRoom(User user) {
        String uuid = UUID.randomUUID().toString();
        ChatRoom chatRoom = new ChatRoom(uuid, user);
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }

    // 사용자별 채팅방 목록 조회
    public List<ChatRoomListResponseDto> getOnesChatRoom(User user) {
        List<ChatRoomListResponseDto> responseDtoList = new ArrayList<>();
        List<AllChatInfo> allChatInfoList = allChatInfoRepository.findAllByUserIdOrderByIdDesc(user.getId());
        for (AllChatInfo allChatInfo : allChatInfoList) {
            ChatRoom chatRoom = allChatInfo.getChatRoom();
            Post post = chatRoom.getPost();
            Long headCountChat = allChatInfoRepository.countAllByChatRoom(chatRoom);
            ChatRoomListResponseDto responseDto = new ChatRoomListResponseDto(chatRoom, post, headCountChat);
            responseDtoList.add(responseDto);
        }
        return responseDtoList;
    }

    // redisTemplate 에 (입장 type) 누가 어떤방에 들어갔는지 정보를 리턴
    public void setUserEnterInfo(String sessionId, String roomId) {
        hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId);
    }

    public String getUserEnterRoomId(String sessionId) {
        return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
    }

    public void removeUserEnterInfo(String sessionId) {
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
    }

    // 게시글 삭제시 채팅방도 삭제
    @Transactional
    public void deleteAllChatInfo(Long roomId, UserDetailsImpl userDetails) {
        AllChatInfo allChatInfo = allChatInfoRepository.findByChatRoom_IdAndUser_Id(roomId, userDetails.getUser().getId());
        allChatInfoRepository.delete(allChatInfo);
    }

    // 채팅방 나가기
    @Transactional
    public void quitChat(Long postId, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("존재하지않는게시글")
        );
        Long roomId = post.getChatRoom().getId();
        // 활성화 게시글이고 글쓴이면 게시글, 채팅방 비활성화
        if (post.isCheckValid() && isChatRoomOwner(post, userDetails)) {
            post.getMenu().updateMenuCount(-1);
            post.expired(false);
            post.deleted(true);
            deleteAllChatInfo(roomId, userDetails);
        // 비활성화 게시글이고 글쓴이면 채팅방 비활성화
        } else if (isChatRoomOwner(post, userDetails)) {
            deleteAllChatInfo(roomId, userDetails);
        // 일반 유저일 때 채팅방 나가기
        } else {
            AllChatInfo allChatInfo = allChatInfoRepository.findByChatRoom_IdAndUser_Id(roomId, userDetails.getUser().getId());
            allChatInfoRepository.delete(allChatInfo);
        }
    }

    // 채팅방 주인 확인
    static boolean isChatRoomOwner(Post post, UserDetailsImpl userDetails) {
        Long roomOwnerId = post.getChatRoom().getOwnUserId();
        Long userId = userDetails.getUser().getId();
        return roomOwnerId.equals(userId);
    }

    // 채팅방 chatValid -> false
    @Transactional
    public void updateChatValid(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()->new IllegalArgumentException(""));
        chatRoom.updatechatValid(false);
    }
}
