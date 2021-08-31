package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {
    ChatRoom findByPostId(Long postId);
}
