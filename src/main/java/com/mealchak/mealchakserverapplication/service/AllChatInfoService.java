package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.model.AllChatInfo;
import com.mealchak.mealchakserverapplication.model.ChatMessage;
import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.AllChatInfoRepository;
import com.mealchak.mealchakserverapplication.repository.ChatMessageQueryRepository;
import com.mealchak.mealchakserverapplication.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AllChatInfoService {

    private final AllChatInfoRepository allChatInfoRepository;
    private final ChatMessageQueryRepository chatMessageQueryRepository;

    public List<User> getUser(Long roomId) {
        return allChatInfoRepository.findAllByChatRoom_Id(roomId)
                .stream()
                .map(AllChatInfo::getUser)
                .collect(Collectors.toList());
    }

    public void deleteAllChatInfo(Long roomId, UserDetailsImpl userDetails) {
        AllChatInfo allChatInfo = allChatInfoRepository.findByChatRoom_IdAndUser_Id(roomId, userDetails.getUser().getId());
        allChatInfoRepository.delete(allChatInfo);
    }

    @Transactional
    public void updateReadMessage(User user,String roomId){
        Long count = chatMessageQueryRepository.countAllByRoomIdAndType(roomId, ChatMessage.MessageType.TALK);
        AllChatInfo allChatInfo = allChatInfoRepository.findByChatRoom_IdAndUser_Id(Long.parseLong(roomId),user.getId());
        allChatInfo.updateCount(count);
    }
}

