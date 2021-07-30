package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.UserRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoomRepository extends JpaRepository<UserRoom,Long> {
     List<UserRoom> findAllByUserId(Long userId);
}
