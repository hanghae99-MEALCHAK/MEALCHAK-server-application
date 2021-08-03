package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.repository.mapping.UserInfoMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByKakaoId(Long kakaoId);
    <T> Optional<T> findByEmail(String email, Class <T>type);
    <T> Optional<T> findById(Long userId, Class <T>type);
}
