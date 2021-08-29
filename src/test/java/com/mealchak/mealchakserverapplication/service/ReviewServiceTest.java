package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.request.ReviewRequestDto;
import com.mealchak.mealchakserverapplication.model.Review;
import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.ReviewRepository;
import com.mealchak.mealchakserverapplication.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mealchak.mealchakserverapplication.model.Review.MannerType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest{

        @InjectMocks
        private ReviewService reviewService;

        @Mock
        private ReviewRepository reviewRepository;
        @Mock
        private UserRepository userRepository;
        @Mock
        private Object ReviewListMapping;

        @Test
        @DisplayName("리뷰 작성하기")
        public void createReview() {
                // given
                User user1 = new User(100L, 101L, "user1", "password", "test@test.com",
                        "profileImg.jpg", "30대", "남" ,"ㅎㅇ", 50f, null);
                User user2 = new User(102L, 103L, "user2", "password", "test@test.com",
                        "profileImg.jpg", "30대", "남" ,"ㅎㅇ", 50f, null);
                UserDetailsImpl writer1 = new UserDetailsImpl(user2);
                ReviewRequestDto requestDto = new ReviewRequestDto("review_test_1", BEST);
                Review review = new Review(requestDto, user1, writer1.getUser());
                ReviewService reviewService = new ReviewService(reviewRepository, userRepository);

                // mocking
                when(userRepository.findById(user1.getId()))
                        .thenReturn(Optional.of(user1));

                // when
                reviewService.createReview(writer1, requestDto, user1.getId());

                // then
                assertEquals(user1.getMannerScore(), 50.1f);
                verify(userRepository, atLeastOnce()).findById(user1.getId());
                verify(reviewRepository, atLeastOnce()).findByUserIdAndWriterId(user1.getId(), writer1.getUser().getId());
        }

        @Test
        @DisplayName("리뷰 불러오기")
        public void getReview() {
                // given
                User user1 = new User(100L, 101L, "user1", "password", "test@test.com",
                        "profileImg.jpg", "30대", "남" ,"ㅎㅇ", 50f, null);
                User user2 = new User(102L, 103L, "user2", "password", "test@test.com",
                        "profileImg.jpg", "30대", "남" ,"ㅎㅇ", 50f, null);
                User user3 = new User(104L, 105L, "user2", "password", "test@test.com",
                        "profileImg.jpg", "30대", "남" ,"ㅎㅇ", 50f, null);
                User user4 = new User(106L, 107L, "user2", "password", "test@test.com",
                        "profileImg.jpg", "30대", "남" ,"ㅎㅇ", 50f, null);

                Review review1 = new Review(108L,"review_test_1", user1, user2, GOOD);
                Review review2 = new Review(109L, "review_test_2", user1, user3, BAD);
                Review review3 = new Review(110L, "review_test_3", user1, user4, BEST);
                List<Review> reviewList = new ArrayList<>();
                reviewList.add(review1);
                reviewList.add(review2);
                reviewList.add(review3);

                // mocking
//                when(reviewRepository.findAllByUserIdOrderByCreatedAtDesc(user1.getId(), ReviewListMapping.class))
//                        .thenReturn(Optional.of(reviewList));

                // when

                // then
                }

        @Test
        @DisplayName("리뷰 수정하기")
        public void updateReview() {
                // given
                User user1 = new User(100L, 101L, "user1", "password", "test@test.com",
                        "profileImg.jpg", "30대", "남" ,"ㅎㅇ", 50f, null);
                User user2 = new User(102L, 103L, "user2", "password", "test@test.com",
                        "profileImg.jpg", "30대", "남" ,"ㅎㅇ", 50f, null);
                UserDetailsImpl writer1 = new UserDetailsImpl(user1);
                Review review1 = new Review(108L,"review_test_1", user1, user2, GOOD);
                ReviewRequestDto requestDto = new ReviewRequestDto("review_test_update_1", BEST);

                // mocking
                when(reviewRepository.findById(review1.getId()))
                        .thenReturn(Optional.of(review1));

                // when
                reviewService.updateReview(writer1, requestDto, review1.getId());

                // then
                verify(reviewRepository, atLeastOnce()).findById(review1.getId());
        }

        @Test
        @DisplayName("리뷰 삭제하기")
        public void deleteReview() {
                // given
                User user1 = new User(100L, 101L, "user1", "password", "test@test.com",
                        "profileImg.jpg", "30대", "남" ,"ㅎㅇ", 50f, null);
                User user2 = new User(102L, 103L, "user2", "password", "test@test.com",
                        "profileImg.jpg", "30대", "남" ,"ㅎㅇ", 50f, null);
                UserDetailsImpl writer1 = new UserDetailsImpl(user1);
                Review review1 = new Review(108L,"review_test_1", user1, user2, GOOD);

                // when
                reviewService.deleteReview(writer1, review1.getId());

                // then
                verify(reviewRepository, atLeastOnce()).deleteByIdAndWriter_Id(review1.getId(), writer1.getUser().getId());
        }
}