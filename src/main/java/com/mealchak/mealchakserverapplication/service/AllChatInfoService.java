package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.model.AllChatInfo;
import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.AllChatInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AllChatInfoService {

    private final AllChatInfoRepository allChatInfoRepository;

    public List<User> getUser(Long roomId) {
        List<AllChatInfo> allChatInfoList = allChatInfoRepository.findAllByChatRoom_Id(roomId);
        List<User> userList = new ArrayList<>();

        for (AllChatInfo allChatInfo : allChatInfoList) {
            User user = allChatInfo.getUser();
            userList.add(user);
        }
        return userList;
    }
}

