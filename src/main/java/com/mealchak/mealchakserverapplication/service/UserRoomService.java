package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.model.UserRoom;
import com.mealchak.mealchakserverapplication.repository.UserRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRoomService {
    private final UserRoomRepository userRoomRepository;

    public void save(UserRoom userRoom){
        userRoomRepository.save(userRoom);
    }
}
