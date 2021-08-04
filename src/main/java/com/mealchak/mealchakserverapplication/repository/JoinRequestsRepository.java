package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.JoinRequests;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JoinRequestsRepository extends JpaRepository<JoinRequests,Long> {
    JoinRequests findByUserIdAndPostId(Long userid,Long postId);
    List<JoinRequests> findByOwnUserId(Long ownUserId);
    List<JoinRequests> findByUserId(Long userId);
}
