package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.request.ChatMessageRequestDto;
import com.mealchak.mealchakserverapplication.model.*;
import com.mealchak.mealchakserverapplication.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.util.Optional;

import static com.mealchak.mealchakserverapplication.model.ChatMessage.MessageType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

    @InjectMocks
    private ChatMessageService chatMessageService;
    @Mock
    private BanUserListService banUserListService;
    @Mock
    private ChatMessageQueryRepository chatMessageQueryRepository;
    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private ChannelTopic channelTopic;
    @Mock
    private RedisTemplate redisTemplate;
    @Mock
    private ChatRoomService chatRoomService;
    @Mock
    private UserService userService;
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private BanUserListRepository banUserListRepository;
    @Mock
    private AllChatInfoRepository allChatInfoRepository;
    @Mock
    private AllChatInfoQueryRepository allChatInfoQueryRepository;
    @Mock
    private HashOperations<String, String, String> hashOpsEnterInfo;
    @Mock
    private HashOperations<String, String, String> hashOpsUserInfo;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostQueryRepository postQueryRepository;

    @Test
    @DisplayName("채팅방 입장")
    public void sendChatMessage_ENTER() {
        // given
        User user = new User(100L, 101L, "user1", "password", "test@test.com",
                "profileImg.jpg", "30대", "남" ,"ㅎㅇ", 50f, null);
        ChatMessageRequestDto requestDto = new ChatMessageRequestDto(ENTER, "1000", user.getId(), "test_sendMessage");

        // mocking
        when(userService.getUser(requestDto.getSenderId())).thenReturn(user);

        // when
        ChatMessage chatMessage = new ChatMessage(140L, requestDto.getType(), requestDto.getRoomId(),
                requestDto.getMessage(), userService.getUser(requestDto.getSenderId()));
        chatMessageService.sendChatMessage(chatMessage);

        // then
        verify(userService).getUser(requestDto.getSenderId());
    }

    @Test
    @DisplayName("채팅방 퇴장")
    public void sendChatMessage_QUIT() {
        // given
        User user = new User(100L, 101L, "user1", "password", "test@test.com",
                "profileImg.jpg", "30대", "남" ,"ㅎㅇ", 50f, null);
        ChatMessageRequestDto requestDto = new ChatMessageRequestDto(QUIT, "1000", user.getId(), "test_sendMessage");

        // mocking
        when(userService.getUser(requestDto.getSenderId())).thenReturn(user);

        // when
        ChatMessage chatMessage = new ChatMessage(140L, requestDto.getType(), requestDto.getRoomId(),
                requestDto.getMessage(), userService.getUser(requestDto.getSenderId()));
        chatMessageService.sendChatMessage(chatMessage);

        // then
        verify(userService).getUser(requestDto.getSenderId());
    }

    @Test
    @DisplayName("채팅방 강퇴")
    public void sendChatMessage_BAN() {
        // given
        User user = new User(100L, 101L, "user", "password", "test@test.com",
                "profileImg.jpg", "30대", "남", "ㅎㅇ", 50f, null);
        User user1 = new User(102L, 103L, "user1", "password", "test@test.com",
                "profileImg.jpg", "30대", "남", "ㅎㅇ", 50f, null);
        Menu menu = new Menu(30L, "떡볶이", "testImgUrl", 1);
        Location location = new Location("서울 강남구", 123.123, 123.123);
        ChatRoom chatRoom = new ChatRoom(20L, "2f48f241-9d64-4d16-bf56-70b9d4e0e79a", user.getId(), true, null);
        Post post = new Post(10L, "test_chatMessage", 4, "testRestaurant", "2021-11-21 11:21:00",
                "test_sendMessage", true, false, chatRoom, user, menu, location, 1000L, 2L, Post.meetingType.SEPARATE);
        chatRoom.setPost(post);
        ChatMessageRequestDto requestDto = new ChatMessageRequestDto(BAN, "20", user1.getId(), "102");
        AllChatInfo allChatInfo = new AllChatInfo(50L, user1, chatRoom, 102L);
        BanUserList banUserList = BanUserList.builder()
                .userId(user1.getId())
                .roomId(chatRoom.getId())
                .build();

        // mocking
        BanUserListService banUserListService = new BanUserListService(banUserListRepository, allChatInfoRepository, allChatInfoQueryRepository);
        when(allChatInfoQueryRepository.findByChatRoom_IdAndUser_Id(chatRoom.getId(), user1.getId())).thenReturn(allChatInfo);

        // when
        ChatMessage chatMessage = new ChatMessage(140L, requestDto.getType(), requestDto.getRoomId(),
                requestDto.getMessage(), user);
        chatMessageService.sendChatMessage(chatMessage);
        banUserListService.banUser(user1.getId(), chatRoom.getId());

        // then
        assertEquals(1L, post.getNowHeadCount());
        verify(banUserListRepository, atLeastOnce()).save(refEq(banUserList));
        verify(allChatInfoQueryRepository, atLeastOnce()).findByChatRoom_IdAndUser_Id(chatRoom.getId(), user1.getId());
        verify(allChatInfoRepository, atLeastOnce()).delete(allChatInfo);
    }

    @Test
    @DisplayName("채팅방 폭파")
    public void sendChatMessage_BREAK() {
        // given
        User user = new User(100L, 101L, "user1", "password", "test@test.com",
                "profileImg.jpg", "30대", "남" ,"ㅎㅇ", 50f, null);
        Menu menu = new Menu(30L, "떡볶이", "testImgUrl", 1);
        Location location = new Location("서울 강남구", 123.123, 123.123);
        ChatRoom chatRoom = new ChatRoom(20L, "2f48f241-9d64-4d16-bf56-70b9d4e0e79a", user.getId(), true, null);
        Post post = new Post(10L, "test_chatMessage", 4, "testRestaurant", "2021-11-21 11:21:00",
                "test_sendMessage", true, false, chatRoom, user, menu, location, 1000L, 2L, Post.meetingType.SEPARATE);
        chatRoom.setPost(post);
        ChatMessageRequestDto requestDto = new ChatMessageRequestDto(BREAK, "20", user.getId(), "test_sendMessage");
        ChatMessage chatMessage = new ChatMessage(140L, requestDto.getType(), requestDto.getRoomId(),
                requestDto.getMessage(), userService.getUser(requestDto.getSenderId()));

        // mocking
        ChatRoomService chatRoomService = new ChatRoomService(allChatInfoQueryRepository, chatRoomRepository, allChatInfoRepository, userRepository, chatMessageQueryRepository, postQueryRepository);
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(Optional.of(chatRoom));

        // when
        chatMessageService.sendChatMessage(chatMessage);
        chatRoomService.updateChatValid(chatRoom.getId());
        chatRoom.updatechatValid(false);

        // then
        assertFalse(chatRoom.isChatValid());
        verify(chatRoomRepository, atLeastOnce()).findById(chatRoom.getId());
    }

    @Test
    @DisplayName("채팅방 메세지 불러오기")
    public void getChatMessageByRoomId() {
    }

    @Test
    @DisplayName("채팅방 ID 불러오기")
    public void getRoomId() {
    }
}