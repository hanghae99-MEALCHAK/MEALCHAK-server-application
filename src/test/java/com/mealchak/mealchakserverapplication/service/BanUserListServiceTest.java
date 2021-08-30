package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.model.*;
import com.mealchak.mealchakserverapplication.repository.AllChatInfoQueryRepository;
import com.mealchak.mealchakserverapplication.repository.AllChatInfoRepository;
import com.mealchak.mealchakserverapplication.repository.BanUserListRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BanUserListServiceTest {

    @InjectMocks
    private BanUserListService banUserListService;

    @Mock
    private BanUserListRepository banUserListRepository;

    @Mock
    private AllChatInfoQueryRepository allChatInfoQueryRepository;

    @Mock
    private AllChatInfoRepository allChatInfoRepository;

    @Test
    @DisplayName("유저_ban처리_성공")
    void benUser() throws Exception {
        // given
        Location location01 = new Location("서울특별시 강남구", 37.111111, 126.111111);
        User user01 = new User(100L, 101L, "user01", "pw", "user01@test.com", "https://gorokke.shop/image/profileDefaultImg.jpg",
                "10", "female", "comment11", 5F, location01);
        Menu cafe = new Menu("카페", 1);
        ChatRoom chatRoom = new ChatRoom();
        Post post = new Post(100L, "title", 3, "restaurant01", "2021-09-01 00:00:00",
                "contents", true, false, chatRoom, user01, cafe, location01,
                2.00, 1L, Post.meetingType.SEPARATE);
        chatRoom = new ChatRoom(111L, "UUID", user01.getId(), true, post);

        BanUserList banUserList = BanUserList.builder()
                .userId(user01.getId())
                .roomId(chatRoom.getId())
                .build();
        AllChatInfo allChatInfo1 = new AllChatInfo(user01, chatRoom);
        // mocking
        when(allChatInfoQueryRepository.findByChatRoom_IdAndUser_Id(chatRoom.getId(), user01.getId()))
                .thenReturn(allChatInfo1);
        // when
        banUserListService.banUser(user01.getId(), chatRoom.getId());
        //then
        verify(banUserListRepository, times(1)).save(refEq(banUserList));
        verify(allChatInfoQueryRepository, times(1))
                .findByChatRoom_IdAndUser_Id(chatRoom.getId(), user01.getId());
        verify(allChatInfoRepository, times(1)).delete(allChatInfo1);
        assertThat(post.getNowHeadCount()).isEqualTo(0L);
    }

}