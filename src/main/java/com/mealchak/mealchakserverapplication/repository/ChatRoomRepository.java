package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {
    List<ChatRoom> findAllByOrderByCreatedAtDesc();
}
