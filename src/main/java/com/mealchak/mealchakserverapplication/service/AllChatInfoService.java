package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.model.AllChatInfo;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.AllChatInfoRepository;
import com.mealchak.mealchakserverapplication.repository.UserRepository;
import com.mealchak.mealchakserverapplication.repository.mapping.UserInfoMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AllChatInfoService {

    private final AllChatInfoRepository allChatInfoRepository;
    private final UserRepository userRepository;

    public List<UserInfoMapping> getUser(Long roomId, UserDetailsImpl userDetails) {
        List<AllChatInfo> allChatInfoList = allChatInfoRepository.findAllByChatRoom_Id(roomId);
        UserInfoMapping ownUserInfoMapping = userRepository.findById(userDetails.getUser().getId(), UserInfoMapping.class).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 유저")
        );

        List<UserInfoMapping> userInfoMappingList = new ArrayList<>();

        for (AllChatInfo allChatInfo : allChatInfoList) {
            Long userId = allChatInfo.getUser().getId();
            UserInfoMapping userInfoMapping = userRepository.findById(userId, UserInfoMapping.class).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 유저"));
            if (!userDetails.getUser().getId().equals(userId)) {
                userInfoMappingList.add(userInfoMapping);
            }
        }
        userInfoMappingList.add(ownUserInfoMapping);
        return userInfoMappingList;
    }
}

