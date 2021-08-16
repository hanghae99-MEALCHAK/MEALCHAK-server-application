package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByCheckValidTrueAndTitleContainingOrCheckValidTrueAndContentsContainingOrCheckValidTrueAndLocation_AddressContainingOrderByOrderTimeAsc(String title, String contents, String address);
    List<Post> findByCheckValidTrueAndTitleContainingOrCheckValidTrueAndContentsContainingOrCheckValidTrueAndLocation_AddressContaining(String title, String contents, String address);
    List<Post> findAllByCheckValidTrue();
    List<Post> findByCheckDeletedFalseAndUser_IdOrderByCreatedAtDesc(Long userId);
    Optional<Post> findByCheckValidTrueAndIdAndUserId(Long postId, Long id);
    Optional<Post> findByCheckValidTrueAndId(Long postId);
    List<Post> findByCheckValidTrueAndLocation_LatitudeBetweenAndLocation_LongitudeBetweenOrderByOrderTimeAsc(double latitudeStart, double latitudeEnd, double longitudeStart, double longitudeEnd);
    List<Post> findByCheckValidTrueAndLocation_LatitudeBetweenAndLocation_LongitudeBetweenAndMenu_CategoryContainingOrderByOrderTimeAsc(double latitudeStart, double latitudeEnd, double longitudeStart, double longitudeEnd, String category);
    List<Post> findByCheckValidTrueAndMenu_CategoryContainingOrderByOrderTimeAsc(String category);
}
