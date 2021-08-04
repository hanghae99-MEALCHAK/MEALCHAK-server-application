package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.AllChatInfo;
import com.mealchak.mealchakserverapplication.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AllChatInfoRepository extends JpaRepository<AllChatInfo, Long> {
    Long countAllByChatRoom(ChatRoom chatRoom);
    void deleteByChatRoom(ChatRoom chatRoom);
    List<AllChatInfo> findAllByUserId(Long userId);
    AllChatInfo findbyRoomIdAndUserId(Long roomId,Long userId);
}
