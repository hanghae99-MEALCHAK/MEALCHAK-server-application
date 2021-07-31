package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.repository.mapping.UserInfoMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<User, Long> {
    Optional<UserInfoMapping> findByEmail(String email);
}
