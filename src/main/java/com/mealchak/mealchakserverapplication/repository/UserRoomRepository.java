package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.AllChatInfo;
import com.mealchak.mealchakserverapplication.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoomRepository extends JpaRepository<AllChatInfo, Long> {
    List<AllChatInfo> findAllById(Long userId);
    Long countAllByChatRoom(ChatRoom chatRoom);
    void deleteByChatRoom(ChatRoom chatRoom);
}
