package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.model.*;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.AllChatInfoQueryRepository;
import com.mealchak.mealchakserverapplication.repository.AllChatInfoRepository;
import com.mealchak.mealchakserverapplication.repository.ChatMessageQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.mealchak.mealchakserverapplication.model.ChatMessage.MessageType.TALK;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AllChatInfoServiceTest {

    @InjectMocks
    private AllChatInfoService allChatInfoService;

    @Mock
    private AllChatInfoQueryRepository allChatInfoQueryRepository;

    @Mock
    private AllChatInfoRepository allChatInfoRepository;

    @Mock
    private ChatMessageQueryRepository chatMessageQueryRepository;

    private User user01;
    private Post post;
    private ChatRoom chatRoom;
    private AllChatInfo allChatInfo1;


    @BeforeEach
    void setUp() {
        Location location01 = new Location("서울특별시 강남구", 37.111111, 126.111111);
        user01 = new User(100L, 101L, "user01", "pw", "user01@test.com", "https://gorokke.shop/image/profileDefaultImg.jpg",
                "10", "female", "comment11", 5F, location01);

        post = new Post();
        chatRoom = new ChatRoom(111L, "UUID", user01.getId(), true, post);
        allChatInfo1 = new AllChatInfo(user01, chatRoom);
    }

    @Test
    @DisplayName("채팅방내_사용자_리스트_반환")
    void getUser() throws Exception {
        // given

        Location location02 = new Location("서울특별시 강남구", 37.222222, 126.222222);
        User user02 = new User(200L, 201L, "user02", "pw", "user01@test.com", "https://gorokke.shop/image/profileDefaultImg.jpg",
                "20", "male", "comment2", 5F, location02);

        List<AllChatInfo> allChatInfoList = new ArrayList<>();
        AllChatInfo allChatInfo2 = new AllChatInfo(user02, chatRoom);
        allChatInfoList.add(allChatInfo1);
        allChatInfoList.add(allChatInfo2);
        // mocking
        when(allChatInfoQueryRepository.findAllByChatRoom_Id(chatRoom.getId())).thenReturn(allChatInfoList);
        // when
        List<User> users = allChatInfoService.getUser(chatRoom.getId());
        //then
        verify(allChatInfoQueryRepository, times(1)).findAllByChatRoom_Id(chatRoom.getId());

        assertThat(users.get(0).getUsername()).isEqualTo(user01.getUsername());
        assertThat(users.get(0).getId()).isEqualTo(user01.getId());
        assertThat(users.get(1).getUsername()).isEqualTo(user02.getUsername());
        assertThat(users.get(1).getId()).isEqualTo(user02.getId());
    }

    @Test
    @DisplayName("채팅방_나가기_성공")
    void deleteAllchatInfo() throws Exception {
        // given
        UserDetailsImpl userDetails01 = new UserDetailsImpl(user01);

        // mocking
        when(allChatInfoQueryRepository.findByChatRoom_IdAndUser_Id(chatRoom.getId(), user01.getId()))
                .thenReturn(allChatInfo1);
        // when
        allChatInfoService.deleteAllChatInfo(chatRoom.getId(), userDetails01);
        //then
        verify(allChatInfoQueryRepository, times(1)).findByChatRoom_IdAndUser_Id(chatRoom.getId(), user01.getId());
        verify(allChatInfoRepository, times(1)).delete(allChatInfo1);
    }

    @Test
    @DisplayName("채팅방_접속종료시_마지막Talk타입_메세지의_Id저장_성공")
    void updateReadMessage() throws Exception {
        // given
        ChatMessage chatMessage = new ChatMessage(1L, TALK, "111L", "message", user01);

        // mocking
        when(chatMessageQueryRepository.findbyRoomIdAndTalk(String.valueOf(chatRoom.getId())))
                .thenReturn(chatMessage);
        when(allChatInfoQueryRepository.findByChatRoom_IdAndUser_Id(chatRoom.getId(), user01.getId()))
                .thenReturn(allChatInfo1);
        // when
        allChatInfoService.updateReadMessage(user01, String.valueOf(chatRoom.getId()));
        //then
        verify(chatMessageQueryRepository, times(1))
                .findbyRoomIdAndTalk(String.valueOf(chatRoom.getId()));
        verify(allChatInfoQueryRepository, times(1))
                .findByChatRoom_IdAndUser_Id(chatRoom.getId(), user01.getId());
        assertThat(allChatInfo1.getLastMessageId()).isEqualTo(1L);

    }
}