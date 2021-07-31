package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByTitleContainingOrContentsContainingOrCategoryContainingOrderByCreatedAtDesc(String title, String contents, String category);
    List<Post> findAllByOrderByCreatedAtDesc();
    List<Post> findAllByLocationAddressIgnoreCase(String address);
}
