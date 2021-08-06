package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.Review;
import com.mealchak.mealchakserverapplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    <T> List<T> findAllByUserIdOrderByCreatedAtDesc(Long userId, Class <T> type);
    Optional<Review> findByUserIdAndWriterId(Long userId, Long writerId);
    <T> List<T> findByWriterOrderByCreatedAtDesc(User user, Class <T> type);
}
