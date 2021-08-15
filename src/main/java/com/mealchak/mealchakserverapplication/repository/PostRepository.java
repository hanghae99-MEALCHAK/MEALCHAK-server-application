package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByCheckValidTrueAndTitleContainingOrContentsContainingOrLocation_AddressContainingOrderByOrderTimeAsc(String title, String contents, String address);
    List<Post> findByCheckValidTrueAndTitleContainingOrContentsContainingOrLocation_AddressContaining(String title, String contents, String address);
    List<Post> findAllByCheckValidTrue();
    List<Post> findByCheckValidTrueAndLocation_AddressContainingOrderByOrderTimeAsc(String address);
    List<Post> findByCheckValidTrueAndLocation_AddressContainingAndMenu_CategoryContainingOrderByOrderTimeAsc(String address, String category);
    List<Post> findByCheckDeletedFalseAndUser_IdOrderByCreatedAtDesc(Long userId);
}
