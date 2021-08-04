package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.Review;
import com.mealchak.mealchakserverapplication.repository.mapping.ReviewListMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    List<ReviewListMapping> findAllByUserId(Long userId);
}
