package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.AllChatInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoomRepository extends JpaRepository<AllChatInfo,Long> {
     List<AllChatInfo> findAllByUserId(Long userId);
     Long countAllByRoomId(Long roomId);
}
