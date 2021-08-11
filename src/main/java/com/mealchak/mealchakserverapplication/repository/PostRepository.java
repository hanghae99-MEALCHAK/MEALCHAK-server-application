package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByCheckValidTrueAndTitleContainingOrContentsContainingOrderByOrderTimeAsc(String title, String contents);
    List<Post> findAllByCheckValidTrueOrderByCreatedAtDesc();
    List<Post> findAllByCheckValidTrue();
    List<Post> findByCheckValidTrueAndLocation_AddressContainingOrderByOrderTimeAsc(String address);
    List<Post> findByCheckValidTrueAndLocation_AddressContainingAndMenu_CategoryContainingOrderByOrderTimeAsc(String address, String category);
    List<Post> findByCheckDeletedFalseAndUser_IdOrderByCreatedAtDesc(Long userId);

}
