package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.model.AllChatInfo;
import com.mealchak.mealchakserverapplication.model.ChatRoom;
import com.mealchak.mealchakserverapplication.model.Location;
import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.AllChatInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserRoomServiceTest {

    @InjectMocks
    private UserRoomService userRoomService;

    @Mock
    private AllChatInfoRepository allChatInfoRepository;

    @Test
    @DisplayName("사용자와_채팅방_pk를_db에_저장_성공")
    void save01() throws Exception {
        // given
        Location location01 = new Location("서울특별시 강남구", 37.111111, 126.111111);
        User user01 = new User(100L, 101L, "user01", "pw", "user01@test.com",
                "https://gorokke.shop/image/profileDefaultImg.jpg",
                "10", "female", "comment11", 5F, location01);
        UserDetailsImpl userDetails = new UserDetailsImpl(user01);
        ChatRoom chatRoom = new ChatRoom("UUID111", userDetails.getUser());

        AllChatInfo allChatInfo = new AllChatInfo(userDetails.getUser(), chatRoom);
        // mocking

        // when
        userRoomService.save(userDetails.getUser(), chatRoom);
        //then
        verify(allChatInfoRepository, times(1)).save(refEq(allChatInfo));

    }



}