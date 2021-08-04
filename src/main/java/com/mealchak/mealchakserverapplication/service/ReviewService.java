package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.request.ReviewRequestDto;
import com.mealchak.mealchakserverapplication.model.Review;
import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.ReviewRepository;
import com.mealchak.mealchakserverapplication.repository.UserRepository;
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
            User user = userRepository.findById(userDetails.getUser().getId()).
                    orElseThrow(()->new IllegalArgumentException("userId가 존재하지 않습니다."));
            return user;
        }else{
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
    }

    public List<Review> getReview(UserDetailsImpl userDetails) {
        User user = getUser(userDetails);
        return reviewRepository.findAllByUserId(user.getId());
    }

    // 리뷰 작성
    public void createReview(UserDetailsImpl userDetails, ReviewRequestDto requestDto, Long userId) {
        User user = getUser(userDetails);
        User writer = userRepository.findById(userId).
                orElseThrow(()->new IllegalArgumentException("작성자가 존재하지 않습니다."));
        Review review = new Review(requestDto, user, writer);
        reviewRepository.save(review);
    }

    // 리뷰 수정
    @Transactional
    public void updateReview(UserDetailsImpl userDetails, ReviewRequestDto requestDto, Long reviewId) {
        User user = getUser(userDetails);
        Review review = reviewRepository.findById(reviewId).
                orElseThrow(()->new IllegalArgumentException("리뷰가 존재하지 않습니다."));
        if (user.equals(review.getUser())) {
            review.updateReview(requestDto);
        }else {
            throw new IllegalArgumentException("리뷰 작성자가 다릅니다.");
        }
    }
}
