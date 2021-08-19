package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    <T> List<T> findAllByUserIdOrderByCreatedAtDesc(Long userId, Class <T> type);
    Optional<Review> findByUserIdAndWriterId(Long userId, Long writerId);
    void deleteByIdAndWriter_Id(Long reviewId, Long writerId);
}
