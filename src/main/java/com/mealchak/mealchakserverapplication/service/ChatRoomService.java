package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.response.ChatRoomListResponseDto;
import com.mealchak.mealchakserverapplication.model.*;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final AllChatInfoQueryRepository allChatInfoQueryRepository;

    // HashOperations 레디스에서 쓰는 자료형
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo;
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsUserInfo;
    private final ChatRoomRepository chatRoomRepository;
    private final AllChatInfoRepository allChatInfoRepository;
    private final UserRepository userRepository;
    private final ChatMessageQueryRepository chatMessageQueryRepository;
    private final PostQueryRepository postQueryRepository;


    public static final String ENTER_INFO = "ENTER_INFO";
    public static final String USER_INFO = "USER_INFO";

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
        List<AllChatInfo> allChatInfoList = allChatInfoQueryRepository.findAllByUserIdOrderByIdDesc(user.getId());
        for (AllChatInfo allChatInfo : allChatInfoList) {
            ChatRoom chatRoom = allChatInfo.getChatRoom();
            Post post = chatRoom.getPost();
            Long headCountChat = allChatInfoQueryRepository.countAllByChatRoom(chatRoom);
            String chatRoomId = Long.toString(chatRoom.getId());
            Long myLastMessageId = allChatInfo.getLastMessageId();
            ChatMessage newLastMessage = chatMessageQueryRepository.findbyRoomIdAndTalk(chatRoomId);
            Long newLastMessageId = 0L;
            if (newLastMessage != null) {
                newLastMessageId = newLastMessage.getId();
            }

            // myLastMessageId 와 newLastMessageId 를 비교하여 현재 채팅방에 새 메세지가 있는지 여부를 함께 내려줌
            ChatRoomListResponseDto responseDto;
            if (myLastMessageId < newLastMessageId){
                responseDto = new ChatRoomListResponseDto(chatRoom, post, headCountChat, true);
            } else {
                responseDto = new ChatRoomListResponseDto(chatRoom, post, headCountChat, false);
            }
            responseDtoList.add(responseDto);
        }
        return responseDtoList;
    }

    // redis 에 입장정보로 sessionId 와 roomId를 저장하고 해단 sessionId 와 토큰에서 받아온 userId를 저장함
    public void setUserEnterInfo(String sessionId, String roomId, Long userId) {
        hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId);
        hashOpsUserInfo.put(USER_INFO, sessionId, Long.toString(userId));
    }

    // redis 에 저장했던 sessionId 로 roomId를 리턴함
    public String getUserEnterRoomId(String sessionId) {
        return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
    }

    // 유저가 나갈때 redis 에 저장했던 해당 세션 / 유저의 정보를 삭제함
    public void removeUserEnterInfo(String sessionId) {
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
        hashOpsUserInfo.delete(USER_INFO, sessionId);
    }

    // redis 에 저장했던 sessionId 로 userId 를 얻어오고 해당 userId 로 User 객체를 찾아 리턴함
    public User chkSessionUser(String sessionId){
        Long userId = Long.parseLong(Objects.requireNonNull(hashOpsUserInfo.get(USER_INFO, sessionId)));
        return userRepository.findById(userId).orElseThrow(()->new IllegalArgumentException("존재하지 않는 사용자"));
    }

    // 게시글 삭제시 채팅방도 삭제
    @Transactional
    public void deleteAllChatInfo(Long roomId, UserDetailsImpl userDetails) {
        AllChatInfo allChatInfo = allChatInfoQueryRepository.findByChatRoom_IdAndUser_Id(roomId, userDetails.getUser().getId());
        if (allChatInfo != null) {
            allChatInfoRepository.delete(allChatInfo);
        }
    }

    // 채팅방 나가기
    @Transactional
    public void quitChat(Long postId, UserDetailsImpl userDetails) {
        Post post = postQueryRepository.findById(postId);
        if (post == null){ throw new IllegalArgumentException("존재하지 않는 게시글입니다."); }
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
            AllChatInfo allChatInfo = allChatInfoQueryRepository.findByChatRoom_IdAndUser_Id(roomId, userDetails.getUser().getId());
            allChatInfoRepository.delete(allChatInfo);
            post.subNowHeadCount();
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
