package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.model.AllChatInfo;
import com.mealchak.mealchakserverapplication.model.ChatRoom;
import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.repository.UserRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserRoomService {
    private final UserRoomRepository userRoomRepository;

    @Transactional
    public void save(User user, ChatRoom chatRoom){
        AllChatInfo allChatInfo = new AllChatInfo(user, chatRoom);
        userRoomRepository.save(allChatInfo);
    }
}
