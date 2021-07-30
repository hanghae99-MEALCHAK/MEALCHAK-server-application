package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByTitleContainingOrContentsContainingOrderByCreatedAtDesc(String title, String contents);

//    @EntityGraph(attributePaths = {"User.username"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Post> findAllByOrderByCreatedAtDesc();
}
