package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.request.ReviewRequestDto;
import com.mealchak.mealchakserverapplication.model.Review;
import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.ReviewRepository;
import com.mealchak.mealchakserverapplication.repository.UserRepository;
import com.mealchak.mealchakserverapplication.repository.mapping.ReviewListMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    // find User
    public User getUser(UserDetailsImpl userDetails) {
        if (userDetails != null) {
            return userRepository.findById(userDetails.getUser().getId()).
                    orElseThrow(() -> new IllegalArgumentException("userId가 존재하지 않습니다."));
        } else {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
    }

    // 본인 리뷰 조회
    public List<ReviewListMapping> getReview(UserDetailsImpl userDetails) {
        User user = getUser(userDetails);
        return reviewRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId(), ReviewListMapping.class);
    }

    // 리뷰 작성
    @Transactional
    public void createReview(UserDetailsImpl userDetails, ReviewRequestDto requestDto, Long userId) {
        User writer = getUser(userDetails);
        User user = userRepository.findById(userId).
                orElseThrow(() -> new IllegalArgumentException("작성자가 존재하지 않습니다."));
        if (reviewRepository.findByUserIdAndWriterId(userId, writer.getId()).isPresent()) {
            throw new IllegalArgumentException("이미 리뷰를 작성한 유저입니다.");
        }
        Review review = new Review(requestDto, user, writer);
        increaseMannerScore(review, user);
        reviewRepository.save(review);
    }

    // 리뷰 수정
    @Transactional
    public void updateReview(UserDetailsImpl userDetails, ReviewRequestDto requestDto, Long reviewId) {
        User user = getUser(userDetails);
        Review review = reviewRepository.findById(reviewId).
                orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));
        if (user.equals(review.getUser())) {
            decreaseMannerScore(review, user);
            review.updateReview(requestDto);
            increaseMannerScore(review, user);
        } else {
            throw new IllegalArgumentException("리뷰 작성자가 다릅니다.");
        }
    }

    // 리뷰 작성, 수정시 유저 mannerScore 증가
    public static void increaseMannerScore(Review review, User user) {
        if (Review.MannerType.BEST.equals(review.getMannerType())) {
            user.updateMannerScore(+0.1f);
        } else if (Review.MannerType.GOOD.equals(review.getMannerType())) {
            user.updateMannerScore(+0.05f);
        }else {
            user.updateMannerScore(-0.05f);
        }
    }

    // 리뷰 수정시 유저 mannerScore 감소
    public static void decreaseMannerScore(Review review, User user) {
        if (Review.MannerType.BEST.equals(review.getMannerType())) {
            user.updateMannerScore(-0.1f);
        } else if (Review.MannerType.GOOD.equals(review.getMannerType())) {
            user.updateMannerScore(-0.05f);
        }else {
            user.updateMannerScore(+0.05f);
        }
    }
}
