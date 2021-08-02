package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {
    ChatRoom findByRoomId(Long roomId);
    ChatRoom findByPostId(Long postId);
    void deleteByPostId(Long postId);
}
