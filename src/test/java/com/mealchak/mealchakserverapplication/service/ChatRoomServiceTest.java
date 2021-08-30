package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.response.ChatRoomListResponseDto;
import com.mealchak.mealchakserverapplication.model.*;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Mock
    private AllChatInfoQueryRepository allChatInfoQueryRepository;

    @Mock
    private ChatMessageQueryRepository chatMessageQueryRepository;

    @Mock
    private AllChatInfoRepository allChatInfoRepository;

    @Mock
    private PostQueryRepository postQueryRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    private UserDetailsImpl userDetails01;
    private UserDetailsImpl userDetails02;
    private ChatRoom chatRoom01;
    private Post post01;

    @BeforeEach
    void setUp() {
        // 사용자 존재 user01
        Location location01 = new Location("서울특별시 강남구", 37.111111, 126.111111);
        User user01 = new User(100L, 101L, "user01", "pw", "user01@test.com", "https://gorokke.shop/image/profileDefaultImg.jpg",
                "10", "female", "comment11", 5F, location01);
        userDetails01 = new UserDetailsImpl(user01);
        // 사용자 user01 의 post01
        Menu cafe = new Menu("카페", 1);
        chatRoom01 = new ChatRoom("UUID111", userDetails01.getUser());
        post01 = new Post(100L, "title", 3, "restaurant01", "2021-09-01 00:00:00",
                "contents", true, false, chatRoom01, userDetails01.getUser(), cafe, location01,
                2.00, 1L);
        chatRoom01 = new ChatRoom(111L, "UUID111", userDetails01.getUser().getId(), true, post01);

        // 사용자 존재 user02
        Location locationUser02 = new Location("부산시 사하구", 37.222222, 126.222222);
        User user02 = new User(200L, 202L, "user02", "pw", "user02@test.com", "https://gorokke.shop/image/profileDefaultImg.jpg",
                "20", "female", "comment22", 6F, locationUser02);
        userDetails02 = new UserDetailsImpl(user02);
    }

    @Test
    @DisplayName("채팅방생성_성공")
    void createChatRoom01() throws Exception {
        // given

        // mocking
        // when
        ChatRoom result = chatRoomService.createChatRoom(userDetails01.getUser());
        //then
        assertThat(result.getOwnUserId()).isEqualTo(userDetails01.getUser().getId());

    }

    @Test
    @DisplayName("새메세지_확인_false내려주기")
    void newMessage01() throws Exception {
        // given
        List<AllChatInfo> allChatInfoList = new ArrayList<>();
        AllChatInfo allChatInfo = new AllChatInfo(1L, userDetails01.getUser(), chatRoom01, 10L);
        allChatInfoList.add(allChatInfo);

        ChatMessage chatMessage = new ChatMessage(10L, ChatMessage.MessageType.TALK,
                String.valueOf(chatRoom01.getId()), "message", userDetails01.getUser());
        // mocking
        when(allChatInfoQueryRepository.findAllByUserIdOrderByIdDesc(userDetails01.getUser().getId()))
                .thenReturn(allChatInfoList);
        when(allChatInfoQueryRepository.countAllByChatRoom(chatRoom01)).thenReturn(1L);
        when(chatMessageQueryRepository.findbyRoomIdAndTalk(String.valueOf(chatRoom01.getId())))
                .thenReturn(chatMessage);
        // when
        List<ChatRoomListResponseDto> results = chatRoomService.getOnesChatRoom(userDetails01.getUser());
        //then
        verify(allChatInfoQueryRepository, times(1))
                .findAllByUserIdOrderByIdDesc(userDetails01.getUser().getId());
        verify(allChatInfoQueryRepository, times(1))
                .countAllByChatRoom(chatRoom01);
        verify(chatMessageQueryRepository, times(1))
                .findbyRoomIdAndTalk(String.valueOf(chatRoom01.getId()));
        assertThat(results.get(0).isNewMessage()).isEqualTo(false);
    }

    @Test
    @DisplayName("새메세지_확인_true내려주기")
    void newMessage02() throws Exception {
        // given
        List<AllChatInfo> allChatInfoList = new ArrayList<>();
        AllChatInfo allChatInfo = new AllChatInfo(1L, userDetails01.getUser(), chatRoom01, 10L);
        allChatInfoList.add(allChatInfo);

        ChatMessage chatMessage = new ChatMessage(11L, ChatMessage.MessageType.TALK,
                String.valueOf(chatRoom01.getId()), "message", userDetails01.getUser());
        // mocking
        when(allChatInfoQueryRepository.findAllByUserIdOrderByIdDesc(userDetails01.getUser().getId()))
                .thenReturn(allChatInfoList);
        when(allChatInfoQueryRepository.countAllByChatRoom(chatRoom01)).thenReturn(1L);
        when(chatMessageQueryRepository.findbyRoomIdAndTalk(String.valueOf(chatRoom01.getId())))
                .thenReturn(chatMessage);
        // when
        List<ChatRoomListResponseDto> results = chatRoomService.getOnesChatRoom(userDetails01.getUser());
        //then
        verify(allChatInfoQueryRepository, times(1))
                .findAllByUserIdOrderByIdDesc(userDetails01.getUser().getId());
        verify(allChatInfoQueryRepository, times(1))
                .countAllByChatRoom(chatRoom01);
        verify(chatMessageQueryRepository, times(1))
                .findbyRoomIdAndTalk(String.valueOf(chatRoom01.getId()));
        assertThat(results.get(0).isNewMessage()).isEqualTo(true);
    }

    @Test
    @DisplayName("게시글_삭제시_채팅방함께 삭제")
    void deleteAllChatInfo01() throws Exception {
        // given
        AllChatInfo allChatInfo = new AllChatInfo(1L, userDetails01.getUser(), chatRoom01, 10L);
        // mocking
        when(allChatInfoQueryRepository.findByChatRoom_IdAndUser_Id(chatRoom01.getId(), userDetails01.getUser().getId()))
                .thenReturn(allChatInfo);
        // when
        chatRoomService.deleteAllChatInfo(chatRoom01.getId(), userDetails01);
        //then
        verify(allChatInfoQueryRepository, times(1))
                .findByChatRoom_IdAndUser_Id(chatRoom01.getId(), userDetails01.getUser().getId());
        verify(allChatInfoRepository, times(1))
                .delete(allChatInfo);
    }

    @Test
    @DisplayName("채팅방_나가기중_post_null일경우_IAE발생")
    void quitChat01() throws Exception {
        // given

        // mocking
        when(postQueryRepository.findById(post01.getId())).thenReturn(null);
        // when

        //then
        assertThrows(IllegalArgumentException.class,
                () -> chatRoomService.quitChat(post01.getId(), userDetails01)
                , "존재하지 않는 게시글입니다.");
    }

    @Test
    @DisplayName("채팅방_나가기중_본인_활성화게시글일_경우")
    void quitChat02() throws Exception {
        // given
        AllChatInfo allChatInfo = new AllChatInfo(1L, userDetails01.getUser(), chatRoom01, 10L);

        // mocking
        when(postQueryRepository.findById(post01.getId())).thenReturn(post01);
        when(allChatInfoQueryRepository.findByChatRoom_IdAndUser_Id(null, userDetails01.getUser().getId()))
                .thenReturn(allChatInfo);
        // when
        chatRoomService.quitChat(post01.getId(), userDetails01);
        //then
        verify(postQueryRepository, times(1))
                .findById(post01.getId());
        verify(allChatInfoQueryRepository, times(1))
                .findByChatRoom_IdAndUser_Id(null, userDetails01.getUser().getId());
        assertThat(post01.isCheckValid()).isEqualTo(false);
        assertThat(post01.isCheckDeleted()).isEqualTo(true);
        assertThat(post01.getMenu().getCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("채팅방_나가기중_본인_비활성화게시글일_경우")
    void quitChat03() throws Exception {
        // given
        Location location01 = new Location("서울특별시 강남구", 37.111111, 126.111111);
        Menu menu = new Menu("분식", 1);
        ChatRoom chatRoom = new ChatRoom("UUID111", userDetails01.getUser());
        Post post = new Post(100L, "title", 3, "restaurant01", "2021-09-01 00:00:00",
                "contents", false, false, chatRoom, userDetails01.getUser(), menu, location01,
                2.00, 1L);
        chatRoom = new ChatRoom(111L, "UUID111", userDetails01.getUser().getId(), true, post);

        AllChatInfo allChatInfo = new AllChatInfo(1L, userDetails01.getUser(), chatRoom, 10L);

        // mocking
        when(postQueryRepository.findById(post.getId())).thenReturn(post);
        when(allChatInfoQueryRepository.findByChatRoom_IdAndUser_Id(post.getChatRoom().getId(), userDetails01.getUser().getId()))
                .thenReturn(allChatInfo);
        // when
        chatRoomService.quitChat(post.getId(), userDetails01);
        //then
        verify(postQueryRepository, times(1)).findById(post.getId());
        verify(allChatInfoQueryRepository, times(1))
                .findByChatRoom_IdAndUser_Id(post.getChatRoom().getId(), userDetails01.getUser().getId());
        assertThat(post.isCheckValid()).isEqualTo(false);
        assertThat(post.isCheckDeleted()).isEqualTo(false);
        assertThat(post.getChatRoom().isChatValid()).isEqualTo(true);
    }
    @Test
    @DisplayName("채팅방_나가기중_일반유저_나가기")
    void quitChat04() throws Exception {
        // given
        AllChatInfo allChatInfo = new AllChatInfo(1L, userDetails02.getUser(), chatRoom01, 10L);
        // mocking
        when(postQueryRepository.findById(post01.getId())).thenReturn(post01);
        when(allChatInfoQueryRepository.findByChatRoom_IdAndUser_Id(null, userDetails02.getUser().getId()))
                .thenReturn(allChatInfo);
        // when
        chatRoomService.quitChat(post01.getId(), userDetails02);
        //then
        verify(postQueryRepository, times(1))
                .findById(post01.getId());
        verify(allChatInfoQueryRepository,times(1))
                .findByChatRoom_IdAndUser_Id(null, userDetails02.getUser().getId());
        verify(allChatInfoRepository, times(1))
                .delete(allChatInfo);
        assertThat(post01.getNowHeadCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("채팅방_주인확인_true")
    void isChatRoomOwner01() throws Exception {
        // given

        // mocking

        // when
        boolean result = chatRoomService.isChatRoomOwner(post01, userDetails01);
        //then
        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("채팅방_주인확인_false")
    void isChatRoomOwner02() throws Exception {
        // given

        // mocking

        // when
        boolean result = chatRoomService.isChatRoomOwner(post01, userDetails02);
        //then
        assertThat(result).isEqualTo(false);
    }


    @Test
    @DisplayName("채팅방상태_true>false로_변경_성공")
    void updateChatValid01() throws Exception {
        // given

        // mocking
        when(chatRoomRepository.findById(chatRoom01.getId())).thenReturn(Optional.of(chatRoom01));
        // when
        chatRoomService.updateChatValid(chatRoom01.getId());
        //then
        verify(chatRoomRepository, times(1))
                .findById(chatRoom01.getId());
        assertThat(chatRoom01.isChatValid()).isEqualTo(false);
    }

    @Test
    @DisplayName("채팅방상태_true>false로_변경_실패")
    void updateChatValid02() throws Exception {
        // given

        // mocking
        when(chatRoomRepository.findById(chatRoom01.getId())).thenReturn(Optional.empty());
        // when

        //then
        assertThrows(IllegalArgumentException.class,
                () -> chatRoomService.updateChatValid(chatRoom01.getId()));
        assertThat(chatRoom01.isChatValid()).isEqualTo(true);
    }

}