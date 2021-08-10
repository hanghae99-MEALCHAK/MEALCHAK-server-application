package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.AllChatInfo;
import com.mealchak.mealchakserverapplication.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AllChatInfoRepository extends JpaRepository<AllChatInfo, Long> {
    Long countAllByChatRoom(ChatRoom chatRoom);
    void deleteAllByChatRoom(ChatRoom chatRoom);
    List<AllChatInfo> findAllByUserId(Long userId);
    List<AllChatInfo> findAllByChatRoom_Id(Long roomId);
    AllChatInfo findByChatRoom_IdAndUser_Id(Long roomId,Long userId);
}
