package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.model.AllChatInfo;
import com.mealchak.mealchakserverapplication.model.BanUserList;
import com.mealchak.mealchakserverapplication.repository.AllChatInfoQueryRepository;
import com.mealchak.mealchakserverapplication.repository.AllChatInfoRepository;
import com.mealchak.mealchakserverapplication.repository.BanUserListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BanUserListService {

    private final BanUserListRepository banUserListRepository;
    private final AllChatInfoRepository allChatInfoRepository;
    private final AllChatInfoQueryRepository allChatInfoQueryRepository;

    @Transactional
    public void banUser(Long userId,Long roomId){
        BanUserList banUserList = BanUserList.builder()
                .userId(userId)
                .roomId(roomId)
                .build();
        banUserListRepository.save(banUserList);
        AllChatInfo allChatInfo = allChatInfoQueryRepository.findByChatRoom_IdAndUser_Id(roomId,userId);
        allChatInfoRepository.delete(allChatInfo);
    }

}
