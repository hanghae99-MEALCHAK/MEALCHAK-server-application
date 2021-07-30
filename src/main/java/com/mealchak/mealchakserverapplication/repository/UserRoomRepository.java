package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.UserRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoomRepository extends JpaRepository<UserRoom,Long> {
}
