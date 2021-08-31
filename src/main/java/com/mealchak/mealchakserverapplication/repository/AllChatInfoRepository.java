package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.AllChatInfo;
import com.mealchak.mealchakserverapplication.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AllChatInfoRepository extends JpaRepository<AllChatInfo, Long> {
}
