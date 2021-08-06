package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByCheckValidTrueAndTitleContainingOrContentsContainingOrderByCreatedAtDesc(String title, String contents);
    List<Post> findAllByCheckValidTrueOrderByCreatedAtDesc();
    List<Post> findAllByCheckValidTrue();
    List<Post> findByCheckValidTrueAndLocation_AddressContaining(String address);
    List<Post> findByUser_IdOrderByCreatedAtDesc(Long userId);
}
