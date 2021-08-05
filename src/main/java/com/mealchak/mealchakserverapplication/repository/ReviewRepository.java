package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    <T> List<T> findAllByUserIdOrderByCreatedAtDesc(Long userId, Class <T> type);
}
