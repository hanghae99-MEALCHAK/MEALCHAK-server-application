package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.model.AllChatInfo;
import com.mealchak.mealchakserverapplication.repository.UserRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRoomService {
    private final UserRoomRepository userRoomRepository;

    public void save(AllChatInfo allChatInfo){
        userRoomRepository.save(allChatInfo);
    }

}
