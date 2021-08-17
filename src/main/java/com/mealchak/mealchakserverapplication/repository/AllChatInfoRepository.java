package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.AllChatInfo;
import com.mealchak.mealchakserverapplication.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AllChatInfoRepository extends JpaRepository<AllChatInfo, Long> {
    Long countAllByChatRoom(ChatRoom chatRoom);
    @Query("select a from AllChatInfo a join fetch a.user join fetch a.chatRoom")
    List<AllChatInfo> findAllByUserIdOrderByIdDesc(Long userId);
    @Query("select a from AllChatInfo a join fetch a.user join fetch a.chatRoom")
    List<AllChatInfo> findAllByChatRoom_Id(Long roomId);
    AllChatInfo findByChatRoom_IdAndUser_Id(Long roomId,Long userId);
}
