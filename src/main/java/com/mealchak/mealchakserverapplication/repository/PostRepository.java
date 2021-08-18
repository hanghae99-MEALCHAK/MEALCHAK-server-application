package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query(value = "select p from Post p join fetch p.chatRoom join fetch p.user join fetch p.menu")
    List<Post> findByCheckValidTrueAndTitleContainingOrCheckValidTrueAndContentsContainingOrCheckValidTrueAndLocation_AddressContainingOrderByOrderTimeAsc(String title, String contents, String address);
    @Query(value = "select p from Post p join fetch p.chatRoom join fetch p.user join fetch p.menu")
    List<Post> findByCheckValidTrueAndTitleContainingOrCheckValidTrueAndContentsContainingOrCheckValidTrueAndLocation_AddressContaining(String title, String contents, String address);
    @Query(value = "select p from Post p join fetch p.chatRoom join fetch p.user join fetch p.menu")
    List<Post> findAllByCheckValidTrue();
    @Query(value = "select p from Post p join fetch p.chatRoom join fetch p.user join fetch p.menu")
    List<Post> findByCheckDeletedFalseAndUser_IdOrderByCreatedAtDesc(Long userId);
    Optional<Post> findByCheckValidTrueAndIdAndUserId(Long postId, Long id);
    Optional<Post> findByCheckValidTrueAndId(Long postId);
    @Query(value = "select p from Post p join fetch p.chatRoom join fetch p.user join fetch p.menu")
    List<Post> findByCheckValidTrueAndLocation_LatitudeBetweenAndLocation_LongitudeBetweenOrderByOrderTimeAsc(double latitudeStart, double latitudeEnd, double longitudeStart, double longitudeEnd);
    @Query(value = "select p from Post p join fetch p.chatRoom join fetch p.user join fetch p.menu")
    List<Post> findByCheckValidTrueAndLocation_LatitudeBetweenAndLocation_LongitudeBetweenAndMenu_CategoryContainingOrderByOrderTimeAsc(double latitudeStart, double latitudeEnd, double longitudeStart, double longitudeEnd, String category);
    @Query(value = "select p from Post p join fetch p.chatRoom join fetch p.user join fetch p.menu")
    List<Post> findByCheckValidTrueAndMenu_CategoryContainingOrderByOrderTimeAsc(String category);
    @Query(value = "select p from Post p join fetch p.chatRoom join fetch p.user join fetch p.menu")
    List<Post> findAllByCheckValidTrueOrderByOrderTimeAsc();
}
